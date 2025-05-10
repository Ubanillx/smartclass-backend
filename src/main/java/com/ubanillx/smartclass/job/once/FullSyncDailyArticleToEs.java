package com.ubanillx.smartclass.job.once;

import cn.hutool.core.collection.CollUtil;
import com.ubanillx.smartclass.esdao.DailyArticleEsDao;
import com.ubanillx.smartclass.model.dto.dailyarticle.DailyArticleEsDTO;
import com.ubanillx.smartclass.model.entity.DailyArticle;
import com.ubanillx.smartclass.service.DailyArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全量同步每日美文到 ES
 */
@Component
@Slf4j
public class FullSyncDailyArticleToEs implements CommandLineRunner {

    @Resource
    private DailyArticleService dailyArticleService;

    @Resource
    private DailyArticleEsDao dailyArticleEsDao;

    @Override
    public void run(String... args) {
        List<DailyArticle> dailyArticleList = dailyArticleService.list();
        if (CollUtil.isEmpty(dailyArticleList)) {
            return;
        }
        List<DailyArticleEsDTO> dailyArticleEsDTOList = dailyArticleList.stream()
                .map(DailyArticleEsDTO::objToDto)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = dailyArticleEsDTOList.size();
        log.info("FullSyncDailyArticleToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            dailyArticleEsDao.saveAll(dailyArticleEsDTOList.subList(i, end));
        }
        log.info("FullSyncDailyArticleToEs end, total {}", total);
    }
} 