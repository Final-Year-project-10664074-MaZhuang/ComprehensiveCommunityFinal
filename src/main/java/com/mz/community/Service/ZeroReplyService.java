package com.mz.community.Service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.mz.community.dao.neo4jMapper.NeoDiscussPostMapper;
import com.mz.community.entity.DiscussPost;
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
public class ZeroReplyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZeroReplyService.class);
    @Autowired
    private NeoDiscussPostMapper neoDiscussPostMapper;
    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    //zero reply list cache
    private LoadingCache<String, List<DiscussPost>> zeroReplyListCache;

    @PostConstruct
    public void init(){
        zeroReplyListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("Param error");
                        }
                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("Param error");
                        }
                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);
                        //Can add secondary cache(redis)

                        LOGGER.debug("load post list from DB");
                        return neoDiscussPostMapper.selectZeroReply(0,offset,limit);
                    }
                });
    }

    public List<DiscussPost> findZeroReply(int userId, int offset, int limit){
        return zeroReplyListCache.get(offset + ":" + limit);
    }
}
