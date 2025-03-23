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
import com.ubanillx.smartclass.service.DailyArticleFavourService;
import com.ubanillx.smartclass.service.DailyArticleService;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 每日文章收藏服务实现
*/
@Service
public class DailyArticleFavourServiceImpl extends ServiceImpl<UserDailyArticleMapper, UserDailyArticle>
        implements DailyArticleFavourService {

    @Resource
    private DailyArticleService dailyArticleService;

    /**
     * 收藏/取消收藏每日文章
     *
     * @param articleId
     * @param loginUser
     * @return
     */
    @Override
    public int doArticleFavour(long articleId, User loginUser) {
        // 判断文章是否存在
        DailyArticle article = dailyArticleService.getById(articleId);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }
        // 获取用户ID
        long userId = loginUser.getId();
        // 每个用户串行收藏，避免并发问题
        // 锁必须要包裹住事务方法
        DailyArticleFavourService dailyArticleFavourService = (DailyArticleFavourService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return dailyArticleFavourService.doArticleFavourInner(userId, articleId);
        }
    }

    /**
     * 分页获取用户收藏的每日文章列表
     *
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    @Override
    public Page<DailyArticleVO> listFavourArticleByPage(IPage<DailyArticle> page, Wrapper<DailyArticle> queryWrapper, long favourUserId) {
        if (favourUserId <= 0) {
            return new Page<>();
        }
        
        // 使用Mapper中的自定义方法查询收藏的文章
        Page<DailyArticle> articlePage = baseMapper.listFavourArticleByPage(page, queryWrapper, favourUserId);
        
        // 将文章实体转换为VO
        List<DailyArticleVO> articleVOList = dailyArticleService.getDailyArticleVO(articlePage.getRecords());
        
        // 构建返回结果
        Page<DailyArticleVO> articleVOPage = new Page<>(page.getCurrent(), page.getSize(), articlePage.getTotal());
        articleVOPage.setRecords(articleVOList);
        
        return articleVOPage;
    }

    /**
     * 判断用户是否收藏了文章
     *
     * @param articleId
     * @param userId
     * @return
     */
    @Override
    public boolean isFavourArticle(long articleId, long userId) {
        // 查询是否存在收藏记录
        QueryWrapper<UserDailyArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("articleId", articleId);
        queryWrapper.eq("isCollected", 1);
        return this.count(queryWrapper) > 0;
    }

    /**
     * 收藏/取消收藏每日文章（内部事务方法）
     *
     * @param userId
     * @param articleId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doArticleFavourInner(long userId, long articleId) {
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
            userDailyArticle.setIsCollected(1); // 设置为已收藏
            userDailyArticle.setCollectTime(now);
            userDailyArticle.setCreateTime(now);
            boolean result = this.save(userDailyArticle);
            return result ? 1 : 0;
        }
        
        // 如果关联记录存在
        Integer isCollected = userDailyArticle.getIsCollected();
        
        // 如果已收藏，则取消收藏
        if (isCollected != null && isCollected == 1) {
            userDailyArticle.setIsCollected(0);
            userDailyArticle.setCollectTime(null);
            boolean result = this.updateById(userDailyArticle);
            return result ? -1 : 0;
        } 
        // 如果未收藏，则设置为收藏
        else {
            userDailyArticle.setIsCollected(1);
            userDailyArticle.setCollectTime(now);
            boolean result = this.updateById(userDailyArticle);
            return result ? 1 : 0;
        }
    }
} 