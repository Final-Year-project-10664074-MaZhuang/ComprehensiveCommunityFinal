package com.mz.community.service;

import com.mz.community.dao.neo4jMapper.TagsMapper;
import com.mz.community.entity.DiscussPost;
import com.mz.community.entity.Tags;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TagsService {
    @Autowired
    private TagsMapper tagsMapper;

    public List<Tags> findHotTags(){
        return tagsMapper.selectHotTags();
    }

    public List<DiscussPost> findPostByTag(String tagName,int offset,int limit){
        if(StringUtils.isBlank(tagName)){
            throw new IllegalArgumentException("tag name can not be null");
        }
        return tagsMapper.selectPostByTag(tagName,offset,limit);
    }

    public int findPostByTagRows(String tagName){
        return tagsMapper.selectPostByTagCount(tagName);
    }


}
