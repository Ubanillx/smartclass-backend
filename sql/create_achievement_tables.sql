-- 切换库
use smart_class;

-- 成就定义表
create table if not exists achievement
(
    id              bigint auto_increment comment 'id' primary key,
    name            varchar(128)                       not null comment '成就名称',
    description     varchar(512)                       not null comment '成就描述',
    iconUrl         varchar(1024)                      not null comment '成就图标URL',
    badgeUrl        varchar(1024)                      not null comment '成就徽章URL',
    bannerUrl       varchar(1024)                      null comment '成就横幅URL',
    category        varchar(64)                        not null comment '成就分类，如：学习、社交、活动等',
    level           tinyint  default 1                 not null comment '成就等级：1-普通，2-稀有，3-史诗，4-传说',
    points          int      default 0                 not null comment '成就点数',
    condition       varchar(512)                       not null comment '获取条件描述',
    conditionType   varchar(64)                        not null comment '条件类型，如：course_complete, login_days, article_read等',
    conditionValue  int      default 1                 not null comment '条件值，如完成10门课程，登录30天等',
    isHidden        tinyint  default 0                 not null comment '是否隐藏成就：0-否，1-是，隐藏成就不会提前显示给用户',
    isSecret        tinyint  default 0                 not null comment '是否是彩蛋成就：0-否，1-是，彩蛋成就是特殊发现的成就',
    rewardType      varchar(64)                        null comment '奖励类型，如：points, badge, coupon等',
    rewardValue     varchar(256)                       null comment '奖励值，如积分数量、优惠券ID等',
    sort            int      default 0                 not null comment '排序，数字越小排序越靠前',
    adminId         bigint                             not null comment '创建管理员id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_category (category),
    index idx_level (level),
    index idx_conditionType (conditionType),
    index idx_sort (sort)
) comment '成就定义' collate = utf8mb4_unicode_ci;

-- 用户成就表
create table if not exists user_achievement
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '用户id',
    achievementId   bigint                             not null comment '成就id',
    progress        int      default 0                 not null comment '当前进度值',
    progressMax     int      default 1                 not null comment '目标进度值',
    progressPercent int      default 0                 not null comment '进度百分比',
    isCompleted     tinyint  default 0                 not null comment '是否完成：0-否，1-是',
    completedTime   datetime                           null comment '完成时间',
    isRewarded      tinyint  default 0                 not null comment '是否已发放奖励：0-否，1-是',
    rewardTime      datetime                           null comment '奖励发放时间',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_achievementId (achievementId),
    index idx_isCompleted (isCompleted),
    unique uk_user_achievement (userId, achievementId)
) comment '用户成就' collate = utf8mb4_unicode_ci;

-- 成就展示配置表
create table if not exists achievement_display
(
    id              bigint auto_increment comment 'id' primary key,
    achievementId   bigint                             not null comment '成就id',
    displayType     varchar(64)                        not null comment '展示类型：profile(个人资料页), card(成就卡片), banner(成就横幅), popup(弹窗通知)',
    title           varchar(256)                       null comment '展示标题，为空则使用成就名称',
    subtitle        varchar(512)                       null comment '展示副标题',
    imageUrl        varchar(1024)                      null comment '展示图片URL，为空则使用成就图标',
    backgroundColor varchar(32)                        null comment '背景颜色，十六进制颜色代码',
    textColor       varchar(32)                        null comment '文字颜色，十六进制颜色代码',
    animationType   varchar(64)                        null comment '动画类型',
    displayDuration int      default 0                 not null comment '展示时长(秒)，0表示永久展示',
    sort            int      default 0                 not null comment '排序，数字越小排序越靠前',
    adminId         bigint                             not null comment '创建管理员id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_achievementId (achievementId),
    index idx_displayType (displayType),
    index idx_sort (sort)
) comment '成就展示配置' collate = utf8mb4_unicode_ci;

-- 用户成就展示记录表
create table if not exists user_achievement_display
(
    id                  bigint auto_increment comment 'id' primary key,
    userId              bigint                             not null comment '用户id',
    achievementId       bigint                             not null comment '成就id',
    achievementDisplayId bigint                            not null comment '成就展示配置id',
    isEnabled           tinyint  default 1                 not null comment '是否启用展示：0-否，1-是',
    isPinned            tinyint  default 0                 not null comment '是否置顶：0-否，1-是',
    customTitle         varchar(256)                       null comment '自定义标题，为空则使用默认标题',
    customImageUrl      varchar(1024)                      null comment '自定义图片URL，为空则使用默认图片',
    displayCount        int      default 0                 not null comment '展示次数',
    lastDisplayTime     datetime                           null comment '最后展示时间',
    createTime          datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime          datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_achievementId (achievementId),
    index idx_achievementDisplayId (achievementDisplayId),
    unique uk_user_achievement_display (userId, achievementId, achievementDisplayId)
) comment '用户成就展示记录' collate = utf8mb4_unicode_ci;

-- 成就里程碑表
create table if not exists achievement_milestone
(
    id              bigint auto_increment comment 'id' primary key,
    name            varchar(128)                       not null comment '里程碑名称',
    description     varchar(512)                       not null comment '里程碑描述',
    iconUrl         varchar(1024)                      not null comment '里程碑图标URL',
    bannerUrl       varchar(1024)                      null comment '里程碑横幅URL',
    category        varchar(64)                        not null comment '里程碑分类',
    requiredPoints  int      default 0                 not null comment '所需成就点数',
    rewardType      varchar(64)                        null comment '奖励类型',
    rewardValue     varchar(256)                       null comment '奖励值',
    sort            int      default 0                 not null comment '排序，数字越小排序越靠前',
    adminId         bigint                             not null comment '创建管理员id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_category (category),
    index idx_requiredPoints (requiredPoints),
    index idx_sort (sort)
) comment '成就里程碑' collate = utf8mb4_unicode_ci;

-- 用户里程碑表
create table if not exists user_milestone
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '用户id',
    milestoneId     bigint                             not null comment '里程碑id',
    currentPoints   int      default 0                 not null comment '当前成就点数',
    isCompleted     tinyint  default 0                 not null comment '是否完成：0-否，1-是',
    completedTime   datetime                           null comment '完成时间',
    isRewarded      tinyint  default 0                 not null comment '是否已发放奖励：0-否，1-是',
    rewardTime      datetime                           null comment '奖励发放时间',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_milestoneId (milestoneId),
    index idx_isCompleted (isCompleted),
    unique uk_user_milestone (userId, milestoneId)
) comment '用户里程碑' collate = utf8mb4_unicode_ci; 