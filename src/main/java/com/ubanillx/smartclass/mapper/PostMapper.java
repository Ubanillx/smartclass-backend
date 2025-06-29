package com.ubanillx.smartclass.mapper;

import com.ubanillx.smartclass.model.entity.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author rog
* @description 针对表【post(帖子)】的数据库操作Mapper
* @createDate 2025-05-03 17:31:42
* @Entity com.ubanillx.smartclass.model.entity.Post
*/
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 查询帖子列表（包括已被删除的数据）
     *
     * @param minUpdateTime 最小更新时间
     * @return 帖子列表
     */
    List<Post> listPostWithDelete(Date minUpdateTime);
    
    /**
     * 更新帖子点赞数
     * 
     * @param postId 帖子id
     * @param num 增加的数量，可以为负数
     * @return 影响行数
     */
    @Update("update post set thumbNum = thumbNum + #{num} where id = #{postId}")
    int updateThumbNum(@Param("postId") long postId, @Param("num") int num);
    
    /**
     * 更新帖子收藏数
     * 
     * @param postId 帖子id
     * @param num 增加的数量，可以为负数
     * @return 影响行数
     */
    @Update("update post set favourNum = favourNum + #{num} where id = #{postId}")
    int updateFavourNum(@Param("postId") long postId, @Param("num") int num);
    
    /**
     * 更新帖子评论数
     * 
     * @param postId 帖子id
     * @param num 增加的数量，可以为负数
     * @return 影响行数
     */
    @Update("update post set commentNum = GREATEST(0, commentNum + #{num}) where id = #{postId}")
    int updateCommentNum(@Param("postId") long postId, @Param("num") int num);
    
    /**
     * 查询帖子评论数量
     * 
     * @param postId 帖子id
     * @return 评论数量
     */
    @Select("select count(*) from post_comment where postId = #{postId} and isDelete = 0")
    int getCommentCount(@Param("postId") long postId);
    
    /**
     * 批量查询帖子评论数量
     * 
     * @param postIds 帖子id列表
     * @return 帖子id -> 评论数量的映射
     */
    @Select("<script>select postId, count(*) as count from post_comment where postId in " +
            "<foreach collection='postIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " and isDelete = 0 group by postId</script>")
    List<Map<String, Object>> batchGetCommentCount(@Param("postIds") List<Long> postIds);
}




