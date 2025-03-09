package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.AiAvatarMapper;
import com.ubanillx.smartclass.model.entity.AiAvatar;
import com.ubanillx.smartclass.service.AiAvatarService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author liulo
* @description 针对表【ai_avatar(AI分身)】的数据库操作Service实现
* @createDate 2025-03-09 11:50:27
*/
@Service
public class AiAvatarServiceImpl extends ServiceImpl<AiAvatarMapper, AiAvatar>
    implements AiAvatarService {

    @Override
    public long addAiAvatar(AiAvatar aiAvatar) {
        // 参数校验
        if (aiAvatar == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 名称不能为空
        if (StringUtils.isBlank(aiAvatar.getName())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "AI分身名称不能为空");
        }
        
        // 分类不能为空
        if (StringUtils.isBlank(aiAvatar.getCategory())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "AI分身分类不能为空");
        }
        
        // 模型类型不能为空
        if (StringUtils.isBlank(aiAvatar.getModelType())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "模型类型不能为空");
        }
        
        // 插入数据
        boolean result = this.save(aiAvatar);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加AI分身失败");
        }
        
        return aiAvatar.getId();
    }

    @Override
    public boolean deleteAiAvatar(long id) {
        // 参数校验
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询是否存在
        AiAvatar aiAvatar = this.getById(id);
        if (aiAvatar == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 逻辑删除
        aiAvatar.setIsDelete(1);
        return this.updateById(aiAvatar);
    }

    @Override
    public boolean updateAiAvatar(AiAvatar aiAvatar) {
        // 参数校验
        if (aiAvatar == null || aiAvatar.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询是否存在
        AiAvatar oldAiAvatar = this.getById(aiAvatar.getId());
        if (oldAiAvatar == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 更新数据
        return this.updateById(aiAvatar);
    }

    @Override
    public AiAvatar getAiAvatarById(long id) {
        // 参数校验
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询数据
        AiAvatar aiAvatar = this.getById(id);
        if (aiAvatar == null || aiAvatar.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        return aiAvatar;
    }

    @Override
    public Page<AiAvatar> listAiAvatarByPage(String category, String keyword, int current, int size) {
        // 创建查询条件
        LambdaQueryWrapper<AiAvatar> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiAvatar::getIsDelete, 0);
        
        // 根据分类查询
        if (StringUtils.isNotBlank(category)) {
            queryWrapper.eq(AiAvatar::getCategory, category);
        }
        
        // 根据关键词查询
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> 
                wrapper.like(AiAvatar::getName, keyword)
                    .or()
                    .like(AiAvatar::getDescription, keyword)
                    .or()
                    .like(AiAvatar::getTags, keyword)
            );
        }
        
        // 按排序字段排序
        queryWrapper.orderByAsc(AiAvatar::getSort);
        
        // 分页查询
        Page<AiAvatar> page = new Page<>(current, size);
        return this.page(page, queryWrapper);
    }

    @Override
    public List<AiAvatar> listPopularAiAvatars(int limit) {
        // 创建查询条件
        QueryWrapper<AiAvatar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0);
        queryWrapper.eq("status", 1);
        queryWrapper.eq("is_public", 1);
        queryWrapper.orderByDesc("usage_count");
        queryWrapper.last("limit " + limit);
        
        return this.list(queryWrapper);
    }

    @Override
    public List<AiAvatar> listAiAvatarsByCategory(String category, int limit) {
        // 参数校验
        if (StringUtils.isBlank(category)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 创建查询条件
        QueryWrapper<AiAvatar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0);
        queryWrapper.eq("status", 1);
        queryWrapper.eq("is_public", 1);
        queryWrapper.eq("category", category);
        queryWrapper.orderByAsc("sort");
        
        if (limit > 0) {
            queryWrapper.last("limit " + limit);
        }
        
        return this.list(queryWrapper);
    }

    @Override
    public boolean updateAiAvatarUsageCount(long id) {
        // 参数校验
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 使用SQL直接更新使用次数，避免并发问题
        return this.baseMapper.incrUsageCount(id) > 0;
    }
    
    @Override
    public void validAiAvatar(AiAvatar aiAvatar, boolean add) {
        if (aiAvatar == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        String name = aiAvatar.getName();
        String category = aiAvatar.getCategory();
        String modelType = aiAvatar.getModelType();
        
        // 创建时，参数不能为空
        if (add) {
            if (StringUtils.isBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "AI分身名称不能为空");
            }
            if (StringUtils.isBlank(category)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "AI分身分类不能为空");
            }
            if (StringUtils.isBlank(modelType)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "模型类型不能为空");
            }
        }
        
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 128) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "AI分身名称过长");
        }
        if (StringUtils.isNotBlank(category) && category.length() > 64) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "AI分身分类过长");
        }
        if (StringUtils.isNotBlank(modelType) && modelType.length() > 64) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "模型类型过长");
        }
    }
}




