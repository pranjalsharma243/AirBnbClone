package com.myprojects.projects.airbnb.ratelimiting;



import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {

    @Bean
    public FilterRegistrationBean<RateLimiter> rateLimitFilterRegistration() {
        FilterRegistrationBean<RateLimiter> reg = new FilterRegistrationBean<>(new RateLimiter());
        reg.setOrder(1);
        reg.addUrlPatterns("/*");
        return reg;
    }
}
