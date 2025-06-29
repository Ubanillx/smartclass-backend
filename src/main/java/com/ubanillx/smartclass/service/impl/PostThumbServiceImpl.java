package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.PostMapper;
import com.ubanillx.smartclass.mapper.PostThumbMapper;
import com.ubanillx.smartclass.model.entity.Post;
import com.ubanillx.smartclass.model.entity.PostThumb;
import com.ubanillx.smartclass.service.PostService;
import com.ubanillx.smartclass.service.PostThumbService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 帖子点赞服务实现
 */
@Service
public class PostThumbServiceImpl extends ServiceImpl<PostThumbMapper, PostThumb>
        implements PostThumbService {

    @Resource
    private PostService postService;

    @Resource
    private PostMapper postMapper;

    /**
     * 添加帖子点赞
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPostThumb(long postId, long userId) {
        // 判断帖子是否存在
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断是否已经点赞
        if (hasThumb(postId, userId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "已经点赞过该帖子");
        }
        // 添加点赞
        PostThumb postThumb = new PostThumb();
        postThumb.setUserId(userId);
        postThumb.setPostId(postId);
        boolean result = this.save(postThumb);
        if (result) {
            // 点赞数 + 1
            postMapper.updateThumbNum(postId, 1);
            return true;
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    /**
     * 取消帖子点赞
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelPostThumb(long postId, long userId) {
        // 判断帖子是否存在
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断是否已经点赞
        if (!hasThumb(postId, userId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "尚未点赞该帖子");
        }
        // 取消点赞
        QueryWrapper<PostThumb> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("postId", postId);
        queryWrapper.eq("userId", userId);
        boolean result = this.remove(queryWrapper);
        if (result) {
            // 点赞数 - 1
            postMapper.updateThumbNum(postId, -1);
            return true;
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    /**
     * 判断用户是否已点赞帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    @Override
    public boolean hasThumb(long postId, long userId) {
        QueryWrapper<PostThumb> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("postId", postId);
        queryWrapper.eq("userId", userId);
        return this.count(queryWrapper) > 0;
    }
} 