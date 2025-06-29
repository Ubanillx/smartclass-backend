package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.dto.postcommentreply.PostCommentReplyQueryRequest;
import com.ubanillx.smartclass.model.entity.PostCommentReply;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.PostCommentReplyVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 帖子评论回复服务
 */
public interface PostCommentReplyService extends IService<PostCommentReply> {
    
    /**
     * 校验评论回复
     * @param postCommentReply 评论回复
     * @param add 是否为添加操作
     */
    void validPostCommentReply(PostCommentReply postCommentReply, boolean add);
    
    /**
     * 获取查询条件
     * @param postCommentReplyQueryRequest 查询请求
     * @return 查询条件包装器
     */
    QueryWrapper<PostCommentReply> getQueryWrapper(PostCommentReplyQueryRequest postCommentReplyQueryRequest);
    
    /**
     * 获取评论回复VO
     * @param postCommentReply 评论回复
     * @param request 请求
     * @return 评论回复VO
     */
    PostCommentReplyVO getPostCommentReplyVO(PostCommentReply postCommentReply, HttpServletRequest request);
    
    /**
     * 分页获取评论回复VO
     * @param postCommentReplyPage 评论回复分页
     * @param request 请求
     * @return 评论回复VO分页
     */
    Page<PostCommentReplyVO> getPostCommentReplyVOPage(Page<PostCommentReply> postCommentReplyPage, 
                                                    HttpServletRequest request);
    
    /**
     * 添加评论回复
     * @param postCommentReply 评论回复
     * @param loginUser 登录用户
     * @return 是否成功
     */
    boolean addPostCommentReply(PostCommentReply postCommentReply, User loginUser);
    
    /**
     * 删除评论回复
     * @param id 评论回复ID
     * @param loginUser 登录用户
     * @return 是否成功
     */
    boolean deletePostCommentReply(Long id, User loginUser);
}
