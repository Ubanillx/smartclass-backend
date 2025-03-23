package com.ubanillx.smartclass.service;

import com.ubanillx.smartclass.model.entity.CourseFavourite;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author liulo
* @description 针对表【course_favourite(课程收藏)】的数据库操作Service
* @createDate 2025-03-21 15:14:50
*/
public interface CourseFavouriteService extends IService<CourseFavourite> {

    /**
     * 收藏课程
     *
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 收藏记录ID
     */
    long favourCourse(Long userId, Long courseId);

    /**
     * 取消收藏
     *
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 是否成功
     */
    boolean unfavourCourse(Long userId, Long courseId);

    /**
     * 判断用户是否收藏课程
     *
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 是否收藏
     */
    boolean hasFavoured(Long userId, Long courseId);

    /**
     * 获取用户收藏的课程ID列表
     *
     * @param userId 用户ID
     * @return 课程ID列表
     */
    List<Long> getUserFavouriteCourseIds(Long userId);

    /**
     * 获取用户收藏的课程数量
     *
     * @param userId 用户ID
     * @return 收藏数量
     */
    long getUserFavouriteCount(Long userId);
}
