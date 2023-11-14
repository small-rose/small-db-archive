package com.small.archive.schedule;

import com.small.archive.core.convertask.ArchiveConfToTaskService;
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
public class ArchiveConfConvertTask {


    @Autowired
    private ArchiveDao archiveDao;

    @Autowired
    private ArchiveConfToTaskService archiveConfToTaskService;



    @Transactional
    public void archiveConfConvert2Task() {
        ArchiveJobConfig query = new ArchiveJobConfig();
        query.setJobStatus(ArchiveJobStatus.CHECKED_SUCCESS.getStatus());
        List<ArchiveJobConfig> archiveJobConfigs = archiveDao.queryArchiveConfList(query);
        if (CollectionUtils.isEmpty(archiveJobConfigs)) {
            log.info("未检测校验成功[CHECKED_SUCCESS]可拆解执行的归档配置！");
        } else {
            for (ArchiveJobConfig conf : archiveJobConfigs) {
                archiveConfToTaskService.conf2Task(conf);
            }
        }
    }
}
