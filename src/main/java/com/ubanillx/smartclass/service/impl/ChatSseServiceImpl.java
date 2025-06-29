package com.ubanillx.smartclass.service.impl;

import com.alibaba.fastjson.JSON;
import com.ubanillx.smartclass.constant.WebSocketMessageType;
import com.ubanillx.smartclass.event.UserStatusEvent;
import com.ubanillx.smartclass.event.WebSocketMessageEvent;
import com.ubanillx.smartclass.model.dto.websocket.WebSocketMessage;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.service.ChatSseService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天消息SSE服务实现类
 * 用于实时发送用户对话消息给前端
 */
@Service
@Slf4j
public class ChatSseServiceImpl implements ChatSseService {

    /**
     * 用户ID -> SSE发射器的映射
     */
    private final Map<Long, SseEmitter> userEmitters = new ConcurrentHashMap<>();
    
    @Autowired
    private UserService userService;
    
    /**
     * SSE连接超时时间，单位毫秒
     */
    private static final long SSE_TIMEOUT = 3600000L; // 1小时
    
    /**
     * 监听用户状态事件
     */
    @EventListener
    public void handleUserStatusEvent(UserStatusEvent event) {
        log.info("接收到用户状态事件: 用户 {} {}", event.getUserId(), event.isOnline() ? "上线" : "下线");
        
        // 发送用户状态更新
        sendUserStatusUpdate(event.getUserId(), event.isOnline());
    }
    
    /**
     * 监听WebSocket消息事件
     * 当有WebSocket消息事件发布时，通过SSE也发送出去
     */
    @EventListener
    public void handleWebSocketMessageEvent(WebSocketMessageEvent event) {
        if (event.isBroadcast()) {
            // 处理广播消息
            String messageStr = event.getMessage();
            try {
                WebSocketMessage message = JSON.parseObject(messageStr, WebSocketMessage.class);
                
                // 根据消息类型分别处理
                switch (message.getType()) {
                    case WebSocketMessageType.SYSTEM:
                        broadcastSystemNotification(message.getContent(), message.getData());
                        break;
                    case WebSocketMessageType.USER_STATUS:
                        // 用户状态消息已经在handleUserStatusEvent中处理
                        break;
                    default:
                        // 其他广播消息直接发送给所有连接的用户
                        for (Long userId : userEmitters.keySet()) {
                            SseEmitter emitter = userEmitters.get(userId);
                            if (emitter != null) {
                                try {
                                    SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event()
                                        .data(messageStr)
                                        .id(message.getMessageId() != null ? message.getMessageId() : UUID.randomUUID().toString())
                                        .name(message.getType());
                                    
                                    emitter.send(eventBuilder);
                                } catch (IOException e) {
                                    log.error("广播消息发送失败: {}", e.getMessage());
                                    closeConnection(userId);
                                }
                            }
                        }
                        break;
                }
            } catch (Exception e) {
                log.error("解析WebSocket广播消息失败: {}", e.getMessage());
            }
        } else {
            // 处理发送给特定用户的消息
            Long userId = event.getUserId();
            String messageStr = event.getMessage();
            
            if (userId != null) {
                SseEmitter emitter = userEmitters.get(userId);
                if (emitter != null) {
                    try {
                        WebSocketMessage message = JSON.parseObject(messageStr, WebSocketMessage.class);
                        
                        // 根据消息类型构建相应的SSE事件
                        SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event()
                            .data(messageStr)
                            .id(message.getMessageId() != null ? message.getMessageId() : UUID.randomUUID().toString())
                            .name(message.getType());
                        
                        emitter.send(eventBuilder);
                        log.debug("通过SSE发送WebSocket消息给用户 {}: 类型={}", userId, message.getType());
                    } catch (Exception e) {
                        log.error("向用户 {} 发送WebSocket消息失败: {}", userId, e.getMessage());
                        closeConnection(userId);
                    }
                } else {
                    log.debug("用户 {} 没有活跃的SSE连接，无法发送WebSocket消息", userId);
                }
            }
        }
    }
    
    @Override
    public SseEmitter createConnection(Long userId) {
        // 先关闭可能存在的旧连接
        closeConnection(userId);
        
        // 创建新的SSE发射器
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        userEmitters.put(userId, emitter);
        
        // 在SSE连接建立后立即发送一个初始事件，确保连接稳定
        try {
            SseEmitter.SseEventBuilder initialEvent = SseEmitter.event()
                .data("{\"event\":\"connected\",\"message\":\"SSE连接已建立\"}")
                .id("connect-" + System.currentTimeMillis())
                .name("connect");
            emitter.send(initialEvent);
        } catch (Exception e) {
            log.warn("发送初始连接事件失败，客户端可能已断开", e);
            closeConnection(userId);
        }
        
        // 添加超时和完成时的回调
        emitter.onTimeout(() -> {
            log.debug("用户 {} 的SSE连接超时", userId);
            closeConnection(userId);
        });
        
        emitter.onCompletion(() -> {
            log.debug("用户 {} 的SSE连接已完成", userId);
            userEmitters.remove(userId);
        });
        
        emitter.onError(error -> {
            log.error("用户 {} 的SSE连接发生错误: {}", userId, error.getMessage());
            closeConnection(userId);
        });
        
        log.info("用户 {} 创建了新的SSE连接", userId);
        return emitter;
    }
    
    @Override
    public void closeConnection(Long userId) {
        SseEmitter emitter = userEmitters.remove(userId);
        if (emitter != null) {
            try {
                emitter.complete();
                log.info("已关闭用户 {} 的SSE连接", userId);
            } catch (Exception e) {
                log.warn("关闭用户 {} 的SSE连接时出错: {}", userId, e.getMessage());
            }
        }
    }
    
    @Override
    public boolean sendChatMessage(Long receiverId, Long senderId, String content, String sessionId) {
        // 获取接收者的SSE发射器
        SseEmitter emitter = userEmitters.get(receiverId);
        if (emitter == null) {
            log.debug("用户 {} 没有活跃的SSE连接", receiverId);
            return false;
        }
        
        try {
            // 获取发送者信息
            User sender = userService.getById(senderId);
            if (sender == null) {
                log.error("发送者 {} 不存在", senderId);
                return false;
            }
            
            // 构建WebSocket消息模型
            WebSocketMessage message = new WebSocketMessage();
            message.setType(WebSocketMessageType.CHAT);
            message.setContent(content);
            message.setSenderId(senderId);
            message.setReceiverId(receiverId);
            message.setSessionId(sessionId);
            message.setTimestamp(System.currentTimeMillis());
            message.setMessageId(UUID.randomUUID().toString());
            
            // 添加发送者信息
            Map<String, Object> senderInfo = new HashMap<>();
            senderInfo.put("id", sender.getId());
            senderInfo.put("name", sender.getUserName());
            senderInfo.put("avatar", sender.getUserAvatar());
            message.setData(senderInfo);
            
            // 发送SSE事件
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                .data(JSON.toJSONString(message))
                .id("msg-" + System.currentTimeMillis())
                .name("chat");
            
            emitter.send(event);
            log.debug("向用户 {} 发送了聊天消息", receiverId);
            return true;
        } catch (IOException e) {
            log.error("向用户 {} 发送聊天消息失败: {}", receiverId, e.getMessage());
            closeConnection(receiverId);
            return false;
        }
    }
    
    @Override
    public boolean sendSystemNotification(Long userId, String content, Object data) {
        // 获取用户的SSE发射器
        SseEmitter emitter = userEmitters.get(userId);
        if (emitter == null) {
            log.debug("用户 {} 没有活跃的SSE连接", userId);
            return false;
        }
        
        try {
            // 构建WebSocket消息模型
            WebSocketMessage message = new WebSocketMessage();
            message.setType(WebSocketMessageType.SYSTEM);
            message.setContent(content);
            message.setReceiverId(userId);
            message.setTimestamp(System.currentTimeMillis());
            message.setMessageId(UUID.randomUUID().toString());
            message.setData(data);
            
            // 发送SSE事件
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                .data(JSON.toJSONString(message))
                .id("sys-" + System.currentTimeMillis())
                .name("system");
            
            emitter.send(event);
            log.debug("向用户 {} 发送了系统通知", userId);
            return true;
        } catch (IOException e) {
            log.error("向用户 {} 发送系统通知失败: {}", userId, e.getMessage());
            closeConnection(userId);
            return false;
        }
    }
    
    @Override
    public boolean broadcastSystemNotification(String content, Object data) {
        // 遍历所有SSE连接并发送系统通知
        boolean allSucceeded = true;
        
        for (Long userId : userEmitters.keySet()) {
            boolean success = sendSystemNotification(userId, content, data);
            if (!success) {
                allSucceeded = false;
            }
        }
        
        return allSucceeded;
    }
    
    @Override
    public boolean sendUserStatusUpdate(Long userId, boolean online) {
        // 遍历所有SSE连接并发送用户状态更新
        boolean allSucceeded = true;
        
        for (Long receiverId : userEmitters.keySet()) {
            // 获取用户的SSE发射器
            SseEmitter emitter = userEmitters.get(receiverId);
            if (emitter == null) {
                continue;
            }
            
            try {
                // 获取状态变更用户信息
                User user = userService.getById(userId);
                if (user == null) {
                    log.error("用户 {} 不存在", userId);
                    continue;
                }
                
                // 构建WebSocket消息模型
                WebSocketMessage message = new WebSocketMessage();
                message.setType(WebSocketMessageType.USER_STATUS);
                message.setSenderId(userId);
                message.setReceiverId(receiverId);
                message.setTimestamp(System.currentTimeMillis());
                message.setMessageId(UUID.randomUUID().toString());
                
                // 添加用户状态信息
                Map<String, Object> statusInfo = new HashMap<>();
                statusInfo.put("userId", userId);
                statusInfo.put("userName", user.getUserName());
                statusInfo.put("userAvatar", user.getUserAvatar());
                statusInfo.put("online", online);
                message.setData(statusInfo);
                
                // 发送SSE事件
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .data(JSON.toJSONString(message))
                    .id("status-" + System.currentTimeMillis())
                    .name("user_status");
                
                emitter.send(event);
            } catch (IOException e) {
                log.error("向用户 {} 发送用户状态更新失败: {}", receiverId, e.getMessage());
                closeConnection(receiverId);
                allSucceeded = false;
            }
        }
        
        return allSucceeded;
    }
    
    /**
     * 安全地完成SseEmitter，避免重复完成导致的异常
     */
    private void safeCompleteEmitter(SseEmitter emitter, Throwable error) {
        try {
            if (error != null) {
                emitter.completeWithError(error);
            } else {
                emitter.complete();
            }
        } catch (Exception e) {
            // 通常这意味着emitter已经被完成或关闭了
            log.debug("完成SSE发射器时出错: {}", e.getMessage());
        }
    }
    
    @Override
    public boolean sendReadStatusUpdate(Long receiverId, Long messageId, String sessionId, boolean isRead) {
        // 获取用户的SSE发射器
        SseEmitter emitter = userEmitters.get(receiverId);
        if (emitter == null) {
            log.debug("用户 {} 没有活跃的SSE连接", receiverId);
            return false;
        }
        
        try {
            // 构建WebSocket消息模型
            WebSocketMessage message = new WebSocketMessage();
            message.setType(WebSocketMessageType.READ_STATUS);
            message.setReceiverId(receiverId);
            message.setTimestamp(System.currentTimeMillis());
            message.setMessageId(messageId.toString());
            message.setSessionId(sessionId);
            
            // 添加已读状态数据
            Map<String, Object> readStatusInfo = new HashMap<>();
            readStatusInfo.put("messageId", messageId);
            readStatusInfo.put("isRead", isRead);
            readStatusInfo.put("sessionId", sessionId);
            message.setData(readStatusInfo);
            
            // 发送SSE事件
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                .data(JSON.toJSONString(message))
                .id("read-status-" + System.currentTimeMillis())
                .name("read_status");
            
            emitter.send(event);
            log.debug("向用户 {} 发送了消息已读状态更新", receiverId);
            return true;
        } catch (IOException e) {
            log.error("向用户 {} 发送消息已读状态更新失败: {}", receiverId, e.getMessage());
            closeConnection(receiverId);
            return false;
        }
    }
    
    @Override
    public boolean sendBatchReadStatusUpdate(Long receiverId, List<Long> messageIds, String sessionId, boolean isRead) {
        // 获取用户的SSE发射器
        SseEmitter emitter = userEmitters.get(receiverId);
        if (emitter == null) {
            log.debug("用户 {} 没有活跃的SSE连接", receiverId);
            return false;
        }
        
        try {
            // 构建WebSocket消息模型
            WebSocketMessage message = new WebSocketMessage();
            message.setType(WebSocketMessageType.READ_STATUS);
            message.setReceiverId(receiverId);
            message.setTimestamp(System.currentTimeMillis());
            message.setSessionId(sessionId);
            
            // 添加批量已读状态数据
            Map<String, Object> readStatusInfo = new HashMap<>();
            readStatusInfo.put("messageIds", messageIds);
            readStatusInfo.put("isRead", isRead);
            readStatusInfo.put("sessionId", sessionId);
            readStatusInfo.put("isBatch", true);
            message.setData(readStatusInfo);
            
            // 发送SSE事件
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                .data(JSON.toJSONString(message))
                .id("batch-read-status-" + System.currentTimeMillis())
                .name("read_status");
            
            emitter.send(event);
            log.debug("向用户 {} 发送了批量消息已读状态更新，共 {} 条消息", receiverId, messageIds.size());
            return true;
        } catch (IOException e) {
            log.error("向用户 {} 发送批量消息已读状态更新失败: {}", receiverId, e.getMessage());
            closeConnection(receiverId);
            return false;
        }
    }
    
    @Override
    public boolean sendSessionReadStatusUpdate(Long receiverId, String sessionId) {
        // 获取用户的SSE发射器
        SseEmitter emitter = userEmitters.get(receiverId);
        if (emitter == null) {
            log.debug("用户 {} 没有活跃的SSE连接", receiverId);
            return false;
        }
        
        try {
            // 构建WebSocket消息模型
            WebSocketMessage message = new WebSocketMessage();
            message.setType(WebSocketMessageType.READ_STATUS);
            message.setReceiverId(receiverId);
            message.setTimestamp(System.currentTimeMillis());
            message.setSessionId(sessionId);
            
            // 添加会话已读状态数据
            Map<String, Object> readStatusInfo = new HashMap<>();
            readStatusInfo.put("sessionId", sessionId);
            readStatusInfo.put("isAllRead", true);
            message.setData(readStatusInfo);
            
            // 发送SSE事件
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                .data(JSON.toJSONString(message))
                .id("session-read-status-" + System.currentTimeMillis())
                .name("read_status");
            
            emitter.send(event);
            log.debug("向用户 {} 发送了会话 {} 的所有消息已读状态更新", receiverId, sessionId);
            return true;
        } catch (IOException e) {
            log.error("向用户 {} 发送会话所有消息已读状态更新失败: {}", receiverId, e.getMessage());
            closeConnection(receiverId);
            return false;
        }
    }
    
    @Override
    public boolean sendUnreadCountUpdate(Long userId, int unreadCount) {
        // 获取用户的SSE发射器
        SseEmitter emitter = userEmitters.get(userId);
        if (emitter == null) {
            log.debug("用户 {} 没有活跃的SSE连接", userId);
            return false;
        }
        
        try {
            // 构建WebSocket消息模型
            WebSocketMessage message = new WebSocketMessage();
            message.setType(WebSocketMessageType.UNREAD_COUNT);
            message.setReceiverId(userId);
            message.setTimestamp(System.currentTimeMillis());
            message.setMessageId(UUID.randomUUID().toString());
            
            // 设置未读数量
            message.setUnreadCount(unreadCount);
            
            // 添加未读计数数据
            Map<String, Object> unreadCountInfo = new HashMap<>();
            unreadCountInfo.put("unreadCount", unreadCount);
            unreadCountInfo.put("userId", userId);
            message.setData(unreadCountInfo);
            
            // 发送SSE事件
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                .data(JSON.toJSONString(message))
                .id("unread-count-" + System.currentTimeMillis())
                .name("unread_count");
            
            emitter.send(event);
            log.debug("向用户 {} 发送了未读消息数量更新: {}", userId, unreadCount);
            return true;
        } catch (IOException e) {
            log.error("向用户 {} 发送未读消息数量更新失败: {}", userId, e.getMessage());
            closeConnection(userId);
            return false;
        }
    }
} 