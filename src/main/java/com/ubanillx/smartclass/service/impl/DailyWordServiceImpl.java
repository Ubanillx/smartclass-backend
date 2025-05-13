package com.ubanillx.smartclass.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.constant.CommonConstant;
import com.ubanillx.smartclass.esdao.DailyWordEsDao;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.model.dto.dailyword.DailyWordEsDTO;
import com.ubanillx.smartclass.model.dto.dailyword.DailyWordQueryRequest;
import com.ubanillx.smartclass.model.entity.DailyWord;
import com.ubanillx.smartclass.model.vo.DailyWordVO;
import com.ubanillx.smartclass.service.DailyWordService;
import com.ubanillx.smartclass.mapper.DailyWordMapper;
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
* @description 针对表【daily_word(每日单词)】的数据库操作Service实现
* @createDate 2025-03-19 00:03:09
*/
@Service
@Slf4j
public class DailyWordServiceImpl extends ServiceImpl<DailyWordMapper, DailyWord>
    implements DailyWordService{

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    
    @Resource
    private DailyWordEsDao dailyWordEsDao;

    @Override
    public long addDailyWord(DailyWord dailyWord, Long adminId) {
        if (dailyWord == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (StringUtils.isBlank(dailyWord.getWord())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单词不能为空");
        }
        if (StringUtils.isBlank(dailyWord.getTranslation())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "翻译不能为空");
        }
        if (dailyWord.getPublishDate() == null) {
            dailyWord.setPublishDate(new Date());
        }
        dailyWord.setAdminId(adminId);
        boolean result = this.save(dailyWord);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加失败");
        }
        return dailyWord.getId();
    }

    @Override
    public List<DailyWordVO> getDailyWordByDate(Date date) {
        if (date == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "日期不能为空");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(date);
        QueryWrapper<DailyWord> queryWrapper = new QueryWrapper<>();
        // 转换为日期字符串进行比较，忽略时分秒
        queryWrapper.apply("DATE_FORMAT(publishDate, '%Y-%m-%d') = {0}", dateString);
        List<DailyWord> dailyWordList = this.list(queryWrapper);
        return this.getDailyWordVO(dailyWordList);
    }

    @Override
    public DailyWordVO getDailyWordVO(DailyWord dailyWord) {
        if (dailyWord == null) {
            return null;
        }
        DailyWordVO dailyWordVO = new DailyWordVO();
        BeanUtils.copyProperties(dailyWord, dailyWordVO);
        return dailyWordVO;
    }

    @Override
    public List<DailyWordVO> getDailyWordVO(List<DailyWord> dailyWordList) {
        if (CollUtil.isEmpty(dailyWordList)) {
            return new ArrayList<>();
        }
        return dailyWordList.stream().map(this::getDailyWordVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<DailyWord> getQueryWrapper(DailyWordQueryRequest dailyWordQueryRequest) {
        if (dailyWordQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = dailyWordQueryRequest.getId();
        String word = dailyWordQueryRequest.getWord();
        String translation = dailyWordQueryRequest.getTranslation();
        Integer difficulty = dailyWordQueryRequest.getDifficulty();
        String category = dailyWordQueryRequest.getCategory();
        Date publishDateStart = dailyWordQueryRequest.getPublishDateStart();
        Date publishDateEnd = dailyWordQueryRequest.getPublishDateEnd();
        Long adminId = dailyWordQueryRequest.getAdminId();
        Date createTime = dailyWordQueryRequest.getCreateTime();
        String sortField = dailyWordQueryRequest.getSortField();
        String sortOrder = dailyWordQueryRequest.getSortOrder();

        QueryWrapper<DailyWord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(word), "word", word);
        queryWrapper.like(StringUtils.isNotBlank(translation), "translation", translation);
        queryWrapper.eq(difficulty != null, "difficulty", difficulty);
        queryWrapper.eq(StringUtils.isNotBlank(category), "category", category);
        queryWrapper.ge(publishDateStart != null, "publishDate", publishDateStart);
        queryWrapper.le(publishDateEnd != null, "publishDate", publishDateEnd);
        queryWrapper.eq(adminId != null, "adminId", adminId);
        queryWrapper.eq(createTime != null, "createTime", createTime);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), 
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), 
                sortField);
        return queryWrapper;
    }

    @Override
    public DailyWordVO getRandomDailyWord(Integer difficulty) {
        QueryWrapper<DailyWord> queryWrapper = new QueryWrapper<>();
        if (difficulty != null) {
            queryWrapper.eq("difficulty", difficulty);
        }
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderByAsc("RAND()");
        queryWrapper.last("LIMIT 1");
        DailyWord dailyWord = this.getOne(queryWrapper);
        return this.getDailyWordVO(dailyWord);
    }
    
    @Override
    public boolean increaseLikeCount(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        UpdateWrapper<DailyWord> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.setSql("likeCount = likeCount + 1");
        return this.update(updateWrapper);
    }
    
    @Override
    public boolean decreaseLikeCount(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        UpdateWrapper<DailyWord> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        // 确保点赞数不会小于0
        updateWrapper.setSql("likeCount = GREATEST(likeCount - 1, 0)");
        return this.update(updateWrapper);
    }
    
    @Override
    public DailyWordVO getRandomLatestWord() {
        // 查询最新的10个单词
        QueryWrapper<DailyWord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderByDesc("publishDate", "createTime");
        queryWrapper.last("LIMIT 10");
        List<DailyWord> latestWords = this.list(queryWrapper);
        
        // 如果没有单词，返回null
        if (CollUtil.isEmpty(latestWords)) {
            return null;
        }
        
        // 从最新单词中随机选择一个
        int randomIndex = (int) (Math.random() * latestWords.size());
        DailyWord randomWord = latestWords.get(randomIndex);
        
        // 返回单词视图对象
        return this.getDailyWordVO(randomWord);
    }

    @Override
    public Page<DailyWord> searchFromEs(String searchText) {
        if (StringUtils.isBlank(searchText)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "搜索关键词为空");
        }
        
        // 设置默认分页参数
        long current = 0; // ES分页从0开始
        long pageSize = 10;
        
        // 创建多字段匹配查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        
        // 只查询未删除的单词
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
        
        // 创建多字段匹配查询
        boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchText)
                .field("word", 3.0f)             // 单词匹配权重最高
                .field("translation", 2.0f)      // 翻译权重较高
                .field("example", 1.0f)          // 例句权重普通
                .field("exampleTranslation", 1.0f) // 例句翻译权重普通
                .field("notes", 1.5f)            // 笔记权重中等
                .field("category", 1.5f)         // 分类权重中等
                .type("best_fields"));           // 使用最佳字段匹配策略
        
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
        SearchHits<DailyWordEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, DailyWordEsDTO.class);
        
        // 处理结果
        Page<DailyWord> page = new Page<>(current + 1, pageSize); // 转换回以1开始的页码
        page.setTotal(searchHits.getTotalHits());
        List<DailyWord> resourceList = new ArrayList<>();
        
        // 查出结果后，从 db 获取最新数据
        if (searchHits.hasSearchHits()) {
            List<SearchHit<DailyWordEsDTO>> searchHitList = searchHits.getSearchHits();
            List<Long> dailyWordIdList = searchHitList.stream()
                    .map(searchHit -> searchHit.getContent().getId())
                    .collect(Collectors.toList());
            
            List<DailyWord> dailyWordList = baseMapper.selectBatchIds(dailyWordIdList);
            if (CollUtil.isNotEmpty(dailyWordList)) {
                Map<Long, List<DailyWord>> idDailyWordMap = dailyWordList.stream()
                        .collect(Collectors.groupingBy(DailyWord::getId));
                
                dailyWordIdList.forEach(dailyWordId -> {
                    if (idDailyWordMap.containsKey(dailyWordId)) {
                        resourceList.add(idDailyWordMap.get(dailyWordId).get(0));
                    } else {
                        // 从 ES 清空 DB 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(dailyWordId), DailyWordEsDTO.class);
                        log.info("Delete dailyWord {}", delete);
                    }
                });
            }
        }
        
        page.setRecords(resourceList);
        return page;
    }
    
    @Override
    public boolean saveDailyWord(DailyWord dailyWord) {
        boolean result = this.save(dailyWord);
        if (result) {
            try {
                // 同步到ES
                DailyWordEsDTO dailyWordEsDTO = DailyWordEsDTO.objToDto(dailyWord);
                dailyWordEsDao.save(dailyWordEsDTO);
                log.info("同步新增每日单词到ES成功, id={}", dailyWord.getId());
            } catch (Exception e) {
                log.error("同步新增每日单词到ES失败", e);
            }
        }
        return result;
    }
    
    @Override
    public boolean updateDailyWord(DailyWord dailyWord) {
        boolean result = this.updateById(dailyWord);
        if (result) {
            try {
                // 同步到ES
                DailyWordEsDTO dailyWordEsDTO = DailyWordEsDTO.objToDto(dailyWord);
                dailyWordEsDao.save(dailyWordEsDTO);
                log.info("同步更新每日单词到ES成功, id={}", dailyWord.getId());
            } catch (Exception e) {
                log.error("同步更新每日单词到ES失败", e);
            }
        }
        return result;
    }
    
    @Override
    public boolean deleteDailyWord(Long id) {
        boolean result = this.removeById(id);
        if (result) {
            try {
                // 从ES中删除
                dailyWordEsDao.deleteById(id);
                log.info("同步删除每日单词到ES成功, id={}", id);
            } catch (Exception e) {
                log.error("同步删除每日单词到ES失败", e);
            }
        }
        return result;
    }
}




