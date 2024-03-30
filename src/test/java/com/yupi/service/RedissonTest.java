package com.yupi.service;

import com.yupaoBackend.YupaoApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@SpringBootTest(classes = YupaoApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;
    @Test
    public void test(){
        RMap<String, Integer> testMap = redissonClient.getMap("testMap");
        testMap.put("aaa",123);
        System.out.println(testMap.get("aaa"));
    }
}
