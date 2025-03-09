package com.ubanillx.smartclass.manager;

import com.ubanillx.smartclass.model.entity.AiAvatar;
import org.springframework.stereotype.Component;

/**
 * AI头像管理类
 */
@Component
public class AiAvatarManager {

    /**
     * 检查AI头像是否可用
     * @param aiAvatar AI头像
     * @return 是否可用
     */
    public boolean isAvatarAvailable(AiAvatar aiAvatar) {
        if (aiAvatar == null) {
            return false;
        }
        // 检查状态是否为启用
        if (aiAvatar.getStatus() != 1) {
            return false;
        }
        // 检查是否被删除
        if (aiAvatar.getIsDelete() != null && aiAvatar.getIsDelete() == 1) {
            return false;
        }
        return true;
    }

    /**
     * 获取AI头像的完整配置
     * @param aiAvatar AI头像
     * @return 完整配置
     */
    public String getFullModelConfig(AiAvatar aiAvatar) {
        if (aiAvatar == null || aiAvatar.getModelConfig() == null) {
            return "{}";
        }
        return aiAvatar.getModelConfig();
    }

    /**
     * 增加使用次数
     * @param aiAvatar AI头像
     */
    public void incrementUsageCount(AiAvatar aiAvatar) {
        if (aiAvatar != null) {
            Integer currentCount = aiAvatar.getUsageCount();
            if (currentCount == null) {
                currentCount = 0;
            }
            aiAvatar.setUsageCount(currentCount + 1);
        }
    }

    /**
     * 计算平均评分
     * @param aiAvatar AI头像
     * @param newRating 新评分
     */
    public void calculateAverageRating(AiAvatar aiAvatar, Integer newRating) {
        if (aiAvatar != null && newRating != null && newRating >= 1 && newRating <= 5) {
            Integer currentCount = aiAvatar.getRatingCount();
            if (currentCount == null) {
                currentCount = 0;
            }
            
            java.math.BigDecimal currentRating = aiAvatar.getRating();
            if (currentRating == null) {
                currentRating = java.math.BigDecimal.ZERO;
            }
            
            // 计算新的平均评分
            java.math.BigDecimal totalScore = currentRating.multiply(new java.math.BigDecimal(currentCount))
                    .add(new java.math.BigDecimal(newRating));
            java.math.BigDecimal newCount = new java.math.BigDecimal(currentCount + 1);
            java.math.BigDecimal newAverage = totalScore.divide(newCount, 2, java.math.RoundingMode.HALF_UP);
            
            aiAvatar.setRating(newAverage);
            aiAvatar.setRatingCount(currentCount + 1);
        }
    }
} 