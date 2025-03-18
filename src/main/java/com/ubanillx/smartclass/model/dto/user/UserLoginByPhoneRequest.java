package com.ubanillx.smartclass.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
*/
@Data
public class UserLoginByPhoneRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userPhone;

    private String userPassword;
}
