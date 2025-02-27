-- 切换库
use smart_class;

-- 每日单词表
create table if not exists daily_word
(
    id              bigint auto_increment comment 'id' primary key,
    word            varchar(128)                       not null comment '单词',
    pronunciation   varchar(128)                       null comment '音标',
    audioUrl        varchar(1024)                      null comment '发音音频URL',
    translation     varchar(512)                       not null comment '翻译',
    example         text                               null comment '例句',
    exampleTranslation text                            null comment '例句翻译',
    difficulty      tinyint  default 1                 not null comment '难度等级：1-简单，2-中等，3-困难',
    category        varchar(64)                        null comment '单词分类',
    notes           text                               null comment '单词笔记或补充说明',
    publishDate     date                               not null comment '发布日期',
    adminId         bigint                             not null comment '创建管理员id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_publishDate (publishDate),
    index idx_word (word),
    index idx_category (category),
    index idx_difficulty (difficulty)
) comment '每日单词' collate = utf8mb4_unicode_ci;

-- 每日文章表
create table if not exists daily_article
(
    id              bigint auto_increment comment 'id' primary key,
    title           varchar(256)                       not null comment '文章标题',
    content         text                               not null comment '文章内容',
    summary         varchar(512)                       null comment '文章摘要',
    coverImage      varchar(1024)                      null comment '封面图片URL',
    author          varchar(128)                       null comment '作者',
    source          varchar(256)                       null comment '来源',
    sourceUrl       varchar(1024)                      null comment '原文链接',
    category        varchar(64)                        null comment '文章分类',
    tags            varchar(512)                       null comment '标签，JSON数组格式',
    difficulty      tinyint  default 1                 not null comment '难度等级：1-简单，2-中等，3-困难',
    readTime        int      default 0                 not null comment '预计阅读时间(分钟)',
    publishDate     date                               not null comment '发布日期',
    adminId         bigint                             not null comment '创建管理员id',
    viewCount       int      default 0                 not null comment '查看次数',
    likeCount       int      default 0                 not null comment '点赞次数',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_publishDate (publishDate),
    index idx_category (category),
    index idx_difficulty (difficulty)
) comment '每日文章' collate = utf8mb4_unicode_ci;

-- 用户单词学习记录表
create table if not exists user_word_record
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '用户id',
    wordId          bigint                             not null comment '单词id',
    learningStatus  tinyint  default 0                 not null comment '学习状态：0-未学习，1-已学习，2-已掌握',
    reviewCount     int      default 0                 not null comment '复习次数',
    lastReviewTime  datetime                           null comment '最后一次复习时间',
    userNotes       text                               null comment '用户笔记',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_wordId (wordId),
    unique uk_user_word (userId, wordId)
) comment '用户单词学习记录' collate = utf8mb4_unicode_ci;

-- 用户文章阅读记录表
create table if not exists user_article_record
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '用户id',
    articleId       bigint                             not null comment '文章id',
    readStatus      tinyint  default 0                 not null comment '阅读状态：0-未读，1-阅读中，2-已读完',
    readProgress    int      default 0                 not null comment '阅读进度(百分比)',
    isLiked         tinyint  default 0                 not null comment '是否点赞',
    userNotes       text                               null comment '用户笔记',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_articleId (articleId),
    unique uk_user_article (userId, articleId)
) comment '用户文章阅读记录' collate = utf8mb4_unicode_ci; 