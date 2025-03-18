package com.ubanillx.smartclass.model.dto.user;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 用户更新请求
*/
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 手机号
     */
    private String userPhone;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户微信号
     */
    private String wechatId;

    /**
     * 用户所在省份
     */
    private String province;

    /**
     * 用户所在城市
     */
    private String city;

    /**
     * 用户所在区县
     */
    private String district;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 用户角色：student/teacher
     */
    private String userRole;

    /**
     * 用户生日
     */
    private Date birthday;

    /**
     * 用户性别：0-男 1-女 2-保密
     */
    private int userGender;

    /**
     * 用户角色：0-管理员 1-student 2-teacher
     */
    private int role;

    private static final long serialVersionUID = 1L;
}