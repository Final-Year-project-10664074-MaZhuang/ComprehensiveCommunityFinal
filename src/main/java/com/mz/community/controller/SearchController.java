package com.mz.community.controller;

import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.entity.DiscussPost;
import com.mz.community.entity.Page;
import com.mz.community.entity.Tags;
import com.mz.community.Service.ElasticSearchService;
import com.mz.community.Service.LikeService;
import com.mz.community.Service.UserService;
import com.mz.community.util.CommunityConstant;
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
public class SearchController implements CommunityConstant {
    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private NeoDiscussPostMapper neoDiscussPostMapper;

    @RequestMapping(path = "/search",method = RequestMethod.GET)
    public String search(Model model, String keyword, Page page){
        //search
        org.springframework.data.domain.Page<DiscussPost> searchResult =
                elasticSearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        //compile data
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(searchResult!=null){
            for (DiscussPost post : searchResult) {
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                map.put("user",userService.findUserById(post.getUserId()));
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId()));
                List<Tags> tags = neoDiscussPostMapper.selectTagsByDiscussPostId(post.getId());
                map.put("postTags",tags);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("keyword",keyword);
        //comment pagination
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());
        return "/site/search";
    }
}
