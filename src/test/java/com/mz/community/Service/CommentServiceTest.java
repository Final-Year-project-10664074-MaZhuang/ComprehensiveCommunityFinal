package com.mz.community.Service;

import com.mz.community.CommunityApplication;
import com.mz.community.entity.Comment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;
    private Comment comment;
    @Before
    public void before(){
        comment = new Comment();
        comment.setId(999999);
        comment.setUserId(5);
        comment.setTargetId(3);
        comment.setEntityId(1);
        comment.setEntityType(1);
        comment.setContent("test comment");
        comment.setStatus(1);
        comment.setCreateTime(new Date());
        int i = commentService.addComment(comment, 5);
        assertEquals(1,i);
    }

    @After
    public void after(){

    }

    @Test
    public void findCommentsByEntity() {

    }

    @Test
    public void findCommentCount() {
    }

    @Test
    public void findCommentById() {
    }
}