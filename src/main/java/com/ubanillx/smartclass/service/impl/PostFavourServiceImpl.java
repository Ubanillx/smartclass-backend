package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.PostFavourMapper;
import com.ubanillx.smartclass.mapper.PostMapper;
import com.ubanillx.smartclass.model.entity.Post;
import com.ubanillx.smartclass.model.entity.PostFavour;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.service.PostFavourService;
import com.ubanillx.smartclass.service.PostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 帖子收藏服务实现
 */
@Service
public class PostFavourServiceImpl extends ServiceImpl<PostFavourMapper, PostFavour>
        implements PostFavourService {

    @Resource
    private PostService postService;

    @Resource
    private PostMapper postMapper;

    @Resource
    private PostFavourMapper postFavourMapper;

    /**
     * 添加帖子收藏
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPostFavour(long postId, long userId) {
        // 判断帖子是否存在
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断是否已经收藏
        if (hasFavour(postId, userId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "已经收藏过该帖子");
        }
        // 添加收藏
        PostFavour postFavour = new PostFavour();
        postFavour.setUserId(userId);
        postFavour.setPostId(postId);
        boolean result = this.save(postFavour);
        if (result) {
            // 收藏数 + 1
            postMapper.updateFavourNum(postId, 1);
            return true;
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    /**
     * 取消帖子收藏
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelPostFavour(long postId, long userId) {
        // 判断帖子是否存在
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断是否已经收藏
        if (!hasFavour(postId, userId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "尚未收藏该帖子");
        }
        // 取消收藏
        QueryWrapper<PostFavour> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("postId", postId);
        queryWrapper.eq("userId", userId);
        boolean result = this.remove(queryWrapper);
        if (result) {
            // 收藏数 - 1
            postMapper.updateFavourNum(postId, -1);
            return true;
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    /**
     * 判断用户是否已收藏帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否已收藏
     */
    @Override
    public boolean hasFavour(long postId, long userId) {
        QueryWrapper<PostFavour> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("postId", postId);
        queryWrapper.eq("userId", userId);
        return this.count(queryWrapper) > 0;
    }

    /**
     * 分页获取用户收藏的帖子列表
     *
     * @param page         分页参数
     * @param queryWrapper 查询条件
     * @param favourUserId 收藏用户id
     * @return 帖子分页列表
     */
    @Override
    public Page<Post> listFavourPostByPage(IPage<Post> page, Wrapper<Post> queryWrapper, long favourUserId) {
        if (favourUserId <= 0) {
            return new Page<>();
        }
        return postFavourMapper.listFavourPostByPage(page, queryWrapper, favourUserId);
    }
}




