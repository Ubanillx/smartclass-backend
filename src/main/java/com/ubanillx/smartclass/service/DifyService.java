package com.ubanillx.smartclass.service;

import com.ubanillx.smartclass.model.entity.AiAvatarChatHistory;

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