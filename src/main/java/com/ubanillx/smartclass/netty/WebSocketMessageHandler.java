package com.ubanillx.smartclass.netty;

import com.alibaba.fastjson.JSON;
import com.ubanillx.smartclass.constant.WebSocketMessageType;
import com.ubanillx.smartclass.model.dto.websocket.WebSocketMessage;
import com.ubanillx.smartclass.service.ChatMessageService;
import com.ubanillx.smartclass.service.ChatSessionUtils;
import com.ubanillx.smartclass.service.PrivateChatSessionService;
import com.ubanillx.smartclass.service.PrivateMessageService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebSocket消息处理器
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class WebSocketMessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    
    @Autowired
    private ChatMessageService chatMessageService;
    
    @Autowired
    private PrivateMessageService privateMessageService;
    
    @Autowired
    private PrivateChatSessionService privateChatSessionService;
    
    @Autowired
    private ChannelManager channelManager;
    
    @Autowired
    private ChatSessionUtils chatSessionUtils;
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        String message = frame.text();
        log.debug("收到WebSocket消息: {}", message);
        
        try {
            // 处理简单心跳消息
            if ("PING".equals(message)) {
                ctx.channel().writeAndFlush(new TextWebSocketFrame("PONG"));
                log.debug("回复心跳消息");
                return;
            }
            
            // 尝试解析为标准WebSocketMessage格式
            WebSocketMessage webSocketMessage = JSON.parseObject(message, WebSocketMessage.class);
            
            if (webSocketMessage == null || StringUtils.isBlank(webSocketMessage.getType())) {
                log.warn("无效的WebSocket消息格式: {}", message);
                sendErrorMessage(ctx, "无效的消息格式");
                return;
            }
            
            // 处理不同类型的消息
            switch (webSocketMessage.getType()) {
                case WebSocketMessageType.CHAT:
                    handleChatMessage(ctx, webSocketMessage);
                    break;
                case WebSocketMessageType.HEARTBEAT:
                    handleHeartbeatMessage(ctx, webSocketMessage);
                    break;
                case WebSocketMessageType.COMMAND:
                    handleCommandMessage(ctx, webSocketMessage);
                    break;
                case WebSocketMessageType.SYSTEM:
                    handleSystemMessage(ctx, webSocketMessage);
                    break;
                case WebSocketMessageType.READ_STATUS:
                    handleReadStatusMessage(ctx, webSocketMessage);
                    break;
                case WebSocketMessageType.TYPING:
                    handleTypingMessage(ctx, webSocketMessage);
                    break;
                case WebSocketMessageType.SESSION_UPDATE:
                    handleSessionUpdateMessage(ctx, webSocketMessage);
                    break;
                default:
                    log.warn("未知的消息类型: {}", webSocketMessage.getType());
                    sendErrorMessage(ctx, "未知的消息类型: " + webSocketMessage.getType());
            }
        } catch (Exception e) {
            log.error("处理WebSocket消息失败", e);
            // 向客户端发送错误信息
            sendErrorMessage(ctx, "消息处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理聊天消息
     */
    private void handleChatMessage(ChannelHandlerContext ctx, WebSocketMessage message) {
        log.debug("处理聊天消息: {}", message.getContent());
        
        // 获取发送者ID
        Long userId = channelManager.getUserIdByChannel(ctx.channel());
        if (userId == null) {
            sendErrorMessage(ctx, "未认证的连接");
            return;
        }
        
        // 设置发送者ID
        message.setSenderId(userId);
        
        try {
            // 调用聊天消息服务
            chatMessageService.handleChatMessage(message);
            
            // 发送确认消息
            WebSocketMessage ackMessage = new WebSocketMessage();
            ackMessage.setType(WebSocketMessageType.CHAT);
            ackMessage.setContent("消息已接收");
            ackMessage.setSessionId(message.getSessionId());
            ackMessage.setTimestamp(System.currentTimeMillis());
            ackMessage.setMessageId(message.getMessageId());
            ackMessage.setStatus(1); // 已发送状态
            
            Map<String, Object> data = new HashMap<>();
            data.put("originalMessage", message);
            data.put("status", "received");
            ackMessage.setData(data);
            
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(ackMessage)));
            
            // 更新未读消息计数并推送给接收方
            if (message.getReceiverId() != null) {
                sendUnreadCountUpdate(message.getReceiverId());
            }
        } catch (Exception e) {
            log.error("处理聊天消息失败", e);
            sendErrorMessage(ctx, "处理聊天消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理心跳消息
     */
    private void handleHeartbeatMessage(ChannelHandlerContext ctx, WebSocketMessage message) {
        log.debug("处理心跳消息");
        
        // 回复心跳消息
        WebSocketMessage heartbeatResponse = new WebSocketMessage();
        heartbeatResponse.setType(WebSocketMessageType.HEARTBEAT);
        heartbeatResponse.setContent("PONG");
        heartbeatResponse.setTimestamp(System.currentTimeMillis());
        
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(heartbeatResponse)));
    }
    
    /**
     * 处理命令消息
     */
    private void handleCommandMessage(ChannelHandlerContext ctx, WebSocketMessage message) {
        log.debug("处理命令消息: {}", message.getContent());
        
        // 获取发送者ID
        Long userId = channelManager.getUserIdByChannel(ctx.channel());
        if (userId == null) {
            sendErrorMessage(ctx, "未认证的连接");
            return;
        }
        
        // 根据命令内容执行不同操作
        WebSocketMessage response = new WebSocketMessage();
        response.setType(WebSocketMessageType.COMMAND);
        response.setSessionId(message.getSessionId());
        response.setTimestamp(System.currentTimeMillis());
        
        try {
            // 这里可以根据具体命令执行不同的操作
            String command = message.getContent();
            switch (command) {
                case "online_users":
                    // 获取在线用户列表
                    response.setContent("在线用户列表");
                    response.setData(channelManager.getOnlineUsers());
                    break;
                case "clear_session":
                    // 清除会话信息
                    if (StringUtils.isNotBlank(message.getSessionId())) {
                        // 执行清除会话的操作
                        response.setContent("会话已清除");
                    } else {
                        response.setContent("会话ID不能为空");
                    }
                    break;
                case "get_unread_count":
                    // 获取未读消息数量
                    int count = privateChatSessionService.getTotalUnreadMessageCount(userId);
                    response.setContent("未读消息数量");
                    response.setUnreadCount(count);
                    break;
                default:
                    response.setContent("未知命令: " + command);
            }
        } catch (Exception e) {
            log.error("处理命令消息失败", e);
            response.setContent("命令执行失败: " + e.getMessage());
        }
        
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(response)));
    }
    
    /**
     * 处理系统消息
     */
    private void handleSystemMessage(ChannelHandlerContext ctx, WebSocketMessage message) {
        log.debug("处理系统消息: {}", message.getContent());
        
        // 系统消息通常不需要处理，只需要广播给目标用户
        // 这里只发送确认消息
        WebSocketMessage response = new WebSocketMessage();
        response.setType(WebSocketMessageType.SYSTEM);
        response.setContent("系统消息已接收");
        response.setTimestamp(System.currentTimeMillis());
        
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(response)));
    }
    
    /**
     * 处理消息已读状态更新
     */
    private void handleReadStatusMessage(ChannelHandlerContext ctx, WebSocketMessage message) {
        log.debug("处理消息已读状态更新: {}", message);
        
        // 获取发送者ID
        Long userId = channelManager.getUserIdByChannel(ctx.channel());
        if (userId == null) {
            sendErrorMessage(ctx, "未认证的连接");
            return;
        }
        
        try {
            // 判断是单条消息已读还是整个会话已读
            if (message.getMessageId() != null) {
                // 单条消息已读
                Long messageId = Long.parseLong(message.getMessageId());
                chatSessionUtils.markMessageAsRead(messageId, userId);
                
                // 向发送者推送消息已读状态
                WebSocketMessage readStatusMessage = new WebSocketMessage();
                readStatusMessage.setType(WebSocketMessageType.READ_STATUS);
                readStatusMessage.setMessageId(message.getMessageId());
                readStatusMessage.setStatus(3); // 已读状态
                readStatusMessage.setReceiverId(userId);
                readStatusMessage.setSenderId(message.getSenderId());
                readStatusMessage.setTimestamp(System.currentTimeMillis());
                
                // 向原发送者发送已读状态更新
                if (message.getSenderId() != null) {
                    channelManager.sendMessage(message.getSenderId(), JSON.toJSONString(readStatusMessage));
                }
            } else if (message.getSessionId() != null) {
                // 整个会话已读
                Long sessionId = Long.parseLong(message.getSessionId());
                privateChatSessionService.markAllMessagesAsRead(sessionId, userId);
                
                // 向会话中的其他用户推送已读状态
                WebSocketMessage readStatusMessage = new WebSocketMessage();
                readStatusMessage.setType(WebSocketMessageType.READ_STATUS);
                readStatusMessage.setSessionId(message.getSessionId());
                readStatusMessage.setStatus(3); // 已读状态
                readStatusMessage.setReceiverId(userId);
                
                if (message.getUserIds() != null) {
                    for (Long otherUserId : message.getUserIds()) {
                        if (!otherUserId.equals(userId)) {
                            readStatusMessage.setSenderId(otherUserId);
                            channelManager.sendMessage(otherUserId, JSON.toJSONString(readStatusMessage));
                        }
                    }
                }
            } else {
                // 所有消息已读
                privateMessageService.markAllMessagesAsRead(userId);
            }
            
            // 更新未读消息计数
            sendUnreadCountUpdate(userId);
            
            // 发送确认消息
            WebSocketMessage response = new WebSocketMessage();
            response.setType(WebSocketMessageType.READ_STATUS);
            response.setContent("已读状态已更新");
            response.setTimestamp(System.currentTimeMillis());
            
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(response)));
        } catch (Exception e) {
            log.error("处理已读状态消息失败", e);
            sendErrorMessage(ctx, "处理已读状态消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理正在输入状态消息
     */
    private void handleTypingMessage(ChannelHandlerContext ctx, WebSocketMessage message) {
        log.debug("处理正在输入状态: {}", message);
        
        // 获取发送者ID
        Long userId = channelManager.getUserIdByChannel(ctx.channel());
        if (userId == null) {
            sendErrorMessage(ctx, "未认证的连接");
            return;
        }
        
        // 设置发送者ID
        message.setSenderId(userId);
        
        // 将消息转发给接收者
        if (message.getReceiverId() != null) {
            channelManager.sendMessage(message.getReceiverId(), JSON.toJSONString(message));
        }
    }
    
    /**
     * 处理会话更新消息
     */
    private void handleSessionUpdateMessage(ChannelHandlerContext ctx, WebSocketMessage message) {
        log.debug("处理会话更新消息: {}", message);
        
        // 获取发送者ID
        Long userId = channelManager.getUserIdByChannel(ctx.channel());
        if (userId == null) {
            sendErrorMessage(ctx, "未认证的连接");
            return;
        }
        
        // 会话更新通常只是客户端状态同步，不需要服务器处理
        // 发送确认消息
        WebSocketMessage response = new WebSocketMessage();
        response.setType(WebSocketMessageType.SESSION_UPDATE);
        response.setContent("会话更新已接收");
        response.setSessionId(message.getSessionId());
        response.setTimestamp(System.currentTimeMillis());
        
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(response)));
    }
    
    /**
     * 发送未读消息计数更新
     */
    private void sendUnreadCountUpdate(Long userId) {
        try {
            int count = privateChatSessionService.getTotalUnreadMessageCount(userId);
            
            WebSocketMessage countMessage = new WebSocketMessage();
            countMessage.setType(WebSocketMessageType.UNREAD_COUNT);
            countMessage.setUnreadCount(count);
            countMessage.setReceiverId(userId);
            countMessage.setTimestamp(System.currentTimeMillis());
            
            channelManager.sendMessage(userId, JSON.toJSONString(countMessage));
        } catch (Exception e) {
            log.error("发送未读消息计数更新失败", e);
        }
    }
    
    /**
     * 发送错误消息
     */
    private void sendErrorMessage(ChannelHandlerContext ctx, String errorMessage) {
        WebSocketMessage errorMsg = new WebSocketMessage();
        errorMsg.setType(WebSocketMessageType.ERROR);
        errorMsg.setContent(errorMessage);
        errorMsg.setTimestamp(System.currentTimeMillis());
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errorMsg)));
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.debug("WebSocket通道已建立: {}", ctx.channel().id());
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.debug("WebSocket通道已关闭: {}", ctx.channel().id());
        // 从通道管理器中移除用户
        channelManager.removeChannel(ctx.channel());
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("WebSocket通道异常", cause);
        ctx.close();
    }
} 