package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.aiavatar.AiAvatarChatRequest;
import com.ubanillx.smartclass.model.entity.AiAvatar;
import com.ubanillx.smartclass.model.entity.AiAvatarChatHistory;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.AiAvatarChatVO;
import com.ubanillx.smartclass.service.AiAvatarChatHistoryService;
import com.ubanillx.smartclass.service.AiAvatarService;
import com.ubanillx.smartclass.service.UserAiAvatarService;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * AI分身聊天接口
 */
@RestController
@RequestMapping("/ai/chat")
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

    /**
     * 发送消息给AI分身
     *
     * @param chatRequest
     * @param request
     * @return
     */
    @PostMapping("/send")
    public BaseResponse<AiAvatarChatVO> sendMessage(@RequestBody AiAvatarChatRequest chatRequest, HttpServletRequest request) {
        if (chatRequest == null || chatRequest.getAiAvatarId() == null || StringUtils.isBlank(chatRequest.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 判断AI分身是否存在
        AiAvatar aiAvatar = aiAvatarService.getAiAvatarById(chatRequest.getAiAvatarId());
        ThrowUtils.throwIf(aiAvatar == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 生成会话ID
        String sessionId = chatRequest.getSessionId();
        if (StringUtils.isBlank(sessionId)) {
            sessionId = UUID.randomUUID().toString();
        }
        
        // 记录用户消息
        AiAvatarChatHistory userMessage = new AiAvatarChatHistory();
        userMessage.setUserId(loginUser.getId());
        userMessage.setAiAvatarId(chatRequest.getAiAvatarId());
        userMessage.setSessionId(sessionId);
        userMessage.setMessageType("user");
        userMessage.setContent(chatRequest.getContent());
        userMessage.setTokens(calculateTokens(chatRequest.getContent()));
        userMessage.setCreateTime(new Date());
        
        boolean saveResult = aiAvatarChatHistoryService.addChatHistory(userMessage);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "保存聊天记录失败");
        
        // 更新用户使用AI分身的记录
        userAiAvatarService.useAiAvatar(loginUser.getId(), chatRequest.getAiAvatarId());
        
        // TODO: 调用AI服务获取回复
        // 这里模拟AI回复
        String aiReply = "这是来自" + aiAvatar.getName() + "的回复：" + chatRequest.getContent();
        
        // 记录AI回复
        AiAvatarChatHistory aiMessage = new AiAvatarChatHistory();
        aiMessage.setUserId(loginUser.getId());
        aiMessage.setAiAvatarId(chatRequest.getAiAvatarId());
        aiMessage.setSessionId(sessionId);
        aiMessage.setMessageType("ai");
        aiMessage.setContent(aiReply);
        aiMessage.setTokens(calculateTokens(aiReply));
        aiMessage.setCreateTime(new Date());
        
        saveResult = aiAvatarChatHistoryService.addChatHistory(aiMessage);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "保存AI回复失败");
        
        // 构建返回结果
        AiAvatarChatVO chatVO = new AiAvatarChatVO();
        BeanUtils.copyProperties(aiMessage, chatVO);
        chatVO.setAiAvatarName(aiAvatar.getName());
        chatVO.setAiAvatarUrl(aiAvatar.getAvatarUrl());
        
        return ResultUtils.success(chatVO);
    }

    /**
     * 获取聊天历史
     *
     * @param aiAvatarId
     * @param sessionId
     * @param current
     * @param size
     * @param request
     * @return
     */
    @GetMapping("/history")
    public BaseResponse<Page<AiAvatarChatVO>> getChatHistory(
            @RequestParam Long aiAvatarId,
            @RequestParam String sessionId,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        
        if (aiAvatarId == null || aiAvatarId <= 0 || StringUtils.isBlank(sessionId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 判断AI分身是否存在
        AiAvatar aiAvatar = aiAvatarService.getAiAvatarById(aiAvatarId);
        ThrowUtils.throwIf(aiAvatar == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 获取聊天历史
        Page<AiAvatarChatHistory> chatHistoryPage = aiAvatarChatHistoryService.getChatHistory(
                loginUser.getId(), aiAvatarId, sessionId, current, size);
        
        // 转换为VO
        Page<AiAvatarChatVO> chatVOPage = new Page<>(current, size, chatHistoryPage.getTotal());
        List<AiAvatarChatVO> chatVOList = chatHistoryPage.getRecords().stream()
                .map(chatHistory -> {
                    AiAvatarChatVO chatVO = new AiAvatarChatVO();
                    BeanUtils.copyProperties(chatHistory, chatVO);
                    chatVO.setAiAvatarName(aiAvatar.getName());
                    chatVO.setAiAvatarUrl(aiAvatar.getAvatarUrl());
                    return chatVO;
                })
                .collect(Collectors.toList());
        chatVOPage.setRecords(chatVOList);
        
        return ResultUtils.success(chatVOPage);
    }

    /**
     * 获取会话列表
     *
     * @param aiAvatarId
     * @param request
     * @return
     */
    @GetMapping("/sessions")
    public BaseResponse<List<String>> getSessionList(@RequestParam Long aiAvatarId, HttpServletRequest request) {
        if (aiAvatarId == null || aiAvatarId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 判断AI分身是否存在
        AiAvatar aiAvatar = aiAvatarService.getAiAvatarById(aiAvatarId);
        ThrowUtils.throwIf(aiAvatar == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 获取会话列表
        List<String> sessionList = aiAvatarChatHistoryService.getSessionList(loginUser.getId(), aiAvatarId);
        return ResultUtils.success(sessionList);
    }

    /**
     * 清空聊天历史
     *
     * @param aiAvatarId
     * @param sessionId
     * @param request
     * @return
     */
    @PostMapping("/clear")
    public BaseResponse<Boolean> clearChatHistory(
            @RequestParam Long aiAvatarId,
            @RequestParam(required = false) String sessionId,
            HttpServletRequest request) {
        
        if (aiAvatarId == null || aiAvatarId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 清空聊天历史
        boolean result = aiAvatarChatHistoryService.clearChatHistory(loginUser.getId(), aiAvatarId, sessionId);
        return ResultUtils.success(result);
    }

    /**
     * 计算文本的token数量（简单实现）
     *
     * @param text
     * @return
     */
    private int calculateTokens(String text) {
        if (StringUtils.isBlank(text)) {
            return 0;
        }
        // 简单实现，按照字符数计算
        return text.length();
    }
} 