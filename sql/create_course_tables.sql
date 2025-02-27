-- 切换库
use smart_class;

-- 课程分类表
create table if not exists course_category
(
    id           bigint auto_increment comment 'id' primary key,
    name         varchar(128)                       not null comment '分类名称',
    description  varchar(512)                       null comment '分类描述',
    icon         varchar(1024)                      null comment '分类图标URL',
    sort         int      default 0                 not null comment '排序权重，数字越大排序越靠前',
    parentId     bigint   default 0                 not null comment '父分类id，0表示一级分类',
    adminId      bigint                             not null comment '创建管理员id',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    index idx_parentId (parentId),
    index idx_sort (sort)
) comment '课程分类' collate = utf8mb4_unicode_ci;

-- 课程表
create table if not exists course
(
    id              bigint auto_increment comment 'id' primary key,
    title           varchar(256)                       not null comment '课程标题',
    subtitle        varchar(512)                       null comment '课程副标题',
    description     text                               null comment '课程描述',
    coverImage      varchar(1024)                      null comment '封面图片URL',
    price           decimal(10, 2) default 0.00        not null comment '课程价格',
    originalPrice   decimal(10, 2) default 0.00        null comment '原价',
    courseType      tinyint  default 0                 not null comment '课程类型：0-公开课，1-付费课，2-会员课',
    difficulty      tinyint  default 1                 not null comment '难度等级：1-入门，2-初级，3-中级，4-高级，5-专家',
    status          tinyint  default 0                 not null comment '状态：0-未发布，1-已发布，2-已下架',
    categoryId      bigint                             not null comment '课程分类id',
    teacherId       bigint                             not null comment '讲师id',
    totalDuration   int      default 0                 not null comment '总时长(分钟)',
    totalChapters   int      default 0                 not null comment '总章节数',
    totalSections   int      default 0                 not null comment '总小节数',
    studentCount    int      default 0                 not null comment '学习人数',
    ratingScore     decimal(2, 1) default 0.0          not null comment '评分，1-5分',
    ratingCount     int      default 0                 not null comment '评分人数',
    tags            varchar(512)                       null comment '标签，JSON数组格式',
    requirements    text                               null comment '学习要求',
    objectives      text                               null comment '学习目标',
    targetAudience  text                               null comment '目标受众',
    adminId         bigint                             not null comment '创建管理员id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_categoryId (categoryId),
    index idx_teacherId (teacherId),
    index idx_courseType (courseType),
    index idx_status (status),
    index idx_difficulty (difficulty)
) comment '课程' collate = utf8mb4_unicode_ci;

-- 讲师表
create table if not exists teacher
(
    id              bigint auto_increment comment 'id' primary key,
    name            varchar(128)                       not null comment '讲师姓名',
    avatar          varchar(1024)                      null comment '讲师头像URL',
    title           varchar(128)                       null comment '讲师职称',
    introduction    text                               null comment '讲师简介',
    expertise       varchar(512)                       null comment '专业领域，JSON数组格式',
    userId          bigint                             null comment '关联的用户id，如果讲师也是平台用户',
    adminId         bigint                             not null comment '创建管理员id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '讲师' collate = utf8mb4_unicode_ci;

-- 课程章节表
create table if not exists course_chapter
(
    id              bigint auto_increment comment 'id' primary key,
    courseId        bigint                             not null comment '课程id',
    title           varchar(256)                       not null comment '章节标题',
    description     text                               null comment '章节描述',
    sort            int      default 0                 not null comment '排序，数字越小排序越靠前',
    adminId         bigint                             not null comment '创建管理员id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_courseId (courseId),
    index idx_sort (sort)
) comment '课程章节' collate = utf8mb4_unicode_ci;

-- 课程小节表
create table if not exists course_section
(
    id              bigint auto_increment comment 'id' primary key,
    courseId        bigint                             not null comment '课程id',
    chapterId       bigint                             not null comment '章节id',
    title           varchar(256)                       not null comment '小节标题',
    description     text                               null comment '小节描述',
    videoUrl        varchar(1024)                      null comment '视频URL',
    duration        int      default 0                 not null comment '时长(秒)',
    sort            int      default 0                 not null comment '排序，数字越小排序越靠前',
    isFree          tinyint  default 0                 not null comment '是否免费：0-否，1-是',
    resourceType    tinyint  default 0                 not null comment '资源类型：0-视频，1-音频，2-文档，3-图片，4-直播',
    resourceUrl     varchar(1024)                      null comment '资源URL',
    adminId         bigint                             not null comment '创建管理员id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_courseId (courseId),
    index idx_chapterId (chapterId),
    index idx_sort (sort)
) comment '课程小节' collate = utf8mb4_unicode_ci;

-- 课程资料表
create table if not exists course_material
(
    id              bigint auto_increment comment 'id' primary key,
    courseId        bigint                             not null comment '课程id',
    title           varchar(256)                       not null comment '资料标题',
    description     text                               null comment '资料描述',
    fileUrl         varchar(1024)                      not null comment '文件URL',
    fileSize        bigint   default 0                 not null comment '文件大小(字节)',
    fileType        varchar(64)                        null comment '文件类型',
    downloadCount   int      default 0                 not null comment '下载次数',
    sort            int      default 0                 not null comment '排序，数字越小排序越靠前',
    adminId         bigint                             not null comment '创建管理员id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_courseId (courseId),
    index idx_sort (sort)
) comment '课程资料' collate = utf8mb4_unicode_ci;

-- 用户课程购买记录表
create table if not exists user_course
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '用户id',
    courseId        bigint                             not null comment '课程id',
    orderNo         varchar(64)                        null comment '订单编号',
    price           decimal(10, 2)                     not null comment '购买价格',
    paymentMethod   varchar(32)                        null comment '支付方式',
    paymentTime     datetime                           null comment '支付时间',
    expireTime      datetime                           null comment '过期时间，null表示永久有效',
    status          tinyint  default 1                 not null comment '状态：0-未支付，1-已支付，2-已过期，3-已退款',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId),
    index idx_courseId (courseId),
    index idx_orderNo (orderNo),
    unique uk_user_course (userId, courseId)
) comment '用户课程购买记录' collate = utf8mb4_unicode_ci;

-- 用户学习进度表
create table if not exists user_course_progress
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '用户id',
    courseId        bigint                             not null comment '课程id',
    sectionId       bigint                             not null comment '小节id',
    progress        int      default 0                 not null comment '学习进度(百分比)',
    watchDuration   int      default 0                 not null comment '观看时长(秒)',
    lastPosition    int      default 0                 not null comment '上次观看位置(秒)',
    isCompleted     tinyint  default 0                 not null comment '是否完成：0-否，1-是',
    completedTime   datetime                           null comment '完成时间',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_courseId (courseId),
    index idx_sectionId (sectionId),
    unique uk_user_section (userId, sectionId)
) comment '用户学习进度' collate = utf8mb4_unicode_ci;

-- 课程评价表
create table if not exists course_review
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '用户id',
    courseId        bigint                             not null comment '课程id',
    content         text                               not null comment '评价内容',
    rating          tinyint  default 5                 not null comment '评分(1-5分)',
    likeCount       int      default 0                 not null comment '点赞数',
    replyCount      int      default 0                 not null comment '回复数',
    adminReply      text                               null comment '管理员回复',
    adminReplyTime  datetime                           null comment '管理员回复时间',
    status          tinyint  default 1                 not null comment '状态：0-待审核，1-已发布，2-已拒绝',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId),
    index idx_courseId (courseId),
    index idx_rating (rating)
) comment '课程评价' collate = utf8mb4_unicode_ci;

-- 课程收藏表
create table if not exists course_favourite
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '用户id',
    courseId        bigint                             not null comment '课程id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_courseId (courseId),
    unique uk_user_course (userId, courseId)
) comment '课程收藏' collate = utf8mb4_unicode_ci;

-- 学习目标类型表
create table if not exists goal_type
(
    id              bigint auto_increment comment 'id' primary key,
    name            varchar(128)                       not null comment '目标类型名称',
    code            varchar(64)                        null comment '目标类型编码',
    icon            varchar(1024)                      null comment '图标URL',
    description     text                               null comment '描述',
    category        varchar(128)                       null comment '分类',
    unit            varchar(64)                        null comment '单位',
    defaultValue    int                                not null comment '默认值',
    minValue        int                                not null comment '最小值',
    maxValue        int                                not null comment '最大值',
    points          int                                not null comment '完成可获得积分',
    experience      int                                not null comment '完成可获得经验值',
    isSystem        tinyint                            not null comment '是否系统预设',
    isEnabled       tinyint                            not null comment '是否启用',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint                            not null comment '逻辑删除标记'
) comment '学习目标类型' collate = utf8mb4_unicode_ci;

-- 初始化数据
INSERT INTO goal_type (name, code, icon, description, category, unit, defaultValue, minValue, maxValue, points, experience, isSystem, isEnabled)
VALUES 
('完成每日单词打卡', 'word_card', NULL, '完成每日单词学习打卡任务', '单词学习', '次', 1, 1, 1, 5, 10, 1, 1),
('听力练习', 'listening', NULL, '完成听力练习', '听力学习', '分钟', 15, 5, 120, 10, 20, 1, 1),
('完成口语课程', 'speaking_course', NULL, '完成一节口语课程', '口语学习', '节', 1, 1, 5, 15, 30, 1, 1); 