package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.DeleteRequest;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.aiavatar.AiAvatarAddRequest;
import com.ubanillx.smartclass.model.dto.aiavatar.AiAvatarQueryRequest;
import com.ubanillx.smartclass.model.dto.aiavatar.AiAvatarUpdateRequest;
import com.ubanillx.smartclass.model.entity.AiAvatar;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.UserAiAvatar;
import com.ubanillx.smartclass.model.vo.AiAvatarVO;
import com.ubanillx.smartclass.service.AiAvatarService;
import com.ubanillx.smartclass.service.UserAiAvatarService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI分身接口
 */
@RestController
@RequestMapping("/ai/avatar")
@Slf4j
public class AiAvatarController {

    @Resource
    private AiAvatarService aiAvatarService;

    @Resource
    private UserAiAvatarService userAiAvatarService;

    @Resource
    private UserService userService;

    /**
     * 创建AI分身
     *
     * @param aiAvatarAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addAiAvatar(@RequestBody AiAvatarAddRequest aiAvatarAddRequest, HttpServletRequest request) {
        if (aiAvatarAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AiAvatar aiAvatar = new AiAvatar();
        BeanUtils.copyProperties(aiAvatarAddRequest, aiAvatar);
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        aiAvatar.setCreatorId(loginUser.getId());
        aiAvatar.setCreatorType("admin");
        
        // 默认值
        if (aiAvatar.getIsPublic() == null) {
            aiAvatar.setIsPublic(1);
        }
        if (aiAvatar.getStatus() == null) {
            aiAvatar.setStatus(1);
        }
        if (aiAvatar.getSort() == null) {
            aiAvatar.setSort(0);
        }
        
        // 校验
        aiAvatarService.validAiAvatar(aiAvatar, true);
        
        long id = aiAvatarService.addAiAvatar(aiAvatar);
        return ResultUtils.success(id);
    }

    /**
     * 删除AI分身
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteAiAvatar(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        
        // 判断是否存在
        AiAvatar oldAiAvatar = aiAvatarService.getById(id);
        ThrowUtils.throwIf(oldAiAvatar == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可删除
        if (!oldAiAvatar.getCreatorId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        boolean b = aiAvatarService.deleteAiAvatar(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新AI分身
     *
     * @param aiAvatarUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateAiAvatar(@RequestBody AiAvatarUpdateRequest aiAvatarUpdateRequest, HttpServletRequest request) {
        if (aiAvatarUpdateRequest == null || aiAvatarUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AiAvatar aiAvatar = new AiAvatar();
        BeanUtils.copyProperties(aiAvatarUpdateRequest, aiAvatar);
        
        // 参数校验
        aiAvatarService.validAiAvatar(aiAvatar, false);
        
        // 判断是否存在
        long id = aiAvatarUpdateRequest.getId();
        AiAvatar oldAiAvatar = aiAvatarService.getById(id);
        ThrowUtils.throwIf(oldAiAvatar == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 仅本人或管理员可修改
        User user = userService.getLoginUser(request);
        if (!oldAiAvatar.getCreatorId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        boolean result = aiAvatarService.updateAiAvatar(aiAvatar);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取AI分身
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<AiAvatarVO> getAiAvatarById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AiAvatar aiAvatar = aiAvatarService.getAiAvatarById(id);
        if (aiAvatar == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        AiAvatarVO aiAvatarVO = getAiAvatarVO(aiAvatar, request);
        return ResultUtils.success(aiAvatarVO);
    }

    /**
     * 分页获取AI分身列表
     *
     * @param aiAvatarQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<AiAvatarVO>> listAiAvatarByPage(@RequestBody AiAvatarQueryRequest aiAvatarQueryRequest, HttpServletRequest request) {
        if (aiAvatarQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 默认查询公开且启用的AI分身
        if (aiAvatarQueryRequest.getIsPublic() == null) {
            aiAvatarQueryRequest.setIsPublic(1);
        }
        if (aiAvatarQueryRequest.getStatus() == null) {
            aiAvatarQueryRequest.setStatus(1);
        }
        
        // 管理员可以查看所有AI分身
        if (userService.isAdmin(request)) {
            aiAvatarQueryRequest.setIsPublic(null);
            aiAvatarQueryRequest.setStatus(null);
        }
        
        // 获取分页参数
        long current = aiAvatarQueryRequest.getCurrent();
        long size = aiAvatarQueryRequest.getPageSize();
        
        // 构建查询条件
        String category = aiAvatarQueryRequest.getCategory();
        String searchText = aiAvatarQueryRequest.getSearchText();
        
        // 分页查询
        Page<AiAvatar> aiAvatarPage = aiAvatarService.listAiAvatarByPage(category, searchText, (int) current, (int) size);
        
        // 转换为VO
        Page<AiAvatarVO> aiAvatarVOPage = new Page<>(current, size, aiAvatarPage.getTotal());
        List<AiAvatarVO> aiAvatarVOList = aiAvatarPage.getRecords().stream()
                .map(aiAvatar -> getAiAvatarVO(aiAvatar, request))
                .collect(Collectors.toList());
        aiAvatarVOPage.setRecords(aiAvatarVOList);
        
        return ResultUtils.success(aiAvatarVOPage);
    }

    /**
     * 获取热门AI分身列表
     *
     * @param limit
     * @param request
     * @return
     */
    @GetMapping("/popular")
    public BaseResponse<List<AiAvatarVO>> listPopularAiAvatars(@RequestParam(defaultValue = "10") int limit, HttpServletRequest request) {
        List<AiAvatar> aiAvatarList = aiAvatarService.listPopularAiAvatars(limit);
        List<AiAvatarVO> aiAvatarVOList = aiAvatarList.stream()
                .map(aiAvatar -> getAiAvatarVO(aiAvatar, request))
                .collect(Collectors.toList());
        return ResultUtils.success(aiAvatarVOList);
    }

    /**
     * 根据分类获取AI分身列表
     *
     * @param category
     * @param limit
     * @param request
     * @return
     */
    @GetMapping("/category")
    public BaseResponse<List<AiAvatarVO>> listAiAvatarsByCategory(@RequestParam String category, 
                                                                 @RequestParam(defaultValue = "10") int limit, 
                                                                 HttpServletRequest request) {
        if (StringUtils.isBlank(category)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<AiAvatar> aiAvatarList = aiAvatarService.listAiAvatarsByCategory(category, limit);
        List<AiAvatarVO> aiAvatarVOList = aiAvatarList.stream()
                .map(aiAvatar -> getAiAvatarVO(aiAvatar, request))
                .collect(Collectors.toList());
        return ResultUtils.success(aiAvatarVOList);
    }

    /**
     * 获取AI分身VO
     *
     * @param aiAvatar
     * @param request
     * @return
     */
    private AiAvatarVO getAiAvatarVO(AiAvatar aiAvatar, HttpServletRequest request) {
        if (aiAvatar == null) {
            return null;
        }
        
        AiAvatarVO aiAvatarVO = new AiAvatarVO();
        BeanUtils.copyProperties(aiAvatar, aiAvatarVO);
        
        // 脱敏，不返回提示词模板
        aiAvatarVO.setPromptTemplate(null);
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取用户与AI分身的关联信息
            UserAiAvatar userAiAvatar = userAiAvatarService.getUserAiAvatarRelation(loginUser.getId(), aiAvatar.getId());
            if (userAiAvatar != null) {
                // 设置用户收藏状态
                aiAvatarVO.setIsFavorite(userAiAvatar.getIsFavorite() == 1);
                // 设置用户使用次数
                aiAvatarVO.setUserUseCount(userAiAvatar.getUseCount());
                // 设置用户评分
                aiAvatarVO.setUserRating(userAiAvatar.getUserRating());
                // 设置用户自定义设置
                aiAvatarVO.setCustomSettings(userAiAvatar.getCustomSettings());
            }
        }
        
        return aiAvatarVO;
    }
} 