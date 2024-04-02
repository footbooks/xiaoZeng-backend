package com.yupaoBackend.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yupaoBackend.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球编号
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    // [加入编程导航](https://t.zsxq.com/0emozsIJh) 深耕编程提升【两年半】、国内净值【最高】的编程社群、用心服务【20000+】求学者、帮你自学编程【不走弯路】

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     * @param
     * @return
     */
    List<User> searchUsersByTags(List<String> tagList);
    /**
     * 修改用户信息
     */
    int updateUser(User user,User loginUser);
    /**
     * 获取当前登录用户信息
     */
    User getLoginUser(HttpServletRequest request);
    //判断当前用户权限
    Boolean isAdmin(HttpServletRequest request);
    //方法重构（判断用户权限）
    Boolean isAdmin(User loginUser);
    //心动模式（根据编辑距离算法给用户推荐匹配度高的用户）
    List<User> matchUsers(long num, User user);
}
