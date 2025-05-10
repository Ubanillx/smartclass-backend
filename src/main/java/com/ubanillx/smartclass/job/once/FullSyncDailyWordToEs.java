package com.ubanillx.smartclass.job.once;

import cn.hutool.core.collection.CollUtil;
import com.ubanillx.smartclass.esdao.DailyWordEsDao;
import com.ubanillx.smartclass.model.dto.dailyword.DailyWordEsDTO;
import com.ubanillx.smartclass.model.entity.DailyWord;
import com.ubanillx.smartclass.service.DailyWordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全量同步每日单词到 ES
 */
@Component
@Slf4j
public class FullSyncDailyWordToEs implements CommandLineRunner {

    @Resource
    private DailyWordService dailyWordService;

    @Resource
    private DailyWordEsDao dailyWordEsDao;

    @Override
    public void run(String... args) {
        List<DailyWord> dailyWordList = dailyWordService.list();
        if (CollUtil.isEmpty(dailyWordList)) {
            return;
        }
        List<DailyWordEsDTO> dailyWordEsDTOList = dailyWordList.stream()
                .map(DailyWordEsDTO::objToDto)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = dailyWordEsDTOList.size();
        log.info("FullSyncDailyWordToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            dailyWordEsDao.saveAll(dailyWordEsDTOList.subList(i, end));
        }
        log.info("FullSyncDailyWordToEs end, total {}", total);
    }
} 