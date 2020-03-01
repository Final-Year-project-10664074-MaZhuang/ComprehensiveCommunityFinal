package com.mz.finalcommunity.finalcommunity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        //Serialization method for setting key
        template.setKeySerializer(RedisSerializer.string());
        //Serialization method for setting values
        template.setValueSerializer(RedisSerializer.json());
        //Set the serialization method of the hash key
        template.setHashKeySerializer(RedisSerializer.string());
        //Set the serialization method of the hash value
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();
        return template;
    }
}
