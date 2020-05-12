package com.mz.community.quartz;

import com.mz.community.Service.DiscussPostService;
import com.mz.community.Service.ElasticSearchService;
import com.mz.community.Service.LikeService;
import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.entity.DiscussPost;
import com.mz.community.util.CommunityConstant;
import com.mz.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job,CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticSearchService elasticsearchService;

    @Autowired
    private NeoDiscussPostMapper neoDiscussPostMapper;

    //Project start time
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-01-31 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("Initialization time failed", e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if (operations.size() == 0) {
            logger.info("Task cancelled with no data to refresh");
            return;
        }

        logger.info("Task started, refreshing score data: " + operations.size());
        while (operations.size() > 0) {
            this.refresh((Integer) operations.pop());
        }
        logger.info("Task stopped, refreshed score data");
    }

    private void refresh(int postId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if (post == null) {
            logger.error("Article is not exists: id=" + postId);
            return;
        }

        //Whether the essence
        boolean wonderful = post.getStatus() == 1;
        //comment number
        int commentCount = post.getCommentCount();
        //like number
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        //Calculate weights
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        //score = log(weights)+Days Since Release Date(days)
        double score = Math.log10(Math.max(w, 1))
                + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);
        //update post
        discussPostService.updateScore(postId, score);

        //Search data synchronously
        post.setScore(score);
        elasticsearchService.saveDiscussPost(post);
        neoDiscussPostMapper.updateDiscussPostScore(postId,score);
    }
}
