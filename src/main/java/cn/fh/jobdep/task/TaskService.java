package cn.fh.jobdep.task;

import cn.fh.jobdep.api.vo.SubmitInfo;
import cn.fh.jobdep.error.JobException;
import cn.fh.jobdep.graph.AdjTaskGraph;
import cn.fh.jobdep.graph.JobEdge;
import cn.fh.jobdep.graph.JobStatus;
import cn.fh.jobdep.graph.JobVertex;
import cn.fh.jobdep.task.store.TaskStore;
import cn.fh.jobdep.task.vo.NotifyRequest;
import cn.fh.jobdep.task.vo.TriggerData;
import cn.fh.jobdep.task.vo.TriggerRequest;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskService {
    @Autowired
    private TaskStore<AdjTaskGraph> taskStore;

    @Autowired
    private HttpService httpService;

    /**
     * 触发任务图;
     *
     * @param yaml
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SubmitInfo startTask(String yaml) {
        AdjTaskGraph graph = buildTaskGraph(yaml);
        Long gid = saveGraph(graph);

        // List<JobVertex> roots = graph.getRoots();
        triggerNextJobs(gid, -1, true, "init");

        return new SubmitInfo(gid);
    }

    /**
     * 将yaml配置转成DAG
     *
     * @param yaml
     * @return
     */
    public AdjTaskGraph buildTaskGraph(String yaml) {
        AdjTaskGraph taskGraph = parseYaml(yaml);
        validateGraph(taskGraph);

        return taskGraph;
    }


    /**
     * 保存任务图
     * @param g
     * @return
     */
    public Long saveGraph(AdjTaskGraph g) {
        return taskStore.saveTask(g);
    }

    /**
     * 触发后序job; 如果已经是最后一个job了, 则触发通知;
     * 在收到job完成请求时调用;
     *
     * @param taskId
     * @param jobId
     * @param success
     * @param lastJobResult
     * @return 有新任务触发返回true
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean triggerNextJobs(Long taskId, Integer jobId, boolean success, String lastJobResult) {
        // 取出任务图
        AdjTaskGraph g = taskStore.getTaskGraph(taskId);
        if (null == g) {
            throw new JobException("invalid taskId");
        }
        if (g.getTaskStatus() == JobStatus.FINISHED || g.getTaskStatus() == JobStatus.FAILED) {
            throw new JobException("cannot modify job at 'finished' or 'failed' status");
        }

        List<JobVertex> nextJobList;
        if (jobId != -1) {
            if (!success) {
                // 任务失败
                g.changeJobStatus(jobId, JobStatus.FAILED);
                log.info("trigger failed nofity for job {}", jobId);
                triggerNotify(taskId, jobId, g);
                return false;
            }

            // 设置此job任务结果
            g.setResult(jobId, lastJobResult);
            g.changeJobStatus(jobId, JobStatus.FINISHED);
            log.info("job {} marked as finished, result = {}", jobId, lastJobResult);

            nextJobList = g.getChildren(jobId);

        } else {
            nextJobList = g.getRoots();
        }

        // 取出后续job
        // List<JobVertex> nextJobList = g.getChildren(jobId);
        if (CollectionUtils.isEmpty(nextJobList)) {
            // 没有后序任务了
            // 触发成功通知
            log.info("trigger success nofity for job {}", jobId);
            triggerNotify(taskId, jobId, g);
            return false;
        }

        // 遍历后续job
        boolean allSuccess = true;
        for (JobVertex job : nextJobList) {
            // 判断是否所有前序job都完成了
            List<JobVertex> preJobList = g.getParents(job.getIndex());

            if (!isAllFinished(preJobList)) {
                // 没都完成, 不能触发
                log.info("job {} does not qualify", job.getName());
                continue;
            }

            // 可以触发
            boolean ok = triggerJob(taskId, g, job, preJobList);
            if (!ok) {
                allSuccess = false;
            }
        }

        if (allSuccess) {
            taskStore.updateTask(taskId, g, lastJobResult);
        }

        return true;
    }


    private void triggerNotify(Long taskId, Integer vertex, AdjTaskGraph g) {
        JobVertex job = g.getJobVertex(vertex);
        try {
            if (job.getStatus() == JobStatus.FINISHED) {
                // 成功
                NotifyRequest req = new NotifyRequest(0, job.getResult());
                httpService.sendRequest(job.getNotifyUrl(), JSON.toJSONString(req));
                g.changeTaskStatus(JobStatus.FINISHED);

                taskStore.updateTask(taskId, g, "success");

            } else {
                NotifyRequest req = new NotifyRequest(-1, "");
                httpService.sendRequest(job.getNotifyUrl(), JSON.toJSONString(req));
                g.changeTaskStatus(JobStatus.FAILED);

                taskStore.updateTask(taskId, g, "failed");
            }

        } catch (Exception e) {
            g.changeTaskStatus(JobStatus.FAILED);
            taskStore.updateTask(taskId, g, "failed to trigger notification, reason = " + e.getMessage());
        }
    }

    private boolean triggerJob(Long taskId, AdjTaskGraph g, JobVertex job, List<JobVertex> preJobList) {
        try {
            // 将前序job结果组合起来
            List<TriggerData> triggerDataList = preJobList.stream()
                    .map( j -> new TriggerData(j.getName(), j.getResult()))
                    .collect(Collectors.toList());

            // 触发任务
            TriggerRequest request = new TriggerRequest(job.getIndex(), triggerDataList);
            String resp = httpService.sendRequest(job.getTriggerUrl(), JSON.toJSONString(request));
            g.changeJobStatus(job.getIndex(), JobStatus.RUNNING);
            log.info("trigger job {}, response = {}", job.getName(), resp);

            return true;

        } catch (Exception e) {
            g.changeTaskStatus(JobStatus.FAILED);

            String reason = "failed to trigger next job " + job.getName() + ", reason = " + e.getMessage();
            taskStore.updateTask(taskId, g, reason);

            log.info(reason);

            return false;
        }
    }


    private boolean isAllFinished(List<JobVertex> jobList) {
        if (CollectionUtils.isEmpty(jobList)) {
            return true;
        }

        return jobList.stream().allMatch(job -> job.getStatus() == JobStatus.FINISHED);
    }

    private AdjTaskGraph parseYaml(String yaml) {
        // 解析yaml
        Yaml parser = new Yaml();
        Map<String, Object> yamlMap = parser.loadAs(yaml, Map.class);

        // 分配jobId
        Map<String, Integer> jobIdMap = allocateJobId(yamlMap.keySet());

        // 生成顶点
        // jobName -> job对象
        Map<String, JobVertex> jobMap = new HashMap<>();

        // 生成边父子关系
        // jobName -> next job name List
        Map<String, List<String>> edgeMap = new HashMap<>();
        for (Map.Entry<String, Object> entries : yamlMap.entrySet()) {
            String jobName = entries.getKey();
            Map<String, Object> jobInfoMap = (Map<String, Object>) entries.getValue();

            // 验证通知url是否非空
            String triggerUrl = (String) jobInfoMap.get("triggerUrl");
            if (null == triggerUrl || triggerUrl.isEmpty()) {
                throw new JobException("invalid job yaml for " + jobName + ", trigger url cannot be empty");
            }

            // 构造顶点对象
            JobVertex jobVertex = new JobVertex();
            jobVertex.setIndex(jobIdMap.get(jobName));
            jobVertex.setName(jobName);
            jobVertex.setNotifyUrl((String) jobInfoMap.get("notifyUrl"));
            jobVertex.setTriggerUrl(triggerUrl);
            jobMap.put(jobName, jobVertex);

            // 维护边父子关系
            List<String> nextJobList = (List<String>) jobInfoMap.get("next");
            if (null == nextJobList) {
                continue;
            }
            for (String nextJob : nextJobList) {
                edgeMap.computeIfAbsent(jobName, key -> new ArrayList<>()).add(nextJob);
            }
        }

        // 生成边
        List<JobEdge> edgeList = new ArrayList<>();
        for (JobVertex job : jobMap.values()) {
            JobVertex from = job;

            List<String> nextJobs = edgeMap.get(from.getName());
            if (null == nextJobs) {
                continue;
            }
            for (String name : nextJobs) {
                JobVertex to = jobMap.get(name);
                JobEdge edge = new JobEdge(from ,to);
                edgeList.add(edge);
            }
        }

        return new AdjTaskGraph(edgeList);
    }

    private void validateGraph(AdjTaskGraph g) {
        // 是否有环
        if (g.hasCircle()) {
            throw new JobException("circle in graph");
        }

        // 只否只有一个最终节点
        List<JobVertex> lastList = g.getLasts();
        if (lastList.size() != 1) {
            throw new JobException("graph does not end with single vertex");
        }

        // 终节点是否有通知url
        String addr = lastList.get(0).getNotifyUrl();
        if (null == addr || addr.isEmpty()) {
            throw new JobException("last job lacks notify url");
        }

    }

    private Map<String, Integer> allocateJobId(Set<String> nameSet) {
        Map<String, Integer> idMap = new HashMap<>();

        int id = 0;
        for (String name : nameSet) {
            idMap.put(name, id);
            ++id;
        }

        return idMap;
    }
}
