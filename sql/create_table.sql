# 数据库初始化


-- 创建库
create database if not exists smart_class;

-- 切换库
use smart_class;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userGender   int                                    null comment '性别 0-男 1-女 2-保密',
    userPhone    varchar(256)                           null comment '手机号',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'student'         not null comment '用户角色：student/teacher/admin/ban',
    province     varchar(100)                           null comment '省份',
    city         varchar(100)                           null comment '城市',
    district     varchar(100)                           null comment '区县',
    wechatId     varchar(256)                           null comment '微信号',
    userEmail        varchar(256)                           null comment '邮箱',
    birthday     datetime                               null comment '生日',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 帖子表
create table if not exists post
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                 not null comment '收藏数',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 帖子点赞表（硬删除）
create table if not exists post_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子点赞';

-- 帖子收藏表（硬删除）
create table if not exists post_favour
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子收藏';

-- 用户等级表
create table if not exists user_level
(
    id            bigint auto_increment comment 'id' primary key,
    level         int                                not null comment '等级数值',
    levelName     varchar(64)                        not null comment '等级名称',
    iconUrl       varchar(1024)                      null comment '等级图标URL',
    minExperience int                                not null comment '最小经验值',
    maxExperience int                                not null comment '最大经验值',
    description   varchar(512)                       null comment '等级描述',
    privileges    varchar(1024)                      null comment '等级特权，JSON格式',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除',
    index idx_level (level)
) comment '用户等级' collate = utf8mb4_unicode_ci;

-- 用户学习统计表
create table if not exists user_learning_stats
(
    id                bigint auto_increment comment 'id' primary key,
    userId            bigint                             not null comment '用户id',
    level             int      default 1                 not null comment '当前等级',
    experience        int      default 0                 not null comment '当前经验值',
    nextLevelExp      int      default 100               not null comment '下一级所需经验值',
    learningDays      int      default 0                 not null comment '学习天数',
    continuousCheckIn int      default 0                 not null comment '连续打卡天数',
    totalCheckIn      int      default 0                 not null comment '总打卡天数',
    totalPoints       int      default 0                 not null comment '总积分',
    totalBadges       int      default 0                 not null comment '获得徽章数',
    lastCheckInTime   datetime                           null comment '最后打卡时间',
    createTime        datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime        datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_level (level),
    index idx_experience (experience),
    index idx_learningDays (learningDays),
    index idx_continuousCheckIn (continuousCheckIn)
) comment '用户学习统计' collate = utf8mb4_unicode_ci;

-- 用户与每日文章关联表
CREATE TABLE IF NOT EXISTS user_daily_article
(
    id             BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    userId         BIGINT                             NOT NULL COMMENT '用户id',
    articleId      BIGINT                             NOT NULL COMMENT '文章id',
    isRead         TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否阅读：0-否，1-是',
    readTime       DATETIME                           NULL COMMENT '阅读时间',
    isLiked        TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否点赞：0-否，1-是',
    likeTime       DATETIME                           NULL COMMENT '点赞时间',
    isCollected    TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否收藏：0-否，1-是',
    collectTime    DATETIME                           NULL COMMENT '收藏时间',
    commentContent TEXT                               NULL COMMENT '评论内容',
    commentTime    DATETIME                           NULL COMMENT '评论时间',
    createTime     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_userId (userId),
    INDEX idx_articleId (articleId),
    UNIQUE uk_user_article (userId, articleId)
) COMMENT '用户与每日文章关联' COLLATE = utf8mb4_unicode_ci;

-- 用户与每日单词关联表
CREATE TABLE IF NOT EXISTS user_daily_word
(
    id             BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    userId         BIGINT                             NOT NULL COMMENT '用户id',
    wordId         BIGINT                             NOT NULL COMMENT '单词id',
    isStudied      TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否学习：0-否，1-是',
    studyTime      DATETIME                           NULL COMMENT '学习时间',
    isLiked        TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否点赞：0-否，1-是',
    likeTime       DATETIME                           NULL COMMENT '点赞时间',
    isCollected    TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否收藏：0-否，1-是',
    collectTime    DATETIME                           NULL COMMENT '收藏时间',
    noteContent    TEXT                               NULL COMMENT '笔记内容',
    noteTime       DATETIME                           NULL COMMENT '笔记时间',
    masteryLevel   TINYINT  DEFAULT 0                 NOT NULL COMMENT '掌握程度：0-未知，1-生词，2-熟悉，3-掌握',
    createTime     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_userId (userId),
    INDEX idx_wordId (wordId),
    UNIQUE uk_user_word (userId, wordId)
) COMMENT '用户与每日单词关联' COLLATE = utf8mb4_unicode_ci;
-- 用户与公告关联表
CREATE TABLE IF NOT EXISTS user_announcement_reader
(
    id              BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    userId         BIGINT                             NOT NULL COMMENT '用户id',
    announcementId BIGINT                             NOT NULL COMMENT '公告id',
    readTime       DATETIME                           NULL COMMENT '阅读时间',
    createTime     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (userId),
    INDEX idx_announcement_id (announcementId),
    UNIQUE uk_user_announcement (userId, announcementId)
) COMMENT '用户公告阅读记录' COLLATE = utf8mb4_unicode_ci;

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
    id           bigint auto_increment comment 'id' primary key,
    name         varchar(128)                       not null comment '目标类型名称',
    code         varchar(64)                        not null comment '目标类型编码',
    icon         varchar(1024)                      null comment '图标URL',
    description  varchar(512)                       null comment '描述',
    category     varchar(64)                        not null comment '分类',
    unit         varchar(32)                        null comment '单位',
    defaultValue int      default 1                 not null comment '默认值',
    minValue     int      default 1                 not null comment '最小值',
    `maxValue`   int                                null comment '最大值',
    points       int      default 5                 not null comment '完成可获得积分',
    experience   int      default 10                not null comment '完成可获得经验值',
    isSystem     tinyint  default 1                 not null comment '是否系统预设：0-否，1-是',
    isEnabled    tinyint  default 1                 not null comment '是否启用：0-否，1-是',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
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
CREATE TABLE IF NOT EXISTS user_learning_record
(
    id           BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    userId       BIGINT                             NOT NULL COMMENT '用户id',
    recordDate   DATE                               NOT NULL COMMENT '记录日期',
    recordType   VARCHAR(64)                        NOT NULL COMMENT '记录类型，如：word_card, listening, course等',
    relatedId    BIGINT                             NULL COMMENT '关联ID，如单词ID、课程ID等',
    lessonNumber INT      DEFAULT 0                 NOT NULL COMMENT '课程中的课次或子活动编号',
    duration     INT      DEFAULT 0                 NOT NULL COMMENT '学习时长(秒)',
    count        INT      DEFAULT 1                 NOT NULL COMMENT '学习数量',
    points       INT      DEFAULT 0                 NOT NULL COMMENT '获得积分',
    experience   INT      DEFAULT 0                 NOT NULL COMMENT '获得经验值',
    accuracy     DECIMAL(5,2) DEFAULT 0            NOT NULL COMMENT '正确率(百分比)',
    status       VARCHAR(32) DEFAULT 'completed'   NOT NULL COMMENT '活动状态，如：in_progress, completed, failed',
    remark       VARCHAR(512)                       NULL COMMENT '备注',
    createTime   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_userId (userId),
    INDEX idx_recordDate (recordDate),
    INDEX idx_recordType (recordType),
    UNIQUE INDEX idx_unique_record (userId, recordDate, recordType, relatedId)

) COMMENT '用户学习记录' COLLATE = utf8mb4_unicode_ci;


-- 成就定义表
create table if not exists achievement
(
    id                   bigint auto_increment comment 'id' primary key,
    name                 varchar(128)                       not null comment '成就名称',
    description          varchar(512)                       not null comment '成就描述',
    iconUrl              varchar(1024)                      not null comment '成就图标URL',
    badgeUrl             varchar(1024)                      not null comment '成就徽章URL',
    bannerUrl            varchar(1024)                      null comment '成就横幅URL',
    category             varchar(64)                        not null comment '成就分类，如：学习、社交、活动等',
    level                tinyint  default 1                 not null comment '成就等级：1-普通，2-稀有，3-史诗，4-传说',
    points               int      default 0                 not null comment '成就点数',
    achievementCondition varchar(512)                       not null comment '获取条件描述',
    conditionType        varchar(64)                        not null comment '条件类型，如：course_complete, login_days, article_read等',
    conditionValue       int      default 1                 not null comment '条件值，如完成10门课程，登录30天等',
    isHidden             tinyint  default 0                 not null comment '是否隐藏成就：0-否，1-是，隐藏成就不会提前显示给用户',
    isSecret             tinyint  default 0                 not null comment '是否是彩蛋成就：0-否，1-是，彩蛋成就是特殊发现的成就',
    rewardType           varchar(64)                        null comment '奖励类型，如：points, badge, coupon等',
    rewardValue          varchar(256)                       null comment '奖励值，如积分数量、优惠券ID等',
    sort                 int      default 0                 not null comment '排序，数字越小排序越靠前',
    adminId              bigint                             not null comment '创建管理员id',
    createTime           datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime           datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete             tinyint  default 0                 not null comment '是否删除',
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
    id                   bigint auto_increment comment 'id' primary key,
    userId               bigint                             not null comment '用户id',
    achievementId        bigint                             not null comment '成就id',
    achievementDisplayId bigint                             not null comment '成就展示配置id',
    isEnabled            tinyint  default 1                 not null comment '是否启用展示：0-否，1-是',
    isPinned             tinyint  default 0                 not null comment '是否置顶：0-否，1-是',
    customTitle          varchar(256)                       null comment '自定义标题，为空则使用默认标题',
    customImageUrl       varchar(1024)                      null comment '自定义图片URL，为空则使用默认图片',
    displayCount         int      default 0                 not null comment '展示次数',
    lastDisplayTime      datetime                           null comment '最后展示时间',
    createTime           datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime           datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_achievementId (achievementId),
    index idx_achievementDisplayId (achievementDisplayId),
    unique uk_user_achievement_display (userId, achievementId, achievementDisplayId)
) comment '用户成就展示记录' collate = utf8mb4_unicode_ci;

-- 成就里程碑表
create table if not exists achievement_milestone
(
    id             bigint auto_increment comment 'id' primary key,
    name           varchar(128)                       not null comment '里程碑名称',
    description    varchar(512)                       not null comment '里程碑描述',
    iconUrl        varchar(1024)                      not null comment '里程碑图标URL',
    bannerUrl      varchar(1024)                      null comment '里程碑横幅URL',
    category       varchar(64)                        not null comment '里程碑分类',
    requiredPoints int      default 0                 not null comment '所需成就点数',
    rewardType     varchar(64)                        null comment '奖励类型',
    rewardValue    varchar(256)                       null comment '奖励值',
    sort           int      default 0                 not null comment '排序，数字越小排序越靠前',
    adminId        bigint                             not null comment '创建管理员id',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除',
    index idx_category (category),
    index idx_requiredPoints (requiredPoints),
    index idx_sort (sort)
) comment '成就里程碑' collate = utf8mb4_unicode_ci;

-- 用户里程碑表
create table if not exists user_milestone
(
    id            bigint auto_increment comment 'id' primary key,
    userId        bigint                             not null comment '用户id',
    milestoneId   bigint                             not null comment '里程碑id',
    currentPoints int      default 0                 not null comment '当前成就点数',
    isCompleted   tinyint  default 0                 not null comment '是否完成：0-否，1-是',
    completedTime datetime                           null comment '完成时间',
    isRewarded    tinyint  default 0                 not null comment '是否已发放奖励：0-否，1-是',
    rewardTime    datetime                           null comment '奖励发放时间',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_milestoneId (milestoneId),
    index idx_isCompleted (isCompleted),
    unique uk_user_milestone (userId, milestoneId)
) comment '用户里程碑' collate = utf8mb4_unicode_ci;

-- 公告表
create table if not exists announcement
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(256)                       not null comment '公告标题',
    content    text                               not null comment '公告内容',
    priority   int      default 0                 not null comment '优先级，数字越大优先级越高',
    status     tinyint  default 1                 not null comment '状态：0-草稿，1-已发布，2-已下线',
    startTime  datetime                           null comment '公告开始展示时间',
    endTime    datetime                           null comment '公告结束展示时间',
    coverImage varchar(1024)                      null comment '封面图片URL',
    adminId    bigint                             not null comment '发布管理员id',
    viewCount  int      default 0                 not null comment '查看次数',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
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


-- 课程分类表
create table if not exists course_category
(
    id          bigint auto_increment comment 'id' primary key,
    name        varchar(128)                       not null comment '分类名称',
    description varchar(512)                       null comment '分类描述',
    icon        varchar(1024)                      null comment '分类图标URL',
    sort        int      default 0                 not null comment '排序权重，数字越大排序越靠前',
    parentId    bigint   default 0                 not null comment '父分类id，0表示一级分类',
    adminId     bigint                             not null comment '创建管理员id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_parentId (parentId),
    index idx_sort (sort)
) comment '课程分类' collate = utf8mb4_unicode_ci;

-- 课程表
create table if not exists course
(
    id             bigint auto_increment comment 'id' primary key,
    title          varchar(256)                             not null comment '课程标题',
    subtitle       varchar(512)                             null comment '课程副标题',
    description    text                                     null comment '课程描述',
    coverImage     varchar(1024)                            null comment '封面图片URL',
    price          decimal(10, 2) default 0.00              not null comment '课程价格',
    originalPrice  decimal(10, 2) default 0.00              null comment '原价',
    courseType     tinyint        default 0                 not null comment '课程类型：0-公开课，1-付费课，2-会员课',
    difficulty     tinyint        default 1                 not null comment '难度等级：1-入门，2-初级，3-中级，4-高级，5-专家',
    status         tinyint        default 0                 not null comment '状态：0-未发布，1-已发布，2-已下架',
    categoryId     bigint                                   not null comment '课程分类id',
    teacherId      bigint                                   not null comment '讲师id',
    totalDuration  int            default 0                 not null comment '总时长(分钟)',
    totalChapters  int            default 0                 not null comment '总章节数',
    totalSections  int            default 0                 not null comment '总小节数',
    studentCount   int            default 0                 not null comment '学习人数',
    ratingScore    decimal(2, 1)  default 0.0               not null comment '评分，1-5分',
    ratingCount    int            default 0                 not null comment '评分人数',
    tags           varchar(512)                             null comment '标签，JSON数组格式',
    requirements   text                                     null comment '学习要求',
    objectives     text                                     null comment '学习目标',
    targetAudience text                                     null comment '目标受众',
    adminId        bigint                                   not null comment '创建管理员id',
    createTime     datetime       default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint        default 0                 not null comment '是否删除',
    index idx_categoryId (categoryId),
    index idx_teacherId (teacherId),
    index idx_courseType (courseType),
    index idx_status (status),
    index idx_difficulty (difficulty)
) comment '课程' collate = utf8mb4_unicode_ci;

-- 讲师表
create table if not exists teacher
(
    id           bigint auto_increment comment 'id' primary key,
    name         varchar(128)                       not null comment '讲师姓名',
    avatar       varchar(1024)                      null comment '讲师头像URL',
    title        varchar(128)                       null comment '讲师职称',
    introduction text                               null comment '讲师简介',
    expertise    varchar(512)                       null comment '专业领域，JSON数组格式',
    userId       bigint                             null comment '关联的用户id，如果讲师也是平台用户',
    adminId      bigint                             not null comment '创建管理员id',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '讲师' collate = utf8mb4_unicode_ci;

-- 课程章节表
create table if not exists course_chapter
(
    id          bigint auto_increment comment 'id' primary key,
    courseId    bigint                             not null comment '课程id',
    title       varchar(256)                       not null comment '章节标题',
    description text                               null comment '章节描述',
    sort        int      default 0                 not null comment '排序，数字越小排序越靠前',
    adminId     bigint                             not null comment '创建管理员id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_courseId (courseId),
    index idx_sort (sort)
) comment '课程章节' collate = utf8mb4_unicode_ci;

-- 课程小节表
create table if not exists course_section
(
    id           bigint auto_increment comment 'id' primary key,
    courseId     bigint                             not null comment '课程id',
    chapterId    bigint                             not null comment '章节id',
    title        varchar(256)                       not null comment '小节标题',
    description  text                               null comment '小节描述',
    videoUrl     varchar(1024)                      null comment '视频URL',
    duration     int      default 0                 not null comment '时长(秒)',
    sort         int      default 0                 not null comment '排序，数字越小排序越靠前',
    isFree       tinyint  default 0                 not null comment '是否免费：0-否，1-是',
    resourceType tinyint  default 0                 not null comment '资源类型：0-视频，1-音频，2-文档，3-图片，4-直播',
    resourceUrl  varchar(1024)                      null comment '资源URL',
    adminId      bigint                             not null comment '创建管理员id',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    index idx_courseId (courseId),
    index idx_chapterId (chapterId),
    index idx_sort (sort)
) comment '课程小节' collate = utf8mb4_unicode_ci;

-- 课程资料表
create table if not exists course_material
(
    id            bigint auto_increment comment 'id' primary key,
    courseId      bigint                             not null comment '课程id',
    title         varchar(256)                       not null comment '资料标题',
    description   text                               null comment '资料描述',
    fileUrl       varchar(1024)                      not null comment '文件URL',
    fileSize      bigint   default 0                 not null comment '文件大小(字节)',
    fileType      varchar(64)                        null comment '文件类型',
    downloadCount int      default 0                 not null comment '下载次数',
    sort          int      default 0                 not null comment '排序，数字越小排序越靠前',
    adminId       bigint                             not null comment '创建管理员id',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除',
    index idx_courseId (courseId),
    index idx_sort (sort)
) comment '课程资料' collate = utf8mb4_unicode_ci;

-- 用户课程购买记录表
create table if not exists user_course
(
    id            bigint auto_increment comment 'id' primary key,
    userId        bigint                             not null comment '用户id',
    courseId      bigint                             not null comment '课程id',
    orderNo       varchar(64)                        null comment '订单编号',
    price         decimal(10, 2)                     not null comment '购买价格',
    paymentMethod varchar(32)                        null comment '支付方式',
    paymentTime   datetime                           null comment '支付时间',
    expireTime    datetime                           null comment '过期时间，null表示永久有效',
    status        tinyint  default 1                 not null comment '状态：0-未支付，1-已支付，2-已过期，3-已退款',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId),
    index idx_courseId (courseId),
    index idx_orderNo (orderNo),
    unique uk_user_course (userId, courseId)
) comment '用户课程购买记录' collate = utf8mb4_unicode_ci;

-- 用户学习进度表
create table if not exists user_course_progress
(
    id            bigint auto_increment comment 'id' primary key,
    userId        bigint                             not null comment '用户id',
    courseId      bigint                             not null comment '课程id',
    sectionId     bigint                             not null comment '小节id',
    progress      int      default 0                 not null comment '学习进度(百分比)',
    watchDuration int      default 0                 not null comment '观看时长(秒)',
    lastPosition  int      default 0                 not null comment '上次观看位置(秒)',
    isCompleted   tinyint  default 0                 not null comment '是否完成：0-否，1-是',
    completedTime datetime                           null comment '完成时间',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_courseId (courseId),
    index idx_sectionId (sectionId),
    unique uk_user_section (userId, sectionId)
) comment '用户学习进度' collate = utf8mb4_unicode_ci;

-- 课程评价表
create table if not exists course_review
(
    id             bigint auto_increment comment 'id' primary key,
    userId         bigint                             not null comment '用户id',
    courseId       bigint                             not null comment '课程id',
    content        text                               not null comment '评价内容',
    rating         tinyint  default 5                 not null comment '评分(1-5分)',
    likeCount      int      default 0                 not null comment '点赞数',
    replyCount     int      default 0                 not null comment '回复数',
    adminReply     text                               null comment '管理员回复',
    adminReplyTime datetime                           null comment '管理员回复时间',
    status         tinyint  default 1                 not null comment '状态：0-待审核，1-已发布，2-已拒绝',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId),
    index idx_courseId (courseId),
    index idx_rating (rating)
) comment '课程评价' collate = utf8mb4_unicode_ci;

-- 课程收藏表
create table if not exists course_favourite
(
    id         bigint auto_increment comment 'id' primary key,
    userId     bigint                             not null comment '用户id',
    courseId   bigint                             not null comment '课程id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_courseId (courseId),
    unique uk_user_course (userId, courseId)
) comment '课程收藏' collate = utf8mb4_unicode_ci;

-- 聊天会话表
create table if not exists chat_session
(
    id          bigint auto_increment comment 'id' primary key,
    userId      bigint                             not null comment '用户id',
    sessionName varchar(256)                       null comment '会话名称',
    aiModel     varchar(128)                       not null comment 'AI模型类型',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment 'AI聊天会话' collate = utf8mb4_unicode_ci;

-- 聊天消息表
create table if not exists chat_message
(
    id         bigint auto_increment comment 'id' primary key,
    sessionId  bigint                             not null comment '会话id',
    userId     bigint                             not null comment '用户id',
    content    text                               not null comment '消息内容',
    role       varchar(32)                        not null comment '消息角色：user/assistant',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_sessionId (sessionId),
    index idx_userId (userId)
) comment 'AI聊天消息' collate = utf8mb4_unicode_ci;

-- 每日单词表
create table if not exists daily_word
(
    id                 bigint auto_increment comment 'id' primary key,
    word               varchar(128)                       not null comment '单词',
    pronunciation      varchar(128)                       null comment '音标',
    audioUrl           varchar(1024)                      null comment '发音音频URL',
    translation        varchar(512)                       not null comment '翻译',
    example            text                               null comment '例句',
    exampleTranslation text                               null comment '例句翻译',
    difficulty         tinyint  default 1                 not null comment '难度等级：1-简单，2-中等，3-困难',
    category           varchar(64)                        null comment '单词分类',
    likeCount          int DEFAULT 0                      not null comment '点赞次数' after adminId;
    notes              text                               null comment '单词笔记或补充说明',
    publishDate        date                               not null comment '发布日期',
    adminId            bigint                             not null comment '创建管理员id',
    createTime         datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime         datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete           tinyint  default 0                 not null comment '是否删除',
    index idx_publishDate (publishDate),
    index idx_word (word),
    index idx_category (category),
    index idx_difficulty (difficulty)
) comment '每日单词' collate = utf8mb4_unicode_ci;

-- 每日文章表
create table if not exists daily_article
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(256)                       not null comment '文章标题',
    content     text                               not null comment '文章内容',
    summary     varchar(512)                       null comment '文章摘要',
    coverImage  varchar(1024)                      null comment '封面图片URL',
    author      varchar(128)                       null comment '作者',
    source      varchar(256)                       null comment '来源',
    sourceUrl   varchar(1024)                      null comment '原文链接',
    category    varchar(64)                        null comment '文章分类',
    tags        varchar(512)                       null comment '标签，JSON数组格式',
    difficulty  tinyint  default 1                 not null comment '难度等级：1-简单，2-中等，3-困难',
    readTime    int      default 0                 not null comment '预计阅读时间(分钟)',
    publishDate date                               not null comment '发布日期',
    adminId     bigint                             not null comment '创建管理员id',
    viewCount   int      default 0                 not null comment '查看次数',
    likeCount   int      default 0                 not null comment '点赞次数',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_publishDate (publishDate),
    index idx_category (category),
    index idx_difficulty (difficulty)
) comment '每日文章' collate = utf8mb4_unicode_ci;

-- 用户单词学习记录表
create table if not exists user_word_record
(
    id             bigint auto_increment comment 'id' primary key,
    userId         bigint                             not null comment '用户id',
    wordId         bigint                             not null comment '单词id',
    learningStatus tinyint  default 0                 not null comment '学习状态：0-未学习，1-已学习，2-已掌握',
    reviewCount    int      default 0                 not null comment '复习次数',
    lastReviewTime datetime                           null comment '最后一次复习时间',
    userNotes      text                               null comment '用户笔记',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_wordId (wordId),
    unique uk_user_word (userId, wordId)
) comment '用户单词学习记录' collate = utf8mb4_unicode_ci;

-- 用户文章阅读记录表
create table if not exists user_article_record
(
    id           bigint auto_increment comment 'id' primary key,
    userId       bigint                             not null comment '用户id',
    articleId    bigint                             not null comment '文章id',
    readStatus   tinyint  default 0                 not null comment '阅读状态：0-未读，1-阅读中，2-已读完',
    readProgress int      default 0                 not null comment '阅读进度(百分比)',
    isLiked      tinyint  default 0                 not null comment '是否点赞',
    userNotes    text                               null comment '用户笔记',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_articleId (articleId),
    unique uk_user_article (userId, articleId)
) comment '用户文章阅读记录' collate = utf8mb4_unicode_ci;

-- AI分身表
create table if not exists ai_avatar
(
    id             bigint auto_increment comment 'id' primary key,
    name           varchar(128)                            not null comment 'AI分身名称',
    baseUrl        varchar(1024)                           null comment '请求地址',
    description    text                                    null comment 'AI分身描述',
    avatarImgUrl   varchar(1024)                           null comment 'AI分身头像URL',
    avatarAuth     varchar(512)                            null comment 'AI分身鉴权，一串随机字符',
    tags           varchar(512)                            null comment '标签，JSON数组格式',
    personality    text                                    null comment '性格特点描述',
    abilities      text                                    null comment '能力描述',
    isPublic       tinyint       default 1                 not null comment '是否公开：0-否，1-是',
    status         tinyint       default 1                 not null comment '状态：0-禁用，1-启用',
    usageCount     int           default 0                 not null comment '使用次数',
    rating         decimal(2, 1) default 0.0               not null comment '评分，1-5分',
    ratingCount    int           default 0                 not null comment '评分人数',
    creatorId      bigint                                  not null comment '创建者id',
    sort           int           default 0                 not null comment '排序，数字越小排序越靠前',
    createTime     datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint       default 0                 not null comment '是否删除',
    index idx_tags (tags),
    index idx_creatorId (creatorId),
    index idx_status (status),
    index idx_sort (sort),
    index idx_usageCount (usageCount)
) comment 'AI分身' collate = utf8mb4_unicode_ci;

-- 用户AI分身关联表
create table if not exists user_ai_avatar
(
    id             bigint auto_increment comment 'id' primary key,
    userId         bigint                             not null comment '用户id',
    aiAvatarId     bigint                             not null comment 'AI分身id',
    isFavorite     tinyint  default 0                 not null comment '是否收藏：0-否，1-是',
    lastUseTime    datetime                           null comment '最后使用时间',
    useCount       int      default 0                 not null comment '使用次数',
    userRating     decimal(2, 1)                      null comment '用户评分，1-5分',
    userFeedback   text                               null comment '用户反馈',
    customSettings text                               null comment '用户自定义设置，JSON格式',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_aiAvatarId (aiAvatarId),
    index idx_isFavorite (isFavorite),
    unique uk_user_avatar (userId, aiAvatarId)
) comment '用户AI分身关联' collate = utf8mb4_unicode_ci;

-- AI分身对话历史表
create table if not exists ai_avatar_chat_history
(
    id          bigint auto_increment comment 'id' primary key,
    userId      bigint                             not null comment '用户id',
    aiAvatarId  bigint                             not null comment 'AI分身id',
    sessionId   varchar(64)                        not null comment '会话ID',
    sessionName varchar(512)                       null comment '会话总结标题',
    messageType varchar(32)                        not null comment '消息类型：user/ai',
    content     text                               not null comment '消息内容',
    tokens      int      default 0                 not null comment '消息token数',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    index idx_userId (userId),
    index idx_aiAvatarId (aiAvatarId),
    index idx_sessionId (sessionId)
) comment 'AI分身对话历史' collate = utf8mb4_unicode_ci;

-- 用户反馈表
create table if not exists user_feedback
(
    id          bigint auto_increment comment 'id' primary key,
    userId      bigint                             not null comment '用户ID',
    feedbackType varchar(64)                       not null comment '反馈类型',
    title       varchar(256)                       null comment '反馈标题',
    content     text                               not null comment '反馈内容',
    attachment  varchar(1024)                      null comment '附件URL',
    status      tinyint  default 0                 not null comment '处理状态：0-待处理，1-处理中，2-已处理',
    adminId     bigint                             null comment '处理管理员ID',
    processTime datetime                           null comment '处理时间',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId),
    index idx_status (status)
) comment '用户反馈' collate = utf8mb4_unicode_ci;
