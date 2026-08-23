package com.example.codeflow.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * 服务间调用的 RestClient：agent-service 通过 HTTP 访问其他服务（不使用共享 Service 代码）。
 */
@Configuration
public class ServiceClientConfig {

    @Bean
    public RestClient contentClient(@Value("${service.url.content}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public RestClient userClient(@Value("${service.url.user}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
