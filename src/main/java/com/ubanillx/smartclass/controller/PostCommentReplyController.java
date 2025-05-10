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
import com.ubanillx.smartclass.model.dto.postcommentreply.PostCommentReplyAddRequest;
import com.ubanillx.smartclass.model.dto.postcommentreply.PostCommentReplyQueryRequest;
import com.ubanillx.smartclass.model.entity.PostCommentReply;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.PostCommentReplyVO;
import com.ubanillx.smartclass.service.PostCommentReplyService;
import com.ubanillx.smartclass.service.UserService;
import com.ubanillx.smartclass.utils.GeoIPUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子评论回复接口
 */
@RestController
@RequestMapping("/post_comment_reply")
@Slf4j
public class PostCommentReplyController {

    @Resource
    private PostCommentReplyService postCommentReplyService;

    @Resource
    private UserService userService;

    /**
     * 创建评论回复
     *
     * @param postCommentReplyAddRequest 评论回复创建请求
     * @param request                   HTTP请求
     * @return 回复ID
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPostCommentReply(@RequestBody PostCommentReplyAddRequest postCommentReplyAddRequest,
                                          HttpServletRequest request) {
        if (postCommentReplyAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        PostCommentReply postCommentReply = new PostCommentReply();
        BeanUtils.copyProperties(postCommentReplyAddRequest, postCommentReply);
        
        // 校验
        postCommentReplyService.validPostCommentReply(postCommentReply, true);
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 处理地理位置信息
        String ipAddress = postCommentReplyAddRequest.getClientIp();
        if (ipAddress != null && !ipAddress.isEmpty()) {
            String[] location = GeoIPUtils.getLocationByIp(ipAddress);
            postCommentReply.setCountry(location[0]);
            postCommentReply.setCity(location[1]);
        }
        
        // 添加评论回复
        boolean result = postCommentReplyService.addPostCommentReply(postCommentReply, loginUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        
        return ResultUtils.success(postCommentReply.getId());
    }

    /**
     * 删除评论回复
     *
     * @param deleteRequest 删除请求
     * @param request       HTTP请求
     * @return 是否成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePostCommentReply(@RequestBody DeleteRequest deleteRequest,
                                               HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        
        boolean result = postCommentReplyService.deletePostCommentReply(id, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 根据评论ID分页获取回复列表
     *
     * @param postCommentReplyQueryRequest 查询请求
     * @param request                     HTTP请求
     * @return 回复分页
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<PostCommentReplyVO>> listPostCommentReplyByPage(@RequestBody PostCommentReplyQueryRequest postCommentReplyQueryRequest,
                                                              HttpServletRequest request) {
        if (postCommentReplyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        long current = postCommentReplyQueryRequest.getCurrent();
        long size = postCommentReplyQueryRequest.getPageSize();
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        
        Page<PostCommentReply> postCommentReplyPage = postCommentReplyService.page(new Page<>(current, size),
                postCommentReplyService.getQueryWrapper(postCommentReplyQueryRequest));
        
        return ResultUtils.success(postCommentReplyService.getPostCommentReplyVOPage(postCommentReplyPage, request));
    }

    /**
     * 根据ID获取评论回复
     *
     * @param id      回复ID
     * @param request HTTP请求
     * @return 回复
     */
    @GetMapping("/get")
    public BaseResponse<PostCommentReplyVO> getPostCommentReplyById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        PostCommentReply postCommentReply = postCommentReplyService.getById(id);
        if (postCommentReply == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        return ResultUtils.success(postCommentReplyService.getPostCommentReplyVO(postCommentReply, request));
    }

    /**
     * 管理员删除评论回复
     *
     * @param deleteRequest 删除请求
     * @return 是否成功
     */
    @PostMapping("/admin/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> adminDeletePostCommentReply(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        long id = deleteRequest.getId();
        PostCommentReply postCommentReply = postCommentReplyService.getById(id);
        if (postCommentReply == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        boolean result = postCommentReplyService.removeById(id);
        return ResultUtils.success(result);
    }
} 