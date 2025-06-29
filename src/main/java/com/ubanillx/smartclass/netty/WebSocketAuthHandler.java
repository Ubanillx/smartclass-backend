package com.ubanillx.smartclass.netty;

import com.alibaba.fastjson.JSON;
import com.ubanillx.smartclass.constant.WebSocketMessageType;
import com.ubanillx.smartclass.model.dto.websocket.WebSocketMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket认证处理器
 * 支持两种认证方式：
 * 1. URL参数token认证，适用于Postman测试
 * 2. 消息认证，客户端发送type=auth的消息进行认证
 */
@Slf4j
public class WebSocketAuthHandler extends ChannelInboundHandlerAdapter {
    
    public static final AttributeKey<Long> USER_ID_KEY = AttributeKey.valueOf("userId");
    public static final AttributeKey<Boolean> AUTH_KEY = AttributeKey.valueOf("authenticated");
    public static final AttributeKey<String> TOKEN_KEY = AttributeKey.valueOf("token");
    
    private final StringRedisTemplate redisTemplate;
    private final ChannelManager channelManager;
    private final Map<String, Long> tokenUserMap = new HashMap<>();
    private final int authTimeoutSeconds;
    
    public WebSocketAuthHandler(int authTimeoutSeconds, StringRedisTemplate redisTemplate, ChannelManager channelManager) {
        this.authTimeoutSeconds = authTimeoutSeconds;
        this.redisTemplate = redisTemplate;
        this.channelManager = channelManager;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 处理HTTP握手请求，从URL中提取token参数
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            
            // 解析URL参数
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            Map<String, List<String>> parameters = decoder.parameters();
            
            // 检查是否有token参数
            if (parameters.containsKey("token")) {
                String token = parameters.get("token").get(0);
                if (StringUtils.isNotBlank(token)) {
                    log.debug("从URL参数中获取到token: {}", token);
                    ctx.channel().attr(TOKEN_KEY).set(token);
                    
                    // 尝试从token验证用户身份
                    Long userId = getUserIdFromToken(token);
                    if (userId != null) {
                        // 设置认证状态
                        authenticateUser(ctx, userId);
                        log.debug("URL参数token认证成功，用户ID: {}", userId);
                    } else {
                        log.warn("URL参数token认证失败: {}", token);
                        // 继续握手，但标记为未认证，让后续消息认证处理
                    }
                }
            }
            
            // 修改URI，移除查询参数（否则会影响WebSocket协议握手）
            if (request.uri().contains("?")) {
                String newUri = request.uri().split("\\?")[0];
                request.setUri(newUri);
            }
        }
        
        // 处理WebSocket消息，包括认证消息
        else if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame frame = (TextWebSocketFrame) msg;
            String text = frame.text();
            
            // 已认证的连接，直接传递消息
            if (isAuthenticated(ctx)) {
                ctx.fireChannelRead(msg);
                return;
            }
            
            // 处理心跳消息，即使未认证也允许
            if ("PING".equals(text)) {
                ctx.fireChannelRead(msg);
                return;
            }
            
            try {
                // 尝试解析为WebSocketMessage格式
                WebSocketMessage message = JSON.parseObject(text, WebSocketMessage.class);
                
                // 认证消息
                if (WebSocketMessageType.AUTH.equals(message.getType())) {
                    handleAuthMessage(ctx, message);
                    return;
                }
                
                // 尝试使用旧格式解析和处理（兼容性支持）
                if (text.contains("\"type\":0")) {
                    try {
                        com.ubanillx.smartclass.model.websocket.WebSocketMessage oldMessage = 
                            JSON.parseObject(text, com.ubanillx.smartclass.model.websocket.WebSocketMessage.class);
                        
                        if (oldMessage.getType() == 0 && oldMessage.getSenderId() != null) {
                            authenticateUser(ctx, oldMessage.getSenderId());
                            
                            // 发送认证成功响应
                            WebSocketMessage authResponse = new WebSocketMessage();
                            authResponse.setType(WebSocketMessageType.AUTH_SUCCESS);
                            authResponse.setContent("认证成功");
                            ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(authResponse)));
                            
                            log.debug("旧格式消息认证成功: {}", oldMessage.getSenderId());
                            return;
                        }
                    } catch (Exception e) {
                        log.debug("旧格式消息解析失败", e);
                    }
                }
                
                // 如果未认证，拒绝处理消息
                if (!isAuthenticated(ctx)) {
                    WebSocketMessage authError = new WebSocketMessage();
                    authError.setType(WebSocketMessageType.AUTH_ERROR);
                    authError.setContent("请先进行认证");
                    ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(authError)));
                    return;
                }
            } catch (Exception e) {
                log.error("处理认证消息失败", e);
                // 如果消息解析失败，且未认证，则拒绝处理
                if (!isAuthenticated(ctx)) {
                    WebSocketMessage authError = new WebSocketMessage();
                    authError.setType(WebSocketMessageType.ERROR);
                    authError.setContent("无效的消息格式，请先进行认证");
                    ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(authError)));
                    return;
                }
            }
        }
        
        // 传递给下一个处理器
        ctx.fireChannelRead(msg);
    }
    
    /**
     * 处理认证消息
     */
    private void handleAuthMessage(ChannelHandlerContext ctx, WebSocketMessage message) {
        // 获取token或userId
        String token = (String) message.getData();
        Long userId = message.getSenderId();
        
        // 优先使用token认证
        if (StringUtils.isNotBlank(token)) {
            userId = getUserIdFromToken(token);
            if (userId == null) {
                sendAuthError(ctx, "无效的token");
                return;
            }
        }
        
        // 如果没有userId，认证失败
        if (userId == null) {
            sendAuthError(ctx, "认证失败，缺少用户ID或token");
            return;
        }
        
        // 认证用户
        authenticateUser(ctx, userId);
        
        // 发送认证成功响应
        WebSocketMessage authResponse = new WebSocketMessage();
        authResponse.setType(WebSocketMessageType.AUTH_SUCCESS);
        authResponse.setContent("认证成功");
        authResponse.setSenderId(userId);
        authResponse.setTimestamp(System.currentTimeMillis());
        ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(authResponse)));
        
        log.debug("用户认证成功: {}", userId);
    }
    
    /**
     * 从token获取用户ID
     */
    private Long getUserIdFromToken(String token) {
        // 首先从内存缓存中查找
        if (tokenUserMap.containsKey(token)) {
            return tokenUserMap.get(token);
        }
        
        // 从Redis中查找（JSESSIONID映射）
        String key = "spring:session:" + token;
        try {
            Object userId = redisTemplate.opsForHash().get(key, "sessionAttr:LOGIN_USER_ID");
            if (userId != null) {
                Long userIdValue = Long.parseLong(userId.toString());
                // 缓存到内存
                tokenUserMap.put(token, userIdValue);
                return userIdValue;
            }
        } catch (Exception e) {
            log.debug("从Redis获取用户ID失败", e);
        }
        
        // 兼容模式：直接将token解析为用户ID（用于Postman测试）
        try {
            if (StringUtils.isNumeric(token)) {
                return Long.parseLong(token);
            }
        } catch (NumberFormatException e) {
            log.debug("token不是有效的用户ID: {}", token);
        }
        
        return null;
    }
    
    /**
     * 设置用户认证状态
     */
    private void authenticateUser(ChannelHandlerContext ctx, Long userId) {
        // 设置用户ID和认证状态
        ctx.channel().attr(USER_ID_KEY).set(userId);
        ctx.channel().attr(AUTH_KEY).set(true);
        
        // 将通道添加到通道管理器
        channelManager.addChannel(userId, ctx.channel());
    }
    
    /**
     * 发送认证错误消息
     */
    private void sendAuthError(ChannelHandlerContext ctx, String errorMessage) {
        WebSocketMessage authError = new WebSocketMessage();
        authError.setType(WebSocketMessageType.AUTH_ERROR);
        authError.setContent(errorMessage);
        authError.setTimestamp(System.currentTimeMillis());
        ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(authError)));
    }
    
    /**
     * 检查是否已认证
     */
    private boolean isAuthenticated(ChannelHandlerContext ctx) {
        Boolean authenticated = ctx.channel().attr(AUTH_KEY).get();
        return authenticated != null && authenticated;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("WebSocket连接已建立，等待认证");
        
        // 设置认证超时
        if (authTimeoutSeconds > 0) {
            ctx.executor().schedule(() -> {
                if (!isAuthenticated(ctx)) {
                    // 发送超时消息
                    WebSocketMessage timeoutMsg = new WebSocketMessage();
                    timeoutMsg.setType(WebSocketMessageType.ERROR);
                    timeoutMsg.setContent("认证超时，连接将被关闭");
                    timeoutMsg.setTimestamp(System.currentTimeMillis());
                    ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(timeoutMsg)));
                    
                    // 关闭连接
                    log.debug("认证超时，关闭连接");
                    ctx.close();
                }
            }, authTimeoutSeconds, TimeUnit.SECONDS);
        }
        
        ctx.fireChannelActive();
    }
} 