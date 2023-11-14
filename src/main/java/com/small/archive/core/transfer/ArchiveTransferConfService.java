package com.small.archive.core.transfer;

import com.small.archive.core.emuns.ArchiveJobStatus;
import com.small.archive.core.emuns.ArchiveTaskStatus;
import com.small.archive.exception.DataArchiverException;
import com.small.archive.pojo.ArchiveJobConfig;
import com.small.archive.pojo.ArchiveJobDetailTask;
import com.small.archive.service.ArchiveJobConfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ ArchiveTransferConfService ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/12 012 19:37
 * @Version: v1.0
 */
@Slf4j
@Service
public class ArchiveTransferConfService implements ConfArchiver {

    @Autowired
    private ArchiveJobConfService archiveJobConfService;
    @Autowired
    private ArchiveTransferService archiveTransferService;


    @Override
    @Transactional( rollbackFor = DataArchiverException.class )
    public void executeConfArchive(ArchiveJobConfig conf) {
        try {
            // 检测可搬运任务
            List<ArchiveJobDetailTask> taskList = archiveJobConfService.queryArchiveConfDetailTaskList(conf, ArchiveTaskStatus.PREPARE);
            if (CollectionUtils.isEmpty(taskList)) {
                log.info("未检测到可执行[PREPARE]的归档任务！");
                return;
            }

            // 标记搬运中完成
            archiveJobConfService.updateArchiveConfStatus(conf, ArchiveJobStatus.MIGRATING);

            for (ArchiveJobDetailTask task : taskList) {
                //数据搬运，任务级事务
                archiveTransferService.executeArchive(task);
            }
            boolean check = archiveTransferService.executeCheckArchive(conf);
            if (check) {
                // 标记搬运完成
                archiveJobConfService.updateArchiveConfStatus(conf, ArchiveJobStatus.MIGRATED_SUCCESS);
            }
        } catch (Exception e) {
            // 标记搬运失败
            archiveJobConfService.updateArchiveConfStatusFailed(conf, ArchiveJobStatus.MIGRATED_FAILED);
            throw new DataArchiverException("归档作业数据搬运失败" + e.getMessage());
        }
    }
}
