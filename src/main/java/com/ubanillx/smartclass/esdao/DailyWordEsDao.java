package com.ubanillx.smartclass.esdao;

import com.ubanillx.smartclass.model.dto.dailyword.DailyWordEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * 每日单词 ES 操作
 */
public interface DailyWordEsDao extends ElasticsearchRepository<DailyWordEsDTO, Long> {

    /**
     * 按单词查找
     * @param word 单词
     * @return 单词列表
     */
    List<DailyWordEsDTO> findByWord(String word);

    /**
     * 按难度级别查找
     * @param difficulty 难度级别
     * @return 单词列表
     */
    List<DailyWordEsDTO> findByDifficulty(Integer difficulty);

    /**
     * 按分类查找
     * @param category 分类
     * @return 单词列表
     */
    List<DailyWordEsDTO> findByCategory(String category);

    /**
     * 按管理员ID查找
     * @param adminId 管理员ID
     * @return 单词列表
     */
    List<DailyWordEsDTO> findByAdminId(Long adminId);
} 