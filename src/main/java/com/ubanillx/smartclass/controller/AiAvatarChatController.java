package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.model.dto.chat.ChatMessageAddRequest;
import com.ubanillx.smartclass.model.dto.chat.ChatMessageQueryRequest;
import com.ubanillx.smartclass.model.dto.chat.ChatSessionQueryRequest;
import com.ubanillx.smartclass.model.dto.chat.ChatSessionUpdateRequest;
import com.ubanillx.smartclass.model.entity.AiAvatar;
import com.ubanillx.smartclass.model.entity.AiAvatarChatHistory;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.ChatMessageVO;
import com.ubanillx.smartclass.model.vo.ChatSessionVO;
import com.ubanillx.smartclass.service.AiAvatarChatHistoryService;
import com.ubanillx.smartclass.service.AiAvatarService;
import com.ubanillx.smartclass.service.DifyService;
import com.ubanillx.smartclass.service.UserAiAvatarService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * AI分身聊天接口
 */
@RestController
@RequestMapping("/chat")
@Slf4j
public class AiAvatarChatController {

    @Resource
    private AiAvatarChatHistoryService aiAvatarChatHistoryService;
    
    @Resource
    private AiAvatarService aiAvatarService;
    
    @Resource
    private UserAiAvatarService userAiAvatarService;
    
    @Resource
    private UserService userService;
    
    @Resource
    private DifyService difyService;
    
    /**
     * 创建新会话
     *
     * @param aiAvatarId AI分身ID
     * @param request HTTP请求
     * @return 会话ID
     */
    @PostMapping("/session/create")
    public BaseResponse<String> createSession(@RequestParam Long aiAvatarId, HttpServletRequest request) {
        if (aiAvatarId == null || aiAvatarId <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        
        // 记录用户使用AI分身
        userAiAvatarService.useAiAvatar(loginUser.getId(), aiAvatarId);
        
        String sessionId = aiAvatarChatHistoryService.createNewSession(loginUser.getId(), aiAvatarId);
        
        return ResultUtils.success(sessionId);
    }
    
    /**
     * 发送消息（阻塞模式）
     *
     * @param chatMessageAddRequest 消息请求
     * @param request HTTP请求
     * @return 消息内容
     */
    @PostMapping("/message/send")
    public BaseResponse<ChatMessageVO> sendMessage(@RequestBody ChatMessageAddRequest chatMessageAddRequest, HttpServletRequest request) {
        if (chatMessageAddRequest == null || StringUtils.isBlank(chatMessageAddRequest.getContent())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        
        Long aiAvatarId = chatMessageAddRequest.getAiAvatarId();
        if (aiAvatarId == null || aiAvatarId <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "AI分身ID不能为空");
        }
        
        User loginUser = userService.getLoginUser(request);
        
        // 获取或创建会话ID
        String sessionId = chatMessageAddRequest.getSessionId();
        if (StringUtils.isBlank(sessionId)) {
            // 直接创建新会话
            sessionId = aiAvatarChatHistoryService.createNewSession(loginUser.getId(), aiAvatarId);
            log.info("创建新会话: {}", sessionId);
        }
        
        try {
            // 获取AI分身信息
            AiAvatar aiAvatar = aiAvatarService.getById(aiAvatarId);
            if (aiAvatar == null) {
                return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "AI分身不存在");
            }
            
            String baseUrl = aiAvatar.getBaseUrl();
            if (StringUtils.isBlank(baseUrl)) {
                return ResultUtils.error(ErrorCode.OPERATION_ERROR, "AI分身API地址不存在");
            }
            
            String avatarAuth = aiAvatar.getAvatarAuth();
            if (StringUtils.isBlank(avatarAuth)) {
                return ResultUtils.error(ErrorCode.OPERATION_ERROR, "AI分身鉴权信息不存在");
            }
            
            // 使用DifyService发送消息
            AiAvatarChatHistory result = difyService.sendChatMessage(
                    loginUser.getId(),
                    aiAvatarId,
                    sessionId,
                    chatMessageAddRequest.getContent(),
                    baseUrl,
                    avatarAuth
            );
            
            // 将结果转换为VO对象
            ChatMessageVO chatMessageVO = new ChatMessageVO();
            BeanUtils.copyProperties(result, chatMessageVO);
            
            // 填充额外信息
            chatMessageVO.setAiAvatarName(aiAvatar.getName());
            chatMessageVO.setAiAvatarImgUrl(aiAvatar.getAvatarImgUrl());
            
            // 获取用户信息
            User user = userService.getById(loginUser.getId());
            if (user != null) {
                chatMessageVO.setUserName(user.getUserName());
                chatMessageVO.setUserAvatar(user.getUserAvatar());
            }
            
            return ResultUtils.success(chatMessageVO);
        } catch (Exception e) {
            log.error("Error sending message", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "发送消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送消息（流式模式）
     *
     * @param chatMessageAddRequest 消息请求
     * @param request HTTP请求
     * @return 事件源
     */
    @PostMapping("/message/stream")
    public SseEmitter sendMessageStream(@RequestBody ChatMessageAddRequest chatMessageAddRequest, HttpServletRequest request) {
        if (chatMessageAddRequest == null || StringUtils.isBlank(chatMessageAddRequest.getContent())) {
            throw new RuntimeException("请求参数错误");
        }
        
        Long aiAvatarId = chatMessageAddRequest.getAiAvatarId();
        if (aiAvatarId == null || aiAvatarId <= 0) {
            throw new RuntimeException("AI分身ID不能为空");
        }
        
        User loginUser = userService.getLoginUser(request);
        
        // 获取或创建会话ID
        String sessionId = chatMessageAddRequest.getSessionId();
        if (StringUtils.isBlank(sessionId)) {
            sessionId = aiAvatarChatHistoryService.createNewSession(loginUser.getId(), aiAvatarId);
            log.info("创建新会话: {}", sessionId);
        }
        
        // 创建SseEmitter，超时设置为5分钟
        final SseEmitter emitter = new SseEmitter(300000L);
        
        try {
            // 获取AI分身信息
            AiAvatar aiAvatar = aiAvatarService.getById(aiAvatarId);
            if (aiAvatar == null) {
                throw new RuntimeException("AI分身不存在");
            }
            
            String baseUrl = aiAvatar.getBaseUrl();
            if (StringUtils.isBlank(baseUrl)) {
                throw new RuntimeException("AI分身API地址不存在");
            }
            
            String avatarAuth = aiAvatar.getAvatarAuth();
            if (StringUtils.isBlank(avatarAuth)) {
                throw new RuntimeException("AI分身鉴权信息不存在");
            }
            
            // 使用异步处理
            final String finalSessionId = sessionId;
            CompletableFuture.runAsync(() -> {
                try {
                    // 使用DifyService发送流式消息
                    difyService.sendChatMessageStreaming(
                            loginUser.getId(),
                            aiAvatarId,
                            finalSessionId,
                            chatMessageAddRequest.getContent(),
                            baseUrl,
                            avatarAuth,
                            new DifyService.DifyStreamCallback() {
                                @Override
                                public void onMessage(String chunk) {
                                    try {
                                        emitter.send(chunk);
                                    } catch (IOException e) {
                                        log.error("Error sending SSE chunk", e);
                                    }
                                }
                                
                                @Override
                                public void onComplete(String fullResponse) {
                                    try {
                                        emitter.complete();
                                    } catch (Exception e) {
                                        log.error("Error completing SSE emitter", e);
                                    }
                                }
                                
                                @Override
                                public void onError(Throwable error) {
                                    try {
                                        emitter.completeWithError(error);
                                    } catch (Exception e) {
                                        log.error("Error completing SSE emitter with error", e);
                                    }
                                }
                            }
                    );
                } catch (Exception e) {
                    log.error("Error in streaming chat", e);
                    try {
                        emitter.completeWithError(e);
                    } catch (Exception ex) {
                        log.error("Error completing SSE emitter with error", ex);
                    }
                }
            });
            
        } catch (Exception e) {
            log.error("Error setting up streaming chat", e);
            emitter.completeWithError(e);
        }
        
        return emitter;
    }
    
    /**
     * 获取会话历史记录
     *
     * @param sessionId 会话ID
     * @param request HTTP请求
     * @return 聊天记录列表
     */
    @GetMapping("/history")
    public BaseResponse<List<ChatMessageVO>> getChatHistory(@RequestParam String sessionId, HttpServletRequest request) {
        if (StringUtils.isBlank(sessionId)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        
        // 检查用户是否有权限访问该会话
        List<AiAvatarChatHistory> historyList = aiAvatarChatHistoryService.getSessionHistory(sessionId);
        
        if (historyList.isEmpty()) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 验证权限
        if (!historyList.get(0).getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 转换为VO对象
        List<ChatMessageVO> chatMessageVOList = historyList.stream().map(history -> {
            ChatMessageVO chatMessageVO = new ChatMessageVO();
            BeanUtils.copyProperties(history, chatMessageVO);
            return chatMessageVO;
        }).collect(Collectors.toList());
        
        return ResultUtils.success(chatMessageVOList);
    }
    
    /**
     * 分页获取会话历史记录
     *
     * @param chatMessageQueryRequest 查询请求
     * @param request HTTP请求
     * @return 分页聊天记录
     */
    @GetMapping("/history/page")
    public BaseResponse<Page<ChatMessageVO>> getChatHistoryByPage(ChatMessageQueryRequest chatMessageQueryRequest, HttpServletRequest request) {
        if (chatMessageQueryRequest == null || StringUtils.isBlank(chatMessageQueryRequest.getSessionId())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        String sessionId = chatMessageQueryRequest.getSessionId();
        
        // 检查用户是否有权限访问该会话
        List<AiAvatarChatHistory> checkList = aiAvatarChatHistoryService.getSessionHistory(sessionId);
        
        if (checkList.isEmpty()) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 验证权限
        if (!checkList.get(0).getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR);
        }
        
        long current = chatMessageQueryRequest.getCurrent();
        long size = chatMessageQueryRequest.getPageSize();
        
        // 限制爬虫
        if (size > 100) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        
        Page<AiAvatarChatHistory> historyPage = aiAvatarChatHistoryService.getSessionHistoryPage(sessionId, current, size);
        
        // 转换为VO对象
        Page<ChatMessageVO> chatMessageVOPage = new Page<>(current, size, historyPage.getTotal());
        List<ChatMessageVO> chatMessageVOList = historyPage.getRecords().stream().map(history -> {
            ChatMessageVO chatMessageVO = new ChatMessageVO();
            BeanUtils.copyProperties(history, chatMessageVO);
            return chatMessageVO;
        }).collect(Collectors.toList());
        
        chatMessageVOPage.setRecords(chatMessageVOList);
        
        return ResultUtils.success(chatMessageVOPage);
    }
    
    /**
     * 获取用户所有会话列表
     *
     * @param aiAvatarId AI分身ID (可选)
     * @param request HTTP请求
     * @return 会话列表
     */
    @GetMapping("/sessions")
    public BaseResponse<List<ChatSessionVO>> getUserSessions(@RequestParam(required = false) Long aiAvatarId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        
        List<ChatSessionVO> sessions = aiAvatarChatHistoryService.getUserSessions(loginUser.getId(), aiAvatarId);
        
        return ResultUtils.success(sessions);
    }
    
    /**
     * 获取用户最近的会话列表
     *
     * @param limit 限制数量
     * @param request HTTP请求
     * @return 最近的会话列表
     */
    @GetMapping("/sessions/recent")
    public BaseResponse<List<ChatSessionVO>> getRecentSessions(@RequestParam(defaultValue = "10") int limit, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        
        List<ChatSessionVO> sessions = aiAvatarChatHistoryService.getRecentSessions(loginUser.getId(), limit);
        
        return ResultUtils.success(sessions);
    }
    
    /**
     * 更新会话名称
     *
     * @param chatSessionUpdateRequest 更新请求
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/session/update")
    public BaseResponse<Boolean> updateSessionName(@RequestBody ChatSessionUpdateRequest chatSessionUpdateRequest, HttpServletRequest request) {
        if (chatSessionUpdateRequest == null || StringUtils.isBlank(chatSessionUpdateRequest.getSessionId()) 
                || StringUtils.isBlank(chatSessionUpdateRequest.getSessionName())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        String sessionId = chatSessionUpdateRequest.getSessionId();
        
        // 检查用户是否有权限访问该会话
        List<AiAvatarChatHistory> checkList = aiAvatarChatHistoryService.getSessionHistory(sessionId);
        
        if (checkList.isEmpty()) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 验证权限
        if (!checkList.get(0).getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR);
        }
        
        boolean result = aiAvatarChatHistoryService.updateSessionName(sessionId, chatSessionUpdateRequest.getSessionName());
        
        return ResultUtils.success(result);
    }
    
    /**
     * 删除会话
     *
     * @param sessionId 会话ID
     * @param request HTTP请求
     * @return 是否成功
     */
    @PostMapping("/session/delete")
    public BaseResponse<Boolean> deleteSession(@RequestParam String sessionId, HttpServletRequest request) {
        if (StringUtils.isBlank(sessionId)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        
        boolean result = aiAvatarChatHistoryService.deleteSession(sessionId, loginUser.getId());
        
        return ResultUtils.success(result);
    }
} 