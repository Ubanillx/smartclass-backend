package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.CourseFavouriteMapper;
import com.ubanillx.smartclass.model.entity.Course;
import com.ubanillx.smartclass.model.entity.CourseFavourite;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.service.CourseFavouriteService;
import com.ubanillx.smartclass.service.CourseService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程收藏服务实现类
 */
@Service
@Slf4j
public class CourseFavouriteServiceImpl extends ServiceImpl<CourseFavouriteMapper, CourseFavourite>
        implements CourseFavouriteService {

    @Resource
    private CourseService courseService;

    @Resource
    private UserService userService;

    /**
     * 收藏课程
     *
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 收藏记录ID
     */
    @Transactional
    public long favourCourse(Long userId, Long courseId) {
        // 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID不合法");
        }

        // 校验课程和用户是否存在
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "课程不存在");
        }

        // 查询是否已收藏
        QueryWrapper<CourseFavourite> favouriteQueryWrapper = new QueryWrapper<>();
        favouriteQueryWrapper.eq("userId", userId);
        favouriteQueryWrapper.eq("courseId", courseId);
        long count = this.count(favouriteQueryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "已经收藏过该课程");
        }

        // 创建收藏记录
        CourseFavourite courseFavourite = new CourseFavourite();
        courseFavourite.setUserId(userId);
        courseFavourite.setCourseId(courseId);

        // 保存收藏记录
        boolean result = this.save(courseFavourite);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "收藏失败");
        }

        return courseFavourite.getId();
    }

    /**
     * 取消收藏
     *
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 是否成功
     */
    @Transactional
    public boolean unfavourCourse(Long userId, Long courseId) {
        // 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID不合法");
        }

        // 查询收藏记录
        QueryWrapper<CourseFavourite> favouriteQueryWrapper = new QueryWrapper<>();
        favouriteQueryWrapper.eq("userId", userId);
        favouriteQueryWrapper.eq("courseId", courseId);
        CourseFavourite favourite = this.getOne(favouriteQueryWrapper);

        if (favourite == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未收藏该课程");
        }

        // 删除收藏记录
        return this.removeById(favourite.getId());
    }

    /**
     * 判断用户是否收藏课程
     *
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 是否收藏
     */
    public boolean hasFavoured(Long userId, Long courseId) {
        // 参数校验
        if (userId == null || userId <= 0) {
            return false;
        }
        if (courseId == null || courseId <= 0) {
            return false;
        }

        // 查询收藏记录
        QueryWrapper<CourseFavourite> favouriteQueryWrapper = new QueryWrapper<>();
        favouriteQueryWrapper.eq("userId", userId);
        favouriteQueryWrapper.eq("courseId", courseId);
        return this.count(favouriteQueryWrapper) > 0;
    }

    /**
     * 获取用户收藏的课程ID列表
     *
     * @param userId 用户ID
     * @return 课程ID列表
     */
    public List<Long> getUserFavouriteCourseIds(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }

        QueryWrapper<CourseFavourite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        List<CourseFavourite> favourites = this.list(queryWrapper);

        return favourites.stream()
                .map(CourseFavourite::getCourseId)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户收藏的课程数量
     *
     * @param userId 用户ID
     * @return 收藏数量
     */
    public long getUserFavouriteCount(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }

        QueryWrapper<CourseFavourite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        return this.count(queryWrapper);
    }
} 