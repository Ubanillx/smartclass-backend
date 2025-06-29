package com.ubanillx.smartclass.esdao;

import com.ubanillx.smartclass.model.dto.dailyarticle.DailyArticleEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * 每日美文 ES 操作
 */
public interface DailyArticleEsDao extends ElasticsearchRepository<DailyArticleEsDTO, Long> {

    /**
     * 按标题查找
     * @param title 标题
     * @return 文章列表
     */
    List<DailyArticleEsDTO> findByTitle(String title);

    /**
     * 按作者查找
     * @param author 作者
     * @return 文章列表
     */
    List<DailyArticleEsDTO> findByAuthor(String author);

    /**
     * 按来源查找
     * @param source 来源
     * @return 文章列表
     */
    List<DailyArticleEsDTO> findBySource(String source);

    /**
     * 按分类查找
     * @param category 分类
     * @return 文章列表
     */
    List<DailyArticleEsDTO> findByCategory(String category);

    /**
     * 按难度级别查找
     * @param difficulty 难度级别
     * @return 文章列表
     */
    List<DailyArticleEsDTO> findByDifficulty(Integer difficulty);

    /**
     * 按管理员ID查找
     * @param adminId 管理员ID
     * @return 文章列表
     */
    List<DailyArticleEsDTO> findByAdminId(Long adminId);
} 