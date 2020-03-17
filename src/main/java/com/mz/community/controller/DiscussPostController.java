package com.mz.community.controller;

import com.mz.community.Service.DiscussPostService;
import com.mz.community.Service.UserService;
import com.mz.community.annotation.LoginRequired;
import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.entity.DiscussPost;
import com.mz.community.entity.Tags;
import com.mz.community.entity.User;
import com.mz.community.util.CommunityUtil;
import com.mz.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private NeoDiscussPostMapper neoDiscussPostMapper;

    @LoginRequired
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content,String tag){
        System.out.println(tag);
        if(StringUtils.isBlank(title)||StringUtils.isBlank(content)){
            return CommunityUtil.getJSONString(403,"Can't post empty content");
        }
        User user = hostHolder.getUser();
        if(user==null){
            return CommunityUtil.getJSONString(403,"You are not logged in!!!");
        }
        DiscussPost post = new DiscussPost();
        Tags tags = new Tags();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        tags.setTagName(tag);
        discussPostService.addDiscussPost(post,tags);
        return CommunityUtil.getJSONString(0,"Published successfully");
    }

    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model){
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);
        List<Tags> tags = neoDiscussPostMapper.selectTagsByDiscussPostId(post.getId());
        model.addAttribute("postTags",tags);
        return "/site/discuss-detail";
    }
}
