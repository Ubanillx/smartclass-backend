package com.ubanillx.smartclass.model.dto.friendrequest;

import lombok.Data;

import java.io.Serializable;

/**
 * 好友申请更新请求
 */
@Data
public class FriendRequestUpdateRequest implements Serializable {
    
    /**
     * 主键
     */
    private Long id;
    
    /**
     * 申请状态：pending/accepted/rejected
     */
    private String status;

    private static final long serialVersionUID = 1L;
}