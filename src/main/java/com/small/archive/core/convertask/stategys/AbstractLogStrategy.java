package com.small.archive.core.convertask.stategys;

import com.small.archive.core.emuns.ArchiveJobPhase;
import com.small.archive.core.emuns.LogResultEnum;
import com.small.archive.pojo.ArchiveJobConfig;
import com.small.archive.pojo.ArchiveTaskLog;
import com.small.archive.service.ArchiveTaskLogService;
import com.small.archive.utils.SmallDateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ AbstractLogStrategy ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/16 016 1:05
 * @Version: v1.0
 */
@Slf4j
public abstract class AbstractLogStrategy {

    private ArchiveTaskLog taskLog ;

    private ArchiveTaskLogService archiveTaskLogService;

    public AbstractLogStrategy(ArchiveTaskLogService archiveTaskLogService) {
        this.archiveTaskLogService = archiveTaskLogService;
    }

    protected void create(){
        taskLog = ArchiveTaskLog.builder().build();
    }


    protected ArchiveTaskLog setTaskLogProperties(ArchiveJobConfig jobConfig, String ex){
        taskLog.setJobId(jobConfig.getId());
        taskLog.setTaskPhase(ArchiveJobPhase.CONVERT.getStatus());
        taskLog.setTaskResult(LogResultEnum.ERROR.getStatus());
        taskLog.setCreateTime(SmallDateUtils.now());
        taskLog.setErrorInfo(ex.substring(1, 2000));
        return taskLog;
    }

    protected ArchiveTaskLog saveTaskLog(){
        try {
            archiveTaskLogService.saveArchiveLog(taskLog);
            log.info("执行归档任务日志保存成功！");
        } catch (Exception e) {
            String message = ExceptionUtils.getStackTrace(e);
            log.info("执行归档任务日志保存失败: {}", message);
        }
        return taskLog;
    }
}
