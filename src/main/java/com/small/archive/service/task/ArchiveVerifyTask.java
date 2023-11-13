package com.small.archive.service.task;

import com.small.archive.core.emuns.ArchiveConfStatus;
import com.small.archive.core.emuns.ArchiveTaskStatus;
import com.small.archive.core.verify.ArchiveTaskVerifyService;
import com.small.archive.pojo.ArchiveConf;
import com.small.archive.pojo.ArchiveConfDetailTask;
import com.small.archive.service.ArchiveConfService;
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
public class ArchiveVerifyTask {


    @Autowired
    private ArchiveConfService archiveConfService;

    @Autowired
    private ArchiveTaskVerifyService archiveTaskVerifyService;


    public void archiveTaskVerify() {

        ArchiveConf query = new ArchiveConf();
        query.setConfStatus(ArchiveConfStatus.MIGRATED.getStatus());
        List<ArchiveConf> archiveConfs = archiveConfService.queryArchiveConfList(query);
        if (CollectionUtils.isEmpty(archiveConfs)) {
            log.info(">>> 归档校对任务 没有找到搬运完成配置，不执行数据搬运");
            return;
        }
        for (ArchiveConf conf : archiveConfs) {
            archiveConfService.updateArchiveConfStatus(conf, ArchiveConfStatus.VERIFYING);
            List<ArchiveConfDetailTask> taskList = archiveConfService.queryArchiveConfDetailTaskList(conf, ArchiveTaskStatus.MIGRATED);
            if (CollectionUtils.isEmpty(taskList)) {
                log.info(">>> 任务[ "+conf.getConfDesc() + " ] 未检测到可校验的归档任务！");
                continue;
            }
            for (ArchiveConfDetailTask task : taskList) {
                //数据校对
                archiveTaskVerifyService.executeVerify(conf, task);
            }
            // 数据校对检查
            boolean check = archiveTaskVerifyService.executeCheckVerify(conf);

            if (check) {
                archiveConfService.updateArchiveConfStatus(conf, ArchiveConfStatus.VERIFIED);
            }
        }


    }
}
