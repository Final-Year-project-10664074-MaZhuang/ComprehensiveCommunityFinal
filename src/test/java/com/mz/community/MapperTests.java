package com.mz.community;

import com.mz.community.dao.mysqlMapper.DiscussPostMapper;
import com.mz.community.dao.mysqlMapper.UserMapper;
import com.mz.community.dao.neo4jMapper.NeoCrawlerDiscussPostMapper;
import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.dao.neo4jMapper.NeoUserMapper;
import com.mz.community.dao.neo4jMapper.TagsMapper;
import com.mz.community.entity.Category;
import com.mz.community.entity.DiscussPost;
import com.mz.community.entity.Tags;
import com.mz.community.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
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
    @Autowired
    private NeoCrawlerDiscussPostMapper neoCrawlerDiscussPostMapper;
    @Autowired
    private TagsMapper tagsMapper;
    @Test
    public void testCrud(){
        System.out.println(userMapper.selectByName("Genshushu"));
        //System.out.println(neoUserMapper.selectByName("SYSTEM"));
    }
    @Test
    public void testSelectPosts(){
        //List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10,0);
        List<DiscussPost> discussPosts = neoDiscussPostMapper.selectZeroReply(0, 0, 10);
        for (DiscussPost discuss : discussPosts) {
            System.out.println(discuss);
        }
        System.out.println(discussPostMapper.selectDiscussPostRows(0));
    }

    @Test
    public void testInsertToMysql(){
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(138);
        discussPost.setTitle("Test url");
        discussPost.setContent("Test Url");
        discussPost.setType(0);
        discussPost.setStatus(0);
        discussPost.setCreateTime(new Date());
        discussPost.setCommentCount(0);
        discussPost.setScore(0);
        discussPostMapper.insertDiscussPost(discussPost);
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

    @Test
    public void insertTags(){
        String[] name={"xxx","yyy"};
        for (int i = 0; i < name.length; i++) {
            neoDiscussPostMapper.insertTags(name[i]);
        }
    }

    @Test
    public void testSelectRelatedTags(){
        String tagName = ".*tomcat.*";
        List<Tags> tagsList = tagsMapper.selectRelatedTags(tagName);
        for (Tags tags : tagsList) {
            System.out.println(tags.getTagName());
        }
    }

    @Test
    public void testSelectVisitSecond(){
        double v=0.0;
        try {
           v = neoDiscussPostMapper.selectVisitSecondByUserId(18, 300335);
            System.out.println(v);
        }catch (Exception e){
            System.out.println(v);
        }
    }

    @Test
    public void testUpdateVisitSecond(){
        neoDiscussPostMapper.updateVisitSecond(300335,182,22.0);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("TestAccount2");
        user.setPassword("25ac0a2e8bd0f28928de3c56149283d6");
        user.setSalt("49f10");
        user.setEmail("test2@qq.com");
        user.setType(0);
        user.setStatus(1);
        user.setHeaderUrl("http://images.nowcoder.com/head/22t.png");
        user.setCreateTime(new Date());

        userMapper.insertUser(user);
        neoUserMapper.insertUser(user);
    }

    @Test
    public void testRelationZeroReply(){
        List<DiscussPost> list = neoDiscussPostMapper.selectRelationZeroReply(4, 1, 3);
        for (DiscussPost discussPost : list) {
            if(StringUtils.isNotBlank(discussPost.getTitle())){
                System.out.println(discussPost.getId());
            }
        }
    }

    @Test
    public void testRecommendPost(){
        String[] tags = {"jdk"};
        List<User> users = neoUserMapper.selectRecommendPostByTags(tags,4);
        for (User user : users) {
            System.out.println(user.getId()+":"+user.getUsername()+":"+user.getEmail());
        }
    }

    @Test
    public void testSelectUser(){
        User user = neoUserMapper.selectById(5);
        System.out.println(user.getId());
        System.out.println(user.getUsername());
        System.out.println(user.getHeaderUrl());
    }

    @Test
    public void testSelectAllCategory(){
        List<Category> categoryList = neoDiscussPostMapper.selectAllCategory();
        for (Category category : categoryList) {
            System.out.println(category.getName());
        }
    }

    @Test
    public void testSelectAllTagsByCategory(){
        List<Tags> tagsList = neoDiscussPostMapper.selectAllTagsByCategory("java");
        for (Tags tags : tagsList) {
            System.out.println(tags.getTagName());
        }
    }

    @Test
    public void testInsertCategory(){
        int html = tagsMapper.insertCategory("html");
        System.out.println(html);
    }

    @Test
    public void testInsertCategoryAndTags(){
        String[] tags={"fragmentShader","vertexShader"};
        List<Tags> tagsList = new ArrayList<>();
        for (String tag : tags) {
            Tags tags1 = new Tags();
            tags1.setTagName(tag);
            tagsList.add(tags1);
        }
        int opengl = neoCrawlerDiscussPostMapper.insertCrawlerTags(tagsList, "opengl");
        System.out.println(opengl);
    }

    @Test
    public void testInsertCrawlerPost(){
        List<DiscussPost> discussPostList = new ArrayList<>();
        String[] tags = {"java","c++"};
        for (int i = 0; i < 500; i++) {
            DiscussPost discussPost = new DiscussPost();
            discussPost.setId(1000+i);
            discussPost.setUserId(3);
            discussPost.setType(4);
            discussPost.setStatus(0);
            discussPost.setLinkUrl("href");
            discussPost.setTitle("test insert");
            discussPost.setContent("test insert");
            discussPost.setScore(1.2);
            discussPost.setCommentCount(10);
            discussPost.setCreateTime(new Date());
            discussPost.setTagName(tags);
            discussPostList.add(discussPost);
        }
        int i = neoCrawlerDiscussPostMapper.insertCrawler(discussPostList);
        System.out.println(i);
    }
}
