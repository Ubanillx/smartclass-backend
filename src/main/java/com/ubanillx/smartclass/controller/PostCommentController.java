package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.DeleteRequest;
import com.ubanillx.smartclass.model.dto.postcomment.PostCommentAddRequest;
import com.ubanillx.smartclass.model.dto.postcomment.PostCommentQueryRequest;
import com.ubanillx.smartclass.model.entity.PostComment;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.PostCommentVO;
import com.ubanillx.smartclass.service.PostCommentService;
import com.ubanillx.smartclass.service.UserService;
import com.ubanillx.smartclass.utils.GeoIPUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子评论接口
 */
@RestController
@RequestMapping("/post-comments")
@Slf4j
public class PostCommentController {

    @Resource
    private PostCommentService postCommentService;

    @Resource
    private UserService userService;

    /**
     * 创建评论
     *
     * @param postCommentAddRequest 评论创建请求
     * @param request               HTTP请求
     * @return 评论ID
     */
    @PostMapping("")
    public BaseResponse<Long> addPostComment(@RequestBody PostCommentAddRequest postCommentAddRequest,
                                          HttpServletRequest request) {
        if (postCommentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        PostComment postComment = new PostComment();
        BeanUtils.copyProperties(postCommentAddRequest, postComment);
        
        // 校验
        postCommentService.validPostComment(postComment, true);
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 处理地理位置信息
        String ipAddress = postCommentAddRequest.getClientIp();
        if (ipAddress != null && !ipAddress.isEmpty()) {
            String[] location = GeoIPUtils.getLocationByIp(ipAddress);
            postComment.setCountry(location[0]);
            postComment.setCity(location[1]);
        }
        
        // 添加评论
        boolean result = postCommentService.addPostComment(postComment, loginUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return ResultUtils.success(postComment.getId());
    }

    /**
     * 删除评论
     *
     * @param id      评论ID
     * @param request HTTP请求
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deletePostComment(@PathVariable("id") long id,
                                               HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        
        boolean result = postCommentService.deletePostComment(id, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 根据帖子ID分页获取评论列表
     *
     * @param postCommentQueryRequest 查询请求
     * @param request                 HTTP请求
     * @return 评论分页
     */
    @GetMapping("/page")
    public BaseResponse<Page<PostCommentVO>> listPostCommentByPage(PostCommentQueryRequest postCommentQueryRequest,
                                                              HttpServletRequest request) {
        if (postCommentQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        long current = postCommentQueryRequest.getCurrent();
        long size = postCommentQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        
        Page<PostComment> postCommentPage = postCommentService.page(new Page<>(current, size),
                postCommentService.getQueryWrapper(postCommentQueryRequest));
        
        return ResultUtils.success(postCommentService.getPostCommentVOPage(postCommentPage, request));
    }

    /**
     * 根据ID获取评论
     *
     * @param id      评论ID
     * @param request HTTP请求
     * @return 评论
     */
    @GetMapping("/{id}")
    public BaseResponse<PostCommentVO> getPostCommentById(@PathVariable("id") long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        PostComment postComment = postCommentService.getById(id);
        if (postComment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        return ResultUtils.success(postCommentService.getPostCommentVO(postComment, request));
    }

    /**
     * 管理员删除评论
     *
     * @param id 评论ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}/admin")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> adminDeletePostComment(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        PostComment postComment = postCommentService.getById(id);
        if (postComment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        boolean result = postCommentService.removeById(id);
        return ResultUtils.success(result);
    }
} 