package com.ubanillx.smartclass.model.dto.useraiavatar;

import com.ubanillx.smartclass.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户AI分身关联查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserAiAvatarQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * AI分身id
     */
    private Long aiAvatarId;

    /**
     * 是否收藏：0-否，1-是
     */
    private Integer isFavorite;

    /**
     * 用户评分，1-5分
     */
    private BigDecimal userRating;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
} 