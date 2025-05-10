package com.ubanillx.smartclass.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.constant.PrivateChatSessionConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.PrivateMessageMapper;
import com.ubanillx.smartclass.model.dto.websocket.WebSocketMessage;
import com.ubanillx.smartclass.model.entity.PrivateChatSession;
import com.ubanillx.smartclass.model.entity.PrivateMessage;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.netty.ChannelManager;
import com.ubanillx.smartclass.service.ChatMessageService;
import com.ubanillx.smartclass.service.ChatSessionUtils;
import com.ubanillx.smartclass.service.UserService;
import com.ubanillx.smartclass.service.WebSocketNotificationService;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 聊天消息服务实现类
 */
@Slf4j
@Service
public class ChatMessageServiceImpl implements ChatMessageService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private PrivateMessageMapper privateMessageMapper;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ChatSessionUtils chatSessionUtils;
    
    @Autowired
    private ChannelManager channelManager;
    
    @Autowired(required = false)
    private WebSocketNotificationService notificationService;
    
    // Redis Key前缀
    private static final String UNREAD_MESSAGES_KEY = "chat:unread:";
    private static final String MESSAGE_KEY = "chat:message:";
    private static final int MESSAGE_EXPIRE_DAYS = 7; // 缓存过期时间，7天
    
    /**
     * 处理新的WebSocket聊天消息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleChatMessage(WebSocketMessage message) {
        log.debug("处理新格式聊天消息: {}", message);
        
        Long senderId = message.getSenderId();
        Long receiverId = message.getReceiverId();
        String content = message.getContent();
        String sessionId = message.getSessionId();
        
        // 参数校验
        if (senderId == null || content == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息参数不完整");
        }
        
        // 根据消息类型处理
        String type = message.getType();
        switch (type) {
            case "chat":
                // 处理私聊消息
                handlePrivateChatMessage(senderId, receiverId, content, sessionId);
                break;
            case "system":
                // 处理系统消息
                handleSystemMessage(senderId, receiverId, content, message.getData());
                break;
            case "command":
                // 处理命令消息
                // 实际处理逻辑取决于具体需求
                log.debug("收到命令消息: {}", content);
                break;
            default:
                log.warn("未知的消息类型: {}", type);
        }
    }
    
    /**
     * 处理私聊消息
     */
    private void handlePrivateChatMessage(Long senderId, Long receiverId, String content, String sessionId) {
        if (receiverId == null) {
            log.warn("接收者ID为空，无法发送私聊消息");
            return;
        }
        
        // 保存消息到数据库
        PrivateMessage message = new PrivateMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setIsRead(PrivateChatSessionConstant.UNREAD);
        message.setCreateTime(new Date());
        privateMessageMapper.insert(message);
        
        // 更新或创建聊天会话
        PrivateChatSession chatSession = chatSessionUtils.getOrCreateChatSession(senderId, receiverId);
        chatSessionUtils.updateSessionLastMessageTime(chatSession);
        
        // 缓存消息
        String messageId = message.getId().toString();
        redisTemplate.opsForValue().set(MESSAGE_KEY + messageId, message, MESSAGE_EXPIRE_DAYS, TimeUnit.DAYS);
        
        // 添加到接收者的未读消息列表
        redisTemplate.opsForList().rightPush(UNREAD_MESSAGES_KEY + receiverId, messageId);
        
        // 尝试实时推送消息
        WebSocketMessage responseMessage = new WebSocketMessage();
        responseMessage.setType("chat");
        responseMessage.setSessionId(sessionId);
        responseMessage.setSenderId(senderId);
        responseMessage.setReceiverId(receiverId);
        responseMessage.setContent(content);
        responseMessage.setTimestamp(System.currentTimeMillis());
        responseMessage.setMessageId(messageId);
        
        // 发送消息
        sendWebSocketMessage(responseMessage);
        
        // 向管理员推送聊天监控数据
        if (notificationService != null) {
            try {
                notificationService.pushChatMonitorData(sessionId, responseMessage);
            } catch (Exception e) {
                log.error("推送聊天监控数据失败", e);
            }
        }
    }
    
    /**
     * 处理系统消息
     */
    private void handleSystemMessage(Long senderId, Long receiverId, String content, Object data) {
        if (receiverId == null) {
            log.warn("接收者ID为空，无法发送系统消息");
            return;
        }
        
        // 构造系统消息
        WebSocketMessage systemMessage = new WebSocketMessage();
        systemMessage.setType("system");
        systemMessage.setSenderId(senderId);
        systemMessage.setReceiverId(receiverId);
        systemMessage.setContent(content);
        systemMessage.setData(data);
        systemMessage.setTimestamp(System.currentTimeMillis());
        
        // 发送消息
        sendWebSocketMessage(systemMessage);
    }
    
    /**
     * 发送WebSocket消息
     */
    @Override
    public void sendWebSocketMessage(WebSocketMessage message) {
        Long receiverId = message.getReceiverId();
        
        if (receiverId == null) {
            log.warn("接收者ID为空，无法发送消息");
            return;
        }
        
        boolean delivered = channelManager.sendMessage(receiverId, JSON.toJSONString(message));
        if (delivered) {
            log.debug("消息已实时推送至用户: {}", receiverId);
        } else {
            log.debug("用户不在线，消息已缓存: {}", receiverId);
        }
    }
    
    /**
     * 处理旧格式私聊消息 (已过时)
     */
    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void handlePrivateMessage(com.ubanillx.smartclass.model.websocket.WebSocketMessage webSocketMessage) {
        Long senderId = webSocketMessage.getSenderId();
        Long receiverId = webSocketMessage.getReceiverId();
        String content = webSocketMessage.getContent();
        
        // 参数校验
        if (senderId == null || receiverId == null || content == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息参数不完整");
        }
        
        // 保存消息到数据库
        PrivateMessage message = new PrivateMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setIsRead(PrivateChatSessionConstant.UNREAD);
        message.setCreateTime(new Date());
        privateMessageMapper.insert(message);
        
        // 更新或创建聊天会话
        PrivateChatSession chatSession = chatSessionUtils.getOrCreateChatSession(senderId, receiverId);
        chatSessionUtils.updateSessionLastMessageTime(chatSession);
        
        // 缓存消息
        String messageId = message.getId().toString();
        redisTemplate.opsForValue().set(MESSAGE_KEY + messageId, message, MESSAGE_EXPIRE_DAYS, TimeUnit.DAYS);
        
        // 添加到接收者的未读消息列表
        redisTemplate.opsForList().rightPush(UNREAD_MESSAGES_KEY + receiverId, messageId);
        
        // 尝试实时推送消息
        com.ubanillx.smartclass.model.websocket.WebSocketMessage responseMessage = new com.ubanillx.smartclass.model.websocket.WebSocketMessage();
        responseMessage.setType(1); // 私聊消息
        responseMessage.setMessageId(messageId);
        responseMessage.setSenderId(senderId);
        responseMessage.setReceiverId(receiverId);
        responseMessage.setContent(content);
        responseMessage.setSendTime(new Date());
        
        boolean delivered = channelManager.sendMessage(receiverId, JSON.toJSONString(responseMessage));
        if (delivered) {
            log.debug("消息已实时推送至用户: {}", receiverId);
        } else {
            log.debug("用户不在线，消息已缓存: {}", receiverId);
        }
    }
    
    /**
     * 处理旧格式好友请求 (已过时)
     */
    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void handleFriendRequest(com.ubanillx.smartclass.model.websocket.WebSocketMessage message) {
        // 好友请求处理逻辑
        // 这里简化处理，可以根据需要扩展
        Long senderId = message.getSenderId();
        Long receiverId = message.getReceiverId();
        
        boolean delivered = channelManager.sendMessage(receiverId, JSON.toJSONString(message));
        if (delivered) {
            log.debug("好友请求已实时推送至用户: {}", receiverId);
        } else {
            log.debug("用户不在线，好友请求已缓存: {}", receiverId);
        }
    }
    
    /**
     * 处理旧格式系统通知 (已过时)
     */
    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void handleSystemNotification(com.ubanillx.smartclass.model.websocket.WebSocketMessage message) {
        // 系统通知处理逻辑
        // 这里简化处理，可以根据需要扩展
        Long receiverId = message.getReceiverId();
        
        boolean delivered = channelManager.sendMessage(receiverId, JSON.toJSONString(message));
        if (delivered) {
            log.debug("系统通知已实时推送至用户: {}", receiverId);
        } else {
            log.debug("用户不在线，系统通知已缓存: {}", receiverId);
        }
    }
    
    @Override
    public void sendPrivateMessage(Long senderId, Long receiverId, String content) {
        // 参数校验
        if (senderId == null || receiverId == null || content == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息参数不完整");
        }
        
        // 使用新格式的WebSocketMessage
        WebSocketMessage message = new WebSocketMessage();
        message.setType("chat");
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setTimestamp(System.currentTimeMillis());
        
        handleChatMessage(message);
    }
    
    @Override
    public List<PrivateMessage> getUnreadMessages(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        List<PrivateMessage> messages = new ArrayList<>();
        List<Object> messageIds = redisTemplate.opsForList().range(UNREAD_MESSAGES_KEY + userId, 0, -1);
        
        if (messageIds != null && !messageIds.isEmpty()) {
            for (Object id : messageIds) {
                String messageId = id.toString();
                Object messageObj = redisTemplate.opsForValue().get(MESSAGE_KEY + messageId);
                
                if (messageObj != null) {
                    if (messageObj instanceof PrivateMessage) {
                        messages.add((PrivateMessage) messageObj);
                    } else {
                        // 处理可能的类型转换问题
                        PrivateMessage message = JSON.parseObject(JSON.toJSONString(messageObj), PrivateMessage.class);
                        messages.add(message);
                    }
                } else {
                    // 缓存中找不到，从数据库加载
                    PrivateMessage message = privateMessageMapper.selectById(Long.parseLong(messageId));
                    if (message != null) {
                        messages.add(message);
                        // 重新放入缓存
                        redisTemplate.opsForValue().set(MESSAGE_KEY + messageId, message, MESSAGE_EXPIRE_DAYS, TimeUnit.DAYS);
                    }
                }
            }
        }
        
        return messages;
    }
    
    @Override
    public void markMessageAsRead(Long messageId, Long userId) {
        if (messageId == null || userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        
        // 从数据库更新消息状态
        PrivateMessage message = privateMessageMapper.selectById(messageId);
        if (message != null && message.getReceiverId().equals(userId)) {
            message.setIsRead(PrivateChatSessionConstant.READ);
            privateMessageMapper.updateById(message);
            
            // 更新缓存
            redisTemplate.opsForValue().set(MESSAGE_KEY + messageId, message, MESSAGE_EXPIRE_DAYS, TimeUnit.DAYS);
            
            // 从未读列表中移除
            redisTemplate.opsForList().remove(UNREAD_MESSAGES_KEY + userId, 0, messageId.toString());
        }
    }
    
    @Override
    public void markAllMessagesAsRead(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        // 获取所有未读消息
        List<PrivateMessage> unreadMessages = getUnreadMessages(userId);
        
        // 批量更新为已读
        for (PrivateMessage message : unreadMessages) {
            message.setIsRead(PrivateChatSessionConstant.READ);
            privateMessageMapper.updateById(message);
            
            // 更新缓存
            redisTemplate.opsForValue().set(MESSAGE_KEY + message.getId(), message, MESSAGE_EXPIRE_DAYS, TimeUnit.DAYS);
        }
        
        // 清空未读列表
        redisTemplate.delete(UNREAD_MESSAGES_KEY + userId);
    }
    
    @Override
    public int getUnreadMessageCount(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        // 优先从Redis获取未读数量
        Long count = redisTemplate.opsForList().size(UNREAD_MESSAGES_KEY + userId);
        if (count != null) {
            return count.intValue();
        }
        
        // Redis中没有，从数据库查询
        QueryWrapper<PrivateMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiverId", userId)
                .eq("isRead", PrivateChatSessionConstant.UNREAD);
        
        return Math.toIntExact(privateMessageMapper.selectCount(queryWrapper));
    }
} 