package com.ubanillx.smartclass.model.dto.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ubanillx.smartclass.common.PageRequest;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询请求
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;


    /**
     * 用户性别 0-男 1-女 2-保密
     */
    private Integer userGender;

    /**
     * 手机号
     */
    private String userPhone;

    /**
     * 用户昵称
     */
    private String userName;


    /**
     * 用户角色：student/teacher/admin/ban
     */
    private String userRole;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户微信号
     */
    private String wechatId;

    /**
     * 用户生日
     */
    private int birthdayYear;


    private static final long serialVersionUID = 1L;
}