package cn.fh.jobdep.test.service;

import cn.fh.jobdep.graph.Graph;
import cn.fh.jobdep.task.TaskService;
import cn.fh.jobdep.test.BaseTestClass;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.io.IOException;

public class TaskServiceTest extends BaseTestClass {
    @Autowired
    private TaskService taskService;

    @Test
    public void testParse() throws IOException {
        String yaml = IOUtils.toString(new FileInputStream("src/test/resources/task.yaml"), "UTF-8");
        Graph g = taskService.buildTaskGraph(yaml);
        System.out.println(g);
    }
}
