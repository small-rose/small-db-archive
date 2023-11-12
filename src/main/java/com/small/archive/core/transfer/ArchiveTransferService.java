package com.small.archive.core.transfer;

import com.small.archive.core.emuns.ArchiveLogPhase;
import com.small.archive.core.emuns.ArchiveLogStatus;
import com.small.archive.core.emuns.ArchiveTaskStatus;
import com.small.archive.dao.ArchiveDao;
import com.small.archive.exception.DataArchiverException;
import com.small.archive.pojo.ArchiveConf;
import com.small.archive.pojo.ArchiveConfDetailTask;
import com.small.archive.pojo.ArchiveConfTaskLog;
import com.small.archive.service.ArchiveLogService;
import com.small.archive.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
public class ArchiveTransferService implements DataArchiver {


    @Autowired
    private ArchiveTaskService archiveTaskService;
    @Autowired
    private ArchiveDao archiveDao;
    @Autowired
    private ArchiveLogService archiveLogService;


    /**
     * 仅执行SQL 将数据搬运到归档库
     *
     * @throws DataArchiverException
     */
    @Override
    @Transactional( rollbackFor = DataArchiverException.class )
    public void executeArchive(ArchiveConfDetailTask acTask) throws DataArchiverException {
        ArchiveConfTaskLog taskLog = new ArchiveConfTaskLog(acTask);

        try {
            taskLog.setConfId(acTask.getConfId());
            taskLog.setTaskId(acTask.getId());
            taskLog.setTaskPhase(ArchiveLogPhase.MIGRATE.getStatus());
            taskLog.setCreateTime(new Date());


            acTask.setTaskStart(new Date());
            String archiveTable = acTask.getTaskTargetTab();
            // 查询满足归档条件的数据
            String whereSql = acTask.getTaskSql();
            String sql = SqlUtils.buildAppendSelectSql(acTask.getTaskSourceTab(), whereSql);
            List<Map<String, Object>> data = archiveTaskService.querySourceList(sql);

            // 组装将数据插入归档表的SQL
            String insertSql = SqlUtils.buildInsertSql(archiveTable, data.get(0));

            List<Object[]> paramsList = new ArrayList<>(data.size());
            for (Map<String, Object> row : data) {
                Object[] params = row.values().toArray();
                paramsList.add(params);
            }
            //此处为批次保存
            int total = archiveTaskService.batchUpdate(insertSql, paramsList);

            acTask.setActualSize(total);
            acTask.setTaskEnd(new Date());
            // 更新本次搬运数据记录数，执行开始时间、结束时间 标记搬运完成
            archiveTaskService.updateArchiveConfDetailTaskStatus(acTask, ArchiveTaskStatus.MIGRATED);

            taskLog.setExecResult(ArchiveLogStatus.SUCCESS.getStatus());
            log.info("数据搬迁task的id = " + acTask.getId() + "任务执行成功！");
        } catch (Exception e) {

            String ex = ExceptionUtils.getStackTrace(e);
            taskLog.setExecResult(ArchiveLogStatus.ERROR.getStatus());
            if (ex.length() > 2000) {
                ex = ex.substring(0, 2000);
            }
            taskLog.setErrorInfo(ex);
            archiveTaskService.updateArchiveConfDetailTaskStatusError(acTask, ArchiveTaskStatus.ERROR);
            log.info("数据搬迁task的id = " + acTask.getId() + "任务执行失败");
            throw new DataArchiverException("执行归档任务失败", e);
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


    public boolean executeCheckArchive(ArchiveConf conf) {
        //检查当前批次任务是否出错
        List<ArchiveConfDetailTask> taskList = archiveDao.queryArchiveConfDetailTaskList(conf, ArchiveTaskStatus.ERROR);
        if (!CollectionUtils.isEmpty(taskList)) {
            log.info("当前批次任务存在出错任务！请人工检查！");
            return false;
        }
        return true;
    }

}
