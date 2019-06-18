package com.ctrip.framework.apollo.configservice.config;

import com.ctrip.framework.apollo.configservice.interceptor.RestHandlerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * Created by TJ on 2019/6/15.
 */
@Configuration
public class CustomWebMvcConfigurerAdapter implements WebMvcConfigurer {

    @Resource
    private RestHandlerInterceptor restHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(restHandlerInterceptor).addPathPatterns("/configs/**");
//                .excludePathPatterns("/api/login",
//                        "/api/getPublicKey","/api/getRSAEncode");
    }
}
