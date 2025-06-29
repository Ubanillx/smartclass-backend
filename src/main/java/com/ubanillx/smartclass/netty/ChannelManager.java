package com.ubanillx.smartclass.netty;

import com.ubanillx.smartclass.event.UserStatusEvent;
import com.ubanillx.smartclass.event.WebSocketMessageEvent;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * WebSocket通道管理器
 */
@Slf4j
@Component
public class ChannelManager {
    
    private static ChannelManager instance;
    
    // 用户ID -> Channel映射
    private final Map<Long, Channel> userChannelMap = new ConcurrentHashMap<>();
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    public ChannelManager() {
        instance = this;
    }
    
    @PostConstruct
    public void init() {
        instance = this;
    }
    
    public static ChannelManager getInstance() {
        return instance;
    }
    
    /**
     * 添加用户通道
     */
    public void addChannel(Long userId, Channel channel) {
        boolean isNewConnection = !userChannelMap.containsKey(userId);
        
        Channel oldChannel = userChannelMap.put(userId, channel);
        if (oldChannel != null && oldChannel.isActive()) {
            log.debug("用户已有活跃连接，关闭旧连接: {}", userId);
            oldChannel.close();
        }
        log.debug("用户通道已添加: {}", userId);
        
        // 如果是新连接（而不是重连），发布用户上线事件
        if (isNewConnection) {
            try {
                eventPublisher.publishEvent(new UserStatusEvent(userId, UserStatusEvent.Type.ONLINE));
            } catch (Exception e) {
                log.error("发布用户上线事件失败", e);
            }
        }
    }
    
    /**
     * 移除用户通道
     */
    public void removeChannel(Channel channel) {
        Long userId = null;
        
        for (Map.Entry<Long, Channel> entry : userChannelMap.entrySet()) {
            if (entry.getValue() == channel) {
                userId = entry.getKey();
                userChannelMap.remove(entry.getKey());
                log.debug("用户通道已移除: {}", entry.getKey());
                break;
            }
        }
        
        // 发布用户下线事件
        if (userId != null) {
            try {
                eventPublisher.publishEvent(new UserStatusEvent(userId, UserStatusEvent.Type.OFFLINE));
            } catch (Exception e) {
                log.error("发布用户下线事件失败", e);
            }
        }
    }
    
    /**
     * 获取用户通道
     */
    public Channel getChannel(Long userId) {
        return userChannelMap.get(userId);
    }
    
    /**
     * 通过Channel获取用户ID
     */
    public Long getUserIdByChannel(Channel channel) {
        return channel.attr(WebSocketAuthHandler.USER_ID_KEY).get();
    }
    
    /**
     * 判断用户是否在线
     */
    public boolean isUserOnline(Long userId) {
        Channel channel = userChannelMap.get(userId);
        return channel != null && channel.isActive();
    }
    
    /**
     * 向用户发送消息
     */
    public boolean sendMessage(Long userId, String message) {
        Channel channel = getChannel(userId);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return true;
        }
        return false;
    }
    
    /**
     * 获取在线用户数量
     */
    public int getOnlineUserCount() {
        return userChannelMap.size();
    }
    
    /**
     * 获取在线用户ID列表
     */
    public List<Long> getOnlineUserIds() {
        return new ArrayList<>(userChannelMap.keySet());
    }
    
    /**
     * 获取所有在线用户信息
     */
    public Map<String, Object> getOnlineUsers() {
        Map<String, Object> result = new ConcurrentHashMap<>();
        result.put("count", getOnlineUserCount());
        result.put("users", getOnlineUserIds());
        return result;
    }
    
    /**
     * 广播消息给所有用户
     */
    public void broadcastMessage(String message) {
        userChannelMap.values().forEach(channel -> {
            if (channel.isActive()) {
                channel.writeAndFlush(new TextWebSocketFrame(message));
            }
        });
    }
    
    /**
     * 广播消息给指定用户组
     */
    public void broadcastMessageToUsers(List<Long> userIds, String message) {
        userIds.forEach(userId -> sendMessage(userId, message));
    }
} 