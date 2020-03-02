package com.mz.finalcommunity.finalcommunity.controller;

import com.mz.finalcommunity.finalcommunity.annotation.LoginRequired;
import com.mz.finalcommunity.finalcommunity.entity.User;
import com.mz.finalcommunity.finalcommunity.service.LikeService;
import com.mz.finalcommunity.finalcommunity.util.CommunityUtil;
import com.mz.finalcommunity.finalcommunity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId,int entityUserId) {
        User user = hostHolder.getUserThreadLocal();
        //likes
        likeService.like(user.getId(), entityType, entityId,entityUserId);
        //count
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //status
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        //return results
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        return CommunityUtil.getJSONString(0, null, map);
    }
}
