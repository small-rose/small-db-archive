package com.small.archive.core.delete.strategy;

import com.small.archive.core.emuns.ArchiveJobPhase;
import com.small.archive.core.emuns.ArchiveLogResult;
import com.small.archive.core.emuns.ArchiveStrategyEnum;
import com.small.archive.core.emuns.ArchiveTaskStatus;
import com.small.archive.core.transfer.ArchiveJobTaskService;
import com.small.archive.exception.DataArchiverException;
import com.small.archive.pojo.ArchiveJobConfig;
import com.small.archive.pojo.ArchiveJobDetailTask;
import com.small.archive.pojo.ArchiveTaskLog;
import com.small.archive.service.ArchiveTaskLogService;
import com.small.archive.utils.DigestUtils;
import com.small.archive.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ ArchiveTaskDeleteService ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/12 012 17:29
 * @Version: v1.0
 */

@Slf4j
@Service
public class ArchiveTaskDeletePkNumberStrategy implements ArchiveTaskDeleteStrategy {


    @Autowired
    private ArchiveJobTaskService archiveJobTaskService;
    @Autowired
    private ArchiveTaskLogService archiveTaskLogService;

    /**
     * 支持按照主键id数字范围删除
      * @return
     */
    @Override
    public ArchiveStrategyEnum getArchiveStrategy() {
        return ArchiveStrategyEnum.ARCHIVE_PK_NUMBER;
    }

    @Override
    @Transactional( rollbackFor = DataArchiverException.class )
    public boolean deleteArchivedSourceData(ArchiveJobConfig conf, ArchiveJobDetailTask acTask) {
        ArchiveTaskLog taskLog = new ArchiveTaskLog(acTask);

        try {
            taskLog.setJobId(acTask.getJobId());
            taskLog.setTaskId(acTask.getId());
            taskLog.setTaskPhase(ArchiveJobPhase.DELETE.getStatus());
            taskLog.setCreateTime(new Date());
            taskLog.setJobBatchNo(acTask.getJobBatchNo());

             // 查询满足删除条件的数据
            String whereSql = acTask.getTaskSql();
            String sql = SqlUtils.buildAppendSelectSql(acTask.getSourceTable(), whereSql);
            List<Map<String, Object>> sourceList = archiveJobTaskService.querySourceList(sql);

            if (conf.getJobDelCheck() > 0) {

                List<Map<String, Object>> targetList = archiveJobTaskService.queryTargetList(sql);
                if (sourceList.size() != targetList.size()) {
                    throw new DataArchiverException("归档删除任务，删除前再次确认归档数据，发现源表和归档表数据量不一致!");
                }

                Set<String> columnsSet = sourceList.get(0).keySet();
                List<String> pkList = columnsSet.stream().filter(s -> conf.getJobColumns().contains(s)).map(String::toUpperCase).collect(Collectors.toList());
                Map<String, String> sources = DigestUtils.messageDigestConvert(sourceList, pkList);
                Map<String, String> targets = DigestUtils.messageDigestConvert(sourceList, pkList);
                boolean total1 = sources.keySet().containsAll(targets.keySet());
                boolean total2 = targets.keySet().containsAll(sources.keySet());
                if (!total1 || !total2) {
                    log.info("归档删除任务，发现两侧库数据校对出现差异！");
                    throw new DataArchiverException("归档任务两侧库数据校对出现差异!");
                }
            }

            String delSql = SqlUtils.buildAppendDeleteSql(acTask.getSourceTable(), whereSql);
            log.info("归档删除任务，清理源库表数据SQL: {}" + delSql);
            int total = archiveJobTaskService.deleteSouceTab(delSql);
            if (total != acTask.getVerifySize()) {
                throw new DataArchiverException("归档删除任务，删除源库数据后校对出现差异，回滚删除!");
            }
            acTask.setDeleteSql(delSql);
            // 更新配置状态要已完成
            archiveJobTaskService.updateJobTaskStatus(acTask, ArchiveTaskStatus.SUCCESS);
        } catch (Exception e) {
            String ex = ExceptionUtils.getStackTrace(e);
            taskLog.setTaskResult(ArchiveLogResult.ERROR.getStatus());
            if (ex.length() > 2000) {
                ex = ex.substring(0, 2000);
            }
            taskLog.setErrorInfo(ex);
            // 更新配置状态出错
            archiveJobTaskService.updateJobTaskStatusError(acTask, ArchiveTaskStatus.ERROR);

            log.info("归档删除任务，执行失败：taskId= " + acTask.getId() + "：exception :" + ex);
            throw new DataArchiverException("归档删除任务，执行失败!", e);
        } finally {
            try {
                archiveTaskLogService.saveArchiveLog(taskLog);
                log.info("执行归档任务日志保存成功！");
            } catch (Exception e) {
                String message = ExceptionUtils.getMessage(e);
                log.info("执行归档任务日志保存失败: {}", message);
            }

        }
        return true;
    }
}
