package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.UserAiAvatarMapper;
import com.ubanillx.smartclass.model.entity.UserAiAvatar;
import com.ubanillx.smartclass.service.AiAvatarService;
import com.ubanillx.smartclass.service.UserAiAvatarService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
* @author liulo
* @description 针对表【user_ai_avatar(用户AI分身关联)】的数据库操作Service实现
* @createDate 2025-03-09 11:50:27
*/
@Service
public class UserAiAvatarServiceImpl extends BaseRelationServiceImpl<UserAiAvatarMapper, UserAiAvatar>
    implements UserAiAvatarService {
    
    @Resource
    private AiAvatarService aiAvatarService;
    
    public UserAiAvatarServiceImpl() {
        // 设置关联ID字段名
        setRelationIdFieldName("aiAvatarId");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean favoriteAiAvatar(Long userId, Long aiAvatarId) {
        // 参数校验
        if (userId == null || aiAvatarId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询AI分身是否存在
        aiAvatarService.getAiAvatarById(aiAvatarId);
        
        // 查询关联记录是否存在
        UserAiAvatar userAiAvatar = getUserAiAvatarRelation(userId, aiAvatarId);
        
        if (userAiAvatar == null) {
            // 不存在则创建新记录
            userAiAvatar = new UserAiAvatar();
            userAiAvatar.setUserId(userId);
            userAiAvatar.setAiAvatarId(aiAvatarId);
            userAiAvatar.setIsFavorite(1);
            userAiAvatar.setUseCount(0);
            userAiAvatar.setCreateTime(new Date());
            userAiAvatar.setUpdateTime(new Date());
            return save(userAiAvatar);
        } else {
            // 存在则更新收藏状态
            LambdaUpdateWrapper<UserAiAvatar> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserAiAvatar::getUserId, userId)
                    .eq(UserAiAvatar::getAiAvatarId, aiAvatarId)
                    .set(UserAiAvatar::getIsFavorite, 1)
                    .set(UserAiAvatar::getUpdateTime, new Date());
            return update(updateWrapper);
        }
    }

    @Override
    public boolean unfavoriteAiAvatar(Long userId, Long aiAvatarId) {
        // 参数校验
        if (userId == null || aiAvatarId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询关联记录是否存在
        UserAiAvatar userAiAvatar = getUserAiAvatarRelation(userId, aiAvatarId);
        if (userAiAvatar == null) {
            return true; // 不存在关联记录，视为取消成功
        }
        
        // 更新收藏状态
        LambdaUpdateWrapper<UserAiAvatar> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserAiAvatar::getUserId, userId)
                .eq(UserAiAvatar::getAiAvatarId, aiAvatarId)
                .set(UserAiAvatar::getIsFavorite, 0)
                .set(UserAiAvatar::getUpdateTime, new Date());
        return update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean useAiAvatar(Long userId, Long aiAvatarId) {
        // 参数校验
        if (userId == null || aiAvatarId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询AI分身是否存在
        aiAvatarService.getAiAvatarById(aiAvatarId);
        
        // 更新AI分身使用次数
        aiAvatarService.updateAiAvatarUsageCount(aiAvatarId);
        
        // 查询关联记录是否存在
        UserAiAvatar userAiAvatar = getUserAiAvatarRelation(userId, aiAvatarId);
        
        if (userAiAvatar == null) {
            // 不存在则创建新记录
            userAiAvatar = new UserAiAvatar();
            userAiAvatar.setUserId(userId);
            userAiAvatar.setAiAvatarId(aiAvatarId);
            userAiAvatar.setIsFavorite(0);
            userAiAvatar.setUseCount(1);
            userAiAvatar.setLastUseTime(new Date());
            userAiAvatar.setCreateTime(new Date());
            userAiAvatar.setUpdateTime(new Date());
            return save(userAiAvatar);
        } else {
            // 存在则更新使用次数和最后使用时间
            return baseMapper.incrUseCount(userId, aiAvatarId) > 0;
        }
    }

    @Override
    public boolean rateAiAvatar(Long userId, Long aiAvatarId, BigDecimal rating, String feedback) {
        // 参数校验
        if (userId == null || aiAvatarId == null || rating == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 评分范围校验
        if (rating.compareTo(BigDecimal.ONE) < 0 || rating.compareTo(new BigDecimal("5")) > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评分必须在1-5分之间");
        }
        
        // 查询关联记录是否存在
        UserAiAvatar userAiAvatar = getUserAiAvatarRelation(userId, aiAvatarId);
        if (userAiAvatar == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "您尚未使用过该AI分身");
        }
        
        // 更新评分和反馈
        LambdaUpdateWrapper<UserAiAvatar> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserAiAvatar::getUserId, userId)
                .eq(UserAiAvatar::getAiAvatarId, aiAvatarId)
                .set(UserAiAvatar::getUserRating, rating)
                .set(UserAiAvatar::getUserFeedback, feedback)
                .set(UserAiAvatar::getUpdateTime, new Date());
        return update(updateWrapper);
    }

    @Override
    public boolean updateCustomSettings(Long userId, Long aiAvatarId, String customSettings) {
        // 参数校验
        if (userId == null || aiAvatarId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询关联记录是否存在
        UserAiAvatar userAiAvatar = getUserAiAvatarRelation(userId, aiAvatarId);
        if (userAiAvatar == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "您尚未使用过该AI分身");
        }
        
        // 更新自定义设置
        LambdaUpdateWrapper<UserAiAvatar> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserAiAvatar::getUserId, userId)
                .eq(UserAiAvatar::getAiAvatarId, aiAvatarId)
                .set(UserAiAvatar::getCustomSettings, customSettings)
                .set(UserAiAvatar::getUpdateTime, new Date());
        return update(updateWrapper);
    }

    @Override
    public Page<UserAiAvatar> listUserFavoriteAiAvatars(Long userId, int current, int size) {
        // 参数校验
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 创建查询条件
        LambdaQueryWrapper<UserAiAvatar> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAiAvatar::getUserId, userId)
                .eq(UserAiAvatar::getIsFavorite, 1)
                .orderByDesc(UserAiAvatar::getUpdateTime);
        
        // 分页查询
        Page<UserAiAvatar> page = new Page<>(current, size);
        return page(page, queryWrapper);
    }

    @Override
    public List<UserAiAvatar> listUserRecentAiAvatars(Long userId, int limit) {
        // 参数校验
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 创建查询条件
        QueryWrapper<UserAiAvatar> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserAiAvatar::getUserId, userId)
                .orderByDesc(UserAiAvatar::getLastUseTime);
        
        if (limit > 0) {
            queryWrapper.last("limit " + limit);
        }
        
        return list(queryWrapper);
    }

    @Override
    public UserAiAvatar getUserAiAvatarRelation(Long userId, Long aiAvatarId) {
        // 参数校验
        if (userId == null || aiAvatarId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 创建查询条件
        LambdaQueryWrapper<UserAiAvatar> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAiAvatar::getUserId, userId)
                .eq(UserAiAvatar::getAiAvatarId, aiAvatarId);
        
        return getOne(queryWrapper);
    }
}




