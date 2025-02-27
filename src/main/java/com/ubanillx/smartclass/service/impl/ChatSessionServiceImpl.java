package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.model.entity.ChatSession;
import com.ubanillx.smartclass.service.ChatSessionService;
import com.ubanillx.smartclass.mapper.ChatSessionMapper;
import org.springframework.stereotype.Service;

/**
* @author liulo
* @description 针对表【chat_session(AI聊天会话)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:02
*/
@Service
public class ChatSessionServiceImpl extends ServiceImpl<ChatSessionMapper, ChatSession>
    implements ChatSessionService{

}




