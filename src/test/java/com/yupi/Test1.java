package com.yupi;

import org.springframework.util.DigestUtils;

public class Test1 {
    public static void main(String[] args) {
       String password = DigestUtils.md5DigestAsHex(("zj666"+"12345678").getBytes());
        System.out.println(password);
    }
}
