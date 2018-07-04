package cn.fh.jobdep.test.service;

import cn.fh.jobdep.graph.AdjTaskGraph;
import cn.fh.jobdep.task.HttpService;
import cn.fh.jobdep.task.TaskService;
import cn.fh.jobdep.test.BaseTestClass;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;

public class TaskServiceTest extends BaseTestClass {
    @Autowired
    @InjectMocks
    private TaskService taskService;

    @Mock
    private HttpService httpService;

    @Test
    public void testParse() throws IOException {
        String yaml = IOUtils.toString(new FileInputStream("src/test/resources/task.yaml"), "UTF-8");
        AdjTaskGraph g = taskService.buildTaskGraph(yaml);
        System.out.println(g);
    }

    @Test
    public void testTrigger() throws Exception {
        String yaml = IOUtils.toString(new FileInputStream("src/test/resources/task.yaml"), "UTF-8");
        AdjTaskGraph g = taskService.buildTaskGraph(yaml);

        Long gid = taskService.saveGraph(g);
        taskService.triggerNextJobs(gid, 0, true, "ok");
    }

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        Mockito.when(httpService.sendRequest(any(), any()))
                .thenReturn("success");
    }

}
