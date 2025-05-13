package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.chat.ChatMessageAddRequest;
import com.ubanillx.smartclass.model.dto.chat.ChatSessionUpdateRequest;
import com.ubanillx.smartclass.model.dto.chat.StopStreamingRequest;
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
import java.util.List;
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
        // 参数校验
        if (chatMessageAddRequest == null || StringUtils.isBlank(chatMessageAddRequest.getContent()) 
                || chatMessageAddRequest.getAiAvatarId() == null || chatMessageAddRequest.getAiAvatarId() <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误或AI分身ID不能为空");
        }
        
        User loginUser = userService.getLoginUser(request);
        
        try {
            // 调用服务层处理消息发送
            ChatMessageVO chatMessageVO = difyService.handleSendMessageRequest(
                    loginUser.getId(),
                    chatMessageAddRequest.getAiAvatarId(),
                    chatMessageAddRequest.getSessionId(),
                    chatMessageAddRequest.getContent(),
                    chatMessageAddRequest.isEndChat(),
                    aiAvatarChatHistoryService,
                    aiAvatarService,
                    userService
            );
            
            return ResultUtils.success(chatMessageVO);
        } catch (Exception e) {
            log.error("发送消息失败", e);
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
    public SseEmitter sendMessageStream(@RequestBody ChatMessageAddRequest chatMessageAddRequest, 
                                     HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(chatMessageAddRequest == null 
                || StringUtils.isBlank(chatMessageAddRequest.getContent())
                || chatMessageAddRequest.getAiAvatarId() == null 
                || chatMessageAddRequest.getAiAvatarId() <= 0,
                ErrorCode.PARAMS_ERROR, "请求参数不完整");
        
        User loginUser = userService.getLoginUser(request);
        
        try {
            // 调用服务层处理流式消息
            return difyService.handleStreamMessageRequest(
                    loginUser.getId(), 
                    chatMessageAddRequest.getAiAvatarId(), 
                    chatMessageAddRequest.getSessionId(), 
                    chatMessageAddRequest.getContent(),
                    aiAvatarChatHistoryService,
                    aiAvatarService
            );
        } catch (Exception e) {
            log.error("流式聊天请求处理失败: {}", e.getMessage());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "流式聊天请求处理失败: " + e.getMessage());
        }
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
    
    /**
     * 获取用户的所有聊天消息
     *
     * @param aiAvatarId AI分身ID (可选)
     * @param request HTTP请求
     * @return 用户所有聊天消息
     */
    @GetMapping("/messages/list")
    public BaseResponse<List<ChatMessageVO>> getUserChatMessages(@RequestParam(required = false) Long aiAvatarId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        
        List<ChatMessageVO> messages = aiAvatarChatHistoryService.getUserMessages(loginUser.getId(), aiAvatarId);
        
        return ResultUtils.success(messages);
    }
    
    /**
     * 停止流式响应接口
     *
     * @param stopStreamingRequest 请求参数
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/message/stop")
    public BaseResponse<Boolean> stopStreamingResponse(@RequestBody StopStreamingRequest stopStreamingRequest, 
                                                    HttpServletRequest request) {
        // 参数校验
        if (stopStreamingRequest == null || stopStreamingRequest.getAiAvatarId() == null || stopStreamingRequest.getAiAvatarId() <= 0
                || StringUtils.isBlank(stopStreamingRequest.getTaskId())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "请求参数不完整");
        }
        
        User loginUser = userService.getLoginUser(request);
        
        try {
            // 获取AI分身信息
            AiAvatar aiAvatar = aiAvatarService.getById(stopStreamingRequest.getAiAvatarId());
            if (aiAvatar == null || StringUtils.isAnyBlank(aiAvatar.getBaseUrl(), aiAvatar.getAvatarAuth())) {
                return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "AI分身不存在或配置不完整");
            }
            
            // 调用服务停止流式响应
            boolean result = difyService.stopStreamingResponse(
                    loginUser.getId(),
                    stopStreamingRequest.getTaskId(),
                    aiAvatar.getBaseUrl(),
                    aiAvatar.getAvatarAuth()
            );
            
            return result ? ResultUtils.success(true) 
                          : ResultUtils.error(ErrorCode.OPERATION_ERROR, "停止流式响应失败");
        } catch (Exception e) {
            log.error("停止流式响应异常", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "停止流式响应异常: " + e.getMessage());
        }
    }
    
    /**
     * 分页获取用户的聊天历史记录
     *
     * @param current 当前页
     * @param pageSize 每页大小
     * @param request HTTP请求
     * @return 分页聊天记录
     */
    @GetMapping("/user/history")
    public BaseResponse<Page<ChatMessageVO>> getUserHistoryPage(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize,
            HttpServletRequest request) {
        
        // 限制爬虫
        if (pageSize > 100) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "每页大小不能超过100");
        }
        
        User loginUser = userService.getLoginUser(request);
        
        // 获取用户每个会话的最新消息
        Page<AiAvatarChatHistory> historyPage = aiAvatarChatHistoryService.getUserLatestMessagesPage(
                loginUser.getId(), current, pageSize);
        
        // 转换为VO对象
        Page<ChatMessageVO> chatMessageVOPage = new Page<>(current, pageSize, historyPage.getTotal());
        List<ChatMessageVO> chatMessageVOList = historyPage.getRecords().stream().map(history -> {
            ChatMessageVO chatMessageVO = new ChatMessageVO();
            BeanUtils.copyProperties(history, chatMessageVO);
            
            // 获取AI分身信息
            AiAvatar aiAvatar = aiAvatarService.getById(history.getAiAvatarId());
            if (aiAvatar != null) {
                chatMessageVO.setAiAvatarName(aiAvatar.getName());
                chatMessageVO.setAiAvatarImgUrl(aiAvatar.getAvatarImgUrl());
            }
            
            // 获取用户信息
            User user = userService.getById(history.getUserId());
            if (user != null) {
                chatMessageVO.setUserName(user.getUserName());
                chatMessageVO.setUserAvatar(user.getUserAvatar());
            }
            
            return chatMessageVO;
        }).collect(Collectors.toList());
        
        chatMessageVOPage.setRecords(chatMessageVOList);
        
        // 添加分页信息
        chatMessageVOPage.setCurrent(current);
        chatMessageVOPage.setSize(pageSize);
        chatMessageVOPage.setPages((historyPage.getTotal() + pageSize - 1) / pageSize); // 计算总页数
        
        return ResultUtils.success(chatMessageVOPage);
    }
} 