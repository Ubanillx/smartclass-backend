package com.ubanillx.smartclass.manager;

import com.ubanillx.smartclass.model.entity.Course;
import com.ubanillx.smartclass.model.entity.CourseReview;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 课程管理类
 */
@Component
public class CourseManager {

    /**
     * 检查课程是否可用
     * @param course 课程
     * @return 是否可用
     */
    public boolean isCourseAvailable(Course course) {
        if (course == null) {
            return false;
        }
        // 检查状态是否为已发布
        if (course.getStatus() != 1) {
            return false;
        }
        // 检查是否被删除
        if (course.getIsDelete() != null && course.getIsDelete() == 1) {
            return false;
        }
        return true;
    }

    /**
     * 增加学习人数
     * @param course 课程
     */
    public void incrementStudentCount(Course course) {
        if (course != null) {
            Integer currentCount = course.getStudentCount();
            if (currentCount == null) {
                currentCount = 0;
            }
            course.setStudentCount(currentCount + 1);
        }
    }


    /**
     * 更新课程总时长
     * @param course 课程
     * @param totalMinutes 总分钟数
     */
    public void updateTotalDuration(Course course, Integer totalMinutes) {
        if (course != null && totalMinutes != null) {
            course.setTotalDuration(totalMinutes);
        }
    }

    /**
     * 更新课程章节数量
     * @param course 课程
     * @param chapterCount 章节数
     * @param sectionCount 小节数
     */
    public void updateChapterAndSectionCount(Course course, Integer chapterCount, Integer sectionCount) {
        if (course != null) {
            if (chapterCount != null) {
                course.setTotalChapters(chapterCount);
            }
            if (sectionCount != null) {
                course.setTotalSections(sectionCount);
            }
        }
    }
} 