package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.service.BaseRelationService;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * 通用关联表服务实现类
 * 用于替代那些没有特定业务逻辑的关联表服务实现类
 * @param <M> Mapper类型
 * @param <T> 实体类型
 */
public abstract class BaseRelationServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements BaseRelationService<T> {
    
    /**
     * 用户ID字段名
     */
    protected String userIdFieldName = "userId";
    
    /**
     * 关联ID字段名
     */
    protected String relationIdFieldName;
    
    /**
     * 创建时间字段名
     */
    protected String createTimeFieldName = "createTime";
    
    /**
     * 更新时间字段名
     */
    protected String updateTimeFieldName = "updateTime";
    
    /**
     * 设置关联ID字段名
     * @param relationIdFieldName 关联ID字段名
     */
    protected void setRelationIdFieldName(String relationIdFieldName) {
        this.relationIdFieldName = relationIdFieldName;
    }
    
    @Override
    public boolean existsByRelationIdAndUserId(Long relationId, Long userId) {
        if (relationId == null || userId == null || relationIdFieldName == null) {
            return false;
        }
        
        try {
            LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>();
            
            // 使用反射获取字段
            Field userIdField = getEntityClass().getDeclaredField(userIdFieldName);
            Field relationIdField = getEntityClass().getDeclaredField(relationIdFieldName);
            
            userIdField.setAccessible(true);
            relationIdField.setAccessible(true);
            
            // 创建一个新的实体实例
            T entity = getEntityClass().newInstance();
            
            // 设置字段值
            userIdField.set(entity, userId);
            relationIdField.set(entity, relationId);
            
            // 构建查询条件
            queryWrapper.setEntity(entity);
            
            return count(queryWrapper) > 0;
        } catch (Exception e) {
            log.error("查询关联记录失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRelation(Long relationId, Long userId) {
        if (relationId == null || userId == null || relationIdFieldName == null) {
            return false;
        }
        
        try {
            // 检查是否已存在
            if (existsByRelationIdAndUserId(relationId, userId)) {
                return true;
            }
            
            // 创建一个新的实体实例
            T entity = getEntityClass().newInstance();
            
            // 使用反射设置字段值
            Field userIdField = getEntityClass().getDeclaredField(userIdFieldName);
            Field relationIdField = getEntityClass().getDeclaredField(relationIdFieldName);
            
            userIdField.setAccessible(true);
            relationIdField.setAccessible(true);
            
            userIdField.set(entity, userId);
            relationIdField.set(entity, relationId);
            
            // 设置创建时间和更新时间
            try {
                Field createTimeField = getEntityClass().getDeclaredField(createTimeFieldName);
                Field updateTimeField = getEntityClass().getDeclaredField(updateTimeFieldName);
                
                createTimeField.setAccessible(true);
                updateTimeField.setAccessible(true);
                
                Date now = new Date();
                createTimeField.set(entity, now);
                updateTimeField.set(entity, now);
            } catch (NoSuchFieldException e) {
                // 忽略，可能没有这些字段
            }
            
            return save(entity);
        } catch (Exception e) {
            log.error("创建关联记录失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeRelation(Long relationId, Long userId) {
        if (relationId == null || userId == null || relationIdFieldName == null) {
            return false;
        }
        
        try {
            LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>();
            
            // 使用反射获取字段
            Field userIdField = getEntityClass().getDeclaredField(userIdFieldName);
            Field relationIdField = getEntityClass().getDeclaredField(relationIdFieldName);
            
            userIdField.setAccessible(true);
            relationIdField.setAccessible(true);
            
            // 创建一个新的实体实例
            T entity = getEntityClass().newInstance();
            
            // 设置字段值
            userIdField.set(entity, userId);
            relationIdField.set(entity, relationId);
            
            // 构建查询条件
            queryWrapper.setEntity(entity);
            
            return remove(queryWrapper);
        } catch (Exception e) {
            log.error("删除关联记录失败", e);
            return false;
        }
    }
} 