package cn.fh.jobdep.test;

import cn.fh.jobdep.JobDepApp;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = JobDepApp.class)
public class BaseTestClass {
}
