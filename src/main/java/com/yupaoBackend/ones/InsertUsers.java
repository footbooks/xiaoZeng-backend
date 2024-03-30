package com.yupaoBackend.ones;

import com.yupaoBackend.entity.User;
import com.yupaoBackend.mapper.UserMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

@Component
public class InsertUsers {
    @Resource
    private UserMapper userMapper;

//    @Scheduled
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for(int i=0;i<50;i++){
            User user = new User();
            user.setUserName("模拟账户");
            user.setUserAccount("123456");
            user.setAvatarUrl("https://gd-hbimg.huaban.com/d2ae598089040d14d4a81f3b6deb9ebf1863694ff994-7cMnfZ_fw658");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("12345678912");
            user.setEmail("123@qq.com");
            user.setPlanetCode("123457");
            user.setTags("[\"java\",\"大三\"]");
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
