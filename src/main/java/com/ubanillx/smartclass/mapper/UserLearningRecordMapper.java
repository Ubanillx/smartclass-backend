package com.ubanillx.smartclass.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ubanillx.smartclass.model.entity.UserLearningRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author liulo
* @description 针对表【user_learning_record(用户学习记录)】的数据库操作Mapper
* @createDate 2025-03-21 15:14:50
* @Entity com.ubanillx.smartclass.model.entity.UserLearningRecord
*/
public interface UserLearningRecordMapper extends BaseMapper<UserLearningRecord> {

    /**
     * 统计用户在指定日期范围内各类型的学习时长
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 各类型学习时长统计
     */
    @Select("SELECT recordType, SUM(duration) as totalDuration " +
            "FROM user_learning_record " +
            "WHERE userId = #{userId} " +
            "AND recordDate BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY recordType")
    List<Map<String, Object>> sumDurationByType(@Param("userId") Long userId, 
                                              @Param("startDate") Date startDate, 
                                              @Param("endDate") Date endDate);
    
    /**
     * 统计用户在指定日期范围内各类型的学习数量
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 各类型学习数量统计
     */
    @Select("SELECT recordType, SUM(count) as totalCount " +
            "FROM user_learning_record " +
            "WHERE userId = #{userId} " +
            "AND recordDate BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY recordType")
    List<Map<String, Object>> sumCountByType(@Param("userId") Long userId, 
                                           @Param("startDate") Date startDate, 
                                           @Param("endDate") Date endDate);
                                           
    /**
     * 统计用户在指定日期范围内获得的总积分和经验值
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 包含totalPoints和totalExperience的Map
     */
    @Select("SELECT SUM(points) as totalPoints, SUM(experience) as totalExperience " +
            "FROM user_learning_record " +
            "WHERE userId = #{userId} " +
            "AND recordDate BETWEEN #{startDate} AND #{endDate}")
    Map<String, Object> sumPointsAndExperience(@Param("userId") Long userId, 
                                             @Param("startDate") Date startDate, 
                                             @Param("endDate") Date endDate);
                                             
    /**
     * 获取用户每日学习记录数量统计
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日学习记录数量
     */
    @Select("SELECT recordDate, COUNT(*) as recordCount " +
            "FROM user_learning_record " +
            "WHERE userId = #{userId} " +
            "AND recordDate BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY recordDate " +
            "ORDER BY recordDate")
    List<Map<String, Object>> countDailyRecords(@Param("userId") Long userId, 
                                              @Param("startDate") Date startDate, 
                                              @Param("endDate") Date endDate);
}




