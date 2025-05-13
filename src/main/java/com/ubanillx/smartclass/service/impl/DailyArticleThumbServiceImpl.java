package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.UserDailyArticleMapper;
import com.ubanillx.smartclass.model.entity.DailyArticle;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserDailyArticle;
import com.ubanillx.smartclass.model.vo.DailyArticleVO;
import com.ubanillx.smartclass.service.DailyArticleService;
import com.ubanillx.smartclass.service.DailyArticleThumbService;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 每日文章点赞服务实现
*/
@Service
public class DailyArticleThumbServiceImpl extends ServiceImpl<UserDailyArticleMapper, UserDailyArticle>
        implements DailyArticleThumbService {

    @Resource
    private DailyArticleService dailyArticleService;

    /**
     * 点赞/取消点赞文章
     *
     * @param articleId
     * @param loginUser
     * @return
     */
    @Override
    public int doArticleThumb(long articleId, User loginUser) {
        // 判断文章是否存在
        DailyArticle article = dailyArticleService.getById(articleId);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }
        // 获取用户ID
        long userId = loginUser.getId();
        // 每个用户串行点赞，避免并发问题
        // 锁必须要包裹住事务方法
        DailyArticleThumbService articleThumbService = (DailyArticleThumbService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return articleThumbService.doArticleThumbInner(userId, articleId);
        }
    }
    
    /**
     * 取消点赞文章
     *
     * @param articleId 文章id
     * @param userId 用户id
     * @return 是否成功
     */
    @Override
    public int cancelArticleThumb(long articleId, long userId) {
        // 判断文章是否存在
        DailyArticle article = dailyArticleService.getById(articleId);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }
        
        // 查询用户与文章的关联记录
        QueryWrapper<UserDailyArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("articleId", articleId);
        UserDailyArticle userDailyArticle = this.getOne(queryWrapper);
        
        // 如果关联记录不存在或已经是未点赞状态，返回失败
        if (userDailyArticle == null || userDailyArticle.getIsLiked() == 0) {
            return 0;
        }
        
        // 设置为未点赞
        userDailyArticle.setIsLiked(0);
        userDailyArticle.setLikeTime(null);
        userDailyArticle.setUpdateTime(new Date());
        boolean result = this.updateById(userDailyArticle);
        if (result) {
            // 更新文章点赞数 -1
            dailyArticleService.decreaseLikeCount(articleId);
            return 1;
        }
        return 0;
    }

    /**
     * 文章点赞（内部事务方法）
     *
     * @param userId
     * @param articleId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doArticleThumbInner(long userId, long articleId) {
        // 查询用户与文章的关联记录
        QueryWrapper<UserDailyArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("articleId", articleId);
        UserDailyArticle userDailyArticle = this.getOne(queryWrapper);
        
        // 当前时间
        Date now = new Date();
        
        // 如果关联记录不存在，创建新记录
        if (userDailyArticle == null) {
            userDailyArticle = new UserDailyArticle();
            userDailyArticle.setUserId(userId);
            userDailyArticle.setArticleId(articleId);
            userDailyArticle.setIsLiked(1); // 设置为已点赞
            userDailyArticle.setLikeTime(now);
            userDailyArticle.setCreateTime(now);
            boolean result = this.save(userDailyArticle);
            if (result) {
                // 更新文章点赞数 +1
                dailyArticleService.increaseLikeCount(articleId);
                return 1;
            } else {
                return 0;
            }
        }
        
        // 如果关联记录存在
        Integer isLiked = userDailyArticle.getIsLiked();
        
        // 如果已点赞，则取消点赞
        if (isLiked != null && isLiked == 1) {
            userDailyArticle.setIsLiked(0);
            userDailyArticle.setLikeTime(null);
            userDailyArticle.setUpdateTime(now);
            boolean result = this.updateById(userDailyArticle);
            if (result) {
                // 更新文章点赞数 -1
                dailyArticleService.decreaseLikeCount(articleId);
                return -1;
            } else {
                return 0;
            }
        } 
        // 如果未点赞，则设置为点赞
        else {
            userDailyArticle.setIsLiked(1);
            userDailyArticle.setLikeTime(now);
            userDailyArticle.setUpdateTime(now);
            boolean result = this.updateById(userDailyArticle);
            if (result) {
                // 更新文章点赞数 +1
                dailyArticleService.increaseLikeCount(articleId);
                return 1;
            } else {
                return 0;
            }
        }
    }
    
    /**
     * 分页获取用户点赞的每日文章列表
     *
     * @param page
     * @param queryWrapper
     * @param thumbUserId
     * @return
     */
    @Override
    public Page<DailyArticleVO> listThumbArticleByPage(IPage<DailyArticle> page, Wrapper<DailyArticle> queryWrapper, long thumbUserId) {
        if (thumbUserId <= 0) {
            return new Page<>();
        }
        
        // 使用Mapper中的自定义方法查询点赞的文章
        Page<DailyArticle> articlePage = baseMapper.listThumbArticleByPage(page, queryWrapper, thumbUserId);
        
        // 转换为VO对象
        List<DailyArticleVO> articleVOList = dailyArticleService.getDailyArticleVO(articlePage.getRecords());
        
        // 构建返回结果
        Page<DailyArticleVO> articleVOPage = new Page<>(page.getCurrent(), page.getSize(), articlePage.getTotal());
        articleVOPage.setRecords(articleVOList);
        
        return articleVOPage;
    }

    /**
     * 判断用户是否点赞了文章
     *
     * @param articleId
     * @param userId
     * @return
     */
    @Override
    public boolean isThumbArticle(long articleId, long userId) {
        // 查询是否存在点赞记录
        QueryWrapper<UserDailyArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("articleId", articleId);
        queryWrapper.eq("isLiked", 1);
        return this.count(queryWrapper) > 0;
    }
} 