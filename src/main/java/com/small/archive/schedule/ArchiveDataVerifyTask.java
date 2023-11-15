package com.small.archive.schedule;

import com.small.archive.core.emuns.ArchiveJobStatus;
import com.small.archive.core.emuns.ArchiveTaskStatusEnum;
import com.small.archive.core.verify.ArchiveTaskVerifyService;
import com.small.archive.pojo.ArchiveJobConfig;
import com.small.archive.pojo.ArchiveJobDetailTask;
import com.small.archive.service.ArchiveJobConfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * @Project : small-db-archive
 * @description: TODO 功能角色说明： ArchiveDataVerifyTask
 * TODO 描述：   数据校对
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */

@Slf4j
@Service
public class ArchiveDataVerifyTask {


    @Autowired
    private ArchiveJobConfService archiveJobConfService;

    @Autowired
    private ArchiveTaskVerifyService archiveTaskVerifyService;


    public void archiveTaskVerify() {

        ArchiveJobConfig query = new ArchiveJobConfig();
        query.setJobStatus(ArchiveJobStatus.MIGRATED_SUCCESS.getStatus());
        List<ArchiveJobConfig> archiveJobConfigs = archiveJobConfService.queryArchiveConfList(query);
        if (CollectionUtils.isEmpty(archiveJobConfigs)) {
            log.info(">>> 归档校对任务 没有找到搬运完成配置，不执行数据搬运");
            return;
        }
        for (ArchiveJobConfig conf : archiveJobConfigs) {
            archiveJobConfService.updateArchiveConfStatus(conf, ArchiveJobStatus.VERIFYING);
            List<ArchiveJobDetailTask> taskList = archiveJobConfService.queryArchiveConfDetailTaskList(conf, ArchiveTaskStatusEnum.MIGRATED);
            if (CollectionUtils.isEmpty(taskList)) {
                log.info(">>> 任务[ "+conf.getJobName() + " ] 未检测到可校验的归档任务！");
                continue;
            }
            for (ArchiveJobDetailTask task : taskList) {
                //数据校对
                archiveTaskVerifyService.executeVerify(conf, task);
            }
            // 数据校对检查
            boolean check = archiveTaskVerifyService.executeCheckVerify(conf);

            if (check) {
                archiveJobConfService.updateArchiveConfStatus(conf, ArchiveJobStatus.VERIFY_SUCCESS);
            }
        }


    }
}
