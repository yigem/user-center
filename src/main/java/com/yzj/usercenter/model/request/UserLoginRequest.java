package com.yzj.usercenter.model.request;


import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 * Serializable 序列化
 * 如果要序列一个java对象的话，只有继承了Serializable类，在序列化的时候才不会冲突
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 6352319971748035909L;

    private String userAccount;
    private String userPassword;
}

