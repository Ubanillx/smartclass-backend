package com.ubanillx.smartclass.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户视图（脱敏）
*/
@Data
public class UserVO implements Serializable {

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
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：student/admin/teacher/ban
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 用户所在省份
     */
    private String province;

    /**
     * 用户所在城市
     */
    private String city;


    /**
     * 用户生日
     */
    private int birthdayYear;

    private static final long serialVersionUID = 1L;

}