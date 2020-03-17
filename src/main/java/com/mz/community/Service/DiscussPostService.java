package com.mz.community.Service;

import com.mz.community.dao.mysqlMapper.DiscussPostMapper;
import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.entity.DiscussPost;
import com.mz.community.entity.Tags;
import com.mz.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private NeoDiscussPostMapper neoDiscussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit){
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }
    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost post, Tags tags){
        if(post==null){
            throw new IllegalArgumentException("Post param can not be null");
        }
        if(tags==null){
            throw new IllegalArgumentException("Tags param can not be null");
        }

        String[] tagsArray = tags.getTagName().split(",");
        for (String aTag : tagsArray) {
            Tags tag = neoDiscussPostMapper.selectTagByTagName(aTag);
            if(tag==null){
                tags.setTagName(aTag);
                neoDiscussPostMapper.insertTags(tags);
            }
        }
        //transfer html
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //filter sensitive word
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        discussPostMapper.insertDiscussPost(post);
        neoDiscussPostMapper.insertDiscussPost(post);
        return neoDiscussPostMapper.insertRelationDiscussPost(post.getUserId(),post.getId(),tagsArray);
    }

    public List<Tags> findAllTags(){
        return neoDiscussPostMapper.selectTags();
    }

    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }
}
