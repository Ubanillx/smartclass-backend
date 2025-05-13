package com.ubanillx.smartclass.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.constant.CommonConstant;
import com.ubanillx.smartclass.esdao.DailyArticleEsDao;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.model.dto.dailyarticle.DailyArticleEsDTO;
import com.ubanillx.smartclass.model.dto.dailyarticle.DailyArticleQueryRequest;
import com.ubanillx.smartclass.model.entity.DailyArticle;
import com.ubanillx.smartclass.model.vo.DailyArticleVO;
import com.ubanillx.smartclass.service.DailyArticleService;
import com.ubanillx.smartclass.mapper.DailyArticleMapper;
import com.ubanillx.smartclass.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    
    @Resource
    private DailyArticleEsDao dailyArticleEsDao;

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
    public DailyArticleVO getRandomLatestArticle() {
        // 查询最新的10篇文章
        QueryWrapper<DailyArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderByDesc("publishDate", "createTime");
        queryWrapper.last("LIMIT 10");
        List<DailyArticle> latestArticles = this.list(queryWrapper);
        
        // 如果没有文章，返回null
        if (CollUtil.isEmpty(latestArticles)) {
            return null;
        }
        
        // 从最新文章中随机选择一篇
        int randomIndex = (int) (Math.random() * latestArticles.size());
        DailyArticle randomArticle = latestArticles.get(randomIndex);
        
        // 返回文章视图对象
        return this.getDailyArticleVO(randomArticle);
    }

    @Override
    public Page<DailyArticle> searchFromEs(String searchText) {
        if (StringUtils.isBlank(searchText)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "搜索关键词为空");
        }
        
        // 设置默认分页参数
        long current = 0; // ES分页从0开始
        long pageSize = 10;
        
        // 创建多字段匹配查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        
        // 只查询未删除的文章
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
        
        // 创建多字段匹配查询
        boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchText)
                .field("title", 3.0f)    // 标题权重最高
                .field("content", 1.0f)  // 内容权重普通
                .field("summary", 2.0f)  // 摘要权重较高
                .field("tags", 2.0f)     // 标签权重较高
                .field("author", 1.5f)   // 作者权重中等
                .field("source", 1.0f)   // 来源权重普通
                .field("category", 1.5f) // 分类权重中等
                .type("best_fields"));   // 使用最佳字段匹配策略
        
        // 默认按相关度排序
        SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
        
        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);
        
        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(pageRequest)
                .withSorts(sortBuilder)
                .build();
        
        // 执行搜索
        SearchHits<DailyArticleEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, DailyArticleEsDTO.class);
        
        // 处理结果
        Page<DailyArticle> page = new Page<>(current + 1, pageSize);  // 转换回以1开始的页码
        page.setTotal(searchHits.getTotalHits());
        List<DailyArticle> resourceList = new ArrayList<>();
        
        // 查出结果后，从 db 获取最新数据
        if (searchHits.hasSearchHits()) {
            List<SearchHit<DailyArticleEsDTO>> searchHitList = searchHits.getSearchHits();
            List<Long> dailyArticleIdList = searchHitList.stream()
                    .map(searchHit -> searchHit.getContent().getId())
                    .collect(Collectors.toList());
            
            List<DailyArticle> dailyArticleList = baseMapper.selectBatchIds(dailyArticleIdList);
            if (CollUtil.isNotEmpty(dailyArticleList)) {
                Map<Long, List<DailyArticle>> idDailyArticleMap = dailyArticleList.stream()
                        .collect(Collectors.groupingBy(DailyArticle::getId));
                
                dailyArticleIdList.forEach(dailyArticleId -> {
                    if (idDailyArticleMap.containsKey(dailyArticleId)) {
                        resourceList.add(idDailyArticleMap.get(dailyArticleId).get(0));
                    } else {
                        // 从 ES 清空 DB 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(dailyArticleId), DailyArticleEsDTO.class);
                        log.info("Delete dailyArticle {}", delete);
                    }
                });
            }
        }
        
        page.setRecords(resourceList);
        return page;
    }
    
    @Override
    public boolean saveDailyArticle(DailyArticle dailyArticle) {
        if (dailyArticle == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        
        // 设置初始值
        if (dailyArticle.getViewCount() == null) {
            dailyArticle.setViewCount(0);
        }
        if (dailyArticle.getLikeCount() == null) {
            dailyArticle.setLikeCount(0);
        }
        if (dailyArticle.getPublishDate() == null) {
            dailyArticle.setPublishDate(new Date());
        }
        if (StringUtils.isBlank(dailyArticle.getSummary()) && StringUtils.isNotBlank(dailyArticle.getContent())) {
            // 如果摘要为空，自动截取内容前100个字符作为摘要
            String content = dailyArticle.getContent();
            int summaryLength = Math.min(content.length(), 100);
            dailyArticle.setSummary(content.substring(0, summaryLength));
        }
        
        boolean result = this.save(dailyArticle);
        if (result) {
            try {
                // 同步到ES
                DailyArticleEsDTO dailyArticleEsDTO = DailyArticleEsDTO.objToDto(dailyArticle);
                dailyArticleEsDao.save(dailyArticleEsDTO);
                log.info("同步新增每日美文到ES成功, id={}", dailyArticle.getId());
            } catch (Exception e) {
                log.error("同步新增每日美文到ES失败", e);
            }
        }
        return result;
    }
    
    @Override
    public boolean updateDailyArticle(DailyArticle dailyArticle) {
        boolean result = this.updateById(dailyArticle);
        if (result) {
            try {
                // 同步到ES
                DailyArticleEsDTO dailyArticleEsDTO = DailyArticleEsDTO.objToDto(dailyArticle);
                dailyArticleEsDao.save(dailyArticleEsDTO);
                log.info("同步更新每日美文到ES成功, id={}", dailyArticle.getId());
            } catch (Exception e) {
                log.error("同步更新每日美文到ES失败", e);
            }
        }
        return result;
    }
    
    @Override
    public boolean deleteDailyArticle(Long id) {
        boolean result = this.removeById(id);
        if (result) {
            try {
                // 从ES中删除
                dailyArticleEsDao.deleteById(id);
                log.info("同步删除每日美文到ES成功, id={}", id);
            } catch (Exception e) {
                log.error("同步删除每日美文到ES失败", e);
            }
        }
        return result;
    }
}




