package com.yzj.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzj.usercenter.common.ErrorCode;
import com.yzj.usercenter.exception.BusinessException;
import com.yzj.usercenter.mapper.UserMapper;
import com.yzj.usercenter.model.User;
import com.yzj.usercenter.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yzj.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
* @author bx
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-04-08 22:36:36
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，用户混淆密码
     */
    private static final String SALT = "YZJ";

    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String planetCode) {
        //1.非空
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        //2.账号不小于4位
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号不小于4位");
        }
        //3.密码不小于8位
        if(userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码不小于8位");
        }
        //4.星球编号大于2位
        if(planetCode.length() < 2){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"编号需大于两位");
        }
        //5.账户不包含特殊字符 java的正则表达式对账号特殊字符的校验
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不能包含特殊字符");
        }
        //6.密码和校验密码相同
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码和校验密码不同");
        }
        //7.账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
        }
        //8.星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode",planetCode);
        count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"编号已存在");
        }
        //8.密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //9.向数据库中插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"注册时向数据库中插入数据失败");
        }
        return user.getId();
    }

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @return
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.非空
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR,"输入数据为空");
        }
        //2.账号长度不小于4位
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度不小于4位");
        }
        //3.密码长度不小于8位
        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度不小于8位");
        }
        //4.账户不包含特殊字符 java的正则表达式对账号特殊字符的校验
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不包含特殊字符");
        }
        //5.校验密码是否输入正确，对输入密码进行加密，和数据库中的密文密码去进行对比
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //6.和数据库中的账户和密码进行对比
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号密码不正确");
        }
        //7.用户脱敏
        User safetyUser = getSafetyUser(user);
        //8.记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser){
        if(originUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"输入数据为空");
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    /**
     * 用户注销
     * @param request
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        //移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




