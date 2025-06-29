package com.ubanillx.smartclass.model.vo;

import com.ubanillx.smartclass.model.entity.UserFeedbackReply;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户反馈回复VO
 */
@Data
public class UserFeedbackReplyVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 关联的反馈ID
     */
    private Long feedbackId;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 发送者角色：0-用户，1-管理员
     */
    private Integer senderRole;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 附件URL
     */
    private String attachment;

    /**
     * 是否已读：0-未读，1-已读
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 发送者信息
     */
    private UserVO sender;

    /**
     * 对象转包装类
     *
     * @param userFeedbackReply
     * @return
     */
    public static UserFeedbackReplyVO objToVo(UserFeedbackReply userFeedbackReply) {
        if (userFeedbackReply == null) {
            return null;
        }
        UserFeedbackReplyVO userFeedbackReplyVO = new UserFeedbackReplyVO();
        BeanUtils.copyProperties(userFeedbackReply, userFeedbackReplyVO);
        return userFeedbackReplyVO;
    }

    private static final long serialVersionUID = 1L;
} 