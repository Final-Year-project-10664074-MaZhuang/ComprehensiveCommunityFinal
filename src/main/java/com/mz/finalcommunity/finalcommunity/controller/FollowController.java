package com.mz.finalcommunity.finalcommunity.controller;

import com.mz.finalcommunity.finalcommunity.annotation.LoginRequired;
import com.mz.finalcommunity.finalcommunity.entity.User;
import com.mz.finalcommunity.finalcommunity.service.FollowService;
import com.mz.finalcommunity.finalcommunity.util.CommunityUtil;
import com.mz.finalcommunity.finalcommunity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FollowController {
    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType,int entityId){
        User user = hostHolder.getUserThreadLocal();

        followService.follow(user.getId(),entityType,entityId);
        return CommunityUtil.getJSONString(0,"Followed");
    }

    @LoginRequired
    @RequestMapping(path = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unFollow(int entityType,int entityId){
        User user = hostHolder.getUserThreadLocal();

        followService.unFollow(user.getId(),entityType,entityId);
        return CommunityUtil.getJSONString(0,"Followed");
    }
}
