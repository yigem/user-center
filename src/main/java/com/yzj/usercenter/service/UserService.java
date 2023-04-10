package com.yzj.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yzj.usercenter.model.User;

import javax.servlet.http.HttpServletRequest;

/**
* @author bx
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-04-08 22:36:36
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    long userRegister(String userAccount,String userPassword,String checkPassword,String planetCode);

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @return
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    User getSafetyUser(User user);

    /**
     * 用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);
}
