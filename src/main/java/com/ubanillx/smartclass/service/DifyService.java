package com.ubanillx.smartclass.service;

import com.ubanillx.smartclass.model.entity.AiAvatarChatHistory;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * DifyAPI服务接口
 */
public interface DifyService {

    /**
     * 发送聊天消息并获取回复
     *
     * @param userId       用户ID
     * @param aiAvatarId   AI分身ID
     * @param sessionId    会话ID
     * @param content      发送的消息内容
     * @param baseUrl      Dify API基础URL
     * @param avatarAuth   AI分身授权token
     * @return             保存的消息记录
     */
    AiAvatarChatHistory sendChatMessage(Long userId, Long aiAvatarId, String sessionId, String content, 
                                       String baseUrl, String avatarAuth);
    
    /**
     * 发送带文件的聊天消息并获取回复
     *
     * @param userId       用户ID
     * @param aiAvatarId   AI分身ID
     * @param sessionId    会话ID
     * @param content      发送的消息内容
     * @param fileIds      文件ID列表
     * @param baseUrl      Dify API基础URL
     * @param avatarAuth   AI分身授权token
     * @return             保存的消息记录
     */
    AiAvatarChatHistory sendChatMessageWithFiles(Long userId, Long aiAvatarId, String sessionId, 
                                               String content, List<String> fileIds,
                                               String baseUrl, String avatarAuth);
    
    /**
     * 发送聊天消息流式处理
     *
     * @param userId       用户ID
     * @param aiAvatarId   AI分身ID
     * @param sessionId    会话ID
     * @param content      发送的消息内容
     * @param baseUrl      Dify API基础URL
     * @param avatarAuth   AI分身授权token
     * @param callback     处理响应块的回调函数
     * @return             保存的消息记录
     */
    AiAvatarChatHistory sendChatMessageStreaming(Long userId, Long aiAvatarId, String sessionId, String content, 
                                               String baseUrl, String avatarAuth, DifyStreamCallback callback);
    
    /**
     * 发送带文件的聊天消息流式处理
     *
     * @param userId       用户ID
     * @param aiAvatarId   AI分身ID
     * @param sessionId    会话ID
     * @param content      发送的消息内容
     * @param fileIds      文件ID列表
     * @param baseUrl      Dify API基础URL
     * @param avatarAuth   AI分身授权token
     * @param callback     处理响应块的回调函数
     * @return             保存的消息记录
     */
    AiAvatarChatHistory sendChatMessageStreamingWithFiles(Long userId, Long aiAvatarId, String sessionId, 
                                                        String content, List<String> fileIds,
                                                        String baseUrl, String avatarAuth, DifyStreamCallback callback);
    
    /**
     * 获取会话总结
     * 
     * @param sessionId    会话ID
     * @param baseUrl      Dify API基础URL
     * @param avatarAuth   AI分身授权token
     * @return             会话总结内容
     */
    String getSessionSummary(String sessionId, String baseUrl, String avatarAuth);
    
    /**
     * 删除Dify会话
     *
     * @param userId       用户ID
     * @param sessionId    会话ID
     * @param baseUrl      Dify API基础URL
     * @param avatarAuth   AI分身授权token
     * @return             是否删除成功
     */
    boolean deleteConversation(Long userId, String sessionId, String baseUrl, String avatarAuth);
    
    /**
     * 文字转语音
     *
     * @param userId       用户ID
     * @param text         要转换的文字内容
     * @param messageId    消息ID (可选，如果提供则优先使用)
     * @param baseUrl      Dify API基础URL
     * @param avatarAuth   AI分身授权token
     * @param response     HTTP响应对象，用于直接输出音频流
     * @return             是否成功
     */
    boolean textToAudio(Long userId, String text, String messageId, String baseUrl, 
                      String avatarAuth, HttpServletResponse response);
    
    /**
     * 停止流式响应
     *
     * @param userId       用户ID
     * @param taskId       任务ID，可从流式返回Chunk中获取
     * @param baseUrl      Dify API基础URL
     * @param avatarAuth   AI分身授权token
     * @return             是否成功
     */
    boolean stopStreamingResponse(Long userId, String taskId, String baseUrl, String avatarAuth);
    
    /**
     * 上传文件到Dify（目前仅支持图片）
     *
     * @param userId       用户ID
     * @param fileName     文件名
     * @param fileInputStream 文件输入流
     * @param mimeType     文件MIME类型
     * @param baseUrl      Dify API基础URL
     * @param avatarAuth   AI分身授权token
     * @return             上传文件的ID
     */
    String uploadFile(Long userId, String fileName, InputStream fileInputStream, 
                   String mimeType, String baseUrl, String avatarAuth);
    
    /**
     * 用于处理Dify流式响应的回调接口
     */
    interface DifyStreamCallback {
        /**
         * 处理单个响应块
         * @param chunk 响应块内容
         */
        void onMessage(String chunk);
        
        /**
         * 所有响应块处理完成
         * @param fullResponse 完整响应内容
         */
        void onComplete(String fullResponse);
        
        /**
         * 处理过程中出现错误
         * @param error 错误信息
         */
        void onError(Throwable error);
    }
} 