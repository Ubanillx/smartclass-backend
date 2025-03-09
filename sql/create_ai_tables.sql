-- 切换库
use smart_class;
-- AI分身表
create table if not exists ai_avatar
(
    id              bigint auto_increment comment 'id' primary key,
    name            varchar(128)                       not null comment 'AI分身名称',
    description     text                               null comment 'AI分身描述',
    avatarUrl       varchar(1024)                      null comment 'AI分身头像URL',
    tags            varchar(512)                       null comment '标签，JSON数组格式',
    category        varchar(64)                        not null comment '分类，如：学习助手、语言教练、职业顾问等',
    personality     text                               null comment '性格特点描述',
    abilities       text                               null comment '能力描述',
    promptTemplate  text                               null comment '提示词模板',
    apiUrl          varchar(1024)                      not null comment 'API请求地址',
    apiKey          varchar(512)                       null comment 'API密钥（加密存储）',
    modelType       varchar(64)                        not null comment '模型类型，如：GPT-4、Claude等',
    modelConfig     text                               null comment '模型配置，JSON格式',
    isPublic        tinyint  default 1                 not null comment '是否公开：0-否，1-是',
    status          tinyint  default 1                 not null comment '状态：0-禁用，1-启用',
    usageCount      int      default 0                 not null comment '使用次数',
    rating          decimal(2, 1) default 0.0          not null comment '评分，1-5分',
    ratingCount     int      default 0                 not null comment '评分人数',
    adminId         bigint                             not null comment '创建管理员id',
    sort            int      default 0                 not null comment '排序，数字越小排序越靠前',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_category (category),
    index idx_tags (tags),
    index idx_adminId (adminId),
    index idx_status (status),
    index idx_sort (sort),
    index idx_usageCount (usageCount)
) comment 'AI分身' collate = utf8mb4_unicode_ci;

-- 用户AI分身关联表
create table if not exists user_ai_avatar
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '用户id',
    aiAvatarId      bigint                             not null comment 'AI分身id',
    isFavorite      tinyint  default 0                 not null comment '是否收藏：0-否，1-是',
    lastUseTime     datetime                           null comment '最后使用时间',
    useCount        int      default 0                 not null comment '使用次数',
    userRating      decimal(2, 1)                      null comment '用户评分，1-5分',
    userFeedback    text                               null comment '用户反馈',
    customSettings  text                               null comment '用户自定义设置，JSON格式',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_aiAvatarId (aiAvatarId),
    index idx_isFavorite (isFavorite),
    unique uk_user_avatar (userId, aiAvatarId)
) comment '用户AI分身关联' collate = utf8mb4_unicode_ci;

-- AI分身对话历史表
create table if not exists ai_avatar_chat_history
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '用户id',
    aiAvatarId      bigint                             not null comment 'AI分身id',
    sessionId       varchar(64)                        not null comment '会话ID',
    messageType     varchar(32)                        not null comment '消息类型：user/ai',
    content         text                               not null comment '消息内容',
    tokens          int      default 0                 not null comment '消息token数',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    index idx_userId (userId),
    index idx_aiAvatarId (aiAvatarId),
    index idx_sessionId (sessionId)
) comment 'AI分身对话历史' collate = utf8mb4_unicode_ci;