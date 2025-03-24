package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.model.entity.UserAiAvatar;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.vo.UserAiAvatarVO;
import java.math.BigDecimal;
import java.util.List;

/**
* @author liulo
* @description 针对表【user_ai_avatar(用户AI分身关联)】的数据库操作Service
* @createDate 2025-03-24 21:35:44
*/
public interface UserAiAvatarService extends IService<UserAiAvatar> {

    /**
     * 收藏/取消收藏AI分身
     * @param userId 用户id
     * @param aiAvatarId AI分身id
     * @param isFavorite 是否收藏：0-否，1-是
     * @return 是否成功
     */
    boolean favoriteAiAvatar(Long userId, Long aiAvatarId, Integer isFavorite);
    
    /**
     * 用户使用AI分身，记录使用次数
     * @param userId 用户id
     * @param aiAvatarId AI分身id
     * @return 是否成功
     */
    boolean useAiAvatar(Long userId, Long aiAvatarId);
    
    /**
     * 用户对AI分身评分和反馈
     * @param userId 用户id
     * @param aiAvatarId AI分身id
     * @param rating 评分
     * @param feedback 反馈内容
     * @return 是否成功
     */
    boolean rateAiAvatar(Long userId, Long aiAvatarId, BigDecimal rating, String feedback);
    
    /**
     * 获取用户的AI分身列表（带分页）
     * @param userId 用户id
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    Page<UserAiAvatarVO> getUserAiAvatarPage(Long userId, long current, long size);
    
    /**
     * 获取用户收藏的AI分身列表
     * @param userId 用户id
     * @return 收藏列表
     */
    List<UserAiAvatarVO> getUserFavoriteAiAvatars(Long userId);
}
