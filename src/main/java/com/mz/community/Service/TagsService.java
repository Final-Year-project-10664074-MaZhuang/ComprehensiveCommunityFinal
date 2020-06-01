package com.mz.community.Service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.mz.community.dao.neo4jMapper.TagsMapper;
import com.mz.community.entity.DiscussPost;
import com.mz.community.entity.Tags;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TagsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagsService.class);
    @Autowired
    private TagsMapper tagsMapper;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    //hot tags cache
    private LoadingCache<String, List<Tags>> hotTagsCache;

    @PostConstruct
    public void init(){
        hotTagsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<Tags>>() {
                    @Nullable
                    @Override
                    public List<Tags> load(@NonNull String s) throws Exception {
                        LOGGER.debug("hot tags list from DB");
                        return tagsMapper.selectHotTags();
                    }
                });
    }

    public List<Tags> findHotTags(){
        return hotTagsCache.get("1");
        //return tagsMapper.selectHotTags();
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


    public List<DiscussPost> findZeroPostByTag(String tagName, int offset, int limit) {
        return tagsMapper.selectZeroPostByTag(tagName,offset,limit);
    }
}
