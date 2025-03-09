package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.model.entity.DailyWord;
import com.ubanillx.smartclass.service.DailyWordService;
import com.ubanillx.smartclass.mapper.DailyWordMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
* @author liulo
* @description 针对表【daily_word(每日单词)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:02
*/
@Service
public class DailyWordServiceImpl extends ServiceImpl<DailyWordMapper, DailyWord>
    implements DailyWordService {

    @Override
    public long addDailyWord(DailyWord dailyWord) {
        // 参数校验
        if (dailyWord == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 校验单词信息
        validDailyWord(dailyWord, true);
        
        // 设置默认值
        if (dailyWord.getDifficulty() == null) {
            dailyWord.setDifficulty(1); // 默认简单难度
        }
        if (dailyWord.getPublishDate() == null) {
            dailyWord.setPublishDate(new Date()); // 默认当前日期
        }
        if (dailyWord.getIsDelete() == null) {
            dailyWord.setIsDelete(0); // 默认未删除
        }
        
        // 插入数据
        boolean result = this.save(dailyWord);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加每日单词失败");
        }
        
        return dailyWord.getId();
    }

    @Override
    public boolean deleteDailyWord(long id) {
        // 参数校验
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询是否存在
        DailyWord dailyWord = this.getById(id);
        if (dailyWord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 逻辑删除
        dailyWord.setIsDelete(1);
        return this.updateById(dailyWord);
    }

    @Override
    public boolean updateDailyWord(DailyWord dailyWord) {
        // 参数校验
        if (dailyWord == null || dailyWord.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 校验单词信息
        validDailyWord(dailyWord, false);
        
        // 查询是否存在
        DailyWord oldDailyWord = this.getById(dailyWord.getId());
        if (oldDailyWord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 更新数据
        return this.updateById(dailyWord);
    }

    @Override
    public DailyWord getDailyWordById(long id) {
        // 参数校验
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询数据
        DailyWord dailyWord = this.getById(id);
        if (dailyWord == null || dailyWord.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        return dailyWord;
    }

    @Override
    public List<DailyWord> getDailyWordsByDate(Date date) {
        if (date == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        return baseMapper.selectByPublishDate(date);
    }

    @Override
    public List<DailyWord> getTodayWords() {
        // 获取今天的日期（只保留年月日）
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();
        
        return getDailyWordsByDate(today);
    }

    @Override
    public Page<DailyWord> listDailyWordByPage(String category, Integer difficulty, String keyword, int current, int size) {
        // 创建查询条件
        LambdaQueryWrapper<DailyWord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DailyWord::getIsDelete, 0);
        
        // 根据分类查询
        if (StringUtils.isNotBlank(category)) {
            queryWrapper.eq(DailyWord::getCategory, category);
        }
        
        // 根据难度等级查询
        if (difficulty != null) {
            queryWrapper.eq(DailyWord::getDifficulty, difficulty);
        }
        
        // 根据关键词查询
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> 
                wrapper.like(DailyWord::getWord, keyword)
                    .or()
                    .like(DailyWord::getTranslation, keyword)
                    .or()
                    .like(DailyWord::getExample, keyword)
            );
        }
        
        // 按发布日期降序排序
        queryWrapper.orderByDesc(DailyWord::getPublishDate);
        
        // 分页查询
        Page<DailyWord> page = new Page<>(current, size);
        return this.page(page, queryWrapper);
    }

    @Override
    public List<DailyWord> listDailyWordsByCategory(String category, int limit) {
        // 参数校验
        if (StringUtils.isBlank(category)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        return baseMapper.selectByCategory(category, limit);
    }

    @Override
    public List<DailyWord> listDailyWordsByDifficulty(Integer difficulty, int limit) {
        // 参数校验
        if (difficulty == null || difficulty <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        return baseMapper.selectByDifficulty(difficulty, limit);
    }

    @Override
    public void validDailyWord(DailyWord dailyWord, boolean add) {
        if (dailyWord == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        String word = dailyWord.getWord();
        String translation = dailyWord.getTranslation();
        
        // 创建时，单词和翻译不能为空
        if (add) {
            if (StringUtils.isBlank(word)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单词不能为空");
            }
            if (StringUtils.isBlank(translation)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "翻译不能为空");
            }
        }
        
        // 有参数则校验
        if (StringUtils.isNotBlank(word) && word.length() > 128) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单词过长");
        }
        if (StringUtils.isNotBlank(translation) && translation.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "翻译过长");
        }
        
        // 难度等级校验
        Integer difficulty = dailyWord.getDifficulty();
        if (difficulty != null && (difficulty < 1 || difficulty > 3)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "难度等级必须在1-3之间");
        }
    }
}




