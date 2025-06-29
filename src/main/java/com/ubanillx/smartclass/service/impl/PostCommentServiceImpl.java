package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.PostCommentMapper;
import com.ubanillx.smartclass.mapper.PostMapper;
import com.ubanillx.smartclass.model.dto.postcomment.PostCommentQueryRequest;
import com.ubanillx.smartclass.model.entity.Post;
import com.ubanillx.smartclass.model.entity.PostComment;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.PostCommentVO;
import com.ubanillx.smartclass.model.vo.UserVO;
import com.ubanillx.smartclass.service.PostCommentService;
import com.ubanillx.smartclass.service.UserService;
import com.ubanillx.smartclass.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 帖子评论服务实现类
 */
@Service
@Slf4j
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment>
        implements PostCommentService {

    @Resource
    private UserService userService;

    @Resource
    private PostMapper postMapper;

    @Override
    public void validPostComment(PostComment postComment, boolean add) {
        if (postComment == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 创建时，参数不能为空
        if (add) {
            if (postComment.getPostId() == null || postComment.getPostId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子不存在");
            }
            if (StringUtils.isBlank(postComment.getContent())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容不能为空");
            }
            if (postComment.getContent().length() > 1000) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容过长");
            }
        }
    }

    @Override
    public QueryWrapper<PostComment> getQueryWrapper(PostCommentQueryRequest postCommentQueryRequest) {
        if (postCommentQueryRequest == null) {
            return new QueryWrapper<>();
        }

        Long postId = postCommentQueryRequest.getPostId();
        Long userId = postCommentQueryRequest.getUserId();
        String content = postCommentQueryRequest.getContent();
        String sortField = postCommentQueryRequest.getSortField();
        String sortOrder = postCommentQueryRequest.getSortOrder();

        QueryWrapper<PostComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(postId != null && postId > 0, "postId", postId);
        queryWrapper.eq(userId != null && userId > 0, "userId", userId);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), 
                SqlUtils.SORT_ORDER_ASC.equals(sortOrder),
                sortField);
        return queryWrapper;
    }

    @Override
    public PostCommentVO getPostCommentVO(PostComment postComment, HttpServletRequest request) {
        if (postComment == null) {
            return null;
        }

        PostCommentVO postCommentVO = new PostCommentVO();
        BeanUtils.copyProperties(postComment, postCommentVO);

        // 添加评论者信息
        Long userId = postComment.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        postCommentVO.setUserVO(userVO);

        return postCommentVO;
    }

    @Override
    public Page<PostCommentVO> getPostCommentVOPage(Page<PostComment> postCommentPage, HttpServletRequest request) {
        List<PostComment> postCommentList = postCommentPage.getRecords();
        Page<PostCommentVO> postCommentVOPage = new Page<>(postCommentPage.getCurrent(), postCommentPage.getSize(), postCommentPage.getTotal());
        if (postCommentList.isEmpty()) {
            return postCommentVOPage;
        }

        // 填充信息
        List<PostCommentVO> postCommentVOList = postCommentList.stream()
                .map(postComment -> getPostCommentVO(postComment, request))
                .collect(Collectors.toList());
        postCommentVOPage.setRecords(postCommentVOList);
        return postCommentVOPage;
    }

    @Override
    public boolean addPostComment(PostComment postComment, User loginUser) {
        // 校验帖子是否存在
        Long postId = postComment.getPostId();
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        }

        // 填充评论者ID
        postComment.setUserId(loginUser.getId());

        boolean result = this.save(postComment);
        
        // 更新帖子评论数，使用SQL直接更新以避免并发问题
        if (result) {
            try {
                // 添加评论数量
                postMapper.updateCommentNum(postId, 1);
            } catch (Exception e) {
                log.error("更新帖子评论数失败, postId=" + postId, e);
                // 不影响评论添加的主要业务
            }
        }
        
        return result;
    }

    @Override
    public boolean deletePostComment(Long id, User loginUser) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 判断评论是否存在
        PostComment postComment = this.getById(id);
        if (postComment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 只有自己或管理员可以删除
        if (!postComment.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        boolean result = this.removeById(id);
        
        // 更新帖子评论数，使用SQL直接更新以避免并发问题
        if (result) {
            try {
                Long postId = postComment.getPostId();
                // 减少评论数量
                postMapper.updateCommentNum(postId, -1);
            } catch (Exception e) {
                log.error("更新帖子评论数失败, id=" + id, e);
                // 不影响评论删除的主要业务
            }
        }
        
        return result;
    }
} 