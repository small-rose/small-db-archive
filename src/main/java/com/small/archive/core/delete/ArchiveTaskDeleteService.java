package com.small.archive.core.delete;

import com.small.archive.core.emuns.ArchiveConfStatus;
import com.small.archive.core.emuns.ArchiveLogPhase;
import com.small.archive.core.emuns.ArchiveLogStatus;
import com.small.archive.core.transfer.ArchiveTaskService;
import com.small.archive.exception.DataArchiverException;
import com.small.archive.pojo.ArchiveConf;
import com.small.archive.pojo.ArchiveConfDetailTask;
import com.small.archive.pojo.ArchiveConfTaskLog;
import com.small.archive.service.ArchiveConfService;
import com.small.archive.service.ArchiveLogService;
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
public class ArchiveTaskDeleteService implements ArchiveTaskDelete {


    @Autowired
    private ArchiveTaskService archiveTaskService;
    @Autowired
    private ArchiveConfService archiveConfService;
    @Autowired
    private ArchiveLogService archiveLogService;


    @Override
    @Transactional( rollbackFor = DataArchiverException.class )
    public void deleteSourceData(ArchiveConf conf, ArchiveConfDetailTask acTask) {
        ArchiveConfTaskLog taskLog = new ArchiveConfTaskLog(acTask);

        try {
            taskLog.setConfId(acTask.getConfId());
            taskLog.setTaskId(acTask.getId());
            taskLog.setTaskPhase(ArchiveLogPhase.DELETE.getStatus());
            taskLog.setCreateTime(new Date());


            archiveConfService.updateArchiveConfStatus(conf, ArchiveConfStatus.DELETE);
            // 查询满足删除条件的数据
            String whereSql = acTask.getTaskSql();
            String sql = SqlUtils.buildAppendSelectSql(acTask.getTaskSourceTab(), whereSql);
            List<Map<String, Object>> sourceList = archiveTaskService.querySourceList(sql);

            if (conf.getConfDelCheck() > 0) {

                List<Map<String, Object>> targetList = archiveTaskService.queryTargetList(sql);
                if (sourceList.size() != targetList.size()) {
                    throw new DataArchiverException("归档删除任务，删除前再次确认归档数据，发现源表和归档表数据量不一致!");
                }

                Set<String> columnsSet = sourceList.get(0).keySet();
                String primaryKey = columnsSet.stream().filter(s -> s.equalsIgnoreCase(conf.getConfPk())).findFirst().get();
                Map<String, String> sources = DigestUtils.messageDigestConvert(sourceList, primaryKey);
                Map<String, String> targets = DigestUtils.messageDigestConvert(sourceList, primaryKey);
                boolean total1 = sources.keySet().containsAll(targets.keySet());
                boolean total2 = targets.keySet().containsAll(sources.keySet());
                if (!total1 || !total2) {
                    log.info("归档删除任务，发现两侧库数据校对出现差异！");
                    throw new DataArchiverException("归档任务两侧库数据校对出现差异!");
                }
            }

            String delSql = SqlUtils.buildAppendDeleteSql(acTask.getTaskSourceTab(), whereSql);
            log.info("归档删除任务，清理源库表数据SQL: {}" + delSql);
            int total = archiveTaskService.deleteSouceTab(delSql);
            if (total != acTask.getVerifySize()) {
                throw new DataArchiverException("归档删除任务，删除源库数据后校对出现差异，回滚删除!");
            }
            // 更新配置状态要已完成
            archiveConfService.updateArchiveConfStatus(conf, ArchiveConfStatus.SUCCESS);
        } catch (Exception e) {
            String ex = ExceptionUtils.getStackTrace(e);
            taskLog.setExecResult(ArchiveLogStatus.ERROR.getStatus());
            if (ex.length() > 2000) {
                ex = ex.substring(0, 2000);
            }
            taskLog.setErrorInfo(ex);

            log.info("归档删除任务，执行失败：taskId= " + acTask.getId() + "：exception :" + ex);
            throw new DataArchiverException("归档删除任务，执行失败!", e);
        } finally {
            try {
                archiveLogService.saveArchiveLog(taskLog);
                log.info("执行归档任务日志保存成功！");
            } catch (Exception e) {
                String message = ExceptionUtils.getMessage(e);
                log.info("执行归档任务日志保存失败: {}", message);
            }

        }
    }
}
