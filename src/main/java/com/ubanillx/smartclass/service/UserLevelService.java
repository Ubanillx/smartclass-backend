package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.dto.userlevel.UserLevelQueryRequest;
import com.ubanillx.smartclass.model.entity.UserLevel;
import com.ubanillx.smartclass.model.vo.UserLevelVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户等级服务
 */
public interface UserLevelService extends IService<UserLevel> {

    /**
     * 校验用户等级信息
     *
     * @param userLevel
     * @param add 是否为创建校验
     */
    void validUserLevel(UserLevel userLevel, boolean add);

    /**
     * 获取查询条件
     *
     * @param userLevelQueryRequest
     * @return
     */
    QueryWrapper<UserLevel> getQueryWrapper(UserLevelQueryRequest userLevelQueryRequest);

    /**
     * 获取用户等级封装
     *
     * @param userLevel
     * @param request
     * @return
     */
    UserLevelVO getUserLevelVO(UserLevel userLevel, HttpServletRequest request);

    /**
     * 获取用户等级封装列表
     *
     * @param userLevelList
     * @param request
     * @return
     */
    List<UserLevelVO> getUserLevelVO(List<UserLevel> userLevelList, HttpServletRequest request);

    /**
     * 分页获取用户等级封装
     *
     * @param userLevelPage
     * @param request
     * @return
     */
    Page<UserLevelVO> getUserLevelVOPage(Page<UserLevel> userLevelPage, HttpServletRequest request);

    /**
     * 添加用户等级
     *
     * @param userLevel
     * @return
     */
    long addUserLevel(UserLevel userLevel);
    
    /**
     * 根据经验值获取对应的用户等级
     *
     * @param experience
     * @return
     */
    UserLevel getUserLevelByExperience(int experience);
    
    /**
     * 获取下一级用户等级
     *
     * @param currentLevel
     * @return
     */
    UserLevel getNextUserLevel(int currentLevel);
    
    /**
     * 获取所有用户等级，按等级升序排列
     *
     * @return
     */
    List<UserLevelVO> getAllUserLevels(HttpServletRequest request);
}
