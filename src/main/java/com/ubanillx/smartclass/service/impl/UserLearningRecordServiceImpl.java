package com.ubanillx.smartclass.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.constant.CommonConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.mapper.UserLearningRecordMapper;
import com.ubanillx.smartclass.model.dto.learningrecord.UserLearningRecordQueryRequest;
import com.ubanillx.smartclass.model.entity.UserLearningRecord;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.entity.DailyWord;
import com.ubanillx.smartclass.model.vo.UserLearningRecordVO;
import com.ubanillx.smartclass.service.DailyArticleService;
import com.ubanillx.smartclass.service.DailyWordService;
import com.ubanillx.smartclass.service.UserLearningRecordService;
import com.ubanillx.smartclass.service.UserService;
import com.ubanillx.smartclass.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户学习记录服务实现
 */
@Service
@Slf4j
public class UserLearningRecordServiceImpl extends ServiceImpl<UserLearningRecordMapper, UserLearningRecord> implements UserLearningRecordService {

    @Resource
    private UserService userService;

    @Resource
    private DailyWordService dailyWordService;

    @Resource
    private DailyArticleService dailyArticleService;

    @Override
    public void validUserLearningRecord(UserLearningRecord userLearningRecord, boolean add) {
        if (userLearningRecord == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        // 创建时必填参数
        if (add) {
            if (userLearningRecord.getUserId() == null || userLearningRecord.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
            }
            
            if (StringUtils.isBlank(userLearningRecord.getRecordType())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "记录类型不能为空");
            }
            
            // 如果没有设置记录日期，默认为当天
            if (userLearningRecord.getRecordDate() == null) {
                userLearningRecord.setRecordDate(new Date());
            }
        }
        
        // 验证关联ID
        if (userLearningRecord.getRelatedId() != null && userLearningRecord.getRelatedId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "关联ID不合法");
        }
        
        // 验证时长和数量（非负数）
        if (userLearningRecord.getDuration() != null && userLearningRecord.getDuration() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学习时长不能为负数");
        }
        
        if (userLearningRecord.getCount() != null && userLearningRecord.getCount() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学习数量不能为负数");
        }
        
        // 验证积分和经验值（非负数）
        if (userLearningRecord.getPoints() != null && userLearningRecord.getPoints() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "积分不能为负数");
        }
        
        if (userLearningRecord.getExperience() != null && userLearningRecord.getExperience() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "经验值不能为负数");
        }
        
        // 验证正确率（0-100之间）
        if (userLearningRecord.getAccuracy() != null) {
            BigDecimal hundred = new BigDecimal(100);
            BigDecimal zero = BigDecimal.ZERO;
            
            if (userLearningRecord.getAccuracy().compareTo(zero) < 0 || userLearningRecord.getAccuracy().compareTo(hundred) > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "正确率必须在0-100之间");
            }
        }
    }

    @Override
    public QueryWrapper<UserLearningRecord> getQueryWrapper(UserLearningRecordQueryRequest userLearningRecordQueryRequest) {
        QueryWrapper<UserLearningRecord> queryWrapper = new QueryWrapper<>();
        if (userLearningRecordQueryRequest == null) {
            return queryWrapper;
        }
        
        Long id = userLearningRecordQueryRequest.getId();
        Long userId = userLearningRecordQueryRequest.getUserId();
        Date startDate = userLearningRecordQueryRequest.getStartDate();
        Date endDate = userLearningRecordQueryRequest.getEndDate();
        String recordType = userLearningRecordQueryRequest.getRecordType();
        Long relatedId = userLearningRecordQueryRequest.getRelatedId();
        Integer lessonNumber = userLearningRecordQueryRequest.getLessonNumber();
        Integer minDuration = userLearningRecordQueryRequest.getMinDuration();
        Integer maxDuration = userLearningRecordQueryRequest.getMaxDuration();
        Integer minCount = userLearningRecordQueryRequest.getMinCount();
        Integer maxCount = userLearningRecordQueryRequest.getMaxCount();
        Integer minPoints = userLearningRecordQueryRequest.getMinPoints();
        Integer maxPoints = userLearningRecordQueryRequest.getMaxPoints();
        Integer minExperience = userLearningRecordQueryRequest.getMinExperience();
        Integer maxExperience = userLearningRecordQueryRequest.getMaxExperience();
        BigDecimal minAccuracy = userLearningRecordQueryRequest.getMinAccuracy();
        BigDecimal maxAccuracy = userLearningRecordQueryRequest.getMaxAccuracy();
        String status = userLearningRecordQueryRequest.getStatus();
        String keyword = userLearningRecordQueryRequest.getKeyword();
        String sortField = userLearningRecordQueryRequest.getSortField();
        String sortOrder = userLearningRecordQueryRequest.getSortOrder();
        
        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        
        // 日期范围查询
        if (startDate != null) {
            queryWrapper.ge("recordDate", startDate);
        }
        
        if (endDate != null) {
            queryWrapper.le("recordDate", endDate);
        }
        
        queryWrapper.eq(StringUtils.isNotBlank(recordType), "recordType", recordType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(relatedId), "relatedId", relatedId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(lessonNumber), "lessonNumber", lessonNumber);
        
        // 时长范围查询
        if (minDuration != null) {
            queryWrapper.ge("duration", minDuration);
        }
        
        if (maxDuration != null) {
            queryWrapper.le("duration", maxDuration);
        }
        
        // 数量范围查询
        if (minCount != null) {
            queryWrapper.ge("count", minCount);
        }
        
        if (maxCount != null) {
            queryWrapper.le("count", maxCount);
        }
        
        // 积分范围查询
        if (minPoints != null) {
            queryWrapper.ge("points", minPoints);
        }
        
        if (maxPoints != null) {
            queryWrapper.le("points", maxPoints);
        }
        
        // 经验值范围查询
        if (minExperience != null) {
            queryWrapper.ge("experience", minExperience);
        }
        
        if (maxExperience != null) {
            queryWrapper.le("experience", maxExperience);
        }
        
        // 正确率范围查询
        if (minAccuracy != null) {
            queryWrapper.ge("accuracy", minAccuracy);
        }
        
        if (maxAccuracy != null) {
            queryWrapper.le("accuracy", maxAccuracy);
        }
        
        queryWrapper.eq(StringUtils.isNotBlank(status), "status", status);
        
        // 备注关键词搜索
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.like("remark", keyword);
        }
        
        // 排序
        String defaultSortField = sortField != null ? sortField : "createTime";
        String defaultSortOrder = sortOrder != null ? sortOrder : CommonConstant.SORT_ORDER_DESC;
        SqlUtils.setDefaultOrder(queryWrapper, defaultSortField, defaultSortOrder);
        
        return queryWrapper;
    }

    @Override
    public UserLearningRecordVO getUserLearningRecordVO(UserLearningRecord userLearningRecord, HttpServletRequest request) {
        if (userLearningRecord == null) {
            return null;
        }
        
        UserLearningRecordVO userLearningRecordVO = new UserLearningRecordVO();
        BeanUtils.copyProperties(userLearningRecord, userLearningRecordVO);
        
        // 获取用户信息
        Long userId = userLearningRecord.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            if (user != null) {
                userLearningRecordVO.setUserName(user.getUserName());
                userLearningRecordVO.setUserAvatar(user.getUserAvatar());
            }
        }
        
        // 设置记录类型名称
        String recordType = userLearningRecord.getRecordType();
        if (StringUtils.isNotBlank(recordType)) {
            switch (recordType) {
                case "word_card":
                    userLearningRecordVO.setRecordTypeName("单词学习");
                    break;
                case "listening":
                    userLearningRecordVO.setRecordTypeName("听力练习");
                    break;
                case "course":
                    userLearningRecordVO.setRecordTypeName("课程学习");
                    break;
                case "reading":
                    userLearningRecordVO.setRecordTypeName("阅读练习");
                    break;
                default:
                    userLearningRecordVO.setRecordTypeName(recordType);
            }
        }
        
        // 设置关联内容名称
        Long relatedId = userLearningRecord.getRelatedId();
        if (relatedId != null && relatedId > 0) {
            if ("word_card".equals(recordType)) {
                try {
                    DailyWord dailyWord = dailyWordService.getById(relatedId);
                    if (dailyWord != null) {
                        userLearningRecordVO.setRelatedName(dailyWord.getWord());
                    }
                } catch (Exception e) {
                    log.error("获取单词信息失败", e);
                }
            } else if ("reading".equals(recordType)) {
                try {
                    userLearningRecordVO.setRelatedName(dailyArticleService.getById(relatedId).getTitle());
                } catch (Exception e) {
                    log.error("获取文章信息失败", e);
                }
            }
            // 其他类型可以在这里添加
        }
        
        // 格式化时长
        Integer duration = userLearningRecord.getDuration();
        if (duration != null) {
            int hours = duration / 3600;
            int minutes = (duration % 3600) / 60;
            int seconds = duration % 60;
            
            if (hours > 0) {
                userLearningRecordVO.setFormattedDuration(String.format("%d小时%d分钟%d秒", hours, minutes, seconds));
            } else if (minutes > 0) {
                userLearningRecordVO.setFormattedDuration(String.format("%d分钟%d秒", minutes, seconds));
            } else {
                userLearningRecordVO.setFormattedDuration(String.format("%d秒", seconds));
            }
        }
        
        // 设置状态名称
        String status = userLearningRecord.getStatus();
        if (StringUtils.isNotBlank(status)) {
            switch (status) {
                case "in_progress":
                    userLearningRecordVO.setStatusName("进行中");
                    break;
                case "completed":
                    userLearningRecordVO.setStatusName("已完成");
                    break;
                case "failed":
                    userLearningRecordVO.setStatusName("未通过");
                    break;
                default:
                    userLearningRecordVO.setStatusName(status);
            }
        }
        
        return userLearningRecordVO;
    }

    @Override
    public List<UserLearningRecordVO> getUserLearningRecordVO(List<UserLearningRecord> userLearningRecordList, HttpServletRequest request) {
        if (CollUtil.isEmpty(userLearningRecordList)) {
            return new ArrayList<>();
        }
        return userLearningRecordList.stream()
                .map(userLearningRecord -> getUserLearningRecordVO(userLearningRecord, request))
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserLearningRecordVO> getUserLearningRecordVOPage(Page<UserLearningRecord> userLearningRecordPage, HttpServletRequest request) {
        List<UserLearningRecord> userLearningRecordList = userLearningRecordPage.getRecords();
        Page<UserLearningRecordVO> userLearningRecordVOPage = new Page<>(userLearningRecordPage.getCurrent(), userLearningRecordPage.getSize(), userLearningRecordPage.getTotal());
        userLearningRecordVOPage.setRecords(getUserLearningRecordVO(userLearningRecordList, request));
        return userLearningRecordVOPage;
    }

    @Override
    public long addUserLearningRecord(UserLearningRecord userLearningRecord, Long userId) {
        if (userLearningRecord == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        
        // 设置用户ID
        userLearningRecord.setUserId(userId);
        
        // 如果没有设置记录日期，默认为当天
        if (userLearningRecord.getRecordDate() == null) {
            userLearningRecord.setRecordDate(new Date());
        }
        
        // 校验记录
        validUserLearningRecord(userLearningRecord, true);
        
        // 保存记录
        boolean result = this.save(userLearningRecord);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "添加学习记录失败");
        
        return userLearningRecord.getId();
    }

    @Override
    public List<Map<String, Object>> getUserLearningDurationStats(Long userId, Date startDate, Date endDate) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        
        // 如果未指定起止日期，则默认为最近7天
        if (startDate == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -7);
            startDate = calendar.getTime();
        }
        
        if (endDate == null) {
            endDate = new Date();
        }
        
        return baseMapper.sumDurationByType(userId, startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getUserLearningCountStats(Long userId, Date startDate, Date endDate) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        
        // 如果未指定起止日期，则默认为最近7天
        if (startDate == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -7);
            startDate = calendar.getTime();
        }
        
        if (endDate == null) {
            endDate = new Date();
        }
        
        return baseMapper.sumCountByType(userId, startDate, endDate);
    }

    @Override
    public Map<String, Object> getUserPointsAndExperienceStats(Long userId, Date startDate, Date endDate) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        
        // 如果未指定起止日期，则默认为最近7天
        if (startDate == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -7);
            startDate = calendar.getTime();
        }
        
        if (endDate == null) {
            endDate = new Date();
        }
        
        Map<String, Object> result = baseMapper.sumPointsAndExperience(userId, startDate, endDate);
        
        // 如果返回为空，创建默认值
        if (result == null) {
            result = new HashMap<>();
            result.put("totalPoints", 0);
            result.put("totalExperience", 0);
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getUserDailyLearningStats(Long userId, Date startDate, Date endDate) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        
        // 如果未指定起止日期，则默认为最近30天
        if (startDate == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -30);
            startDate = calendar.getTime();
        }
        
        if (endDate == null) {
            endDate = new Date();
        }
        
        return baseMapper.countDailyRecords(userId, startDate, endDate);
    }

    @Override
    public long recordCourseStudy(Long userId, Long courseId, Long sectionId, Integer duration, Integer progress) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        if (courseId == null || courseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "课程ID不合法");
        }
        if (duration == null || duration < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学习时长不能为负数");
        }
        
        // 创建学习记录
        UserLearningRecord record = new UserLearningRecord();
        record.setUserId(userId);
        record.setRecordType("course");
        record.setRelatedId(courseId);
        
        if (sectionId != null && sectionId > 0) {
            record.setLessonNumber(Math.toIntExact(sectionId));
        }
        
        record.setDuration(duration);
        
        // 根据进度设置状态
        if (progress != null) {
            if (progress >= 100) {
                record.setStatus("completed");
            } else if (progress > 0) {
                record.setStatus("in_progress");
                record.setCount(progress); // 可以用count字段记录进度百分比
            } else {
                record.setStatus("in_progress");
            }
        } else {
            record.setStatus("in_progress");
        }
        
        // 计算获得的积分和经验值（根据业务规则定制）
        // 这里假设每学习1分钟获得1积分和2经验值
        int minutes = (duration + 30) / 60; // 四舍五入到分钟
        record.setPoints(minutes);
        record.setExperience(minutes * 2);
        
        return addUserLearningRecord(record, userId);
    }

    @Override
    public long recordWordStudy(Long userId, Long wordId, Integer count, Double accuracy) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        if (wordId == null || wordId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单词ID不合法");
        }
        
        // 创建学习记录
        UserLearningRecord record = new UserLearningRecord();
        record.setUserId(userId);
        record.setRecordType("word_card");
        record.setRelatedId(wordId);
        
        // 设置学习数量
        if (count != null && count > 0) {
            record.setCount(count);
        } else {
            record.setCount(1); // 默认学习了1个单词
        }
        
        // 设置正确率
        if (accuracy != null) {
            record.setAccuracy(BigDecimal.valueOf(accuracy));
            
            // 根据正确率设置状态
            if (accuracy >= 80) {
                record.setStatus("completed");
            } else if (accuracy >= 60) {
                record.setStatus("in_progress");
            } else {
                record.setStatus("failed");
            }
        } else {
            record.setStatus("in_progress");
        }
        
        // 计算获得的积分和经验值（根据业务规则定制）
        // 这里假设每个单词获得2积分，如果正确率高于80%，额外获得1积分
        int points = record.getCount() * 2;
        if (accuracy != null && accuracy >= 80) {
            points += record.getCount();
        }
        
        record.setPoints(points);
        record.setExperience(points * 2); // 经验值是积分的两倍
        
        return addUserLearningRecord(record, userId);
    }

    @Override
    public long recordListeningPractice(Long userId, Long listeningId, Integer duration, Double accuracy) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        if (listeningId == null || listeningId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "听力ID不合法");
        }
        if (duration == null || duration < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学习时长不能为负数");
        }
        
        // 创建学习记录
        UserLearningRecord record = new UserLearningRecord();
        record.setUserId(userId);
        record.setRecordType("listening");
        record.setRelatedId(listeningId);
        record.setDuration(duration);
        
        // 设置正确率
        if (accuracy != null) {
            record.setAccuracy(BigDecimal.valueOf(accuracy));
            
            // 根据正确率设置状态
            if (accuracy >= 80) {
                record.setStatus("completed");
            } else if (accuracy >= 60) {
                record.setStatus("in_progress");
            } else {
                record.setStatus("failed");
            }
        } else {
            record.setStatus("in_progress");
        }
        
        // 计算获得的积分和经验值（根据业务规则定制）
        // 这里假设每分钟听力练习获得3积分，如果正确率高于80%，额外获得duration/60积分
        int minutes = (duration + 30) / 60; // 四舍五入到分钟
        int points = minutes * 3;
        
        if (accuracy != null && accuracy >= 80) {
            points += minutes;
        }
        
        record.setPoints(points);
        record.setExperience(points * 2); // 经验值是积分的两倍
        
        return addUserLearningRecord(record, userId);
    }

    @Override
    public long recordReadingPractice(Long userId, Long articleId, Integer duration, Double accuracy) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不合法");
        }
        if (duration == null || duration < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学习时长不能为负数");
        }
        
        // 创建学习记录
        UserLearningRecord record = new UserLearningRecord();
        record.setUserId(userId);
        record.setRecordType("reading");
        record.setRelatedId(articleId);
        record.setDuration(duration);
        
        // 设置正确率
        if (accuracy != null) {
            record.setAccuracy(BigDecimal.valueOf(accuracy));
            
            // 根据正确率设置状态
            if (accuracy >= 80) {
                record.setStatus("completed");
            } else if (accuracy >= 60) {
                record.setStatus("in_progress");
            } else {
                record.setStatus("failed");
            }
        } else {
            record.setStatus("in_progress");
        }
        
        // 计算获得的积分和经验值（根据业务规则定制）
        // 这里假设每分钟阅读练习获得2积分，如果正确率高于80%，额外获得duration/60积分
        int minutes = (duration + 30) / 60; // 四舍五入到分钟
        int points = minutes * 2;
        
        if (accuracy != null && accuracy >= 80) {
            points += minutes;
        }
        
        record.setPoints(points);
        record.setExperience(points * 2); // 经验值是积分的两倍
        
        return addUserLearningRecord(record, userId);
    }
} 