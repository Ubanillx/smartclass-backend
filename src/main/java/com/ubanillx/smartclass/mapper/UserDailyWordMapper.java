package com.ubanillx.smartclass.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.model.entity.DailyWord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ubanillx.smartclass.model.entity.UserDailyWord;
import org.apache.ibatis.annotations.Param;

/**
* @description 针对表【user_daily_word(用户与每日单词关联)】的数据库操作Mapper
* @Entity com.ubanillx.smartclass.model.entity.UserDailyWord
*/
public interface UserDailyWordMapper extends BaseMapper<UserDailyWord> {
    /**
     * 获取用户点赞的单词分页列表
     * 
     * @param page
     * @param queryWrapper
     * @param thumbUserId
     * @return
     */
    Page<DailyWord> listThumbWordByPage(IPage<DailyWord> page, 
            @Param(Constants.WRAPPER) Wrapper<DailyWord> queryWrapper, 
            @Param("thumbUserId") long thumbUserId);
} 