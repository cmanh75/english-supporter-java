package com.englishsupporter.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        // Apache HttpClient tự động xử lý gzip/deflate compression
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                        .setDefaultSocketConfig(SocketConfig.custom()
                                .setSoTimeout(Timeout.of(30, TimeUnit.SECONDS))
                                .build())
                        .build())
                .evictIdleConnections(Timeout.of(30, TimeUnit.SECONDS))
                .build();
        
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(30000); // 30 seconds in milliseconds
        factory.setConnectionRequestTimeout(30000); // 30 seconds in milliseconds
        
        return new RestTemplate(factory);
    }
}

