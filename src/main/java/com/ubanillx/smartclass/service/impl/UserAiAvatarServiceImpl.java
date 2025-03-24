package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.mapper.AiAvatarMapper;
import com.ubanillx.smartclass.mapper.UserMapper;
import com.ubanillx.smartclass.model.entity.AiAvatar;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserAiAvatar;
import com.ubanillx.smartclass.mapper.UserAiAvatarMapper;
import com.ubanillx.smartclass.model.vo.UserAiAvatarVO;
import com.ubanillx.smartclass.service.AiAvatarService;
import com.ubanillx.smartclass.service.UserAiAvatarService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liulo
 * @description 针对表【user_ai_avatar(用户AI分身关联)】的数据库操作Service实现
 * @createDate 2025-03-24 21:35:44
 */
@Service
public class UserAiAvatarServiceImpl extends ServiceImpl<UserAiAvatarMapper, UserAiAvatar>
    implements UserAiAvatarService {
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private AiAvatarMapper aiAvatarMapper;
    
    @Resource
    private AiAvatarService aiAvatarService;
    
    @Override
    @Transactional
    public boolean favoriteAiAvatar(Long userId, Long aiAvatarId, Integer isFavorite) {
        // 查询是否已存在记录
        QueryWrapper<UserAiAvatar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId).eq("aiAvatarId", aiAvatarId);
        UserAiAvatar userAiAvatar = this.getOne(queryWrapper);
        
        if (userAiAvatar != null) {
            // 已存在记录，更新收藏状态
            userAiAvatar.setIsFavorite(isFavorite);
            return this.updateById(userAiAvatar);
        } else {
            // 不存在记录，新增
            userAiAvatar = new UserAiAvatar();
            userAiAvatar.setUserId(userId);
            userAiAvatar.setAiAvatarId(aiAvatarId);
            userAiAvatar.setIsFavorite(isFavorite);
            userAiAvatar.setUseCount(0);
            userAiAvatar.setCreateTime(new Date());
            userAiAvatar.setUpdateTime(new Date());
            return this.save(userAiAvatar);
        }
    }
    
    @Override
    @Transactional
    public boolean useAiAvatar(Long userId, Long aiAvatarId) {
        // 查询是否已存在记录
        QueryWrapper<UserAiAvatar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId).eq("aiAvatarId", aiAvatarId);
        UserAiAvatar userAiAvatar = this.getOne(queryWrapper);
        
        if (userAiAvatar != null) {
            // 已存在记录，更新使用次数和最后使用时间
            userAiAvatar.setUseCount(userAiAvatar.getUseCount() + 1);
            userAiAvatar.setLastUseTime(new Date());
            userAiAvatar.setUpdateTime(new Date());
            return this.updateById(userAiAvatar);
        } else {
            // 不存在记录，新增
            userAiAvatar = new UserAiAvatar();
            userAiAvatar.setUserId(userId);
            userAiAvatar.setAiAvatarId(aiAvatarId);
            userAiAvatar.setIsFavorite(0);
            userAiAvatar.setUseCount(1);
            userAiAvatar.setLastUseTime(new Date());
            userAiAvatar.setCreateTime(new Date());
            userAiAvatar.setUpdateTime(new Date());
            boolean saveResult = this.save(userAiAvatar);
            
            // 更新AI分身的总使用次数
            AiAvatar aiAvatar = aiAvatarMapper.selectById(aiAvatarId);
            if (aiAvatar != null) {
                aiAvatar.setUsageCount(aiAvatar.getUsageCount() + 1);
                aiAvatarMapper.updateById(aiAvatar);
            }
            
            return saveResult;
        }
    }
    
    @Override
    @Transactional
    public boolean rateAiAvatar(Long userId, Long aiAvatarId, BigDecimal rating, String feedback) {
        // 查询是否已存在记录
        QueryWrapper<UserAiAvatar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId).eq("aiAvatarId", aiAvatarId);
        UserAiAvatar userAiAvatar = this.getOne(queryWrapper);
        
        if (userAiAvatar != null) {
            // 记录用户的评分和反馈
            userAiAvatar.setUserRating(rating);
            userAiAvatar.setUserFeedback(feedback);
            userAiAvatar.setUpdateTime(new Date());
            boolean updateResult = this.updateById(userAiAvatar);
            
            // 更新AI分身的总评分
            AiAvatar aiAvatar = aiAvatarMapper.selectById(aiAvatarId);
            if (aiAvatar != null) {
                // 如果用户之前没有评分，则评分人数+1
                if (userAiAvatar.getUserRating() == null) {
                    aiAvatar.setRatingCount(aiAvatar.getRatingCount() + 1);
                }
                
                // 重新计算平均评分
                QueryWrapper<UserAiAvatar> ratingQuery = new QueryWrapper<>();
                ratingQuery.eq("aiAvatarId", aiAvatarId).isNotNull("userRating");
                List<UserAiAvatar> ratedRecords = this.list(ratingQuery);
                
                if (!ratedRecords.isEmpty()) {
                    BigDecimal totalRating = BigDecimal.ZERO;
                    for (UserAiAvatar record : ratedRecords) {
                        totalRating = totalRating.add(record.getUserRating());
                    }
                    aiAvatar.setRating(totalRating.divide(new BigDecimal(ratedRecords.size()), 2, BigDecimal.ROUND_HALF_UP));
                    aiAvatar.setRatingCount(ratedRecords.size());
                }
                
                aiAvatarMapper.updateById(aiAvatar);
            }
            
            return updateResult;
        } else {
            // 不存在记录，先使用一次再评分
            this.useAiAvatar(userId, aiAvatarId);
            return this.rateAiAvatar(userId, aiAvatarId, rating, feedback);
        }
    }
    
    @Override
    public Page<UserAiAvatarVO> getUserAiAvatarPage(Long userId, long current, long size) {
        // 查询用户的AI分身关联记录
        Page<UserAiAvatar> page = new Page<>(current, size);
        QueryWrapper<UserAiAvatar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId)
                .orderByDesc("lastUseTime");
        Page<UserAiAvatar> userAiAvatarPage = this.page(page, queryWrapper);
        
        // 转换为VO对象
        Page<UserAiAvatarVO> userAiAvatarVOPage = new Page<>(current, size, userAiAvatarPage.getTotal());
        List<UserAiAvatarVO> userAiAvatarVOList = new ArrayList<>();
        
        for (UserAiAvatar userAiAvatar : userAiAvatarPage.getRecords()) {
            UserAiAvatarVO userAiAvatarVO = new UserAiAvatarVO();
            BeanUtils.copyProperties(userAiAvatar, userAiAvatarVO);
            
            // 获取用户信息
            User user = userMapper.selectById(userId);
            if (user != null) {
                userAiAvatarVO.setUserName(user.getUserName());
                userAiAvatarVO.setUserAvatar(user.getUserAvatar());
            }
            
            // 获取AI分身信息
            AiAvatar aiAvatar = aiAvatarMapper.selectById(userAiAvatar.getAiAvatarId());
            if (aiAvatar != null) {
                userAiAvatarVO.setAiAvatarName(aiAvatar.getName());
                userAiAvatarVO.setAiAvatarImgUrl(aiAvatar.getAvatarImgUrl());
                userAiAvatarVO.setAiAvatarDescription(aiAvatar.getDescription());
            }
            
            userAiAvatarVOList.add(userAiAvatarVO);
        }
        
        userAiAvatarVOPage.setRecords(userAiAvatarVOList);
        return userAiAvatarVOPage;
    }
    
    @Override
    public List<UserAiAvatarVO> getUserFavoriteAiAvatars(Long userId) {
        // 查询用户收藏的AI分身
        QueryWrapper<UserAiAvatar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId)
                .eq("isFavorite", 1)
                .orderByDesc("updateTime");
        List<UserAiAvatar> userAiAvatarList = this.list(queryWrapper);
        
        // 转换为VO对象
        return userAiAvatarList.stream().map(userAiAvatar -> {
            UserAiAvatarVO userAiAvatarVO = new UserAiAvatarVO();
            BeanUtils.copyProperties(userAiAvatar, userAiAvatarVO);
            
            // 获取用户信息
            User user = userMapper.selectById(userId);
            if (user != null) {
                userAiAvatarVO.setUserName(user.getUserName());
                userAiAvatarVO.setUserAvatar(user.getUserAvatar());
            }
            
            // 获取AI分身信息
            AiAvatar aiAvatar = aiAvatarMapper.selectById(userAiAvatar.getAiAvatarId());
            if (aiAvatar != null) {
                userAiAvatarVO.setAiAvatarName(aiAvatar.getName());
                userAiAvatarVO.setAiAvatarImgUrl(aiAvatar.getAvatarImgUrl());
                userAiAvatarVO.setAiAvatarDescription(aiAvatar.getDescription());
            }
            
            return userAiAvatarVO;
        }).collect(Collectors.toList());
    }
} 