package com.small.archive.service.task;

import com.small.archive.core.check.inteceptors.CheckInterceptorManager;
import com.small.archive.core.emuns.ArchiveConfStatus;
import com.small.archive.dao.ArchiveDao;
import com.small.archive.pojo.ArchiveConf;
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
        ArchiveConf query = new ArchiveConf();
        query.setConfStatus(ArchiveConfStatus.PREPARE.getStatus());
        List<ArchiveConf> archiveConfs = archiveDao.queryArchiveConfList(query);
        if (CollectionUtils.isEmpty(archiveConfs)) {
            log.info("未检测到预备[PREPARE]的归档配置！");
        } else {
            for (ArchiveConf conf : archiveConfs) {
                checkInterceptorManager.applyCheck(conf);
            }
        }

        // 补偿上次操作CHECKING 失败的
        query.setConfStatus(ArchiveConfStatus.CHECKED_FAILED.getStatus());
        List<ArchiveConf> archiveCheckFailed = archiveDao.queryArchiveConfList(query);
        if (CollectionUtils.isEmpty(archiveCheckFailed)) {
            log.info("检查上次 CHECKED_FAILED的数据, 未检测到 CHECKED_FAILED 的归档配置！");
            return;
        }
        log.info("检查上次 CHECKED_FAILED的数据, 检测到 CHECKED_FAILED 的归档配置: " + archiveCheckFailed.size() + " 个");
        for (ArchiveConf confFailed : archiveCheckFailed) {
            checkInterceptorManager.applyCheck(confFailed);
        }
    }
}
