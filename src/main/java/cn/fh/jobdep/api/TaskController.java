package cn.fh.jobdep.api;

import cn.fh.jobdep.api.vo.DepResponse;
import cn.fh.jobdep.api.vo.FinishRequest;
import cn.fh.jobdep.api.vo.SubmitInfo;
import cn.fh.jobdep.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/task", produces = "application/json;charset=utf8")
public class TaskController extends BaseController {
    @Autowired
    private TaskService taskService;

    @PostMapping(value = "/finish")
    public DepResponse jobFinishNotify(@RequestBody FinishRequest req) {
        taskService.triggerNextJobs(req.getTaskId(), req.getJobId(), req.getSuccess(), req.getResult());

        return succResp;
    }

    @PostMapping(value = "/submit")
    public DepResponse<SubmitInfo> startTask(@RequestBody String yaml) {
        SubmitInfo info = taskService.startTask(yaml);

        return buildData(info);
    }
}
