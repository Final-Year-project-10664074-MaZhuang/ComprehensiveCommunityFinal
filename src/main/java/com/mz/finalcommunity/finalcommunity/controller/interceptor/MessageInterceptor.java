package com.mz.finalcommunity.finalcommunity.controller.interceptor;

import com.mz.finalcommunity.finalcommunity.entity.User;
import com.mz.finalcommunity.finalcommunity.service.MessageService;
import com.mz.finalcommunity.finalcommunity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUserThreadLocal();
        if (user!=null&&modelAndView!=null){
            int letterUnreadCount = messageService.findLetterUnread(user.getId(),null);
            int noticeUnreadCount = messageService.findNoticeUnread(user.getId(),null);
            modelAndView.addObject("allUnreadCount",letterUnreadCount+noticeUnreadCount);
        }
    }
}
