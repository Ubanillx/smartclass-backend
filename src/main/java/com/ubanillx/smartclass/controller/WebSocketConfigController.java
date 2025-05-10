package com.ubanillx.smartclass.controller;

import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.config.NettyWebSocketConfig;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.WebSocketConfigVO;
import com.ubanillx.smartclass.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * WebSocket配置控制器
 * 提供WebSocket相关配置和接口
 */
@RestController
@RequestMapping("/websocket")
@Api(tags = "WebSocket配置")
@Slf4j
public class WebSocketConfigController {

    @Value("${netty.websocket.port:12346}")
    private String websocketPort;
    
    @Value("${netty.websocket.heartbeat-timeout:60}")
    private int heartbeatTimeout;
    
    @Value("${netty.websocket.auth-timeout:10}")
    private int authTimeout;
    
    @Autowired
    private NettyWebSocketConfig webSocketConfig;
    
    @Resource
    private UserService userService;
    
    /**
     * 获取WebSocket配置信息
     */
    @GetMapping("/config")
    @ApiOperation(value = "获取WebSocket配置", notes = "返回WebSocket服务器配置信息，用于客户端连接")
    public BaseResponse<WebSocketConfigVO> getWebSocketConfig(HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 构建WebSocket配置信息
        WebSocketConfigVO configVO = new WebSocketConfigVO();
        // 使用当前服务器地址，只替换端口
        String serverUrl = request.getRequestURL().toString();
        // 获取协议部分 (http或https)
        String protocol = request.getScheme();
        // 将http替换为ws, https替换为wss
        String wsProtocol = protocol.equals("https") ? "wss" : "ws";
        // 获取服务器地址和端口（不包含路径）
        String serverAddress = request.getServerName();
        
        // 构建WebSocket URL
        String wsUrl = wsProtocol + "://" + serverAddress + ":" + webSocketConfig.getPort() + "/ws";
        
        configVO.setWsUrl(wsUrl);
        configVO.setPort(webSocketConfig.getPort());
        configVO.setHeartbeatTimeout(webSocketConfig.getHeartbeatTimeout());
        configVO.setAuthTimeout(webSocketConfig.getAuthTimeout());
        configVO.setUserId(loginUser.getId());
        
        return ResultUtils.success(configVO);
    }
    
    /**
     * 兼容旧版配置获取方式
     */
    @GetMapping("/old-config")
    @ApiOperation(value = "获取WebSocket配置(旧版)", notes = "旧版配置获取方式，保留向后兼容")
    public BaseResponse<WebSocketConfigVO> getOldWebSocketConfig(HttpServletRequest request) {
        WebSocketConfigVO config = new WebSocketConfigVO();
        try {
            String host = getHostAddress(request);
            config.setWsUrl("ws://" + host + ":" + websocketPort + "/ws");
            config.setPort(Integer.parseInt(websocketPort));
            config.setHeartbeatTimeout(heartbeatTimeout);
            config.setAuthTimeout(authTimeout);
            
            // 获取当前登录用户
            try {
                User loginUser = userService.getLoginUser(request);
                if (loginUser != null) {
                    config.setUserId(loginUser.getId());
                }
            } catch (Exception e) {
                log.warn("获取登录用户信息失败，WebSocket配置将不包含用户ID", e);
            }
            
            return ResultUtils.success(config);
        } catch (Exception e) {
            log.error("获取WebSocket配置失败", e);
            return ResultUtils.error(500, "获取WebSocket配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取主机地址
     */
    private String getHostAddress(HttpServletRequest request) throws UnknownHostException {
        String serverName = request.getServerName();
        
        // 尝试获取真实IP，防止反向代理影响
        if (serverName.equals("localhost") || serverName.equals("127.0.0.1")) {
            return InetAddress.getLocalHost().getHostAddress();
        }
        
        return serverName;
    }
} 