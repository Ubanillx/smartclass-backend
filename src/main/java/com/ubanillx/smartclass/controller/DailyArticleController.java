package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.DeleteRequest;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.dailyarticle.DailyArticleAddRequest;
import com.ubanillx.smartclass.model.dto.dailyarticle.DailyArticleQueryRequest;
import com.ubanillx.smartclass.model.dto.dailyarticle.DailyArticleUpdateRequest;
import com.ubanillx.smartclass.model.entity.DailyArticle;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserArticleRecord;
import com.ubanillx.smartclass.model.vo.DailyArticleVO;
import com.ubanillx.smartclass.service.DailyArticleService;
import com.ubanillx.smartclass.service.UserArticleRecordService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 每日文章接口
 */
@RestController
@RequestMapping("/daily/article")
@Slf4j
public class DailyArticleController {

    @Resource
    private DailyArticleService dailyArticleService;

    @Resource
    private UserService userService;

    @Resource
    private UserArticleRecordService userArticleRecordService;

    /**
     * 创建每日文章
     *
     * @param dailyArticleAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addDailyArticle(@RequestBody DailyArticleAddRequest dailyArticleAddRequest, HttpServletRequest request) {
        if (dailyArticleAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DailyArticle dailyArticle = new DailyArticle();
        BeanUtils.copyProperties(dailyArticleAddRequest, dailyArticle);
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        dailyArticle.setAdminId(loginUser.getId());
        
        // 校验
        dailyArticleService.validDailyArticle(dailyArticle, true);
        
        long id = dailyArticleService.addDailyArticle(dailyArticle);
        return ResultUtils.success(id);
    }

    /**
     * 删除每日文章
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteDailyArticle(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        
        // 判断是否存在
        DailyArticle oldDailyArticle = dailyArticleService.getById(id);
        ThrowUtils.throwIf(oldDailyArticle == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可删除
        if (!oldDailyArticle.getAdminId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        boolean b = dailyArticleService.deleteDailyArticle(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新每日文章
     *
     * @param dailyArticleUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateDailyArticle(@RequestBody DailyArticleUpdateRequest dailyArticleUpdateRequest, HttpServletRequest request) {
        if (dailyArticleUpdateRequest == null || dailyArticleUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DailyArticle dailyArticle = new DailyArticle();
        BeanUtils.copyProperties(dailyArticleUpdateRequest, dailyArticle);
        
        // 参数校验
        dailyArticleService.validDailyArticle(dailyArticle, false);
        
        // 判断是否存在
        long id = dailyArticleUpdateRequest.getId();
        DailyArticle oldDailyArticle = dailyArticleService.getById(id);
        ThrowUtils.throwIf(oldDailyArticle == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可修改
        User user = userService.getLoginUser(request);
        if (!oldDailyArticle.getAdminId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        boolean result = dailyArticleService.updateDailyArticle(dailyArticle);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取每日文章
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<DailyArticleVO> getDailyArticleById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        DailyArticle dailyArticle = dailyArticleService.getDailyArticleById(id);
        if (dailyArticle == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 增加查看次数
        dailyArticleService.incrViewCount(id);
        
        DailyArticleVO dailyArticleVO = getDailyArticleVO(dailyArticle, request);
        return ResultUtils.success(dailyArticleVO);
    }

    /**
     * 分页获取每日文章列表
     *
     * @param dailyArticleQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<DailyArticleVO>> listDailyArticleByPage(@RequestBody DailyArticleQueryRequest dailyArticleQueryRequest, HttpServletRequest request) {
        if (dailyArticleQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取分页参数
        long current = dailyArticleQueryRequest.getCurrent();
        long size = dailyArticleQueryRequest.getPageSize();
        
        // 构建查询条件
        String category = dailyArticleQueryRequest.getCategory();
        Integer difficulty = dailyArticleQueryRequest.getDifficulty();
        String searchText = dailyArticleQueryRequest.getSearchText();
        
        // 分页查询
        Page<DailyArticle> dailyArticlePage = dailyArticleService.listDailyArticleByPage(category, difficulty, searchText, (int) current, (int) size);
        
        // 转换为VO
        Page<DailyArticleVO> dailyArticleVOPage = new Page<>(current, size, dailyArticlePage.getTotal());
        List<DailyArticleVO> dailyArticleVOList = dailyArticlePage.getRecords().stream()
                .map(dailyArticle -> getDailyArticleVO(dailyArticle, request))
                .collect(Collectors.toList());
        dailyArticleVOPage.setRecords(dailyArticleVOList);
        
        return ResultUtils.success(dailyArticleVOPage);
    }

    /**
     * 获取今日文章
     *
     * @param request
     * @return
     */
    @GetMapping("/today")
    public BaseResponse<List<DailyArticleVO>> getTodayArticles(HttpServletRequest request) {
        List<DailyArticle> dailyArticleList = dailyArticleService.getTodayArticles();
        List<DailyArticleVO> dailyArticleVOList = dailyArticleList.stream()
                .map(dailyArticle -> getDailyArticleVO(dailyArticle, request))
                .collect(Collectors.toList());
        return ResultUtils.success(dailyArticleVOList);
    }

    /**
     * 获取指定日期的文章
     *
     * @param date
     * @param request
     * @return
     */
    @GetMapping("/date")
    public BaseResponse<List<DailyArticleVO>> getDailyArticlesByDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date, HttpServletRequest request) {
        if (date == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<DailyArticle> dailyArticleList = dailyArticleService.getDailyArticlesByDate(date);
        List<DailyArticleVO> dailyArticleVOList = dailyArticleList.stream()
                .map(dailyArticle -> getDailyArticleVO(dailyArticle, request))
                .collect(Collectors.toList());
        return ResultUtils.success(dailyArticleVOList);
    }

    /**
     * 根据分类获取文章列表
     *
     * @param category
     * @param limit
     * @param request
     * @return
     */
    @GetMapping("/category")
    public BaseResponse<List<DailyArticleVO>> listDailyArticlesByCategory(@RequestParam String category, 
                                                                         @RequestParam(defaultValue = "10") int limit, 
                                                                         HttpServletRequest request) {
        if (category == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<DailyArticle> dailyArticleList = dailyArticleService.listDailyArticlesByCategory(category, limit);
        List<DailyArticleVO> dailyArticleVOList = dailyArticleList.stream()
                .map(dailyArticle -> getDailyArticleVO(dailyArticle, request))
                .collect(Collectors.toList());
        return ResultUtils.success(dailyArticleVOList);
    }

    /**
     * 根据难度等级获取文章列表
     *
     * @param difficulty
     * @param limit
     * @param request
     * @return
     */
    @GetMapping("/difficulty")
    public BaseResponse<List<DailyArticleVO>> listDailyArticlesByDifficulty(@RequestParam Integer difficulty, 
                                                                           @RequestParam(defaultValue = "10") int limit, 
                                                                           HttpServletRequest request) {
        if (difficulty == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<DailyArticle> dailyArticleList = dailyArticleService.listDailyArticlesByDifficulty(difficulty, limit);
        List<DailyArticleVO> dailyArticleVOList = dailyArticleList.stream()
                .map(dailyArticle -> getDailyArticleVO(dailyArticle, request))
                .collect(Collectors.toList());
        return ResultUtils.success(dailyArticleVOList);
    }

    /**
     * 点赞文章
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/like")
    public BaseResponse<Boolean> likeArticle(@RequestParam Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 判断文章是否存在
        DailyArticle dailyArticle = dailyArticleService.getDailyArticleById(id);
        ThrowUtils.throwIf(dailyArticle == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 增加点赞次数
        boolean result = dailyArticleService.incrLikeCount(id);
        
        // 更新用户文章记录
        UserArticleRecord userArticleRecord = userArticleRecordService.getUserArticleRecord(loginUser.getId(), id);
        if (userArticleRecord != null) {
            userArticleRecord.setIsLiked(1);
            userArticleRecordService.updateById(userArticleRecord);
        } else {
            userArticleRecord = new UserArticleRecord();
            userArticleRecord.setUserId(loginUser.getId());
            userArticleRecord.setArticleId(id);
            userArticleRecord.setIsLiked(1);
            userArticleRecord.setReadProgress(0);
            userArticleRecord.setCreateTime(new Date());
            userArticleRecord.setUpdateTime(new Date());
            userArticleRecordService.save(userArticleRecord);
        }
        
        return ResultUtils.success(result);
    }

    /**
     * 获取文章VO
     *
     * @param dailyArticle
     * @param request
     * @return
     */
    private DailyArticleVO getDailyArticleVO(DailyArticle dailyArticle, HttpServletRequest request) {
        if (dailyArticle == null) {
            return null;
        }
        
        DailyArticleVO dailyArticleVO = new DailyArticleVO();
        BeanUtils.copyProperties(dailyArticle, dailyArticleVO);
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取用户阅读记录
            UserArticleRecord userArticleRecord = userArticleRecordService.getUserArticleRecord(loginUser.getId(), dailyArticle.getId());
            if (userArticleRecord != null) {
                // 设置用户阅读状态
                dailyArticleVO.setHasRead(userArticleRecord.getReadProgress() > 0);
                dailyArticleVO.setReadingProgress(userArticleRecord.getReadProgress());
                dailyArticleVO.setHasLiked(userArticleRecord.getIsLiked() == 1);
            } else {
                dailyArticleVO.setHasRead(false);
                dailyArticleVO.setReadingProgress(0);
                dailyArticleVO.setHasLiked(false);
            }
        } else {
            dailyArticleVO.setHasRead(false);
            dailyArticleVO.setReadingProgress(0);
            dailyArticleVO.setHasLiked(false);
        }
        
        return dailyArticleVO;
    }
} 