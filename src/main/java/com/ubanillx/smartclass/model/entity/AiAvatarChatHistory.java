package com.ubanillx.smartclass.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * AI分身对话历史
 * @TableName ai_avatar_chat_history
 */
@TableName(value ="ai_avatar_chat_history")
@Data
public class AiAvatarChatHistory implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * AI分身id
     */
    private Long aiAvatarId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 会话总结标题
     */
    private String sessionName;

    /**
     * 消息类型：user/ai
     */
    private String messageType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息token数
     */
    private Integer tokens;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}