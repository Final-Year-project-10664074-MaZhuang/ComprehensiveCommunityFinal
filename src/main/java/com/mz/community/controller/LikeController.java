package com.mz.community.controller;

import com.mz.community.annotation.LoginRequired;
import com.mz.community.entity.Event;
import com.mz.community.entity.User;
import com.mz.community.event.EventProducer;
import com.mz.community.Service.LikeService;
import com.mz.community.util.CommunityUtil;
import com.mz.community.util.HostHolder;
import com.mz.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

import static com.mz.community.util.CommunityConstant.ENTITY_TYPE_POST;
import static com.mz.community.util.CommunityConstant.TOPIC_LIKE;

@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    @LoginRequired
    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User user = hostHolder.getUser();
        //likes
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        //count
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //status
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        //return results
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        if(likeStatus==0){
            likeStatus=-1;
        }
        //Departure like event
        Event event = new Event()
                .setTopic(TOPIC_LIKE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityUserId)
                .setData("postId", postId)
                .setLikeStatus(likeStatus);
        eventProducer.fireEvent(event);

        if (entityType == ENTITY_TYPE_POST) {
            //Calculate score
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, postId);
        }
        return CommunityUtil.getJSONString(0, null, map);
    }
}
