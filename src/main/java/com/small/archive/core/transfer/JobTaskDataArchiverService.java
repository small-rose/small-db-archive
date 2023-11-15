package com.small.archive.core.transfer;

import com.small.archive.core.emuns.ArchiveJobPhase;
import com.small.archive.core.emuns.ArchiveTaskStatusEnum;
import com.small.archive.core.emuns.LogResultEnum;
import com.small.archive.dao.ArchiveDao;
import com.small.archive.exception.DataArchiverException;
import com.small.archive.pojo.ArchiveJobConfig;
import com.small.archive.pojo.ArchiveJobDetailTask;
import com.small.archive.pojo.ArchiveTaskLog;
import com.small.archive.service.ArchiveJobTaskService;
import com.small.archive.service.ArchiveTaskLogService;
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
public class JobTaskDataArchiverService implements JobTaskDataArchiver {


    @Autowired
    private ArchiveJobTaskService archiveJobTaskService;
    @Autowired
    private ArchiveDao archiveDao;
    @Autowired
    private ArchiveTaskLogService archiveTaskLogService;


    /**
     * 仅执行SQL 将数据搬运到归档库
     *
     * @throws DataArchiverException
     */
    @Override
    @Transactional( rollbackFor = DataArchiverException.class )
    public void executeTaskDataArchive(ArchiveJobDetailTask acTask) throws DataArchiverException {
        ArchiveTaskLog taskLog = new ArchiveTaskLog(acTask);

        try {
            taskLog.setJobId(acTask.getJobId());
            taskLog.setTaskId(acTask.getId());
            taskLog.setTaskPhase(ArchiveJobPhase.MIGRATE.getStatus());
            taskLog.setCreateTime(new Date());


            acTask.setTaskStart(new Date());
             // 查询满足归档条件的数据
            String selSql = acTask.getTaskSelSql();
            List<Map<String, Object>> data = archiveJobTaskService.querySourceList(selSql);

            // 组装将数据插入归档表的SQL
            String insertSql = SqlUtils.buildInsertSql(acTask.getTargetTable(), data.get(0));

            List<Object[]> paramsList = new ArrayList<>(data.size());
            for (Map<String, Object> row : data) {
                Object[] params = row.values().toArray();
                paramsList.add(params);
            }
            //此处为批次保存
            long total = archiveJobTaskService.batchUpdate(insertSql, paramsList);

            acTask.setActualSize(total);
            acTask.setTaskEnd(new Date());
            // 更新本次搬运数据记录数，执行开始时间、结束时间 标记搬运完成
            archiveJobTaskService.updateJobTaskStatus(acTask, ArchiveTaskStatusEnum.MIGRATED);

            taskLog.setTaskResult(LogResultEnum.SUCCESS.getStatus());
            log.info("数据搬迁task的id = " + acTask.getId() + "任务执行成功！");
        } catch (Exception e) {

            String ex = ExceptionUtils.getStackTrace(e);
            taskLog.setTaskResult(LogResultEnum.ERROR.getStatus());
            if (ex.length() > 2000) {
                ex = ex.substring(0, 2000);
            }
            taskLog.setErrorInfo(ex);
            archiveJobTaskService.updateJobTaskStatusError(acTask, ArchiveTaskStatusEnum.ERROR);
            log.info("数据搬迁task的id = " + acTask.getId() + "任务执行失败");
            throw new DataArchiverException("执行归档任务失败", e);
        } finally {
            try {
                archiveTaskLogService.saveArchiveLog(taskLog);
                log.info("执行归档任务日志保存成功！");
            } catch (Exception e) {
                String message = ExceptionUtils.getMessage(e);
                log.info("执行归档任务日志保存失败: {}", message);
            }

        }
    }


    public boolean executeCheckArchive(ArchiveJobConfig conf) {
        //检查当前批次任务是否出错
        List<ArchiveJobDetailTask> taskList = archiveDao.queryArchiveConfDetailTaskList(conf, ArchiveTaskStatusEnum.ERROR);
        if (!CollectionUtils.isEmpty(taskList)) {
            log.info("当前批次任务存在出错任务！请人工检查！");
            return false;
        }
        return true;
    }

}
