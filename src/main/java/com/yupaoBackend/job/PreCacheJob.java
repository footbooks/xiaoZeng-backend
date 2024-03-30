package com.yupaoBackend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupaoBackend.entity.User;
import com.yupaoBackend.mapper.UserMapper;
import com.yupaoBackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热
 */
@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    //VIP客户id
    private List<Long> mainUserList = Arrays.asList(1l,2l,3l,4l,5l);
    @Resource
    private RedissonClient redissonClient;
    @Scheduled(cron = "0 0 20 * * *")
    public void doCacheRecommend(){
        RLock lock = redissonClient.getLock("yupao:preCacheJob:docache:lock");
        try {
            //等待时间设置为0，只需要一台服务器缓存预热
            if(lock.tryLock(0,30000l,TimeUnit.MILLISECONDS)){
                for(Long userId : mainUserList){
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20));
                    String redisKey = String.format("yupao:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    try{
                        valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
                    }catch (Exception e){
                        log.info("doCache in redis error",e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("preCacheJob error",e);
        }finally {
            //只有是当前线程加的锁，才会释放
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }
}
