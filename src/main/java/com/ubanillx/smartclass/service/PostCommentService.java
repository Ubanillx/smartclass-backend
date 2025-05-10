package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.dto.postcomment.PostCommentQueryRequest;
import com.ubanillx.smartclass.model.entity.PostComment;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.PostCommentVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子评论服务
 */
public interface PostCommentService extends IService<PostComment> {
    
    /**
     * 校验评论
     * @param postComment 帖子评论
     * @param add 是否为添加操作
     */
    void validPostComment(PostComment postComment, boolean add);
    
    /**
     * 获取查询条件
     * @param postCommentQueryRequest 查询请求
     * @return 查询条件包装器
     */
    QueryWrapper<PostComment> getQueryWrapper(PostCommentQueryRequest postCommentQueryRequest);
    
    /**
     * 获取帖子评论VO
     * @param postComment 帖子评论
     * @param request 请求
     * @return 帖子评论VO
     */
    PostCommentVO getPostCommentVO(PostComment postComment, HttpServletRequest request);
    
    /**
     * 分页获取帖子评论VO
     * @param postCommentPage 帖子评论分页
     * @param request 请求
     * @return 帖子评论VO分页
     */
    Page<PostCommentVO> getPostCommentVOPage(Page<PostComment> postCommentPage, HttpServletRequest request);
    
    /**
     * 添加评论
     * @param postComment 帖子评论
     * @param loginUser 登录用户
     * @return 是否成功
     */
    boolean addPostComment(PostComment postComment, User loginUser);
    
    /**
     * 删除评论
     * @param id 评论ID
     * @param loginUser 登录用户
     * @return 是否成功
     */
    boolean deletePostComment(Long id, User loginUser);
} 