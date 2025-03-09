package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.model.entity.DailyArticle;
import com.ubanillx.smartclass.service.DailyArticleService;
import com.ubanillx.smartclass.mapper.DailyArticleMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
* @author liulo
* @description 针对表【daily_article(每日文章)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:02
*/
@Service
public class DailyArticleServiceImpl extends ServiceImpl<DailyArticleMapper, DailyArticle>
    implements DailyArticleService {

    @Override
    public long addDailyArticle(DailyArticle dailyArticle) {
        // 参数校验
        if (dailyArticle == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 校验文章信息
        validDailyArticle(dailyArticle, true);
        
        // 设置默认值
        if (dailyArticle.getDifficulty() == null) {
            dailyArticle.setDifficulty(1); // 默认简单难度
        }
        if (dailyArticle.getReadTime() == null) {
            // 根据内容长度估算阅读时间（假设每分钟阅读500字）
            String content = dailyArticle.getContent();
            if (StringUtils.isNotBlank(content)) {
                int readTime = Math.max(1, content.length() / 500);
                dailyArticle.setReadTime(readTime);
            } else {
                dailyArticle.setReadTime(1); // 默认1分钟
            }
        }
        if (dailyArticle.getPublishDate() == null) {
            dailyArticle.setPublishDate(new Date()); // 默认当前日期
        }
        if (dailyArticle.getViewCount() == null) {
            dailyArticle.setViewCount(0); // 默认查看次数为0
        }
        if (dailyArticle.getLikeCount() == null) {
            dailyArticle.setLikeCount(0); // 默认点赞次数为0
        }
        if (dailyArticle.getIsDelete() == null) {
            dailyArticle.setIsDelete(0); // 默认未删除
        }
        
        // 插入数据
        boolean result = this.save(dailyArticle);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加每日文章失败");
        }
        
        return dailyArticle.getId();
    }

    @Override
    public boolean deleteDailyArticle(long id) {
        // 参数校验
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询是否存在
        DailyArticle dailyArticle = this.getById(id);
        if (dailyArticle == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 逻辑删除
        dailyArticle.setIsDelete(1);
        return this.updateById(dailyArticle);
    }

    @Override
    public boolean updateDailyArticle(DailyArticle dailyArticle) {
        // 参数校验
        if (dailyArticle == null || dailyArticle.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 校验文章信息
        validDailyArticle(dailyArticle, false);
        
        // 查询是否存在
        DailyArticle oldDailyArticle = this.getById(dailyArticle.getId());
        if (oldDailyArticle == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 如果内容有更新，重新计算阅读时间
        if (StringUtils.isNotBlank(dailyArticle.getContent()) && 
            !dailyArticle.getContent().equals(oldDailyArticle.getContent())) {
            int readTime = Math.max(1, dailyArticle.getContent().length() / 500);
            dailyArticle.setReadTime(readTime);
        }
        
        // 更新数据
        return this.updateById(dailyArticle);
    }

    @Override
    public DailyArticle getDailyArticleById(long id) {
        // 参数校验
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询数据
        DailyArticle dailyArticle = this.getById(id);
        if (dailyArticle == null || dailyArticle.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        return dailyArticle;
    }

    @Override
    public List<DailyArticle> getDailyArticlesByDate(Date date) {
        if (date == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        return baseMapper.selectByPublishDate(date);
    }

    @Override
    public List<DailyArticle> getTodayArticles() {
        // 获取今天的日期（只保留年月日）
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();
        
        return getDailyArticlesByDate(today);
    }

    @Override
    public Page<DailyArticle> listDailyArticleByPage(String category, Integer difficulty, String keyword, int current, int size) {
        // 创建查询条件
        LambdaQueryWrapper<DailyArticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DailyArticle::getIsDelete, 0);
        
        // 根据分类查询
        if (StringUtils.isNotBlank(category)) {
            queryWrapper.eq(DailyArticle::getCategory, category);
        }
        
        // 根据难度等级查询
        if (difficulty != null) {
            queryWrapper.eq(DailyArticle::getDifficulty, difficulty);
        }
        
        // 根据关键词查询
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> 
                wrapper.like(DailyArticle::getTitle, keyword)
                    .or()
                    .like(DailyArticle::getContent, keyword)
                    .or()
                    .like(DailyArticle::getSummary, keyword)
                    .or()
                    .like(DailyArticle::getTags, keyword)
            );
        }
        
        // 按发布日期降序排序
        queryWrapper.orderByDesc(DailyArticle::getPublishDate);
        
        // 分页查询
        Page<DailyArticle> page = new Page<>(current, size);
        return this.page(page, queryWrapper);
    }

    @Override
    public List<DailyArticle> listDailyArticlesByCategory(String category, int limit) {
        // 参数校验
        if (StringUtils.isBlank(category)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        return baseMapper.selectByCategory(category, limit);
    }

    @Override
    public List<DailyArticle> listDailyArticlesByDifficulty(Integer difficulty, int limit) {
        // 参数校验
        if (difficulty == null || difficulty <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        return baseMapper.selectByDifficulty(difficulty, limit);
    }

    @Override
    public boolean incrViewCount(long id) {
        // 参数校验
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 增加查看次数
        return baseMapper.incrViewCount(id) > 0;
    }

    @Override
    public boolean incrLikeCount(long id) {
        // 参数校验
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 增加点赞次数
        return baseMapper.incrLikeCount(id) > 0;
    }

    @Override
    public void validDailyArticle(DailyArticle dailyArticle, boolean add) {
        if (dailyArticle == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        String title = dailyArticle.getTitle();
        String content = dailyArticle.getContent();
        
        // 创建时，标题和内容不能为空
        if (add) {
            if (StringUtils.isBlank(title)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章标题不能为空");
            }
            if (StringUtils.isBlank(content)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章内容不能为空");
            }
        }
        
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 128) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章标题过长");
        }
        
        // 摘要长度校验
        String summary = dailyArticle.getSummary();
        if (StringUtils.isNotBlank(summary) && summary.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章摘要过长");
        }
        
        // 难度等级校验
        Integer difficulty = dailyArticle.getDifficulty();
        if (difficulty != null && (difficulty < 1 || difficulty > 3)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "难度等级必须在1-3之间");
        }
        
        // 阅读时间校验
        Integer readTime = dailyArticle.getReadTime();
        if (readTime != null && readTime <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "阅读时间必须大于0");
        }
    }
}




