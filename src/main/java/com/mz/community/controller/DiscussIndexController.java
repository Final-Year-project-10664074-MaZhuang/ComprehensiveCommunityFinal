package com.mz.community.controller;

import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.entity.*;
import com.mz.community.service.DiscussPostService;
import com.mz.community.service.LikeService;
import com.mz.community.service.TagsService;
import com.mz.community.service.UserService;
import com.mz.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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
    @Autowired
    private TagsService tagsService;
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/discussIndex", method = RequestMethod.GET)
    public String getDiscussIndex(Model model, Page page,
                                  @RequestParam(name = "orderMode", defaultValue = "0") int orderMode) {
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/discussIndex?orderMode=" + orderMode);

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit(), orderMode);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                List<Tags> tags = neoDiscussPostMapper.selectTagsByDiscussPostId(post.getId());
                map.put("postTags", tags);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }

        List<Category> categoryList = neoDiscussPostMapper.selectAllCategory();
        List<Map<String, Object>> AllTags = new ArrayList<>();
        if (categoryList!=null){
            for (Category category : categoryList) {
                Map<String,Object> map = new HashMap<>();
                map.put("category",category);
                List<Tags> tagsList = discussPostService.findAllTagsByCategory(category.getName());
                map.put("tagsList",tagsList);
                AllTags.add(map);
            }
        }
        model.addAttribute("categoryList", AllTags);
        List<Tags> hotTags = tagsService.findHotTags();
        model.addAttribute("hotTags", hotTags);
        if (hostHolder.getUser() != null) {
            List<DiscussPost> zeroRelationList = neoDiscussPostMapper.selectRelationZeroReply(hostHolder.getUser().getId(), page.getOffset(), page.getLimit());
            if (zeroRelationList.size()>0){
                model.addAttribute("zeroReply", zeroRelationList);
            }else {
                List<DiscussPost> zeroList = neoDiscussPostMapper.selectZeroReply(hostHolder.getUser().getId(), page.getOffset(), page.getLimit());
                model.addAttribute("zeroReply", zeroList);
            }
        } else {
            List<DiscussPost> zeroList = neoDiscussPostMapper.selectZeroReply(0, page.getOffset(), page.getLimit());
            model.addAttribute("zeroReply", zeroList);
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("orderMode", orderMode);
        return "site/discussIndex";
    }

    @RequestMapping(path = "/myPost/{userId}", method = RequestMethod.GET)
    public String getMyPostPage(@PathVariable("userId") int userId, Page page, @RequestParam(name = "orderMode", defaultValue = "1") int orderMode,
                                Model model) {
        if (userId == 182) {
            return "/site/Crawler-post";
        }
        User user = userService.findUserById(userId);
        if (user != null) {
            model.addAttribute("user", user);

        } else {
            throw new RuntimeException("This user does not exist");
        }

        page.setRows(discussPostService.findDiscussPostRows(userId));
        page.setPath("/user/myPost/" + userId + "?orderMode=" + orderMode);
        model.addAttribute("row", page.getRows());

        List<DiscussPost> myPostList = discussPostService.findDiscussPosts(userId, page.getOffset(), page.getLimit(), 1);
        List<Map<String, Object>> myPosts = new ArrayList<>();
        if (myPostList != null) {
            for (DiscussPost post : myPostList) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                List<Tags> tags = neoDiscussPostMapper.selectTagsByDiscussPostId(post.getId());
                map.put("postTags", tags);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);

                myPosts.add(map);
            }
        }
        List<Tags> tags = discussPostService.findAllTags();
        model.addAttribute("AllTags", tags);
        model.addAttribute("myPosts", myPosts);
        model.addAttribute("orderMode", orderMode);
        return "/site/my-post";
    }
}
