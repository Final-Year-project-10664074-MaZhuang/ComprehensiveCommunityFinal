package com.mz.community;

import com.mz.community.dao.mysqlMapper.DiscussPostMapper;
import com.mz.community.dao.mysqlMapper.UserMapper;
import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.dao.neo4jMapper.NeoUserMapper;
import com.mz.community.entity.DiscussPost;
import com.mz.community.entity.Tags;
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
    @Autowired
    private NeoDiscussPostMapper neoDiscussPostMapper;
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

    @Test
    public void testRelationInsert(){
        String[] tag = {"JAVA","C++","C#"};
        neoDiscussPostMapper.insertRelationDiscussPost(179,300312,tag);
    }

    @Test
    public void testStringArray(){
        /*Tags tags = new Tags();
        String s = "SpringBoot";
        String[] tagsArray = s.split(",");
        for (String aTag : tagsArray) {
            System.out.println("aTag: "+aTag);
            Tags tag = neoDiscussPostMapper.selectTagByTagName(aTag);
            if(tag==null){
                tags.setTagName(aTag);
                neoDiscussPostMapper.insertTags(tags);
                System.out.println("new tag!!!!!");
            }
        }*/
        List<Tags> tags = neoDiscussPostMapper.selectTags();
        for (Tags tag : tags) {
            System.out.println(tag.getTagName());
        }
    }
    @Test
    public void testFindPostOfTag(){
        List<Tags> tags = neoDiscussPostMapper.selectTagsByDiscussPostId(300316);
        System.out.println(tags.size());
        for (Tags tag : tags) {
            System.out.println(tag.getTagName());
        }
    }
}
