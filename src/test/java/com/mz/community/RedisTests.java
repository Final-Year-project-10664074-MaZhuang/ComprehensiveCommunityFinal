package com.mz.community;

import com.mz.community.util.RedisKeyUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes =CommunityApplication.class)
public class RedisTests {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        String redisKey = "test:count";

        redisTemplate.opsForValue().set(redisKey,1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testSet(){
        String crawlerKey = RedisKeyUtil.getCrawlerKey();
        Long add2 = redisTemplate.opsForSet().add(crawlerKey, "123");
        Long add1 = redisTemplate.opsForSet().add(crawlerKey, "123");
        Long add = redisTemplate.opsForSet().add(crawlerKey, "789");
        if(add!=0){
            System.out.println(redisTemplate.opsForSet().members(crawlerKey));
        }

    }
}
