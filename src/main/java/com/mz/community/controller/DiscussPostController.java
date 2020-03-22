package com.mz.community.controller;

import com.mz.community.Service.CommentService;
import com.mz.community.Service.DiscussPostService;
import com.mz.community.Service.LikeService;
import com.mz.community.Service.UserService;
import com.mz.community.annotation.LoginRequired;
import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.entity.*;
import com.mz.community.util.CommunityConstant;
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

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private CommentService commentService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private NeoDiscussPostMapper neoDiscussPostMapper;

    @LoginRequired
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content, String tag) {
        System.out.println(tag);
        if (StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            return CommunityUtil.getJSONString(403, "Can't post empty content");
        }
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "You are not logged in!!!");
        }
        DiscussPost post = new DiscussPost();
        Tags tags = new Tags();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        tags.setTagName(tag);
        discussPostService.addDiscussPost(post, tags);
        return CommunityUtil.getJSONString(0, "Published successfully");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        List<Tags> tags = neoDiscussPostMapper.selectTagsByDiscussPostId(post.getId());
        model.addAttribute("postTags", tags);
        //likes Count
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount", likeCount);

        //like status
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeStatus", likeStatus);
        //pagination
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());
        //comment
        //reply
        //comment list
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        //comment view object list
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                //comment view object
                Map<String, Object> commentVo = new HashMap<>();
                //add comment to view object
                commentVo.put("comment", comment);
                //add author to view object
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                //likes Count
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);

                //like status
                likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);

                //reply list
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                //reply view object list
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        //reply view object
                        Map<String, Object> replyVo = new HashMap<>();
                        //add reply to view object
                        replyVo.put("reply", reply);
                        //add author to view object
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        //reply target
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        //likes Count
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);

                        //like status
                        likeStatus = hostHolder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replies", replyVoList);
                //reply count
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }
}
