-- 切换库
use smart_class;

-- 用户等级表
create table if not exists user_level
(
    id              bigint auto_increment comment 'id' primary key,
    level           int                                 not null comment '等级数值',
    levelName       varchar(64)                         not null comment '等级名称',
    iconUrl         varchar(1024)                       null comment '等级图标URL',
    minExperience   int                                 not null comment '最小经验值',
    maxExperience   int                                 not null comment '最大经验值',
    description     varchar(512)                        null comment '等级描述',
    privileges      varchar(1024)                       null comment '等级特权，JSON格式',
    createTime      datetime default CURRENT_TIMESTAMP  not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP  not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                  not null comment '是否删除',
    index idx_level (level)
) comment '用户等级' collate = utf8mb4_unicode_ci;

-- 用户学习统计表
create table if not exists user_learning_stats
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '用户id',
    level           int      default 1                 not null comment '当前等级',
    experience      int      default 0                 not null comment '当前经验值',
    nextLevelExp    int      default 100               not null comment '下一级所需经验值',
    nickname        varchar(128)                       null comment '用户昵称',
    learningDays    int      default 0                 not null comment '学习天数',
    continuousCheckIn int    default 0                 not null comment '连续打卡天数',
    totalCheckIn    int      default 0                 not null comment '总打卡天数',
    totalPoints     int      default 0                 not null comment '总积分',
    totalBadges     int      default 0                 not null comment '获得徽章数',
    lastCheckInTime datetime                           null comment '最后打卡时间',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_level (level),
    index idx_experience (experience),
    index idx_learningDays (learningDays),
    index idx_continuousCheckIn (continuousCheckIn)
) comment '用户学习统计' collate = utf8mb4_unicode_ci;

-- 用户每日学习目标表
create table if not exists user_daily_goal
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '用户id',
    goalDate        date                               not null comment '目标日期',
    totalGoals      int      default 0                 not null comment '总目标数',
    completedGoals  int      default 0                 not null comment '已完成目标数',
    progressPercent int      default 0                 not null comment '完成百分比',
    isCompleted     tinyint  default 0                 not null comment '是否全部完成：0-否，1-是',
    completedTime   datetime                           null comment '全部完成时间',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_goalDate (goalDate),
    unique uk_user_date (userId, goalDate)
) comment '用户每日学习目标' collate = utf8mb4_unicode_ci;

-- 学习目标类型表
create table if not exists goal_type
(
    id              bigint auto_increment comment 'id' primary key,
    name            varchar(128)                       not null comment '目标类型名称',
    code            varchar(64)                        not null comment '目标类型编码',
    icon            varchar(1024)                      null comment '图标URL',
    description     varchar(512)                       null comment '描述',
    category        varchar(64)                        not null comment '分类',
    unit            varchar(32)                        null comment '单位',
    defaultValue    int      default 1                 not null comment '默认值',
    minValue        int      default 1                 not null comment '最小值',
    maxValue        int                                null comment '最大值',
    points          int      default 5                 not null comment '完成可获得积分',
    experience      int      default 10                not null comment '完成可获得经验值',
    isSystem        tinyint  default 1                 not null comment '是否系统预设：0-否，1-是',
    isEnabled       tinyint  default 1                 not null comment '是否启用：0-否，1-是',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_code (code),
    index idx_category (category)
) comment '学习目标类型' collate = utf8mb4_unicode_ci;

-- 用户学习目标项表
create table if not exists user_goal_item
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '用户id',
    dailyGoalId     bigint                             not null comment '每日目标id',
    goalTypeId      bigint                             not null comment '目标类型id',
    goalDate        date                               not null comment '目标日期',
    title           varchar(256)                       not null comment '目标标题',
    targetValue     int      default 1                 not null comment '目标值',
    currentValue    int      default 0                 not null comment '当前值',
    progressPercent int      default 0                 not null comment '完成百分比',
    isCompleted     tinyint  default 0                 not null comment '是否完成：0-否，1-是',
    completedTime   datetime                           null comment '完成时间',
    isRewarded      tinyint  default 0                 not null comment '是否已发放奖励：0-否，1-是',
    rewardTime      datetime                           null comment '奖励发放时间',
    sort            int      default 0                 not null comment '排序，数字越小排序越靠前',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_dailyGoalId (dailyGoalId),
    index idx_goalTypeId (goalTypeId),
    index idx_goalDate (goalDate),
    index idx_isCompleted (isCompleted)
) comment '用户学习目标项' collate = utf8mb4_unicode_ci;

-- 用户学习记录表
create table if not exists user_learning_record
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '用户id',
    recordDate      date                               not null comment '记录日期',
    recordType      varchar(64)                        not null comment '记录类型，如：word_card, listening, course等',
    relatedId       bigint                             null comment '关联ID，如单词ID、课程ID等',
    duration        int      default 0                 not null comment '学习时长(秒)',
    count           int      default 1                 not null comment '学习数量',
    points          int      default 0                 not null comment '获得积分',
    experience      int      default 0                 not null comment '获得经验值',
    remark          varchar(512)                       null comment '备注',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_recordDate (recordDate),
    index idx_recordType (recordType)
) comment '用户学习记录' collate = utf8mb4_unicode_ci;

-- 初始化学习目标类型数据
INSERT INTO goal_type (name, code, icon, description, category, unit, defaultValue, minValue, maxValue, points, experience, isSystem, isEnabled)
VALUES 
('完成每日单词打卡', 'word_card', NULL, '完成每日单词学习打卡任务', '单词学习', '次', 1, 1, 1, 5, 10, 1, 1),
('听力练习', 'listening', NULL, '完成听力练习', '听力学习', '分钟', 15, 5, 120, 10, 20, 1, 1),
('完成口语课程', 'speaking_course', NULL, '完成一节口语课程', '口语学习', '节', 1, 1, 5, 15, 30, 1, 1); 