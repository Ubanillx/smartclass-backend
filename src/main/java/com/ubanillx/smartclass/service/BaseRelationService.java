package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 通用关联表服务接口
 * 用于替代那些没有特定业务逻辑的关联表服务接口
 * @param <T> 实体类型
 */
public interface BaseRelationService<T> extends IService<T> {
    
    /**
     * 根据关联ID查询记录
     * @param relationId 关联ID
     * @param userId 用户ID
     * @return 是否存在
     */
    default boolean existsByRelationIdAndUserId(Long relationId, Long userId) {
        return false;
    }
    
    /**
     * 创建关联记录
     * @param relationId 关联ID
     * @param userId 用户ID
     * @return 是否成功
     */
    default boolean createRelation(Long relationId, Long userId) {
        return false;
    }
    
    /**
     * 删除关联记录
     * @param relationId 关联ID
     * @param userId 用户ID
     * @return 是否成功
     */
    default boolean removeRelation(Long relationId, Long userId) {
        return false;
    }
} 