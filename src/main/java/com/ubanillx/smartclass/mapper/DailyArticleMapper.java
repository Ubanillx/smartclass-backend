package com.ubanillx.smartclass.mapper;

import com.ubanillx.smartclass.model.entity.DailyArticle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
* @author liulo
* @description 针对表【daily_article(每日文章)】的数据库操作Mapper
* @createDate 2025-02-27 21:52:02
* @Entity com.ubanillx.smartclass.model.entity.DailyArticle
*/
public interface DailyArticleMapper extends BaseMapper<DailyArticle> {

    /**
     * 根据发布日期查询每日文章
     * @param publishDate 发布日期
     * @return 文章列表
     */
    @Select("SELECT * FROM daily_article WHERE publish_date = #{publishDate} AND is_delete = 0 ORDER BY id ASC")
    List<DailyArticle> selectByPublishDate(@Param("publishDate") Date publishDate);
    
    /**
     * 根据分类查询每日文章
     * @param category 分类
     * @param limit 限制数量
     * @return 文章列表
     */
    @Select("SELECT * FROM daily_article WHERE category = #{category} AND is_delete = 0 ORDER BY publish_date DESC LIMIT #{limit}")
    List<DailyArticle> selectByCategory(@Param("category") String category, @Param("limit") int limit);
    
    /**
     * 根据难度等级查询每日文章
     * @param difficulty 难度等级
     * @param limit 限制数量
     * @return 文章列表
     */
    @Select("SELECT * FROM daily_article WHERE difficulty = #{difficulty} AND is_delete = 0 ORDER BY publish_date DESC LIMIT #{limit}")
    List<DailyArticle> selectByDifficulty(@Param("difficulty") Integer difficulty, @Param("limit") int limit);
    
    /**
     * 增加文章查看次数
     * @param id 文章ID
     * @return 影响行数
     */
    @Update("UPDATE daily_article SET view_count = view_count + 1 WHERE id = #{id} AND is_delete = 0")
    int incrViewCount(@Param("id") Long id);
    
    /**
     * 增加文章点赞次数
     * @param id 文章ID
     * @return 影响行数
     */
    @Update("UPDATE daily_article SET like_count = like_count + 1 WHERE id = #{id} AND is_delete = 0")
    int incrLikeCount(@Param("id") Long id);
}




