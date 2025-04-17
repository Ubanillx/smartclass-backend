package com.ubanillx.smartclass.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.constant.CommonConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.model.dto.dailyword.DailyWordQueryRequest;
import com.ubanillx.smartclass.model.entity.DailyWord;
import com.ubanillx.smartclass.model.vo.DailyWordVO;
import com.ubanillx.smartclass.service.DailyWordService;
import com.ubanillx.smartclass.mapper.DailyWordMapper;
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
* @description 针对表【daily_word(每日单词)】的数据库操作Service实现
* @createDate 2025-03-19 00:03:09
*/
@Service
@Slf4j
public class DailyWordServiceImpl extends ServiceImpl<DailyWordMapper, DailyWord>
    implements DailyWordService{

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
        updateWrapper.setSql("like_count = like_count + 1");
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
        updateWrapper.setSql("like_count = GREATEST(like_count - 1, 0)");
        return this.update(updateWrapper);
    }
}




