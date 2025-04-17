package com.ubanillx.smartclass.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.constant.CommonConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.model.dto.dailyarticle.DailyArticleQueryRequest;
import com.ubanillx.smartclass.model.entity.DailyArticle;
import com.ubanillx.smartclass.model.vo.DailyArticleVO;
import com.ubanillx.smartclass.service.DailyArticleService;
import com.ubanillx.smartclass.mapper.DailyArticleMapper;
import com.ubanillx.smartclass.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author liulo
* @description 针对表【daily_article(每日文章)】的数据库操作Service实现
* @createDate 2025-03-19 00:03:09
*/
@Service
@Slf4j
public class DailyArticleServiceImpl extends ServiceImpl<DailyArticleMapper, DailyArticle>
    implements DailyArticleService{

    @Override
    public long addDailyArticle(DailyArticle dailyArticle, Long adminId) {
        if (dailyArticle == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (StringUtils.isBlank(dailyArticle.getTitle())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章标题不能为空");
        }
        if (StringUtils.isBlank(dailyArticle.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章内容不能为空");
        }
        if (StringUtils.isBlank(dailyArticle.getSummary())) {
            // 如果摘要为空，自动截取内容前100个字符作为摘要
            String content = dailyArticle.getContent();
            int summaryLength = Math.min(content.length(), 100);
            dailyArticle.setSummary(content.substring(0, summaryLength));
        }
        if (dailyArticle.getPublishDate() == null) {
            dailyArticle.setPublishDate(new Date());
        }
        dailyArticle.setAdminId(adminId);
        dailyArticle.setViewCount(0);
        dailyArticle.setLikeCount(0);
        boolean result = this.save(dailyArticle);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加失败");
        }
        return dailyArticle.getId();
    }

    @Override
    public List<DailyArticleVO> getDailyArticleByDate(Date date) {
        if (date == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "日期不能为空");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(date);
        QueryWrapper<DailyArticle> queryWrapper = new QueryWrapper<>();
        // 转换为日期字符串进行比较，忽略时分秒
        queryWrapper.apply("DATE_FORMAT(publishDate, '%Y-%m-%d') = {0}", dateString);
        List<DailyArticle> dailyArticleList = this.list(queryWrapper);
        return this.getDailyArticleVO(dailyArticleList);
    }

    @Override
    public DailyArticleVO getDailyArticleVO(DailyArticle dailyArticle) {
        if (dailyArticle == null) {
            return null;
        }
        DailyArticleVO dailyArticleVO = new DailyArticleVO();
        BeanUtils.copyProperties(dailyArticle, dailyArticleVO);
        return dailyArticleVO;
    }

    @Override
    public List<DailyArticleVO> getDailyArticleVO(List<DailyArticle> dailyArticleList) {
        if (CollUtil.isEmpty(dailyArticleList)) {
            return new ArrayList<>();
        }
        return dailyArticleList.stream().map(this::getDailyArticleVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<DailyArticle> getQueryWrapper(DailyArticleQueryRequest dailyArticleQueryRequest) {
        if (dailyArticleQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = dailyArticleQueryRequest.getId();
        String title = dailyArticleQueryRequest.getTitle();
        String summary = dailyArticleQueryRequest.getSummary();
        String author = dailyArticleQueryRequest.getAuthor();
        String source = dailyArticleQueryRequest.getSource();
        String category = dailyArticleQueryRequest.getCategory();
        String tags = dailyArticleQueryRequest.getTags();
        Integer difficulty = dailyArticleQueryRequest.getDifficulty();
        Date publishDateStart = dailyArticleQueryRequest.getPublishDateStart();
        Date publishDateEnd = dailyArticleQueryRequest.getPublishDateEnd();
        Long adminId = dailyArticleQueryRequest.getAdminId();
        Integer minReadTime = dailyArticleQueryRequest.getMinReadTime();
        Integer maxReadTime = dailyArticleQueryRequest.getMaxReadTime();
        Integer minViewCount = dailyArticleQueryRequest.getMinViewCount();
        Date createTime = dailyArticleQueryRequest.getCreateTime();
        String sortField = dailyArticleQueryRequest.getSortField();
        String sortOrder = dailyArticleQueryRequest.getSortOrder();

        QueryWrapper<DailyArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(summary), "summary", summary);
        queryWrapper.like(StringUtils.isNotBlank(author), "author", author);
        queryWrapper.like(StringUtils.isNotBlank(source), "source", source);
        queryWrapper.eq(StringUtils.isNotBlank(category), "category", category);
        queryWrapper.like(StringUtils.isNotBlank(tags), "tags", tags);
        queryWrapper.eq(difficulty != null, "difficulty", difficulty);
        queryWrapper.ge(publishDateStart != null, "publishDate", publishDateStart);
        queryWrapper.le(publishDateEnd != null, "publishDate", publishDateEnd);
        queryWrapper.eq(adminId != null, "adminId", adminId);
        queryWrapper.ge(minReadTime != null, "readTime", minReadTime);
        queryWrapper.le(maxReadTime != null, "readTime", maxReadTime);
        queryWrapper.ge(minViewCount != null, "viewCount", minViewCount);
        queryWrapper.eq(createTime != null, "createTime", createTime);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), 
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), 
                sortField);
        return queryWrapper;
    }

    @Override
    public boolean increaseViewCount(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        UpdateWrapper<DailyArticle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.setSql("viewCount = viewCount + 1");
        return this.update(updateWrapper);
    }

    @Override
    public boolean increaseLikeCount(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        UpdateWrapper<DailyArticle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.setSql("likeCount = likeCount + 1");
        return this.update(updateWrapper);
    }
    
    @Override
    public boolean decreaseLikeCount(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        UpdateWrapper<DailyArticle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        // 确保点赞数不会小于0
        updateWrapper.setSql("likeCount = GREATEST(likeCount - 1, 0)");
        return this.update(updateWrapper);
    }

    @Override
    public List<DailyArticleVO> getRecommendArticles(String category, Integer difficulty, int limit) {
        QueryWrapper<DailyArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", 0);
        if (StringUtils.isNotBlank(category)) {
            queryWrapper.eq("category", category);
        }
        if (difficulty != null) {
            queryWrapper.eq("difficulty", difficulty);
        }
        // 按照查看次数和点赞次数排序，获取热门文章
        queryWrapper.orderByDesc("viewCount", "likeCount");
        queryWrapper.last("LIMIT " + limit);
        List<DailyArticle> dailyArticleList = this.list(queryWrapper);
        return this.getDailyArticleVO(dailyArticleList);
    }
}




