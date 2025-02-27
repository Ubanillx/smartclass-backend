-- 切换库
use smart_class;

-- 聊天会话表
create table if not exists chat_session
(
    id           bigint auto_increment comment 'id' primary key,
    userId       bigint                             not null comment '用户id',
    sessionName  varchar(256)                       null comment '会话名称',
    aiModel      varchar(128)                       not null comment 'AI模型类型',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment 'AI聊天会话' collate = utf8mb4_unicode_ci;

-- 聊天消息表
create table if not exists chat_message
(
    id           bigint auto_increment comment 'id' primary key,
    sessionId    bigint                             not null comment '会话id',
    userId       bigint                             not null comment '用户id',
    content      text                               not null comment '消息内容',
    role         varchar(32)                        not null comment '消息角色：user/assistant',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    index idx_sessionId (sessionId),
    index idx_userId (userId)
) comment 'AI聊天消息' collate = utf8mb4_unicode_ci; 
