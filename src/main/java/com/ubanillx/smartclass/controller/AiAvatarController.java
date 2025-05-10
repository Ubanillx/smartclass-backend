package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.model.dto.DeleteRequest;
import com.ubanillx.smartclass.model.dto.aiavatar.AiAvatarAddRequest;
import com.ubanillx.smartclass.model.dto.aiavatar.AiAvatarQueryRequest;
import com.ubanillx.smartclass.model.dto.aiavatar.AiAvatarUpdateRequest;
import com.ubanillx.smartclass.model.entity.AiAvatar;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.AiAvatarVO;
import com.ubanillx.smartclass.service.AiAvatarService;
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
@RequestMapping("/ai_avatar")
@Slf4j
public class AiAvatarController {

    @Resource
    private AiAvatarService aiAvatarService;

    @Resource
    private UserService userService;

    /**
     * 创建AI分身
     *
     * @param aiAvatarAddRequest 添加请求
     * @param request 请求体
     * @return baseResponse
     */
    @PostMapping("/add")
    public BaseResponse<Long> addAiAvatar(@RequestBody AiAvatarAddRequest aiAvatarAddRequest, HttpServletRequest request) {
        if (aiAvatarAddRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        AiAvatar aiAvatar = new AiAvatar();
        BeanUtils.copyProperties(aiAvatarAddRequest, aiAvatar);
        // 校验
        User loginUser = userService.getLoginUser(request);
        aiAvatar.setCreatorId(loginUser.getId());
        boolean result = aiAvatarService.save(aiAvatar);
        if (!result) {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(aiAvatar.getId());
    }

    /**
     * 删除AI分身
     *
     * @param deleteRequest 删除请求
     * @param request 请求体
     * @return baseResponse
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteAiAvatar(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        AiAvatar oldAiAvatar = aiAvatarService.getById(id);
        if (oldAiAvatar == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldAiAvatar.getCreatorId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = aiAvatarService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新AI分身
     *
     * @param aiAvatarUpdateRequest ai更新请求
     * @param request 请求体
     * @return baseResponse
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateAiAvatar(@RequestBody AiAvatarUpdateRequest aiAvatarUpdateRequest,
            HttpServletRequest request) {
        if (aiAvatarUpdateRequest == null || aiAvatarUpdateRequest.getId() <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        
        // 参数校验
        User loginUser = userService.getLoginUser(request);
        long id = aiAvatarUpdateRequest.getId();
        
        // 判断是否存在
        AiAvatar oldAiAvatar = aiAvatarService.getById(id);
        if (oldAiAvatar == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 仅本人或管理员可修改
        if (!oldAiAvatar.getCreatorId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 复制非空字段，实现增量更新
        AiAvatar aiAvatar = new AiAvatar();
        aiAvatar.setId(id); // 设置ID
        
        // 增量更新每个字段，只有非null值才会被更新
        if (aiAvatarUpdateRequest.getName() != null) {
            aiAvatar.setName(aiAvatarUpdateRequest.getName());
        }
        
        if (aiAvatarUpdateRequest.getBaseUrl() != null) {
            aiAvatar.setBaseUrl(aiAvatarUpdateRequest.getBaseUrl());
        }
        
        if (aiAvatarUpdateRequest.getDescription() != null) {
            aiAvatar.setDescription(aiAvatarUpdateRequest.getDescription());
        }
        
        if (aiAvatarUpdateRequest.getAvatarImgUrl() != null) {
            aiAvatar.setAvatarImgUrl(aiAvatarUpdateRequest.getAvatarImgUrl());
        }
        
        if (aiAvatarUpdateRequest.getAvatarAuth() != null) {
            aiAvatar.setAvatarAuth(aiAvatarUpdateRequest.getAvatarAuth());
        }
        
        if (aiAvatarUpdateRequest.getTags() != null) {
            aiAvatar.setTags(aiAvatarUpdateRequest.getTags());
        }
        
        if (aiAvatarUpdateRequest.getPersonality() != null) {
            aiAvatar.setPersonality(aiAvatarUpdateRequest.getPersonality());
        }
        
        if (aiAvatarUpdateRequest.getAbilities() != null) {
            aiAvatar.setAbilities(aiAvatarUpdateRequest.getAbilities());
        }
        
        if (aiAvatarUpdateRequest.getIsPublic() != null) {
            aiAvatar.setIsPublic(aiAvatarUpdateRequest.getIsPublic());
        }
        
        if (aiAvatarUpdateRequest.getStatus() != null) {
            aiAvatar.setStatus(aiAvatarUpdateRequest.getStatus());
        }
        
        if (aiAvatarUpdateRequest.getSort() != null) {
            aiAvatar.setSort(aiAvatarUpdateRequest.getSort());
        }
        
        boolean result = aiAvatarService.updateById(aiAvatar);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取AI分身
     *
     * @param id ai分身id
     * @return baseResponse
     */
    @GetMapping("/get")
    public BaseResponse<AiAvatarVO> getAiAvatarById(long id) {
        if (id <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        AiAvatar aiAvatar = aiAvatarService.getById(id);
        if (aiAvatar == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        }
        AiAvatarVO aiAvatarVO = new AiAvatarVO();
        BeanUtils.copyProperties(aiAvatar, aiAvatarVO);
        return ResultUtils.success(aiAvatarVO);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param aiAvatarQueryRequest ai查询请求
     * @return baseResponse
     */
    @GetMapping("/list")
    public BaseResponse<List<AiAvatarVO>> listAiAvatar(AiAvatarQueryRequest aiAvatarQueryRequest) {
        if (aiAvatarQueryRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        AiAvatar aiAvatarQuery = new AiAvatar();
        BeanUtils.copyProperties(aiAvatarQueryRequest, aiAvatarQuery);
        
        QueryWrapper<AiAvatar> queryWrapper = new QueryWrapper<>(aiAvatarQuery);
        List<AiAvatar> aiAvatarList = aiAvatarService.list(queryWrapper);
        List<AiAvatarVO> aiAvatarVOList = aiAvatarList.stream().map(aiAvatar -> {
            AiAvatarVO aiAvatarVO = new AiAvatarVO();
            BeanUtils.copyProperties(aiAvatar, aiAvatarVO);
            return aiAvatarVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(aiAvatarVOList);
    }

    /**
     * 分页获取列表
     *
     * @param aiAvatarQueryRequest ai查询请求
     * @return baseResponse
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<AiAvatarVO>> listAiAvatarByPage(AiAvatarQueryRequest aiAvatarQueryRequest) {
        if (aiAvatarQueryRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        AiAvatar aiAvatarQuery = new AiAvatar();
        BeanUtils.copyProperties(aiAvatarQueryRequest, aiAvatarQuery);
        long current = aiAvatarQueryRequest.getCurrent();
        long size = aiAvatarQueryRequest.getPageSize();
        String sortField = aiAvatarQueryRequest.getSortField();
        String sortOrder = aiAvatarQueryRequest.getSortOrder();
        
        // 限制爬虫
        if (size > 50) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        
        QueryWrapper<AiAvatar> queryWrapper = new QueryWrapper<>(aiAvatarQuery);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals("ascend"), sortField);
        Page<AiAvatar> aiAvatarPage = aiAvatarService.page(new Page<>(current, size), queryWrapper);
        
        // 转换为VO
        Page<AiAvatarVO> aiAvatarVOPage = new Page<>(current, size, aiAvatarPage.getTotal());
        List<AiAvatarVO> aiAvatarVOList = aiAvatarPage.getRecords().stream().map(aiAvatar -> {
            AiAvatarVO aiAvatarVO = new AiAvatarVO();
            BeanUtils.copyProperties(aiAvatar, aiAvatarVO);
            return aiAvatarVO;
        }).collect(Collectors.toList());
        aiAvatarVOPage.setRecords(aiAvatarVOList);
        return ResultUtils.success(aiAvatarVOPage);
    }
} 