package com.ubanillx.smartclass.mapper;

import com.ubanillx.smartclass.model.entity.DailyWord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
* @author liulo
* @description 针对表【daily_word(每日单词)】的数据库操作Mapper
* @createDate 2025-02-27 21:52:02
* @Entity com.ubanillx.smartclass.model.entity.DailyWord
*/
public interface DailyWordMapper extends BaseMapper<DailyWord> {

    /**
     * 根据发布日期查询每日单词
     * @param publishDate 发布日期
     * @return 单词列表
     */
    @Select("SELECT * FROM daily_word WHERE publish_date = #{publishDate} AND is_delete = 0 ORDER BY id ASC")
    List<DailyWord> selectByPublishDate(@Param("publishDate") Date publishDate);
    
    /**
     * 根据分类查询每日单词
     * @param category 分类
     * @param limit 限制数量
     * @return 单词列表
     */
    @Select("SELECT * FROM daily_word WHERE category = #{category} AND is_delete = 0 ORDER BY publish_date DESC LIMIT #{limit}")
    List<DailyWord> selectByCategory(@Param("category") String category, @Param("limit") int limit);
    
    /**
     * 根据难度等级查询每日单词
     * @param difficulty 难度等级
     * @param limit 限制数量
     * @return 单词列表
     */
    @Select("SELECT * FROM daily_word WHERE difficulty = #{difficulty} AND is_delete = 0 ORDER BY publish_date DESC LIMIT #{limit}")
    List<DailyWord> selectByDifficulty(@Param("difficulty") Integer difficulty, @Param("limit") int limit);
}




