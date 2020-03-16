package com.mz.community;

import com.mz.community.dao.mysqlMapper.DiscussPostMapper;
import com.mz.community.dao.mysqlMapper.UserMapper;
import com.mz.community.dao.neo4jMapper.NeoUserMapper;
import com.mz.community.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@SpringBootTest
@ContextConfiguration(classes =CommunityApplication.class)
public class MapperTests {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NeoUserMapper neoUserMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Test
    public void testCrud(){
        System.out.println(userMapper.selectByName("SYSTEM"));
        System.out.println(neoUserMapper.selectByName("SYSTEM"));
    }
    @Test
    public void testSelectPosts(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost discuss : discussPosts) {
            System.out.println(discuss);
        }
        System.out.println(discussPostMapper.selectDiscussPostRows(0));
    }
}
