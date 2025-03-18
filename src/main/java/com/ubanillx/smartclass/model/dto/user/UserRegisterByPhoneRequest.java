package com.ubanillx.smartclass.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
*/
@Data
public class UserRegisterByPhoneRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userPhone;

    private String userPassword;

    private String checkPassword;
}
