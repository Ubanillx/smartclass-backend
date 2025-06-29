package com.ubanillx.smartclass.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * SSE配置类
 * 配置SSE相关的参数，如CORS和异步请求超时设置
 */
@Configuration
public class SseConfig implements WebMvcConfigurer {

    /**
     * SSE异步请求超时时间，单位毫秒
     */
    private static final long SSE_TIMEOUT = 3600000L; // 1小时
    
    /**
     * 配置异步请求支持
     * 设置SSE请求的超时时间和任务执行器
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // 设置异步请求超时时间
        configurer.setDefaultTimeout(SSE_TIMEOUT);
        
        // 可以使用自定义线程池处理异步请求
        // configurer.setTaskExecutor(taskExecutor());
    }
    
    /**
     * 配置CORS跨域支持
     * 允许SSE请求的跨域访问
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/chat/sse/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .exposedHeaders("Access-Control-Allow-Origin")
            .allowCredentials(true)
            .maxAge(3600);
    }
    
    /**
     * 配置缓存控制
     * SSE请求不应该被缓存
     */
    @Bean
    public CacheControl sseNoCache() {
        return CacheControl.noCache()
                .noTransform()
                .mustRevalidate()
                .noStore()
                .maxAge(0, TimeUnit.SECONDS);
    }
} 