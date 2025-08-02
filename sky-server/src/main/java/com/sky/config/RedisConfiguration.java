package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {

    //模版通过传入连接工厂对象
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("开始创建redis模版对象");
        RedisTemplate redisTemplate = new RedisTemplate();
        //设置redis连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置redis key 序列化器(通过字符串序列化器生成)
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //Spring操作Redis 时，默认使用JDK序列化（JdkSerializationRedisSerializer）。因为在这只为key设置了序列化器，因为value在客户端显示中是乱码的
        return redisTemplate;
    }
}
