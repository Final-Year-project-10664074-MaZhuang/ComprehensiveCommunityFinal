package com.mz.community.controller.interceptor;

import com.mz.community.Service.UserService;
import com.mz.community.entity.LoginTicket;
import com.mz.community.entity.User;
import com.mz.community.util.CookieUtil;
import com.mz.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //get ticket value from cookie
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket != null) {
            //query login ticket
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //Check if the login ticket are valid
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                //according the ticket find user information
                User user = userService.findUserById(loginTicket.getUserId());
                //Let this request hold users
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
