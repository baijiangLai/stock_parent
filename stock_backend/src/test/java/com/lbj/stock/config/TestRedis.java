package com.lbj.stock.config;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class TestRedis {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private Cache<String,Object> caffeineCache;

    @Test
    public void test01(){
//        //存入值
//        redisTemplate.opsForValue().set("myname","zhangsan");
//        //获取值
//        String myname = redisTemplate.opsForValue().get("myname");
//        System.out.println(myname);
        Object innerMarketKey = caffeineCache.getIfPresent("innerMarketKey");
        System.out.println(innerMarketKey);
    }
}
