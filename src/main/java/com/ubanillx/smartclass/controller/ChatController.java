package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.privateMessage.PrivateMessageAddRequest;
import com.ubanillx.smartclass.model.dto.privateMessage.PrivateMessageQueryRequest;
import com.ubanillx.smartclass.model.dto.privatechatsession.PrivateChatSessionAddRequest;
import com.ubanillx.smartclass.model.dto.privatechatsession.PrivateChatSessionQueryRequest;
import com.ubanillx.smartclass.model.dto.chat.ChatMessageAddRequest;
import com.ubanillx.smartclass.model.entity.PrivateChatSession;
import com.ubanillx.smartclass.model.entity.PrivateMessage;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.PrivateChatSessionVO;
import com.ubanillx.smartclass.model.vo.PrivateMessageVO;
import com.ubanillx.smartclass.service.ChatMessageService;
import com.ubanillx.smartclass.service.ChatSseService;
import com.ubanillx.smartclass.service.PrivateChatSessionService;
import com.ubanillx.smartclass.service.PrivateMessageService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 私聊相关接口
 * 提供用户间私聊功能，并使用SSE技术实现实时消息推送
 * 所有实时通信均通过SSE（Server-Sent Events）实现，不再使用WebSocket
 */
@RestController
@RequestMapping("/private-chat")
@Slf4j
public class ChatController {

    @Resource
    private PrivateChatSessionService privateChatSessionService;

    @Resource
    private PrivateMessageService privateMessageService;

    @Resource
    private UserService userService;
    
    @Resource
    private ChatSseService chatSseService;
    
    @Resource
    private ChatMessageService chatMessageService;

    // ==================== 会话管理接口 ====================

    /**
     * 创建私聊会话
     *
     * @param request 请求体
     * @param httpServletRequest HTTP请求
     * @return 会话ID
     */
    @PostMapping("/sessions")
    public BaseResponse<Long> createSession(@RequestBody PrivateChatSessionAddRequest request,
                                            HttpServletRequest httpServletRequest) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId1 = request.getUserId1();
        Long userId2 = request.getUserId2();
        
        if (userId2 == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "目标用户ID不能为空");
        }
        
        // 如果没有指定userId1，则使用当前登录用户的ID
        if (userId1 == null) {
            User loginUser = userService.getLoginUser(httpServletRequest);
            userId1 = loginUser.getId();
        } else {
            // 如果指定了userId1，需要验证权限（只有管理员可以为其他用户创建会话）
            User loginUser = userService.getLoginUser(httpServletRequest);
            if (!userService.isAdmin(loginUser) && !loginUser.getId().equals(userId1)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权为其他用户创建聊天会话");
            }
        }
        
        long id = privateChatSessionService.createChatSession(userId1, userId2);
        return ResultUtils.success(id);
    }

    /**
     * 获取用户的所有聊天会话
     *
     * @param userId 用户ID，为空则返回当前登录用户的会话
     * @param request HTTP请求
     * @return 聊天会话列表
     */
    @GetMapping("/sessions/list")
    public BaseResponse<List<PrivateChatSessionVO>> listUserSessions(@RequestParam(required = false) Long userId,
                                                                      HttpServletRequest request) {
        // 如果没有指定用户ID，则使用当前登录用户的ID
        if (userId == null) {
            User loginUser = userService.getLoginUser(request);
            userId = loginUser.getId();
        } else {
            // 如果指定了用户ID，需要验证权限（只有管理员或者用户本人可以查看会话列表）
            User loginUser = userService.getLoginUser(request);
            if (!userService.isAdmin(loginUser) && !loginUser.getId().equals(userId)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权查看其他用户的聊天会话");
            }
        }
        
        List<PrivateChatSessionVO> sessionList = privateChatSessionService.listUserChatSessions(userId, request);
        return ResultUtils.success(sessionList);
    }

    /**
     * 获取与指定用户的聊天会话
     *
     * @param targetUserId 目标用户ID
     * @param request HTTP请求
     * @return 聊天会话
     */
    @GetMapping("/sessions/users/{targetUserId}")
    public BaseResponse<PrivateChatSessionVO> getSessionWithUser(@PathVariable Long targetUserId,
                                                                 HttpServletRequest request) {
        if (targetUserId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "目标用户ID不能为空");
        }
        
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        
        PrivateChatSession chatSession = privateChatSessionService.getOrCreateChatSession(userId, targetUserId);
        PrivateChatSessionVO sessionVO = privateChatSessionService.getPrivateChatSessionVO(chatSession, userId);
        
        return ResultUtils.success(sessionVO);
    }

    /**
     * 获取会话的未读消息数量
     *
     * @param sessionId 会话ID
     * @param request HTTP请求
     * @return 未读消息数量
     */
    @GetMapping("/sessions/{sessionId}/unread/count")
    public BaseResponse<Integer> getSessionUnreadCount(@PathVariable Long sessionId, HttpServletRequest request) {
        if (sessionId == null || sessionId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "会话ID不合法");
        }
        
        User loginUser = userService.getLoginUser(request);
        int count = privateChatSessionService.getUnreadMessageCount(sessionId, loginUser.getId());
        return ResultUtils.success(count);
    }

    /**
     * 获取用户的所有未读消息数量
     *
     * @param request HTTP请求
     * @return 未读消息数量
     */
    @GetMapping("/unread/count")
    public BaseResponse<Integer> getTotalUnreadCount(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        
        // 获取用户的未读消息数量
        int count = chatMessageService.getUnreadMessageCount(loginUser.getId());
        
        return ResultUtils.success(count);
    }

    /**
     * 标记会话中的所有消息为已读
     *
     * @param sessionId 会话ID
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/sessions/{sessionId}/read/all")
    public BaseResponse<Boolean> markSessionMessagesAsRead(@PathVariable Long sessionId, HttpServletRequest request) {
        if (sessionId == null || sessionId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "会话ID不合法");
        }
        
        User loginUser = userService.getLoginUser(request);
        
        // 标记会话所有消息为已读
        boolean result = privateChatSessionService.markAllMessagesAsRead(sessionId, loginUser.getId());
        
        if (result) {
            // 通过SSE实时推送会话已读状态更新
            chatSseService.sendSessionReadStatusUpdate(loginUser.getId(), sessionId.toString());
            
            // 获取更新后的未读消息数量
            int unreadCount = chatMessageService.getUnreadMessageCount(loginUser.getId());
            // 推送未读数量更新
            chatSseService.sendUnreadCountUpdate(loginUser.getId(), unreadCount);
        }
        
        return ResultUtils.success(result);
    }

    // ==================== 消息管理接口 ====================

    /**
     * 获取会话中的消息列表
     *
     * @param sessionId 会话ID
     * @param current 当前页码，默认为1
     * @param size 页大小，默认为20
     * @param request HTTP请求
     * @return 消息列表分页
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public BaseResponse<Page<PrivateMessageVO>> listSessionMessages(
            @PathVariable Long sessionId,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size,
            HttpServletRequest request) {
        if (sessionId == null || sessionId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "会话ID不合法");
        }
        
        // 限制爬虫
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR, "页大小不能超过50");
        
        Page<PrivateMessageVO> messagePage = privateChatSessionService.listSessionMessages(sessionId, current, size, request);
        return ResultUtils.success(messagePage);
    }

    /**
     * 标记消息为已读
     *
     * @param messageId 消息ID
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/messages/{messageId}/read")
    public BaseResponse<Boolean> markMessageAsRead(@PathVariable Long messageId, 
                                                @RequestParam(required = false) String sessionId,
                                                HttpServletRequest request) {
        if (messageId == null || messageId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息ID不合法");
        }
        
        User loginUser = userService.getLoginUser(request);
        
        // 标记消息为已读
        boolean result = privateMessageService.markMessageAsRead(messageId, request);
        
        if (result) {
            // 通过SSE实时推送已读状态更新
            chatSseService.sendReadStatusUpdate(loginUser.getId(), messageId, sessionId, true);
            
            // 获取更新后的未读消息数量
            int unreadCount = chatMessageService.getUnreadMessageCount(loginUser.getId());
            // 推送未读数量更新
            chatSseService.sendUnreadCountUpdate(loginUser.getId(), unreadCount);
        }
        
        return ResultUtils.success(result);
    }

    /**
     * 批量标记消息为已读
     *
     * @param messageIds 消息ID列表
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/messages/batch/read")
    public BaseResponse<Boolean> markMessagesAsRead(@RequestBody List<Long> messageIds, 
                                                 @RequestParam(required = false) String sessionId,
                                                 HttpServletRequest request) {
        if (messageIds == null || messageIds.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息ID列表不能为空");
        }
        
        User loginUser = userService.getLoginUser(request);
        
        // 标记消息为已读
        boolean result = privateMessageService.markMessagesAsRead(messageIds, request);
        
        if (result) {
            // 通过SSE实时推送批量已读状态更新
            chatSseService.sendBatchReadStatusUpdate(loginUser.getId(), messageIds, sessionId, true);
            
            // 获取更新后的未读消息数量
            int unreadCount = chatMessageService.getUnreadMessageCount(loginUser.getId());
            // 推送未读数量更新
            chatSseService.sendUnreadCountUpdate(loginUser.getId(), unreadCount);
        }
        
        return ResultUtils.success(result);
    }

    /**
     * 标记所有消息为已读
     *
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/messages/read/all")
    public BaseResponse<Boolean> markAllMessagesAsRead(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        
        // 标记所有消息为已读
        boolean result = privateMessageService.markAllMessagesAsRead(loginUser.getId());
        
        if (result) {
            // 推送未读数量更新
            chatSseService.sendUnreadCountUpdate(loginUser.getId(), 0);
        }
        
        return ResultUtils.success(result);
    }

    // ==================== SSE实时通信接口 ====================
    
    /**
     * 建立SSE连接
     * 前端可以通过EventSource订阅此端点获取实时消息推送
     *
     * @param request HTTP请求
     * @return SSE发射器
     */
    @GetMapping("/connect")
    public SseEmitter connect(HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new RuntimeException("用户未登录");
        }
        
        // 创建SSE连接
        SseEmitter emitter = chatSseService.createConnection(loginUser.getId());
        
        log.info("用户 {} 创建了SSE连接", loginUser.getId());
        return emitter;
    }
    
    /**
     * 关闭SSE连接
     *
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/disconnect")
    public BaseResponse<Boolean> disconnect(HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }
        
        // 关闭SSE连接
        chatSseService.closeConnection(loginUser.getId());
        
        log.info("用户 {} 关闭了SSE连接", loginUser.getId());
        return ResultUtils.success(true);
    }
    
    /**
     * 发送用户间私聊消息
     * 消息会持久化存储并实时推送给接收者
     *
     * @param privateMessageAddRequest 私聊消息请求
     * @param request HTTP请求
     * @return 消息ID
     */
    @PostMapping("/send")
    public BaseResponse<Long> sendMessage(@RequestBody PrivateMessageAddRequest privateMessageAddRequest,
                                          HttpServletRequest request) {
        // 参数校验
        if (privateMessageAddRequest == null || 
            StringUtils.isBlank(privateMessageAddRequest.getContent()) ||
            privateMessageAddRequest.getReceiverId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不完整");
        }
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        Long senderId = loginUser.getId();
        Long receiverId = privateMessageAddRequest.getReceiverId();
        
        // 不能给自己发消息
        if (senderId.equals(receiverId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能给自己发送消息");
        }
        
        // 保存消息并发送实时通知
        long messageId = privateMessageService.sendPrivateMessage(senderId, privateMessageAddRequest, request);
        
        return ResultUtils.success(messageId);
    }
    
    /**
     * 发送系统通知
     * 仅管理员可调用
     *
     * @param content 通知内容
     * @param userId 接收者ID，为空则广播给所有在线用户
     * @param request HTTP请求
     * @return 发送结果
     */
    @PostMapping("/notify")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> sendSystemNotification(@RequestParam String content,
                                                     @RequestParam(required = false) Long userId,
                                                     @RequestParam(required = false) Object data,
                                                     HttpServletRequest request) {
        // 参数校验
        if (StringUtils.isBlank(content)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "通知内容不能为空");
        }
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }
        
        boolean result;
        if (userId != null && userId > 0) {
            // 发送给指定用户
            result = chatSseService.sendSystemNotification(userId, content, data);
            log.info("管理员 {} 发送系统通知给用户 {}: {}", loginUser.getId(), userId, content);
        } else {
            // 广播给所有在线用户
            result = chatSseService.broadcastSystemNotification(content, data);
            log.info("管理员 {} 广播系统通知: {}", loginUser.getId(), content);
        }
        
        return ResultUtils.success(result);
    }

    /**
     * 获取实时聊天状态
     * 用于客户端获取当前聊天连接状态
     *
     * @param request HTTP请求
     * @return 聊天状态
     */
    @GetMapping("/status")
    public BaseResponse<Map<String, Object>> getChatStatus(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Map<String, Object> statusInfo = new HashMap<>();
        
        // 获取用户总未读消息数
        int unreadCount = chatMessageService.getUnreadMessageCount(loginUser.getId());
        
        // 获取在线状态
        boolean online = true; // 由于用户能调用此API，所以一定在线
        
        statusInfo.put("userId", loginUser.getId());
        statusInfo.put("userName", loginUser.getUserName());
        statusInfo.put("unreadCount", unreadCount);
        statusInfo.put("online", online);
        statusInfo.put("timestamp", System.currentTimeMillis());
        
        // 通过SSE推送一次未读数量更新
        chatSseService.sendUnreadCountUpdate(loginUser.getId(), unreadCount);
        
        return ResultUtils.success(statusInfo);
    }
} 