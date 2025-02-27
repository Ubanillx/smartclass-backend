package com.ubanillx.smartclass.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.model.entity.ChatMessage;
import com.ubanillx.smartclass.service.ChatMessageService;
import com.ubanillx.smartclass.mapper.ChatMessageMapper;
import org.springframework.stereotype.Service;

/**
* @author liulo
* @description 针对表【chat_message(AI聊天消息)】的数据库操作Service实现
* @createDate 2025-02-27 21:52:01
*/
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage>
    implements ChatMessageService{

}




