package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.model.entity.AiAvatar;
import com.ubanillx.smartclass.mapper.AiAvatarMapper;
import com.ubanillx.smartclass.model.vo.AiAvatarBriefVO;
import com.ubanillx.smartclass.model.dto.aiavatar.AiAvatarUpdateRequest;
import com.ubanillx.smartclass.service.AiAvatarService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liulo
 * @description 针对表【ai_avatar(AI分身)】的数据库操作Service实现
 * @createDate 2025-03-18 23:08:38
 */
@Service
public class AiAvatarServiceImpl extends ServiceImpl<AiAvatarMapper, AiAvatar>
    implements AiAvatarService {

    @Override
    public List<AiAvatarBriefVO> listAllAiAvatarBrief() {
        // 查询所有未删除且已启用的AI分身
        QueryWrapper<AiAvatar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", 0)
                .orderByAsc("sort"); // 按排序字段升序排列
        
        List<AiAvatar> aiAvatarList = this.list(queryWrapper);
        
        // 转换为简要视图对象
        return aiAvatarList.stream().map(aiAvatar -> {
            AiAvatarBriefVO briefVO = new AiAvatarBriefVO();
            BeanUtils.copyProperties(aiAvatar, briefVO);
            return briefVO;
        }).collect(Collectors.toList());
    }
    
    @Override
    public AiAvatar createUpdateEntity(AiAvatarUpdateRequest updateRequest) {
        // 创建实体对象并设置ID
        AiAvatar aiAvatar = new AiAvatar();
        aiAvatar.setId(updateRequest.getId());
        
        // 增量更新每个字段，只有非null值才会被设置
        if (updateRequest.getName() != null) {
            aiAvatar.setName(updateRequest.getName());
        }
        
        if (updateRequest.getBaseUrl() != null) {
            aiAvatar.setBaseUrl(updateRequest.getBaseUrl());
        }
        
        if (updateRequest.getDescription() != null) {
            aiAvatar.setDescription(updateRequest.getDescription());
        }
        
        if (updateRequest.getAvatarImgUrl() != null) {
            aiAvatar.setAvatarImgUrl(updateRequest.getAvatarImgUrl());
        }
        
        if (updateRequest.getAvatarAuth() != null) {
            aiAvatar.setAvatarAuth(updateRequest.getAvatarAuth());
        }
        
        if (updateRequest.getTags() != null) {
            aiAvatar.setTags(updateRequest.getTags());
        }
        
        if (updateRequest.getPersonality() != null) {
            aiAvatar.setPersonality(updateRequest.getPersonality());
        }
        
        if (updateRequest.getAbilities() != null) {
            aiAvatar.setAbilities(updateRequest.getAbilities());
        }
        
        if (updateRequest.getIsPublic() != null) {
            aiAvatar.setIsPublic(updateRequest.getIsPublic());
        }
        
        if (updateRequest.getStatus() != null) {
            aiAvatar.setStatus(updateRequest.getStatus());
        }
        
        if (updateRequest.getSort() != null) {
            aiAvatar.setSort(updateRequest.getSort());
        }
        
        return aiAvatar;
    }
} 