package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.AiAvatarChatHistoryMapper;
import com.ubanillx.smartclass.model.entity.AiAvatarChatHistory;
import com.ubanillx.smartclass.service.AiAvatarChatHistoryService;
import com.ubanillx.smartclass.service.AiAvatarService;
import com.ubanillx.smartclass.service.UserAiAvatarService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author liulo
* @description 针对表【ai_avatar_chat_history(AI分身聊天历史)】的数据库操作Service实现
* @createDate 2025-03-09 11:50:27
*/
@Service
public class AiAvatarChatHistoryServiceImpl extends BaseRelationServiceImpl<AiAvatarChatHistoryMapper, AiAvatarChatHistory>
    implements AiAvatarChatHistoryService {
    
    @Resource
    private AiAvatarService aiAvatarService;
    
    @Resource
    private UserAiAvatarService userAiAvatarService;
    
    public AiAvatarChatHistoryServiceImpl() {
        // 设置关联ID字段名
        setRelationIdFieldName("aiAvatarId");
    }

    @Override
    public boolean addChatHistory(AiAvatarChatHistory chatHistory) {
        // 参数校验
        if (chatHistory == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 用户ID不能为空
        if (chatHistory.getUserId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        // AI分身ID不能为空
        if (chatHistory.getAiAvatarId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "AI分身ID不能为空");
        }
        
        // 会话ID不能为空
        if (StringUtils.isBlank(chatHistory.getSessionId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "会话ID不能为空");
        }
        
        // 消息类型不能为空
        if (StringUtils.isBlank(chatHistory.getMessageType())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息类型不能为空");
        }
        
        // 消息内容不能为空
        if (StringUtils.isBlank(chatHistory.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息内容不能为空");
        }
        
        // 设置创建时间
        if (chatHistory.getCreateTime() == null) {
            chatHistory.setCreateTime(new Date());
        }
        
        // 如果是用户消息，更新用户使用AI分身的记录
        if ("user".equals(chatHistory.getMessageType())) {
            userAiAvatarService.useAiAvatar(chatHistory.getUserId(), chatHistory.getAiAvatarId());
        }
        
        // 保存聊天记录
        return save(chatHistory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAddChatHistory(List<AiAvatarChatHistory> chatHistoryList) {
        // 参数校验
        if (chatHistoryList == null || chatHistoryList.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 批量保存
        return saveBatch(chatHistoryList);
    }

    @Override
    public Page<AiAvatarChatHistory> getChatHistory(Long userId, Long aiAvatarId, String sessionId, int current, int size) {
        // 参数校验
        if (userId == null || aiAvatarId == null || StringUtils.isBlank(sessionId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 创建查询条件
        LambdaQueryWrapper<AiAvatarChatHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiAvatarChatHistory::getUserId, userId)
                .eq(AiAvatarChatHistory::getAiAvatarId, aiAvatarId)
                .eq(AiAvatarChatHistory::getSessionId, sessionId)
                .orderByAsc(AiAvatarChatHistory::getCreateTime);
        
        // 分页查询
        Page<AiAvatarChatHistory> page = new Page<>(current, size);
        return page(page, queryWrapper);
    }

    @Override
    public List<String> getSessionList(Long userId, Long aiAvatarId) {
        // 参数校验
        if (userId == null || aiAvatarId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 创建查询条件
        QueryWrapper<AiAvatarChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT session_id")
                .eq("user_id", userId)
                .eq("ai_avatar_id", aiAvatarId)
                .orderByDesc("create_time");
        
        // 查询会话ID列表
        List<AiAvatarChatHistory> list = list(queryWrapper);
        return list.stream()
                .map(AiAvatarChatHistory::getSessionId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean clearChatHistory(Long userId, Long aiAvatarId, String sessionId) {
        // 参数校验
        if (userId == null || aiAvatarId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 创建删除条件
        LambdaQueryWrapper<AiAvatarChatHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiAvatarChatHistory::getUserId, userId)
                .eq(AiAvatarChatHistory::getAiAvatarId, aiAvatarId);
        
        // 如果指定了会话ID，则只删除该会话的聊天记录
        if (StringUtils.isNotBlank(sessionId)) {
            queryWrapper.eq(AiAvatarChatHistory::getSessionId, sessionId);
        }
        
        // 执行删除
        return remove(queryWrapper);
    }

    @Override
    public List<AiAvatarChatHistory> getRecentChatHistory(Long userId, int limit) {
        // 参数校验
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 创建查询条件
        QueryWrapper<AiAvatarChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("create_time");
        
        if (limit > 0) {
            queryWrapper.last("limit " + limit);
        }
        
        // 查询最近的聊天记录
        return list(queryWrapper);
    }
}




