package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.mapper.AiAvatarMapper;
import com.ubanillx.smartclass.mapper.UserMapper;
import com.ubanillx.smartclass.model.entity.AiAvatar;
import com.ubanillx.smartclass.model.entity.AiAvatarChatHistory;
import com.ubanillx.smartclass.mapper.AiAvatarChatHistoryMapper;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.ChatMessageVO;
import com.ubanillx.smartclass.model.vo.ChatSessionVO;
import com.ubanillx.smartclass.service.AiAvatarChatHistoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liulo
 * @description 针对表【ai_avatar_chat_history(AI分身对话历史)】的数据库操作Service实现
 * @createDate 2025-03-24 21:35:44
 */
@Service
public class AiAvatarChatHistoryServiceImpl extends ServiceImpl<AiAvatarChatHistoryMapper, AiAvatarChatHistory>
    implements AiAvatarChatHistoryService {
    
    private static final Logger log = LoggerFactory.getLogger(AiAvatarChatHistoryServiceImpl.class);
    
    @Resource
    private AiAvatarMapper aiAvatarMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private ApplicationContext applicationContext;
    
    @Override
    @Transactional
    public boolean saveMessage(Long userId, Long aiAvatarId, String sessionId, String messageType, String content) {
        if (userId == null || aiAvatarId == null || sessionId == null || messageType == null || content == null) {
            return false;
        }
        
        // 过滤"会话已创建"的系统消息
        if ("system".equals(messageType) && "会话已创建".equals(content)) {
            // 不保存这类消息到数据库
            return true;
        }
        
        AiAvatarChatHistory chatHistory = new AiAvatarChatHistory();
        chatHistory.setUserId(userId);
        chatHistory.setAiAvatarId(aiAvatarId);
        chatHistory.setSessionId(sessionId);
        chatHistory.setMessageType(messageType);
        chatHistory.setContent(content);
        chatHistory.setCreateTime(new Date());
        
        // 计算tokens (简单实现)
        int tokens = content.length() / 4; // 简单估算，4个字符约等于1个token
        chatHistory.setTokens(tokens);
        
        return this.save(chatHistory);
    }
    
    @Override
    public String createNewSession(Long userId, Long aiAvatarId) {
        // 生成标准UUID格式的会话ID
        String sessionId = UUID.randomUUID().toString();
        
        // 不再创建"会话已创建"的初始记录
        // 直接返回新生成的会话ID
        return sessionId;
    }
    
    @Override
    public List<ChatSessionVO> getUserSessions(Long userId, Long aiAvatarId) {
        // 查询用户与AI分身的所有会话，获取会话ID和名称
        QueryWrapper<AiAvatarChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        
        if (aiAvatarId != null) {
            queryWrapper.eq("aiAvatarId", aiAvatarId);
        }
        
        queryWrapper.select("DISTINCT sessionId, sessionName, aiAvatarId")
                .orderByDesc("MAX(createTime)");
        
        List<Map<String, Object>> sessionsList = this.listMaps(queryWrapper);
        
        List<ChatSessionVO> result = new ArrayList<>();
        
        for (Map<String, Object> session : sessionsList) {
            String sessionId = (String) session.get("sessionId");
            if (sessionId == null) {
                continue;
            }
            
            ChatSessionVO chatSessionVO = new ChatSessionVO();
            chatSessionVO.setSessionId(sessionId);
            chatSessionVO.setSessionName((String) session.get("sessionName"));
            
            Long sessionAiAvatarId = (Long) session.get("aiAvatarId");
            chatSessionVO.setAiAvatarId(sessionAiAvatarId);
            
            // 获取AI分身信息
            AiAvatar aiAvatar = aiAvatarMapper.selectById(sessionAiAvatarId);
            if (aiAvatar != null) {
                chatSessionVO.setAiAvatarName(aiAvatar.getName());
                chatSessionVO.setAiAvatarImgUrl(aiAvatar.getAvatarImgUrl());
            }
            
            // 获取会话的最后一条消息
            QueryWrapper<AiAvatarChatHistory> messageQuery = new QueryWrapper<>();
            messageQuery.eq("sessionId", sessionId)
                    .orderByDesc("createTime")
                    .last("LIMIT 1");
            
            AiAvatarChatHistory lastMessage = this.getOne(messageQuery);
            if (lastMessage != null) {
                chatSessionVO.setLastMessage(lastMessage.getContent());
                chatSessionVO.setLastMessageTime(lastMessage.getCreateTime());
            }
            
            // 获取会话消息数量
            QueryWrapper<AiAvatarChatHistory> countQuery = new QueryWrapper<>();
            countQuery.eq("sessionId", sessionId);
            long count = this.count(countQuery);
            chatSessionVO.setMessageCount((int) count);
            
            result.add(chatSessionVO);
        }
        
        return result;
    }
    
    @Override
    public List<AiAvatarChatHistory> getSessionHistory(String sessionId) {
        if (sessionId == null) {
            return new ArrayList<>();
        }
        
        QueryWrapper<AiAvatarChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sessionId", sessionId)
                .orderByAsc("createTime");
        
        return this.list(queryWrapper);
    }
    
    @Override
    public Page<AiAvatarChatHistory> getSessionHistoryPage(String sessionId, long current, long size) {
        if (sessionId == null) {
            return new Page<>(current, size);
        }
        
        QueryWrapper<AiAvatarChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sessionId", sessionId)
                .orderByAsc("createTime");
        
        return this.page(new Page<>(current, size), queryWrapper);
    }
    
    @Override
    @Transactional
    public boolean updateSessionName(String sessionId, String sessionName) {
        if (sessionId == null || sessionName == null) {
            return false;
        }
        
        // 查询会话中的所有消息，并更新会话名称
        QueryWrapper<AiAvatarChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sessionId", sessionId);
        
        List<AiAvatarChatHistory> chatHistories = this.list(queryWrapper);
        
        if (chatHistories.isEmpty()) {
            return false;
        }
        
        // 更新所有消息的会话名称
        for (AiAvatarChatHistory history : chatHistories) {
            history.setSessionName(sessionName);
        }
        
        return this.updateBatchById(chatHistories);
    }
    
    @Override
    @Transactional
    public boolean deleteSession(String sessionId, Long userId) {
        if (sessionId == null || userId == null) {
            return false;
        }
        
        // 验证用户是否有权限删除该会话
        QueryWrapper<AiAvatarChatHistory> authQuery = new QueryWrapper<>();
        authQuery.eq("sessionId", sessionId)
                .eq("userId", userId)
                .last("LIMIT 1");
        
        AiAvatarChatHistory chatHistory = this.getOne(authQuery);
        
        if (chatHistory == null) {
            return false; // 会话不存在或用户无权限
        }
        
        // 删除会话中的所有消息
        QueryWrapper<AiAvatarChatHistory> deleteQuery = new QueryWrapper<>();
        deleteQuery.eq("sessionId", sessionId);
        
        return this.remove(deleteQuery);
    }
    
    @Override
    public Page<AiAvatarChatHistory> getUserHistoryPage(Long userId, Long aiAvatarId, long current, long size) {
        if (userId == null) {
            return new Page<>(current, size);
        }
        
        QueryWrapper<AiAvatarChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        
        if (aiAvatarId != null && aiAvatarId > 0) {
            queryWrapper.eq("aiAvatarId", aiAvatarId);
        }
        
        queryWrapper.orderByDesc("createTime");
        
        return this.page(new Page<>(current, size), queryWrapper);
    }
    
    @Override
    public List<ChatSessionVO> getRecentSessions(Long userId, int limit) {
        if (userId == null) {
            return new ArrayList<>();
        }
        
        // 修复SQL查询，避免DISTINCT和ORDER BY不兼容的问题
        // 先获取用户的会话ID列表，然后单独查询每个会话的最后消息
        QueryWrapper<AiAvatarChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId)
                .select("MAX(id) as id, sessionId")
                .groupBy("sessionId")
                .orderByDesc("MAX(createTime)")
                .last("LIMIT " + limit);
        
        List<Map<String, Object>> results = this.listMaps(queryWrapper);
        
        List<String> sessionIds = results.stream()
                .map(item -> (String) item.get("sessionId"))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        if (sessionIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<ChatSessionVO> sessions = new ArrayList<>();
        
        for (String sessionId : sessionIds) {
            // 查询每个会话的详细信息
            QueryWrapper<AiAvatarChatHistory> sessionQuery = new QueryWrapper<>();
            sessionQuery.eq("sessionId", sessionId)
                    .orderByDesc("createTime")
                    .last("LIMIT 1");
            
            AiAvatarChatHistory lastMessage = this.getOne(sessionQuery);
            
            if (lastMessage != null) {
                ChatSessionVO sessionVO = new ChatSessionVO();
                sessionVO.setSessionId(sessionId);
                sessionVO.setSessionName(lastMessage.getSessionName());
                sessionVO.setAiAvatarId(lastMessage.getAiAvatarId());
                sessionVO.setLastMessage(lastMessage.getContent());
                sessionVO.setLastMessageTime(lastMessage.getCreateTime());
                
                // 获取AI分身信息
                AiAvatar aiAvatar = aiAvatarMapper.selectById(lastMessage.getAiAvatarId());
                if (aiAvatar != null) {
                    sessionVO.setAiAvatarName(aiAvatar.getName());
                    sessionVO.setAiAvatarImgUrl(aiAvatar.getAvatarImgUrl());
                }
                
                // 获取会话消息数量
                QueryWrapper<AiAvatarChatHistory> countQuery = new QueryWrapper<>();
                countQuery.eq("sessionId", sessionId);
                long count = this.count(countQuery);
                sessionVO.setMessageCount((int) count);
                
                sessions.add(sessionVO);
            }
        }
        
        return sessions;
    }
    
    @Override
    public List<ChatMessageVO> getUserMessages(Long userId, Long aiAvatarId) {
        // 构建查询条件
        QueryWrapper<AiAvatarChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        
        if (aiAvatarId != null && aiAvatarId > 0) {
            queryWrapper.eq("aiAvatarId", aiAvatarId);
        }
        
        // 按时间倒序排列
        queryWrapper.orderByDesc("createTime");
        
        // 查询数据
        List<AiAvatarChatHistory> messageList = this.list(queryWrapper);
        
        // 转换为VO对象
        List<ChatMessageVO> result = new ArrayList<>();
        
        for (AiAvatarChatHistory message : messageList) {
            ChatMessageVO chatMessageVO = new ChatMessageVO();
            BeanUtils.copyProperties(message, chatMessageVO);
            
            // 获取AI分身信息
            AiAvatar aiAvatar = aiAvatarMapper.selectById(message.getAiAvatarId());
            if (aiAvatar != null) {
                chatMessageVO.setAiAvatarName(aiAvatar.getName());
                chatMessageVO.setAiAvatarImgUrl(aiAvatar.getAvatarImgUrl());
            }
            
            // 获取用户信息
            User user = userMapper.selectById(message.getUserId());
            if (user != null) {
                chatMessageVO.setUserName(user.getUserName());
                chatMessageVO.setUserAvatar(user.getUserAvatar());
            }
            
            result.add(chatMessageVO);
        }
        
        return result;
    }
    
    @Override
    public boolean updateSessionSummary(String sessionId, String summary) {
        if (StringUtils.hasLength(sessionId) == false || StringUtils.hasLength(summary) == false) {
            return false;
        }
        
        // 查询会话中的所有消息
        QueryWrapper<AiAvatarChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sessionId", sessionId);
        
        // 更新所有消息的会话名称
        AiAvatarChatHistory updateEntity = new AiAvatarChatHistory();
        updateEntity.setSessionName(summary);
        
        return this.update(updateEntity, queryWrapper);
    }
    
    @Override
    @Transactional
    public boolean deleteSessionCompletely(String sessionId, Long userId, String baseUrl, String avatarAuth) {
        if (!StringUtils.hasLength(sessionId) || userId == null) {
            return false;
        }
        
        // 验证用户是否有权限删除该会话
        QueryWrapper<AiAvatarChatHistory> authQuery = new QueryWrapper<>();
        authQuery.eq("sessionId", sessionId)
                .eq("userId", userId)
                .last("LIMIT 1");
        
        AiAvatarChatHistory chatHistory = this.getOne(authQuery);
        
        if (chatHistory == null) {
            log.warn("会话不存在或用户无权限删除: sessionId={}, userId={}", sessionId, userId);
            return false; // 会话不存在或用户无权限
        }
        
        // 先删除Dify远程会话，使用延迟加载的方式获取DifyService
        boolean difyDeleted = true;
        if (StringUtils.hasLength(baseUrl) && StringUtils.hasLength(avatarAuth)) {
            try {
                // 从ApplicationContext中获取DifyService，避免循环依赖
                Object difyServiceObj = applicationContext.getBean("difyServiceImpl");
                // 反射调用deleteConversation方法
                Class<?> clazz = difyServiceObj.getClass();
                boolean result = (boolean) clazz.getMethod("deleteConversation", Long.class, String.class, String.class, String.class)
                        .invoke(difyServiceObj, userId, sessionId, baseUrl, avatarAuth);
                difyDeleted = result;
                if (!difyDeleted) {
                    log.warn("删除Dify远程会话失败: sessionId={}", sessionId);
                    // 继续删除本地会话，不返回错误
                }
            } catch (Exception e) {
                log.error("调用DifyService.deleteConversation方法异常", e);
                // 继续删除本地会话，不返回错误
            }
        }
        
        // 删除本地会话记录
        QueryWrapper<AiAvatarChatHistory> deleteQuery = new QueryWrapper<>();
        deleteQuery.eq("sessionId", sessionId);
        
        boolean localDeleted = this.remove(deleteQuery);
        if (!localDeleted) {
            log.error("删除本地会话记录失败: sessionId={}", sessionId);
            return false;
        }
        
        return true;
    }
    
    @Override
    public List<ChatSessionVO> getHistoryDialogsList(Long userId, int limit, int offset) {
        if (userId == null) {
            return new ArrayList<>();
        }
        
        // 查询用户的会话列表，按最后更新时间降序排序
        String sql = "SELECT h.sessionId, h.sessionName, h.aiAvatarId, MAX(h.createTime) as lastTime, " +
                     "COUNT(*) as msgCount " +
                     "FROM ai_avatar_chat_history h " +
                     "WHERE h.userId = ? " +
                     "GROUP BY h.sessionId, h.sessionName, h.aiAvatarId " +
                     "ORDER BY lastTime DESC " +
                     "LIMIT ?, ?";
        
        // 使用自定义SQL查询
        try {
            List<ChatSessionVO> sessionList = new ArrayList<>();
            
            // 使用mybatis-plus的自定义SQL查询
            List<Map<String, Object>> resultMaps = this.baseMapper.selectMaps(
                    new QueryWrapper<AiAvatarChatHistory>()
                            .select("sessionId", "MAX(createTime) as lastTime")
                            .eq("userId", userId)
                            .groupBy("sessionId")
                            .orderByDesc("lastTime")
                            .last("LIMIT " + offset + ", " + limit)
            );
            
            for (Map<String, Object> resultMap : resultMaps) {
                String sessionId = (String) resultMap.get("sessionId");
                if (StringUtils.isEmpty(sessionId)) {
                    continue;
                }
                
                // 查询会话的最后一条消息和详细信息
                QueryWrapper<AiAvatarChatHistory> lastMessageQuery = new QueryWrapper<>();
                lastMessageQuery.eq("sessionId", sessionId)
                        .orderByDesc("createTime")
                        .last("LIMIT 1");
                
                AiAvatarChatHistory lastMessage = this.getOne(lastMessageQuery);
                if (lastMessage == null) {
                    continue;
                }
                
                // 查询会话的消息数量
                long msgCount = this.count(new QueryWrapper<AiAvatarChatHistory>()
                        .eq("sessionId", sessionId));
                
                // 构建会话VO
                ChatSessionVO sessionVO = new ChatSessionVO();
                sessionVO.setSessionId(sessionId);
                sessionVO.setSessionName(lastMessage.getSessionName());
                sessionVO.setAiAvatarId(lastMessage.getAiAvatarId());
                sessionVO.setLastMessage(lastMessage.getContent());
                sessionVO.setLastMessageTime(lastMessage.getCreateTime());
                sessionVO.setMessageCount((int) msgCount);
                
                // 获取AI分身信息
                AiAvatar aiAvatar = aiAvatarMapper.selectById(lastMessage.getAiAvatarId());
                if (aiAvatar != null) {
                    sessionVO.setAiAvatarName(aiAvatar.getName());
                    sessionVO.setAiAvatarImgUrl(aiAvatar.getAvatarImgUrl());
                }
                
                sessionList.add(sessionVO);
            }
            
            return sessionList;
        } catch (Exception e) {
            log.error("获取历史对话列表失败", e);
            return new ArrayList<>();
        }
    }
} 