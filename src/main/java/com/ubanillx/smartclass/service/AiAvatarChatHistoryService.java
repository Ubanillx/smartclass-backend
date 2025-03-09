package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.model.entity.AiAvatarChatHistory;

import java.util.List;

/**
* @author liulo
* @description 针对表【ai_avatar_chat_history(AI分身聊天历史)】的数据库操作Service
* @createDate 2025-03-09 11:50:28
*/
public interface AiAvatarChatHistoryService extends BaseRelationService<AiAvatarChatHistory> {

    /**
     * 添加聊天记录
     * @param chatHistory 聊天记录信息
     * @return 是否成功
     */
    boolean addChatHistory(AiAvatarChatHistory chatHistory);

    /**
     * 批量添加聊天记录
     * @param chatHistoryList 聊天记录列表
     * @return 是否成功
     */
    boolean batchAddChatHistory(List<AiAvatarChatHistory> chatHistoryList);

    /**
     * 获取用户与AI分身的聊天历史
     * @param userId 用户ID
     * @param aiAvatarId AI分身ID
     * @param sessionId 会话ID
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页结果
     */
    Page<AiAvatarChatHistory> getChatHistory(Long userId, Long aiAvatarId, String sessionId, int current, int size);

    /**
     * 获取用户与AI分身的会话列表
     * @param userId 用户ID
     * @param aiAvatarId AI分身ID
     * @return 会话ID列表
     */
    List<String> getSessionList(Long userId, Long aiAvatarId);

    /**
     * 清空用户与AI分身的聊天历史
     * @param userId 用户ID
     * @param aiAvatarId AI分身ID
     * @param sessionId 会话ID，如果为null则清空所有会话
     * @return 是否成功
     */
    boolean clearChatHistory(Long userId, Long aiAvatarId, String sessionId);

    /**
     * 获取用户最近的聊天记录
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 聊天记录列表
     */
    List<AiAvatarChatHistory> getRecentChatHistory(Long userId, int limit);
}
