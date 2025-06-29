package com.ubanillx.smartclass.listener;

import com.ubanillx.smartclass.event.WebSocketMessageEvent;
import com.ubanillx.smartclass.netty.ChannelManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * WebSocket事件监听器
 * 负责接收WebSocket事件并执行实际的消息发送操作
 */
@Component
@Slf4j
public class WebSocketEventListener {

    @Autowired
    private ChannelManager channelManager;

    /**
     * 处理WebSocket消息事件
     * 使用@Async注解实现异步处理，减少事件发布者的阻塞时间
     */
    @Async
    @EventListener
    public void handleWebSocketMessageEvent(WebSocketMessageEvent event) {
        try {
            if (event.isBroadcast()) {
                // 广播消息
                channelManager.broadcastMessage(event.getMessage());
                log.debug("广播WebSocket消息");
            } else {
                // 发送给指定用户
                Long userId = event.getUserId();
                if (userId != null) {
                    boolean sent = channelManager.sendMessage(userId, event.getMessage());
                    log.debug("发送WebSocket消息到用户 {}: {}", userId, sent ? "成功" : "失败");
                }
            }
        } catch (Exception e) {
            log.error("处理WebSocket消息事件失败", e);
        }
    }
} 