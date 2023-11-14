package com.small.archive.schedule;

import com.small.archive.core.check.inteceptors.CheckInterceptorManager;
import com.small.archive.core.emuns.ArchiveJobStatus;
import com.small.archive.dao.ArchiveDao;
import com.small.archive.pojo.ArchiveJobConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Project : small-db-archive
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */

@Slf4j
@Service
public class ArchiveConfCheckTask {


    @Autowired
    private ArchiveDao archiveDao;

    @Autowired
    private CheckInterceptorManager checkInterceptorManager;




    @Transactional
    public void archiveConfWatch() {
        ArchiveJobConfig query = new ArchiveJobConfig();
        query.setJobStatus(ArchiveJobStatus.PREPARE.getStatus());
        List<ArchiveJobConfig> archiveJobConfigs = archiveDao.queryArchiveConfList(query);
        if (CollectionUtils.isEmpty(archiveJobConfigs)) {
            log.info("未检测到预备[PREPARE]的归档配置！");
        } else {
            for (ArchiveJobConfig conf : archiveJobConfigs) {
                checkInterceptorManager.applyCheck(conf);
            }
        }

        // 补偿上次操作CHECKING 失败的
        query.setJobStatus(ArchiveJobStatus.CHECKED_FAILED.getStatus());
        List<ArchiveJobConfig> archiveCheckFailed = archiveDao.queryArchiveConfList(query);
        if (CollectionUtils.isEmpty(archiveCheckFailed)) {
            log.info("检查上次 CHECKED_FAILED的数据, 未检测到 CHECKED_FAILED 的归档配置！");
            return;
        }
        log.info("检查上次 CHECKED_FAILED的数据, 检测到 CHECKED_FAILED 的归档配置: " + archiveCheckFailed.size() + " 个");
        for (ArchiveJobConfig confFailed : archiveCheckFailed) {
            checkInterceptorManager.applyCheck(confFailed);
        }
    }
}
