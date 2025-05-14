package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.entity.PostFavour;
import com.ubanillx.smartclass.model.entity.Post;

/**
 * 帖子收藏服务
*/
public interface PostFavourService extends IService<PostFavour> {

    /**
     * 添加帖子收藏
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean addPostFavour(long postId, long userId);

    /**
     * 取消帖子收藏
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean cancelPostFavour(long postId, long userId);

    /**
     * 判断用户是否已收藏帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否已收藏
     */
    boolean hasFavour(long postId, long userId);

    /**
     * 分页获取用户收藏的帖子列表
     *
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    Page<Post> listFavourPostByPage(IPage<Post> page, Wrapper<Post> queryWrapper,
                                    long favourUserId);
}
