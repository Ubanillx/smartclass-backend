-- 切换库
use smart_class;

-- 公告表
create table if not exists announcement
(
    id           bigint auto_increment comment 'id' primary key,
    title        varchar(256)                       not null comment '公告标题',
    content      text                               not null comment '公告内容',
    priority     int      default 0                 not null comment '优先级，数字越大优先级越高',
    status       tinyint  default 1                 not null comment '状态：0-草稿，1-已发布，2-已下线',
    startTime    datetime                           null comment '公告开始展示时间',
    endTime      datetime                           null comment '公告结束展示时间',
    coverImage   varchar(1024)                      null comment '封面图片URL',
    adminId      bigint                             not null comment '发布管理员id',
    viewCount    int      default 0                 not null comment '查看次数',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    index idx_adminId (adminId),
    index idx_status (status),
    index idx_priority (priority)
) comment '系统公告' collate = utf8mb4_unicode_ci;

-- 公告阅读记录表
create table if not exists announcement_read
(
    id             bigint auto_increment comment 'id' primary key,
    announcementId bigint                             not null comment '公告id',
    userId         bigint                             not null comment '用户id',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '阅读时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_announcementId (announcementId),
    index idx_userId (userId),
    unique uk_announcement_user (announcementId, userId)
) comment '公告阅读记录' collate = utf8mb4_unicode_ci; 