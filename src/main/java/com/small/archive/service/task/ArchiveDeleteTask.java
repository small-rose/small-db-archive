package com.small.archive.service.task;

import com.small.archive.core.delete.ArchiveTaskDeleteService;
import com.small.archive.core.emuns.ArchiveConfStatus;
import com.small.archive.core.emuns.ArchiveTaskStatus;
import com.small.archive.dao.ArchiveDao;
import com.small.archive.pojo.ArchiveConf;
import com.small.archive.pojo.ArchiveConfDetailTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * @Project : small-db-archive
 * @description: TODO 功能角色说明： ArchiveTaskWatchService
 * TODO 描述：   数据校对
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */

@Slf4j
@Service
public class ArchiveDeleteTask {


    @Autowired
    private ArchiveDao archiveDao;

    @Autowired
    private ArchiveTaskDeleteService archiveTaskDeleteService;


    public void archiveTaskDeleteSourceData() {

        ArchiveConf query = new ArchiveConf();
        query.setConfStatus(ArchiveConfStatus.VERIFIED.getStatus());
        List<ArchiveConf> archiveConfs = archiveDao.queryArchiveConfList(query);
        if (CollectionUtils.isEmpty(archiveConfs)) {
            log.info("位找到搬运完成配置，不执行数据搬运");
            return;
        }
        for (ArchiveConf conf : archiveConfs) {
            List<ArchiveConfDetailTask> taskList = archiveDao.queryArchiveConfDetailTaskList(conf, ArchiveTaskStatus.MIGRATED);
            if (CollectionUtils.isEmpty(taskList)) {
                log.info("未检测到可校验的归档任务！");
                return;
            }
            for (ArchiveConfDetailTask task : taskList) {
                //数据校对
                archiveTaskDeleteService.deleteSourceData(conf, task);
            }
        }

    }
}
