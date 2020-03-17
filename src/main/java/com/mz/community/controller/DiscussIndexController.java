package com.mz.community.controller;

import com.mz.community.Service.DiscussPostService;
import com.mz.community.Service.UserService;
import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.entity.DiscussPost;
import com.mz.community.entity.Page;
import com.mz.community.entity.Tags;
import com.mz.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DiscussIndexController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private NeoDiscussPostMapper neoDiscussPostMapper;

    @RequestMapping(value = "/discussIndex",method = RequestMethod.GET)
    public String getDiscussIndex(Model model, Page page){
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/discussIndex");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (list!=null){
            for (DiscussPost post: list) {
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                List<Tags> tags = neoDiscussPostMapper.selectTagsByDiscussPostId(post.getId());
                map.put("postTags",tags);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);
            }
        }
        List<Tags> tags = discussPostService.findAllTags();
        model.addAttribute("AllTags",tags);
        model.addAttribute("discussPosts",discussPosts);
        return "site/discussIndex";
    }
}
