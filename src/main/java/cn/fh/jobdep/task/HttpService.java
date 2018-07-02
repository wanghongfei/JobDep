package cn.fh.jobdep.task;

import cn.fh.jobdep.error.JobException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;

@Service
public class HttpService {
    @Autowired
    private CloseableHttpClient httpClient;

    public String sendRequest(String addr, String body) {
        HttpPost post = new HttpPost(addr);

        try {
            post.setEntity(new StringEntity(body, "UTF-8"));

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, "UTF-8");

            } catch (IOException e) {
                throw new JobException(e.getMessage());
            }


        } catch (UnsupportedCharsetException e) {
            throw new JobException(e.getMessage());
        }

    }
}
