package com.yupi.service;
import java.time.LocalDateTime;

import com.yupaoBackend.YupaoApplication;
import com.yupaoBackend.entity.User;
import com.yupaoBackend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest(classes = YupaoApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class InsertUsersTest {

    @Resource
    private UserService userService;

    private ExecutorService executorService = new ThreadPoolExecutor(40, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    /**
     * 批量插入用户
     */
    @Test
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUserName("模拟用户2");
            user.setUserAccount("zxcvbd");
            user.setAvatarUrl("https://cn.bing.com/images/search?view=detailV2&ccid=HNVqqaye&id=9BB5C61E7DC36E8F6310EBF01A269FC0F982B6AF&thid=OIP.HNVqqayem7nqKVCUuXygBgHaHa&mediaurl=https%3a%2f%2fcbu01.alicdn.com%2fimg%2fibank%2fO1CN016CtFOz1IMcvdJr84O_!!2214758190879-0-cib.jpg&exph=1200&expw=1200&q=%e5%9d%a4%e5%9d%a4%e5%a4%b4%e5%83%8f&simid=607987878841896428&FORM=IRPRST&ck=171C30985B076261E88B07EECB463F1F&selectedIndex=15&itb=0");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("123@qq.com");
            user.setTags("[]");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("1111111");
            userList.add(user);
        }
        // 20 秒 10 万条
        userService.saveBatch(userList, 100);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    /**
     * 并发批量插入用户
     */
    @Test
    public void doConcurrencyInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 分十组
        int batchSize = 5000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            List<User> userList = new ArrayList<>();
            while (true) {
                j++;
                User user = new User();
                user.setUserName("假用户");
                user.setUserAccount("qwert");
                user.setAvatarUrl("https://cn.bing.com/images/search?view=detailV2&ccid=HNVqqaye&id=9BB5C61E7DC36E8F6310EBF01A269FC0F982B6AF&thid=OIP.HNVqqayem7nqKVCUuXygBgHaHa&mediaurl=https%3a%2f%2fcbu01.alicdn.com%2fimg%2fibank%2fO1CN016CtFOz1IMcvdJr84O_!!2214758190879-0-cib.jpg&exph=1200&expw=1200&q=%e5%9d%a4%e5%9d%a4%e5%a4%b4%e5%83%8f&simid=607987878841896428&FORM=IRPRST&ck=171C30985B076261E88B07EECB463F1F&selectedIndex=15&itb=0");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("123");
                user.setEmail("123@qq.com");
                user.setTags("[]");
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setPlanetCode("11111111");
                userList.add(user);
                if (j % batchSize == 0) {
                    break;
                }
            }
            // 异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName: " + Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            }, executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        // 20 秒 10 万条
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
