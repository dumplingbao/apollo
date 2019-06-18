package com.ctrip.framework.apollo.configservice.interceptor;

import com.ctrip.framework.apollo.biz.service.AppService;
import com.ctrip.framework.apollo.common.entity.App;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 针对 /configs/** 请求进行鉴权处理
 * Created by TJ on 2019/6/15.
 */
@Component
public class RestHandlerInterceptor implements HandlerInterceptor {

    @Autowired
    private AppService appService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        for(MethodParameter methodParameter : methodParameters){
            System.out.println(methodParameter.getParameterName());
        }

        Map pathVariable = (Map)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String appId = (String)pathVariable.get("appId");

        String appApiKey = null;
        App app = appService.findOne(appId);
        if (app != null) {
            appApiKey = app.getApiKey();
        }
        String apiKey = request.getHeader("apiKey");

        if(StringUtils.isEmpty(apiKey) || !apiKey.equals(appApiKey)){
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, String.format("apiKey is required or it is wrong"));
//            Tracer.logEvent("Apollo.Config.NotFound", assembleKey(appId, clusterName, originalNamespace, dataCenter));
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        for(MethodParameter methodParameter : methodParameters){
            System.out.println(methodParameter.getParameterName());
        }
    }
}
