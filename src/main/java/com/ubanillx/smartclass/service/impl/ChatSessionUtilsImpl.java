package com.ubanillx.smartclass.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.constant.PrivateChatSessionConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.mapper.PrivateChatSessionMapper;
import com.ubanillx.smartclass.mapper.PrivateMessageMapper;
import com.ubanillx.smartclass.model.entity.PrivateChatSession;
import com.ubanillx.smartclass.model.entity.PrivateMessage;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.service.ChatSessionUtils;
import com.ubanillx.smartclass.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 聊天会话工具服务实现类
 */
@Service
@Slf4j
public class ChatSessionUtilsImpl implements ChatSessionUtils {

    @Resource
    private PrivateChatSessionMapper privateChatSessionMapper;
    
    @Resource
    private PrivateMessageMapper privateMessageMapper;
    
    @Resource
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // Redis Key前缀
    private static final String SESSION_KEY = "chat:session:";
    private static final String SESSION_USER_KEY = "chat:session:user:";
    private static final String UNREAD_MESSAGES_KEY = "chat:unread:";
    private static final String MESSAGE_KEY = "chat:message:";
    private static final int SESSION_EXPIRE_DAYS = 30; // 会话缓存过期时间，30天
    private static final int MESSAGE_EXPIRE_DAYS = 7; // 消息缓存过期时间，7天
    
    @Override
    public PrivateChatSession getOrCreateChatSession(Long userId1, Long userId2) {
        if (userId1 == null || userId2 == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        if (userId1.equals(userId2)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能创建与自己的聊天会话");
        }
        
        // 确保ID顺序，保证一致性
        if (userId1 > userId2) {
            Long temp = userId1;
            userId1 = userId2;
            userId2 = temp;
        }
        
        // 优先从Redis获取会话
        String sessionKey = String.format("%s%d_%d", SESSION_KEY, userId1, userId2);
        Object sessionObj = redisTemplate.opsForValue().get(sessionKey);
        
        if (sessionObj != null) {
            if (sessionObj instanceof PrivateChatSession) {
                return (PrivateChatSession) sessionObj;
            } else {
                // 处理可能的类型转换问题
                PrivateChatSession session = JSON.parseObject(JSON.toJSONString(sessionObj), PrivateChatSession.class);
                return session;
            }
        }
        
        // Redis中没有，查询数据库
        QueryWrapper<PrivateChatSession> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId1", userId1).eq("userId2", userId2);
        
        PrivateChatSession chatSession = privateChatSessionMapper.selectOne(queryWrapper);
        if (chatSession != null) {
            // 缓存会话信息
            redisTemplate.opsForValue().set(sessionKey, chatSession, SESSION_EXPIRE_DAYS, TimeUnit.DAYS);
            return chatSession;
        }
        
        // 创建新的聊天会话
        return createChatSession(userId1, userId2);
    }
    
    @Override
    public void updateSessionLastMessageTime(PrivateChatSession chatSession) {
        if (chatSession == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "会话不能为空");
        }
        
        chatSession.setLastMessageTime(new Date());
        privateChatSessionMapper.updateById(chatSession);
        
        // 更新缓存
        Long userId1 = chatSession.getUserId1();
        Long userId2 = chatSession.getUserId2();
        String sessionKey = String.format("%s%d_%d", SESSION_KEY, userId1, userId2);
        redisTemplate.opsForValue().set(sessionKey, chatSession, SESSION_EXPIRE_DAYS, TimeUnit.DAYS);
    }
    
    @Override
    public Long getUnreadMessageCount(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        // 优先从Redis获取未读数量
        Long count = redisTemplate.opsForList().size(UNREAD_MESSAGES_KEY + userId);
        if (count != null) {
            return count;
        }
        
        // Redis中没有，从数据库查询
        QueryWrapper<PrivateMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiverId", userId)
                .eq("isRead", PrivateChatSessionConstant.UNREAD);
        
        return privateMessageMapper.selectCount(queryWrapper);
    }
    
    /**
     * 创建新的聊天会话
     */
    private PrivateChatSession createChatSession(Long userId1, Long userId2) {
        // 确保用户存在
        User user1 = userService.getById(userId1);
        User user2 = userService.getById(userId2);
        if (user1 == null || user2 == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        
        // 创建新的聊天会话
        PrivateChatSession chatSession = new PrivateChatSession();
        chatSession.setUserId1(userId1);
        chatSession.setUserId2(userId2);
        chatSession.setLastMessageTime(new Date());
        
        privateChatSessionMapper.insert(chatSession);
        
        // 缓存会话信息
        String sessionKey = String.format("%s%d_%d", SESSION_KEY, userId1, userId2);
        redisTemplate.opsForValue().set(sessionKey, chatSession, SESSION_EXPIRE_DAYS, TimeUnit.DAYS);
        
        // 缓存用户的会话列表
        redisTemplate.opsForSet().add(SESSION_USER_KEY + userId1, chatSession.getId());
        redisTemplate.opsForSet().add(SESSION_USER_KEY + userId2, chatSession.getId());
        
        return chatSession;
    }

    @Override
    public void markMessageAsRead(Long messageId, Long userId) {
        if (messageId == null || userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        
        // 从数据库更新消息状态
        PrivateMessage message = privateMessageMapper.selectById(messageId);
        if (message != null && message.getReceiverId().equals(userId)) {
            message.setIsRead(PrivateChatSessionConstant.READ);
            privateMessageMapper.updateById(message);
            
            // 更新缓存
            redisTemplate.opsForValue().set(MESSAGE_KEY + messageId, message, MESSAGE_EXPIRE_DAYS, TimeUnit.DAYS);
            
            // 从未读列表中移除
            redisTemplate.opsForList().remove(UNREAD_MESSAGES_KEY + userId, 0, messageId.toString());
        }
    }
} 