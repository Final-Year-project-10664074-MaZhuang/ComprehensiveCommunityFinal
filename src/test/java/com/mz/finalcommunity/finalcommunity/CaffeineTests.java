package com.mz.finalcommunity.finalcommunity;

import com.mz.finalcommunity.finalcommunity.entity.DiscussPost;
import com.mz.finalcommunity.finalcommunity.service.DiscussPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = FinalcommunityApplication.class)
public class CaffeineTests {
    @Autowired
    private DiscussPostService discussPostService;

    @Test
    public void initDataForTest(){
        for (int i = 0; i < 300000; i++) {
            DiscussPost post = new DiscussPost();
            post.setUserId(111);
            post.setTitle("should I install next.js and express on same project?");
            post.setContent("I want to do a project using Next.js and use Express for backend.\n" +
                    "\n" +
                    "should I install next and express in same project and do backend without API request to another route ( or subdomain ) for accessing database and other backend stuff ( in other words, I mean Next.js be something like a \"Template Engine\" for the Express ), or use express as another project and connect the Next.js app to Express app using it's API route ?\n" +
                    "\n" +
                    "I'm confused about it");
            post.setCreateTime(new Date());
            post.setScore(Math.random()*2000);
            discussPostService.addDiscussPost(post);
        }
    }

    @Test
    public void testCache(){
        System.out.println(discussPostService.findDiscussPosts(0,0,10,1));
        System.out.println(discussPostService.findDiscussPosts(0,0,10,1));
        System.out.println(discussPostService.findDiscussPosts(0,0,10,1));
        System.out.println(discussPostService.findDiscussPosts(0,0,10,0));
    }
}
