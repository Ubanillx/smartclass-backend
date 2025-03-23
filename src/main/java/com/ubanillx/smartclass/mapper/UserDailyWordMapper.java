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
* @author liulo
* @description 针对表【user_daily_word(用户与每日单词关联)】的数据库操作Mapper
* @createDate 2025-03-20 14:25:20
* @Entity com.ubanillx.smartclass.model.entity.UserDailyWord
*/
public interface UserDailyWordMapper extends BaseMapper<UserDailyWord> {

    /**
     * 获取用户收藏的单词分页列表
     * 
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    Page<DailyWord> listFavourWordByPage(IPage<DailyWord> page, 
            @Param(Constants.WRAPPER) Wrapper<DailyWord> queryWrapper, 
            @Param("favourUserId") long favourUserId);
            
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




