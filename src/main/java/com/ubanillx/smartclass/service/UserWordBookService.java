package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.dto.userwordbook.UserWordBookQueryRequest;
import com.ubanillx.smartclass.model.entity.UserWordBook;
import com.ubanillx.smartclass.model.vo.UserWordBookVO;

import java.util.List;

/**
* @author rog
* @description 针对表【user_word_book(用户生词本)】的数据库操作Service
* @createDate 2025-04-17 22:49:58
*/
public interface UserWordBookService extends IService<UserWordBook> {

    /**
     * 添加单词到生词本
     * 
     * @param userId 用户ID
     * @param wordId 单词ID
     * @param difficulty 难度等级
     * @return 是否添加成功
     */
    boolean addToWordBook(Long userId, Long wordId, Integer difficulty);
    
    /**
     * 从生词本移除单词
     * 
     * @param userId 用户ID
     * @param wordId 单词ID
     * @return 是否移除成功
     */
    boolean removeFromWordBook(Long userId, Long wordId);
    
    /**
     * 更新单词学习状态
     * 
     * @param userId 用户ID
     * @param wordId 单词ID
     * @param learningStatus 学习状态：0-未学习，1-已学习，2-已掌握
     * @return 是否更新成功
     */
    boolean updateLearningStatus(Long userId, Long wordId, Integer learningStatus);
    
    /**
     * 更新单词难度
     * 
     * @param userId 用户ID
     * @param wordId 单词ID
     * @param difficulty 难度等级：1-简单，2-中等，3-困难
     * @return 是否更新成功
     */
    boolean updateDifficulty(Long userId, Long wordId, Integer difficulty);
    
    /**
     * 获取用户生词本列表
     * 
     * @param userId 用户ID
     * @param learningStatus 学习状态：0-未学习，1-已学习，2-已掌握，null表示全部
     * @param isCollected 是否收藏：0-否，1-是，null表示全部
     * @return 生词本列表
     */
    List<UserWordBookVO> getUserWordBookList(Long userId, Integer learningStatus, Integer isCollected);

    /**
     * 根据条件获取单词本查询条件
     *
     * @param userWordBookQueryRequest 查询请求
     * @return 查询条件
     */
    QueryWrapper<UserWordBook> getQueryWrapper(UserWordBookQueryRequest userWordBookQueryRequest);

    /**
     * 获取单词本视图对象
     *
     * @param userWordBook 单词本实体
     * @return 单词本视图对象
     */
    UserWordBookVO getUserWordBookVO(UserWordBook userWordBook);

    /**
     * 获取单词本视图对象列表
     *
     * @param userWordBookList 单词本实体列表
     * @return 单词本视图对象列表
     */
    List<UserWordBookVO> getUserWordBookVO(List<UserWordBook> userWordBookList);
    
    /**
     * 查询用户生词数量统计
     * 
     * @param userId 用户ID
     * @return 返回统计数据：总收藏数，已学习数，待复习数
     */
    int[] getUserWordBookStatistics(Long userId);
    
    /**
     * 判断单词是否在用户的生词本中
     * 
     * @param userId 用户ID
     * @param wordId 单词ID
     * @return 是否在生词本中
     */
    boolean isWordInUserBook(Long userId, Long wordId);
}
