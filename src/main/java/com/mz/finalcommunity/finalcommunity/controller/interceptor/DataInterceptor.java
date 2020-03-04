package com.mz.finalcommunity.finalcommunity.controller.interceptor;

import com.mz.finalcommunity.finalcommunity.entity.User;
import com.mz.finalcommunity.finalcommunity.service.DataService;
import com.mz.finalcommunity.finalcommunity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DataInterceptor implements HandlerInterceptor {
    @Autowired
    private DataService dataService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //Statistical UV
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);
        //Statistical DAU
        User user = hostHolder.getUserThreadLocal();
        if(user!=null){
            dataService.recordDAU(user.getId());
        }
        return true;
    }
}
