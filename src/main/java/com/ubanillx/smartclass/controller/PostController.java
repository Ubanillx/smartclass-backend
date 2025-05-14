package com.ubanillx.smartclass.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.post.PostAddRequest;
import com.ubanillx.smartclass.model.dto.post.PostEditRequest;
import com.ubanillx.smartclass.model.dto.post.PostQueryRequest;
import com.ubanillx.smartclass.model.dto.post.PostUpdateRequest;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.PostVO;
import com.ubanillx.smartclass.service.PostService;
import com.ubanillx.smartclass.service.UserService;
import com.ubanillx.smartclass.utils.GeoIPUtils;
import com.ubanillx.smartclass.utils.IdGenerator;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ubanillx.smartclass.model.entity.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.lang3.StringUtils;

/**
 * 帖子接口
*/
@RestController
@RequestMapping("/posts")
@Slf4j
public class PostController {

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建帖子
     *
     * @param postAddRequest 帖子创建请求，包含标题、内容、标签等信息
     * @param request HTTP请求，用于获取当前登录用户
     * @return 创建成功的帖子ID
     */
    @PostMapping("")
    public BaseResponse<Long> addPost(@RequestBody PostAddRequest postAddRequest, HttpServletRequest request) {
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = new Post();
        BeanUtils.copyProperties(postAddRequest, post);
        List<String> tags = postAddRequest.getTags();
        if (tags != null) {
            post.setTags(JSONUtil.toJsonStr(tags));
        }
        postService.validPost(post, true);
        User loginUser = userService.getLoginUser(request);
        post.setUserId(loginUser.getId());
        post.setFavourNum(0);
        post.setThumbNum(0);
        post.setCommentNum(0);
        
        // 使用前端传入的IP地址
        String ipAddress = postAddRequest.getClientIp();
        
        // 根据IP地址获取地理位置信息
        String[] location = GeoIPUtils.getLocationByIp(ipAddress);
        post.setCountry(location[0]);
        post.setCity(location[1]);
        
        // 生成8位数字ID
        String eightDigitId = IdGenerator.generateRandomEightDigitId();
        // 转换为Long类型
        Long postId = Long.parseLong(eightDigitId);
        post.setId(postId);
        
        boolean result = postService.savePost(post);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newPostId = post.getId();
        return ResultUtils.success(newPostId);
    }

    /**
     * 删除帖子
     *
     * @param id 要删除的帖子ID
     * @param request HTTP请求，用于获取当前登录用户
     * @return 删除结果，true表示删除成功
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deletePost(@PathVariable Long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        // 判断是否存在
        Post oldPost = postService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldPost.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = postService.deletePost(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新帖子（仅管理员）
     *
     * @param id 要更新的帖子ID
     * @param postUpdateRequest 帖子更新请求，包含标题、内容、标签等需要更新的信息
     * @return 更新结果，true表示更新成功
     */
    @PutMapping("/{id}/admin")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePost(@PathVariable Long id, @RequestBody PostUpdateRequest postUpdateRequest) {
        if (postUpdateRequest == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = new Post();
        BeanUtils.copyProperties(postUpdateRequest, post);
        post.setId(id);
        List<String> tags = postUpdateRequest.getTags();
        if (tags != null) {
            post.setTags(JSONUtil.toJsonStr(tags));
        }
        // 参数校验
        postService.validPost(post, false);
        // 判断是否存在
        Post oldPost = postService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = postService.updatePost(post);
        return ResultUtils.success(result);
    }

    /**
     * 根据ID获取帖子详情
     *
     * @param id 帖子ID
     * @param request HTTP请求，用于获取当前登录用户信息
     * @return 帖子视图对象，包含帖子详情和作者信息
     */
    @GetMapping("/{id}")
    public BaseResponse<PostVO> getPostVOById(@PathVariable Long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = postService.getById(id);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(postService.getPostVO(post, request));
    }

    /**
     * 分页获取帖子列表（仅管理员）
     *
     * @param postQueryRequest 查询条件，包含页码、每页大小、标题关键词等筛选条件
     * @return 帖子分页列表
     */
    @GetMapping("/admin/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Post>> listPostByPage(PostQueryRequest postQueryRequest) {
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        Page<Post> postPage = postService.page(new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest));
        return ResultUtils.success(postPage);
    }

    /**
     * 分页获取帖子列表（所有用户可见）
     *
     * @param postQueryRequest 查询条件，包含页码、每页大小、标题关键词等筛选条件
     * @param request HTTP请求，用于获取当前登录用户信息
     * @return 帖子视图对象的分页列表
     */
    @GetMapping("/page")
    public BaseResponse<Page<PostVO>> listPostVOByPage(PostQueryRequest postQueryRequest,
            HttpServletRequest request) {
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postService.page(new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest));
        return ResultUtils.success(postService.getPostVOPage(postPage, request));
    }

    /**
     * 分页获取当前用户创建的帖子列表
     *
     * @param postQueryRequest 查询条件，包含页码、每页大小、标题关键词等筛选条件
     * @param request HTTP请求，用于获取当前登录用户信息
     * @return 当前用户的帖子视图对象分页列表
     */
    @GetMapping("/me/page")
    public BaseResponse<Page<PostVO>> listMyPostVOByPage(PostQueryRequest postQueryRequest,
            HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        postQueryRequest.setUserId(loginUser.getId());
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postService.page(new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest));
        return ResultUtils.success(postService.getPostVOPage(postPage, request));
    }

    // endregion

    /**
     * 分页搜索帖子（从ES查询）
     *
     * @param searchText 搜索关键词
     * @param request HTTP请求，用于获取当前登录用户信息
     * @return 符合搜索条件的帖子视图对象分页列表
     */
    @GetMapping("/search/page")
    public BaseResponse<Page<PostVO>> searchPostVOByPage(String searchText, HttpServletRequest request) {
        if (StringUtils.isBlank(searchText)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "搜索词不能为空");
        }
        Page<Post> postPage = postService.searchFromEs(searchText);
        return ResultUtils.success(postService.getPostVOPage(postPage, request));
    }

    /**
     * 编辑帖子（用户）
     *
     * @param id 要编辑的帖子ID
     * @param postEditRequest 帖子编辑请求，包含标题、内容、标签等需要更新的信息
     * @param request HTTP请求，用于获取当前登录用户
     * @return 编辑结果，true表示编辑成功
     */
    @PutMapping("/{id}")
    public BaseResponse<Boolean> editPost(@PathVariable Long id, @RequestBody PostEditRequest postEditRequest, HttpServletRequest request) {
        if (postEditRequest == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = new Post();
        BeanUtils.copyProperties(postEditRequest, post);
        post.setId(id);
        List<String> tags = postEditRequest.getTags();
        if (tags != null) {
            post.setTags(JSONUtil.toJsonStr(tags));
        }
        // 参数校验
        postService.validPost(post, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        Post oldPost = postService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldPost.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 使用前端传入的IP地址
        String ipAddress = postEditRequest.getClientIp();
        
        // 根据IP地址获取地理位置信息
        String[] location = GeoIPUtils.getLocationByIp(ipAddress);
        post.setCountry(location[0]);
        post.setCity(location[1]);
        
        boolean result = postService.updatePost(post);
        return ResultUtils.success(result);
    }
}
