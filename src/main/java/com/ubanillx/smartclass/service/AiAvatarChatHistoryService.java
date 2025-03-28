package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.model.entity.AiAvatarChatHistory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.vo.ChatMessageVO;
import com.ubanillx.smartclass.model.vo.ChatSessionVO;

import java.util.List;

/**
* @author liulo
* @description 针对表【ai_avatar_chat_history(AI分身对话历史)】的数据库操作Service
* @createDate 2025-03-24 21:35:44
*/
public interface AiAvatarChatHistoryService extends IService<AiAvatarChatHistory> {

    /**
     * 保存用户与AI分身的聊天记录
     *
     * @param userId 用户ID
     * @param aiAvatarId AI分身ID
     * @param sessionId 会话ID
     * @param messageType 消息类型：user/ai
     * @param content 消息内容
     * @return 是否保存成功
     */
    boolean saveMessage(Long userId, Long aiAvatarId, String sessionId, String messageType, String content);
    
    /**
     * 创建新的会话
     *
     * @param userId 用户ID
     * @param aiAvatarId AI分身ID
     * @return 新的会话ID
     */
    String createNewSession(Long userId, Long aiAvatarId);
    
    /**
     * 获取用户与指定AI分身的会话列表
     *
     * @param userId 用户ID
     * @param aiAvatarId AI分身ID
     * @return 会话ID和会话名称列表
     */
    List<ChatSessionVO> getUserSessions(Long userId, Long aiAvatarId);
    
    /**
     * 获取特定会话的聊天记录
     *
     * @param sessionId 会话ID
     * @return 聊天记录列表
     */
    List<AiAvatarChatHistory> getSessionHistory(String sessionId);
    
    /**
     * 分页获取特定会话的聊天记录
     *
     * @param sessionId 会话ID
     * @param current 当前页
     * @param size 每页大小
     * @return 聊天记录分页
     */
    Page<AiAvatarChatHistory> getSessionHistoryPage(String sessionId, long current, long size);
    
    /**
     * 更新会话名称
     *
     * @param sessionId 会话ID
     * @param sessionName 会话名称
     * @return 是否更新成功
     */
    boolean updateSessionName(String sessionId, String sessionName);
    
    /**
     * 删除会话及其所有聊天记录
     *
     * @param sessionId 会话ID
     * @param userId 用户ID (用于验证权限)
     * @return 是否删除成功
     */
    boolean deleteSession(String sessionId, Long userId);
    
    /**
     * 完整删除会话（同时删除本地和远程Dify会话）
     *
     * @param sessionId 会话ID
     * @param userId 用户ID (用于验证权限)
     * @param baseUrl Dify API基础URL
     * @param avatarAuth AI分身授权token
     * @return 是否删除成功
     */
    boolean deleteSessionCompletely(String sessionId, Long userId, String baseUrl, String avatarAuth);
    
    /**
     * 获取用户最近的会话列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近的会话列表
     */
    List<ChatSessionVO> getRecentSessions(Long userId, int limit);
    
    /**
     * 获取用户的所有聊天消息
     *
     * @param userId 用户ID
     * @param aiAvatarId AI分身ID (可选)
     * @return 聊天消息VO列表
     */
    List<ChatMessageVO> getUserMessages(Long userId, Long aiAvatarId);
    
    /**
     * 根据会话ID更新会话总结
     * 
     * @param sessionId 会话ID
     * @param summary 会话总结
     * @return 是否更新成功
     */
    boolean updateSessionSummary(String sessionId, String summary);
}
