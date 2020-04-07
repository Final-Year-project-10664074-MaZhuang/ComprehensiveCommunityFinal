package com.mz.community.event;

import com.alibaba.fastjson.JSONObject;
import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.entity.DiscussPost;
import com.mz.community.entity.Event;
import com.mz.community.entity.Message;
import com.mz.community.service.*;
import com.mz.community.util.CommunityConstant;
import com.mz.community.util.MailClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {
    private static final Logger LOGGER = LoggerFactory.getLogger(Event.class);
    @Autowired
    private NeoFollowService neoFollowService;
    @Autowired
    private NeoCommentService neoCommentService;
    @Autowired
    private NeoLikeService neoLikeService;
    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private NeoDiscussPostMapper neoDiscussPostMapper;
    @Autowired
    private MessageService messageService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private ElasticSearchService elasticSearchService;
    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW,TOPIC_UNFOLLOW})
    public void handleCommentMessage(ConsumerRecord record){
        if(record==null||record.value()==null){
            LOGGER.error("The content of the message is empty");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if(event==null){
            LOGGER.error("Message format error");
            return;
        }
        //send system notice
        Message message = new Message();
        message.setFromId(SYSTEM_USERID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        Map<String,Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        if(!event.getData().isEmpty()){
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(),entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        if(TOPIC_LIKE.equals(event.getTopic())){
            if(event.getLikeStatus()==1){
                neoLikeService.addLike(event.getUserId(),event.getEntityId());
                messageService.addMessage(message);
            }
            if(event.getLikeStatus()==-1){
                neoLikeService.deleteLike(event.getUserId(),event.getEntityId());
            }
        }else if(TOPIC_FOLLOW.equals(event.getTopic())){
            neoFollowService.addFollow(event.getUserId(),event.getEntityId());
            messageService.addMessage(message);
        }else if(TOPIC_UNFOLLOW.equals(event.getTopic())){
            neoFollowService.deleteFollow(event.getUserId(),event.getEntityId());
        }else if(TOPIC_COMMENT.equals(event.getTopic())){
            neoCommentService.addComment(event.getUserId(),event.getEntityId());
            neoCommentService.updateCommentCount(event.getEntityId(),event.getCommentCount());
            messageService.addMessage(message);
        }

    }

    //Consumer Post Article Event
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            LOGGER.error("The content of the message is empty");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            LOGGER.error("Message format error");
            return;
        }
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticSearchService.saveDiscussPost(post);
    }

    //Consumer add Post Article Event
    @KafkaListener(topics = {TOPIC_ADD_POST})
    public void handleAddMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            LOGGER.error("The content of the message is empty");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            LOGGER.error("Message format error");
            return;
        }
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        String[] tags = event.getTags();
        //add new tags
        /*for (String aTag : tags) {
            Tags tag = neoDiscussPostMapper.selectTagByTagName(aTag);
            if(tag==null){
                tags.setTagName(aTag);
                neoDiscussPostMapper.insertTags(tags);
            }
        }*/
        neoDiscussPostMapper.insertDiscussPost(post);
        neoDiscussPostMapper.insertRelationDiscussPost(post.getUserId(),post.getId(),tags);
        elasticSearchService.saveDiscussPost(post);
    }

    //Consumer delete Article Event
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            LOGGER.error("The content of the message is empty");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            LOGGER.error("Message format error");
            return;
        }
        elasticSearchService.deleteDiscussPost(event.getEntityId());
        neoDiscussPostMapper.updateDiscussPostStatus(event.getEntityId(),event.getStatus());
    }

    @KafkaListener(topics = {TOPIC_CRAWLER})
    public void handleCrawlerPost(ConsumerRecord record){
        if (record == null || record.value() == null) {
            LOGGER.error("The content of the crawler is empty");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            LOGGER.error("crawler format error");
            return;
        }
        List<DiscussPost> crawlerFromStackOverFlow = crawlerService.getCrawlerFromStackOverFlow(event.getTags());
        Context context = new Context();
        String content =null;
        if (crawlerFromStackOverFlow!=null){
            for (DiscussPost discussPost : crawlerFromStackOverFlow) {
                elasticSearchService.saveDiscussPost(discussPost);
            }
            context.setVariable("content","Data crawl completed");
            content=templateEngine.process("/mail/tagResult", context);
            mailClient.sendMail("zhuang.ma@students.plymouth.ac.uk", "Data crawl completed", content);
        }else {
            context.setVariable("content","Data crawling failed, please check the code and Stack Overflow official website");
            content=templateEngine.process("/mail/tagResult", context);
            mailClient.sendMail("zhuang.ma@students.plymouth.ac.uk", "Data crawl failed", content);
        }
    }

    @KafkaListener(topics = {TOPIC_VISITSECOND})
    public void handleVisitSecond(ConsumerRecord record){
        if (record == null || record.value() == null) {
            LOGGER.error("Access time cannot be empty");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            LOGGER.error("Access time format error");
            return;
        }
        double second = 0.0;
        try {
            second=neoDiscussPostMapper.selectVisitSecondByUserId(event.getUserId(),event.getEntityId());
            neoDiscussPostMapper.updateVisitSecond(event.getEntityId(),event.getUserId(),event.getSecond()+second);
        }catch (Exception e){
            neoDiscussPostMapper.insertVisitSecond(event.getEntityId(),event.getUserId(),event.getSecond());
        }
    }
}
