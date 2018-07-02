package cn.fh.jobdep.task;

import cn.fh.jobdep.error.JobException;
import cn.fh.jobdep.graph.AdjTaskGraph;
import cn.fh.jobdep.graph.Graph;
import cn.fh.jobdep.graph.JobEdge;
import cn.fh.jobdep.graph.JobVertex;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TaskService {

    /**
     * 将yaml配置转成DAG
     *
     * @param yaml
     * @return
     */
    public Graph buildTaskGraph(String yaml) {
        Graph taskGraph = parseYaml(yaml);
        validateGraph(taskGraph);

        return taskGraph;
    }

    private Graph parseYaml(String yaml) {
        // 解析yaml
        Yaml parser = new Yaml();
        Map<String, Object> yamlMap = parser.loadAs(yaml, Map.class);

        // 分配jobId
        Map<String, Integer> jobIdMap = allocateJobId(yamlMap.keySet());

        // 生成顶点
        // jobName -> job
        Map<String, JobVertex> jobMap = new HashMap<>();
        // 生成边父子关系
        // jobName -> next job name List
        Map<String, List<String>> edgeMap = new HashMap<>();
        for (Map.Entry<String, Object> entries : yamlMap.entrySet()) {
            String jobName = entries.getKey();
            Map<String, Object> jobInfoMap = (Map<String, Object>) entries.getValue();

            // 构造顶点对象
            JobVertex jobVertex = new JobVertex();
            jobVertex.setIndex(jobIdMap.get(jobName));
            jobVertex.setName(jobName);
            jobVertex.setNotifyUrl((String) jobInfoMap.get("notifyUrl"));
            jobVertex.setTriggerUrl((String) jobInfoMap.get("triggerUrl"));
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

    private void validateGraph(Graph g) {
        if (g.hasCircle()) {
            throw new JobException("circle in graph");
        }

        if (g.getLasts().size() != 1) {
            throw new JobException("graph does not end with single vertex");
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
