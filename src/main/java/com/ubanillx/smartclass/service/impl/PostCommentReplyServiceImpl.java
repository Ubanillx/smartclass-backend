package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.PostCommentMapper;
import com.ubanillx.smartclass.mapper.PostCommentReplyMapper;
import com.ubanillx.smartclass.mapper.PostMapper;
import com.ubanillx.smartclass.model.dto.postcommentreply.PostCommentReplyQueryRequest;
import com.ubanillx.smartclass.model.entity.Post;
import com.ubanillx.smartclass.model.entity.PostComment;
import com.ubanillx.smartclass.model.entity.PostCommentReply;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.PostCommentReplyVO;
import com.ubanillx.smartclass.model.vo.UserVO;
import com.ubanillx.smartclass.service.PostCommentReplyService;
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
 * 帖子评论回复服务实现类
 */
@Service
@Slf4j
public class PostCommentReplyServiceImpl extends ServiceImpl<PostCommentReplyMapper, PostCommentReply>
        implements PostCommentReplyService {

    @Resource
    private UserService userService;

    @Resource
    private PostMapper postMapper;

    @Resource
    private PostCommentMapper postCommentMapper;

    @Override
    public void validPostCommentReply(PostCommentReply postCommentReply, boolean add) {
        if (postCommentReply == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 创建时，参数不能为空
        if (add) {
            if (postCommentReply.getPostId() == null || postCommentReply.getPostId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子不存在");
            }
            if (postCommentReply.getCommentId() == null || postCommentReply.getCommentId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论不存在");
            }
            if (StringUtils.isBlank(postCommentReply.getContent())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "回复内容不能为空");
            }
            if (postCommentReply.getContent().length() > 1000) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "回复内容过长");
            }
        }
    }

    @Override
    public QueryWrapper<PostCommentReply> getQueryWrapper(PostCommentReplyQueryRequest postCommentReplyQueryRequest) {
        if (postCommentReplyQueryRequest == null) {
            return new QueryWrapper<>();
        }

        Long postId = postCommentReplyQueryRequest.getPostId();
        Long commentId = postCommentReplyQueryRequest.getCommentId();
        Long userId = postCommentReplyQueryRequest.getUserId();
        String content = postCommentReplyQueryRequest.getContent();
        String sortField = postCommentReplyQueryRequest.getSortField();
        String sortOrder = postCommentReplyQueryRequest.getSortOrder();

        QueryWrapper<PostCommentReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(postId != null && postId > 0, "postId", postId);
        queryWrapper.eq(commentId != null && commentId > 0, "commentId", commentId);
        queryWrapper.eq(userId != null && userId > 0, "userId", userId);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                SqlUtils.SORT_ORDER_ASC.equals(sortOrder),
                sortField);
        return queryWrapper;
    }

    @Override
    public PostCommentReplyVO getPostCommentReplyVO(PostCommentReply postCommentReply, HttpServletRequest request) {
        if (postCommentReply == null) {
            return null;
        }

        PostCommentReplyVO postCommentReplyVO = new PostCommentReplyVO();
        BeanUtils.copyProperties(postCommentReply, postCommentReplyVO);

        // 添加回复者信息
        Long userId = postCommentReply.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        postCommentReplyVO.setUserVO(userVO);

        return postCommentReplyVO;
    }

    @Override
    public Page<PostCommentReplyVO> getPostCommentReplyVOPage(Page<PostCommentReply> postCommentReplyPage, HttpServletRequest request) {
        List<PostCommentReply> postCommentReplyList = postCommentReplyPage.getRecords();
        Page<PostCommentReplyVO> postCommentReplyVOPage = new Page<>(postCommentReplyPage.getCurrent(), postCommentReplyPage.getSize(), postCommentReplyPage.getTotal());
        if (postCommentReplyList.isEmpty()) {
            return postCommentReplyVOPage;
        }

        // 填充信息
        List<PostCommentReplyVO> postCommentReplyVOList = postCommentReplyList.stream()
                .map(postCommentReply -> getPostCommentReplyVO(postCommentReply, request))
                .collect(Collectors.toList());
        postCommentReplyVOPage.setRecords(postCommentReplyVOList);
        return postCommentReplyVOPage;
    }

    @Override
    public boolean addPostCommentReply(PostCommentReply postCommentReply, User loginUser) {
        // 校验帖子是否存在
        Long postId = postCommentReply.getPostId();
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        }

        // 校验评论是否存在
        Long commentId = postCommentReply.getCommentId();
        PostComment comment = postCommentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评论不存在");
        }

        // 确保评论属于该帖子
        if (!comment.getPostId().equals(postId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论不属于该帖子");
        }

        // 填充回复者ID
        postCommentReply.setUserId(loginUser.getId());

        return this.save(postCommentReply);
    }

    @Override
    public boolean deletePostCommentReply(Long id, User loginUser) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 判断回复是否存在
        PostCommentReply postCommentReply = this.getById(id);
        if (postCommentReply == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 只有自己或管理员可以删除
        if (!postCommentReply.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        return this.removeById(id);
    }
} 