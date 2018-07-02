package cn.fh.jobdep.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {
    @Bean
    public CloseableHttpClient httpClient() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(2000)
                .setSocketTimeout(2000)
                .setConnectionRequestTimeout(2000)
                .build();

        return HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build();
    }
}
