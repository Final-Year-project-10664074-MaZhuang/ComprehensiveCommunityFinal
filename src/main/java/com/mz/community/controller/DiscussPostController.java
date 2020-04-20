package com.mz.community.controller;

import com.mz.community.annotation.LoginRequired;
import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.entity.*;
import com.mz.community.event.EventProducer;
import com.mz.community.Service.*;
import com.mz.community.util.CommunityConstant;
import com.mz.community.util.CommunityUtil;
import com.mz.community.util.HostHolder;
import com.mz.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    private ElasticSearchService elasticSearchService;
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
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TagsService tagsService;

    @LoginRequired
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content, String tag) {
        if (StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            return CommunityUtil.getJSONString(403, "Can't post empty content");
        }
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "You are not logged in!!!");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        if (tag == null) {
            throw new IllegalArgumentException("Tags param can not be null");
        }
        String[] tagsArray = tag.split(",");
        Event event = new Event()
                .setTopic(TOPIC_ADD_POST)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId())
                .setTags(tagsArray);
        eventProducer.fireEvent(event);
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, post.getId());

        event = new Event()
                .setTopic(TOPIC_RECOMMEND_POST)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId())
                .setTags(tagsArray);
        eventProducer.fireEvent(event);

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
        org.springframework.data.domain.Page<DiscussPost> resultList = elasticSearchService.searchDiscussPost(post.getTitle(), 0, 5);
        //compile data
        List<Map<String,Object>> resultPosts = new ArrayList<>();
        if(resultList!=null){
            for (DiscussPost resultPost : resultList) {
                if(resultPost.getId()!=post.getId()){
                    Map<String,Object> map = new HashMap<>();
                    map.put("post",resultPost);
                    map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,resultPost.getId()));
                    List<Tags> resultTags = neoDiscussPostMapper.selectTagsByDiscussPostId(resultPost.getId());
                    map.put("postTags",resultTags);
                    resultPosts.add(map);
                }
            }
        }
        model.addAttribute("resultPosts",resultPosts);
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

    //Sticky
    @RequestMapping(path = "/top", method = RequestMethod.POST)
    @ResponseBody
    public String setTop(int id) {
        discussPostService.updateType(id, 1);
        //Trigger post event
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }

    //Set to essence
    @RequestMapping(path = "/wonderful", method = RequestMethod.POST)
    @ResponseBody
    public String setWonderful(int id) {
        discussPostService.updateStatus(id, 1);
        //Trigger post event
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id)
                .setStatus(1);
        eventProducer.fireEvent(event);
        //Calculate score
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, id);
        return CommunityUtil.getJSONString(0);
    }

    //delete
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String setDelete(int id) {
        discussPostService.updateStatus(id, 2);
        //Trigger delete post event
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id)
                .setStatus(2);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0);
    }

    @RequestMapping(path = "/tag/{tagName}", method = RequestMethod.GET)
    public String getPostByTag(@PathVariable("tagName") String tagName, Model model, Page page) {
        page.setRows(tagsService.findPostByTagRows(tagName));
        page.setPath("/discuss/tag/" + tagName);
        List<DiscussPost> postByTag = tagsService.findPostByTag(tagName, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (postByTag != null) {
            for (DiscussPost post : postByTag) {
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
        List<Tags> tags = discussPostService.findAllTags();
        model.addAttribute("AllTags", tags);
        List<Tags> hotTags = tagsService.findHotTags();
        model.addAttribute("hotTags", hotTags);
        List<DiscussPost> zeroPostList = tagsService.findZeroPostByTag(tagName, page.getOffset(), page.getLimit());
        model.addAttribute("zeroPostList", zeroPostList);
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("tagName", tagName);
        return "site/tagPost";
    }

    @RequestMapping(path = "/visitTime", method = RequestMethod.POST)
    @ResponseBody
    public String setVisitTime(int postId, int AuthorID, double second) {
        int currentUserId = hostHolder.getUser().getId();
        if(AuthorID==currentUserId){
            return CommunityUtil.getJSONString(0);
        }
        //Trigger delete post event
        Event event = new Event()
                .setTopic(TOPIC_VISITSECOND)
                .setUserId(currentUserId)
                .setEntityId(postId)
                .setSecond(second);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0);
    }
}
