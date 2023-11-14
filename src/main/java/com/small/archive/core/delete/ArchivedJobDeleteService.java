package com.small.archive.core.delete;

import com.small.archive.core.emuns.ArchiveJobStatus;
import com.small.archive.core.emuns.ArchiveTaskStatus;
import com.small.archive.exception.DataArchiverException;
import com.small.archive.pojo.ArchiveJobConfig;
import com.small.archive.pojo.ArchiveJobDetailTask;
import com.small.archive.service.ArchiveJobConfService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ ArchiveTaskDeleteConfService ] 说明： 无
 * @Function: 功能描述： 循环处理某张表的归档删除数据任务
 * @Date: 2023/11/14 014 23:20
 * @Version: v1.0
 */
@Slf4j
@Service
public class ArchivedJobDeleteService implements ArchivedJobDelete {

    @Autowired
    private ArchiveJobConfService archiveJobConfService;
    @Autowired
    private ArchivedJobTaskDeleteService archivedJobTaskDeleteService;

    @Override
    @Transactional(rollbackFor = DataArchiverException.class )
    public void jobArchivedDataDelete(ArchiveJobConfig conf) {
        try {
            // 检测可删除的任务
            List<ArchiveJobDetailTask> taskList = archiveJobConfService.queryArchiveConfDetailTaskList(conf, ArchiveTaskStatus.VERIFIED);
            if (CollectionUtils.isEmpty(taskList)){
                log.info("未检测到已校验[VERIFIED]的归档任务！");
                return;
            }
            // 标记删除中的
            archiveJobConfService.updateArchiveConfStatus(conf, ArchiveJobStatus.DELETE);

            for (ArchiveJobDetailTask task : taskList) {
                //数据删除，任务级事务
                archivedJobTaskDeleteService.deleteArchivedData(conf, task);
            }
            boolean check = archivedJobTaskDeleteService.executeCheckDelete(conf);
            if (check) {
                // 标记删除完成
                archiveJobConfService.updateArchiveConfStatus(conf, ArchiveJobStatus.SUCCESS);
            }else {

            }
        }catch (Exception e){
            // 标记删除
            String ex = ExceptionUtils.getStackTrace(e);
            log.info("当前批次归档作业执行失败，批次号：jobBatchNo = " + conf.getJobBatchNo() + "：exception :" + ex);
            archiveJobConfService.updateArchiveConfStatusFailed(conf, ArchiveJobStatus.DELETE_FAILED);
            throw new DataArchiverException("当前批次归档作业执行失败，批次号["+conf.getJobBatchNo()+"]");
        }
    }
}
