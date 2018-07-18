package cn.fh.jobdep.api;

import cn.fh.jobdep.api.vo.DepResponse;
import cn.fh.jobdep.api.vo.FinishRequest;
import cn.fh.jobdep.api.vo.SubmitInfo;
import cn.fh.jobdep.graph.AdjTaskGraph;
import cn.fh.jobdep.graph.JobFormatter;
import cn.fh.jobdep.graph.JobVertex;
import cn.fh.jobdep.task.TaskService;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/task", produces = "application/json;charset=utf8")
public class TaskController extends BaseController {
    @Autowired
    private TaskService taskService;

    /**
     * 接收任务完成通知
     * @param req
     * @return
     */
    @PostMapping(value = "/finish")
    public DepResponse jobFinishNotify(@RequestBody FinishRequest req) {
        taskService.triggerNextJobs(req.getTaskId(), req.getJobId(), req.getSuccess(), req.getResult());

        return succResp;
    }

    /**
     * 提交任务
     * @param yaml
     * @return
     */
    @PostMapping(value = "/submit")
    public DepResponse<SubmitInfo> startTask(@RequestBody String yaml) {
        SubmitInfo info = taskService.startTask(yaml);

        return buildData(info);
    }

    /**
     * 查询指定Task下的所有Job状态
     * @param taskId
     * @return
     */
    @GetMapping(value = "/{taskId}")
    public DepResponse<List<JobVertex>> listJobs(@PathVariable Long taskId) {
        AdjTaskGraph g = taskService.queryGraph(taskId);
        List<JobVertex> jobList = g.format(new JobFormatter());

        return buildData(jobList);
    }
}
