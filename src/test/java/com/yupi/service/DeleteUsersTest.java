package com.yupi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yupaoBackend.YupaoApplication;
import com.yupaoBackend.entity.User;
import com.yupaoBackend.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@SpringBootTest(classes = YupaoApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class DeleteUsersTest {
    @Resource
    private UserMapper userMapper;
    @Test
    public void deleteUsers(){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.gt(User::getId,10000);
        userMapper.delete(queryWrapper);
    }
}
