package com.ubanillx.smartclass.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.constant.CommonConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.UserLevelMapper;
import com.ubanillx.smartclass.model.dto.userlevel.UserLevelQueryRequest;
import com.ubanillx.smartclass.model.entity.UserLevel;
import com.ubanillx.smartclass.model.vo.UserLevelVO;
import com.ubanillx.smartclass.service.UserLevelService;
import com.ubanillx.smartclass.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户等级服务实现
 */
@Service
@Slf4j
public class UserLevelServiceImpl extends ServiceImpl<UserLevelMapper, UserLevel> implements UserLevelService {

    @Override
    public void validUserLevel(UserLevel userLevel, boolean add) {
        if (userLevel == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        Integer level = userLevel.getLevel();
        String levelName = userLevel.getLevelName();
        Integer minExperience = userLevel.getMinExperience();
        Integer maxExperience = userLevel.getMaxExperience();
        
        // 创建时必填参数
        if (add) {
            if (level == null || level < 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "等级数值必须为非负整数");
            }
            if (StringUtils.isBlank(levelName)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "等级名称不能为空");
            }
            if (minExperience == null || minExperience < 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "最小经验值必须为非负整数");
            }
            if (maxExperience == null || maxExperience <= minExperience) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "最大经验值必须大于最小经验值");
            }
            
            // 检查等级数值是否重复
            QueryWrapper<UserLevel> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("level", level);
            queryWrapper.eq("isDelete", 0);
            long count = this.count(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "等级数值已存在");
            }
            
            // 检查经验值范围是否与其他等级重叠
            QueryWrapper<UserLevel> overlapWrapper = new QueryWrapper<>();
            overlapWrapper.and(wrapper -> 
                    wrapper.and(w -> w.le("minExperience", minExperience).ge("maxExperience", minExperience))
                    .or(w -> w.le("minExperience", maxExperience).ge("maxExperience", maxExperience))
                    .or(w -> w.ge("minExperience", minExperience).le("maxExperience", maxExperience))
            );
            overlapWrapper.eq("isDelete", 0);
            count = this.count(overlapWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "经验值范围与现有等级重叠");
            }
        }
        
        // 有参数则校验
        if (StringUtils.isNotBlank(levelName) && levelName.length() > 64) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "等级名称过长");
        }
        if (minExperience != null && maxExperience != null && minExperience >= maxExperience) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最大经验值必须大于最小经验值");
        }
    }

    @Override
    public QueryWrapper<UserLevel> getQueryWrapper(UserLevelQueryRequest userLevelQueryRequest) {
        QueryWrapper<UserLevel> queryWrapper = new QueryWrapper<>();
        if (userLevelQueryRequest == null) {
            return queryWrapper;
        }
        
        Long id = userLevelQueryRequest.getId();
        Integer level = userLevelQueryRequest.getLevel();
        String levelName = userLevelQueryRequest.getLevelName();
        Integer minExperienceStart = userLevelQueryRequest.getMinExperienceStart();
        Integer minExperienceEnd = userLevelQueryRequest.getMinExperienceEnd();
        Integer maxExperienceStart = userLevelQueryRequest.getMaxExperienceStart();
        Integer maxExperienceEnd = userLevelQueryRequest.getMaxExperienceEnd();
        String sortField = userLevelQueryRequest.getSortField();
        String sortOrder = userLevelQueryRequest.getSortOrder();
        
        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(level), "level", level);
        if (StringUtils.isNotBlank(levelName)) {
            queryWrapper.like("levelName", levelName);
        }
        
        // 经验值范围查询
        queryWrapper.ge(ObjectUtils.isNotEmpty(minExperienceStart), "minExperience", minExperienceStart);
        queryWrapper.le(ObjectUtils.isNotEmpty(minExperienceEnd), "minExperience", minExperienceEnd);
        queryWrapper.ge(ObjectUtils.isNotEmpty(maxExperienceStart), "maxExperience", maxExperienceStart);
        queryWrapper.le(ObjectUtils.isNotEmpty(maxExperienceEnd), "maxExperience", maxExperienceEnd);
        
        queryWrapper.eq("isDelete", 0);
        
        // 默认按等级升序排序
        String defaultSortField = sortField != null ? sortField : "level";
        String defaultSortOrder = sortOrder != null ? sortOrder : CommonConstant.SORT_ORDER_ASC;
        queryWrapper.orderBy(SqlUtils.validSortField(defaultSortField), 
                defaultSortOrder.equals(CommonConstant.SORT_ORDER_ASC), defaultSortField);
                
        return queryWrapper;
    }

    @Override
    public UserLevelVO getUserLevelVO(UserLevel userLevel, HttpServletRequest request) {
        if (userLevel == null) {
            return null;
        }
        
        UserLevelVO userLevelVO = new UserLevelVO();
        BeanUtils.copyProperties(userLevel, userLevelVO);
        return userLevelVO;
    }

    @Override
    public List<UserLevelVO> getUserLevelVO(List<UserLevel> userLevelList, HttpServletRequest request) {
        if (CollUtil.isEmpty(userLevelList)) {
            return new ArrayList<>();
        }
        
        return userLevelList.stream().map(userLevel -> {
            UserLevelVO userLevelVO = new UserLevelVO();
            BeanUtils.copyProperties(userLevel, userLevelVO);
            return userLevelVO;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<UserLevelVO> getUserLevelVOPage(Page<UserLevel> userLevelPage, HttpServletRequest request) {
        List<UserLevel> userLevelList = userLevelPage.getRecords();
        Page<UserLevelVO> userLevelVOPage = new Page<>(userLevelPage.getCurrent(), userLevelPage.getSize(), userLevelPage.getTotal());
        List<UserLevelVO> userLevelVOList = getUserLevelVO(userLevelList, request);
        userLevelVOPage.setRecords(userLevelVOList);
        return userLevelVOPage;
    }

    @Override
    public long addUserLevel(UserLevel userLevel) {
        if (userLevel == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        
        // 校验数据
        validUserLevel(userLevel, true);
        
        boolean result = this.save(userLevel);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加失败");
        }
        return userLevel.getId();
    }

    @Override
    public UserLevel getUserLevelByExperience(int experience) {
        if (experience < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "经验值不能为负数");
        }
        
        // 查找符合条件的用户等级
        QueryWrapper<UserLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("minExperience", experience);
        queryWrapper.ge("maxExperience", experience);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderBy(true, true, "level");
        queryWrapper.last("LIMIT 1");
        
        UserLevel userLevel = this.getOne(queryWrapper);
        
        // 如果没有找到匹配的等级，返回最低等级
        if (userLevel == null) {
            QueryWrapper<UserLevel> lowestLevelWrapper = new QueryWrapper<>();
            lowestLevelWrapper.eq("isDelete", 0);
            lowestLevelWrapper.orderBy(true, true, "level");
            lowestLevelWrapper.last("LIMIT 1");
            userLevel = this.getOne(lowestLevelWrapper);
            
            if (userLevel == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "系统未配置等级信息");
            }
        }
        
        return userLevel;
    }

    @Override
    public UserLevel getNextUserLevel(int currentLevel) {
        if (currentLevel < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "等级不能为负数");
        }
        
        // 查找下一级别
        QueryWrapper<UserLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("level", currentLevel);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderBy(true, true, "level");
        queryWrapper.last("LIMIT 1");
        
        return this.getOne(queryWrapper);
    }

    @Override
    public List<UserLevelVO> getAllUserLevels(HttpServletRequest request) {
        // 查询所有等级，按等级升序排列
        QueryWrapper<UserLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", 0);
        queryWrapper.orderBy(true, true, "level");
        
        List<UserLevel> userLevelList = this.list(queryWrapper);
        return getUserLevelVO(userLevelList, request);
    }
} 