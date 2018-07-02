package cn.fh.jobdep.test.service;

import cn.fh.jobdep.task.HttpService;
import cn.fh.jobdep.test.BaseTestClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class HttpServiceTest extends BaseTestClass {
    @Autowired
    private HttpService httpService;

    @Test
    public void testSendRequest() {
        String resp = httpService.sendRequest("http://www.baidu.com", "");
        System.out.println(resp);
    }
}
