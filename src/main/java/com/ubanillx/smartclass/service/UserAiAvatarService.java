package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.model.entity.UserAiAvatar;

import java.math.BigDecimal;
import java.util.List;

/**
* @author liulo
* @description 针对表【user_ai_avatar(用户AI分身关联)】的数据库操作Service
* @createDate 2025-03-09 11:50:28
*/
public interface UserAiAvatarService extends BaseRelationService<UserAiAvatar> {

    /**
     * 用户收藏AI分身
     * @param userId 用户ID
     * @param aiAvatarId AI分身ID
     * @return 是否成功
     */
    boolean favoriteAiAvatar(Long userId, Long aiAvatarId);

    /**
     * 用户取消收藏AI分身
     * @param userId 用户ID
     * @param aiAvatarId AI分身ID
     * @return 是否成功
     */
    boolean unfavoriteAiAvatar(Long userId, Long aiAvatarId);

    /**
     * 用户使用AI分身
     * @param userId 用户ID
     * @param aiAvatarId AI分身ID
     * @return 是否成功
     */
    boolean useAiAvatar(Long userId, Long aiAvatarId);

    /**
     * 用户评分AI分身
     * @param userId 用户ID
     * @param aiAvatarId AI分身ID
     * @param rating 评分（1-5分）
     * @param feedback 反馈内容
     * @return 是否成功
     */
    boolean rateAiAvatar(Long userId, Long aiAvatarId, BigDecimal rating, String feedback);

    /**
     * 更新用户自定义设置
     * @param userId 用户ID
     * @param aiAvatarId AI分身ID
     * @param customSettings 自定义设置（JSON格式）
     * @return 是否成功
     */
    boolean updateCustomSettings(Long userId, Long aiAvatarId, String customSettings);

    /**
     * 获取用户收藏的AI分身列表
     * @param userId 用户ID
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页结果
     */
    Page<UserAiAvatar> listUserFavoriteAiAvatars(Long userId, int current, int size);

    /**
     * 获取用户最近使用的AI分身列表
     * @param userId 用户ID
     * @param limit 限制数量
     * @return AI分身列表
     */
    List<UserAiAvatar> listUserRecentAiAvatars(Long userId, int limit);

    /**
     * 获取用户与AI分身的关联信息
     * @param userId 用户ID
     * @param aiAvatarId AI分身ID
     * @return 关联信息
     */
    UserAiAvatar getUserAiAvatarRelation(Long userId, Long aiAvatarId);
}
