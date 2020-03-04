package com.mz.finalcommunity.finalcommunity;

import com.mz.finalcommunity.finalcommunity.dao.DiscussPostMapper;
import com.mz.finalcommunity.finalcommunity.dao.MessageMapper;
import com.mz.finalcommunity.finalcommunity.dao.UserMapper;
import com.mz.finalcommunity.finalcommunity.entity.DiscussPost;
import com.mz.finalcommunity.finalcommunity.entity.Message;
import com.mz.finalcommunity.finalcommunity.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes =FinalcommunityApplication.class)
public class MapperTests {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123");
        user.setSalt("as");
        user.setEmail("aaa@.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }
    @Test
    public void testUpdateUser(){
        int rows = userMapper.updateStatus(150,1);
        System.out.println(rows);
        rows = userMapper.updateHeader(150,"http://www.nowcoder.com/102.png");
        System.out.println(rows);
        rows = userMapper.updatePassword(150,"abcd123");
        System.out.println(rows);
    }

    @Test
    public void testSelectPosts(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149,0,10,0);
        for (DiscussPost post:list){
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testSelectLetters(){
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
        for (Message message : list) {
            System.out.println(message);
        }

        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        List<Message> list1 = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : list1) {
            System.out.println(message);
        }

        int count1 = messageMapper.selectLetterCount("111_112");
        System.out.println(count1);

        int i = messageMapper.selectLetterUnread(131, "111_131");
        System.out.println(i);
    }
}
