package com.mz.community.controller;

import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.entity.DiscussPost;
import com.mz.community.entity.Page;
import com.mz.community.entity.Tags;
import com.mz.community.entity.User;
import com.mz.community.service.DiscussPostService;
import com.mz.community.service.LikeService;
import com.mz.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mz.community.util.CommunityConstant.ENTITY_TYPE_POST;

@Controller
public class DiscussIndexController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private NeoDiscussPostMapper neoDiscussPostMapper;
    @Autowired
    private LikeService likeService;

    @RequestMapping(value = "/discussIndex",method = RequestMethod.GET)
    public String getDiscussIndex(Model model, Page page,
                                  @RequestParam(name = "orderMode", defaultValue = "1") int orderMode){
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/discussIndex?orderMode=" + orderMode);

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit(),orderMode);
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (list!=null){
            for (DiscussPost post: list) {
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                List<Tags> tags = neoDiscussPostMapper.selectTagsByDiscussPostId(post.getId());
                map.put("postTags",tags);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        List<Tags> tags = discussPostService.findAllTags();
        model.addAttribute("AllTags",tags);
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode", orderMode);
        return "site/discussIndex";
    }
}
