package com.ubanillx.smartclass.controller;

import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.model.dto.postthumb.PostThumbAddRequest;
import com.ubanillx.smartclass.model.entity.PostThumb;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.service.PostThumbService;
import com.ubanillx.smartclass.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 帖子点赞接口
*/
@RestController
@RequestMapping("/post_thumb")
@Slf4j
public class PostThumbController {

    @Resource
    private PostThumbService postThumbService;

    @Resource
    private UserService userService;

    /**
     * 点赞 / 取消点赞
     *
     * @param postThumbAddRequest
     * @param request
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest,
            HttpServletRequest request) {
        if (postThumbAddRequest == null || postThumbAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long postId = postThumbAddRequest.getPostId();
        int result = postThumbService.doPostThumb(postId, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 判断当前登录用户是否已点赞
     * 
     * @param postId 帖子id
     * @param request HTTP请求
     * @return 是否已点赞
     */
    @PostMapping("/has_thumb")
    public BaseResponse<Boolean> hasThumb(Long postId, HttpServletRequest request) {
        if (postId == null || postId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能判断
        final User loginUser = userService.getLoginUser(request);
        long userId = loginUser.getId();
        // 判断是否已点赞
        boolean result = postThumbService.lambdaQuery()
                .eq(PostThumb::getPostId, postId)
                .eq(PostThumb::getUserId, userId)
                .count() > 0;
        return ResultUtils.success(result);
    }

}
