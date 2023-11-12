package com.small.archive.core.verify;

import com.small.archive.core.emuns.ArchiveLogPhase;
import com.small.archive.core.emuns.ArchiveLogStatus;
import com.small.archive.core.emuns.ArchiveTaskStatus;
import com.small.archive.core.transfer.ArchiveTaskService;
import com.small.archive.dao.ArchiveDao;
import com.small.archive.exception.DataArchiverException;
import com.small.archive.pojo.ArchiveConf;
import com.small.archive.pojo.ArchiveConfDetailTask;
import com.small.archive.pojo.ArchiveConfTaskLog;
import com.small.archive.service.ArchiveLogService;
import com.small.archive.utils.DigestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ ArchiveTaskVerifyService ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/12 012 16:16
 * @Version: v1.0
 */
@Slf4j
@Service
public class ArchiveTaskVerifyService implements DataArchiverVerify {

    @Autowired
    private ArchiveTaskService archiveTaskService;
    @Autowired
    private ArchiveDao archiveDao;

    @Autowired
    private ArchiveLogService archiveLogService;


    @Override
    @Transactional( rollbackFor = DataArchiverException.class )
    public void executeVerify(ArchiveConf conf, ArchiveConfDetailTask acTask) {
        ArchiveConfTaskLog taskLog = new ArchiveConfTaskLog(acTask);

        try {
            taskLog.setConfId(acTask.getConfId());
            taskLog.setTaskId(acTask.getId());
            taskLog.setTaskPhase(ArchiveLogPhase.VERIFY.getStatus());
            taskLog.setCreateTime(new Date());


            acTask.setVerifyStart(new Date());
            acTask.setVerifyStart(null);
            acTask.setVerifySize(0);
            archiveTaskService.updateVerifyTaskStatus(acTask, ArchiveTaskStatus.VERIFYING);

            String sql = acTask.getTaskSql();
            if (acTask.getExpectSize() != acTask.getActualSize()) {
                throw new DataArchiverException("归档任务校验taskId= " + acTask.getId() + "搬运数据记录预期数量和实际数量不符，校验失败");
            }
            List<Map<String, Object>> sourceList = archiveTaskService.querySourceList(sql);
            List<Map<String, Object>> targetList = archiveTaskService.queryTargetList(sql);
            if (CollectionUtils.isEmpty(sourceList) || CollectionUtils.isEmpty(targetList)) {
                throw new DataArchiverException("归档任务校验taskId= " + acTask.getId() + "根据SQL查询到的记录出现为空，校验失败");
            }
            if (sourceList.size() != targetList.size()) {
                throw new DataArchiverException("归档任务校验taskId= " + acTask.getId() + "根据SQL查询到的源库和归档库的记录数不相符，校验失败");
            }
            if (acTask.getActualSize() == sourceList.size()) {
                throw new DataArchiverException("归档任务校验taskId= " + acTask.getId() + "根据搬运记录数和SQL查询记录数不相符，校验失败");
            }
            Set<String> columnsSet = sourceList.get(0).keySet();
            String primaryKey = columnsSet.stream().filter(s -> s.equalsIgnoreCase(conf.getConfPk())).findFirst().get();
            Map<String, String> sources = DigestUtils.messageDigestConvert(sourceList, primaryKey);
            Map<String, String> targets = DigestUtils.messageDigestConvert(sourceList, primaryKey);
            boolean total1 = sources.keySet().containsAll(targets.keySet());
            boolean total2 = targets.keySet().containsAll(sources.keySet());
            if (!total1 || !total2) {
                log.info("归档任务两侧库数据校对出现差异！");
                throw new DataArchiverException("归档任务两侧库数据校对出现差异！");
            }
            log.info("归档任务两侧库数据校对成功！");

            acTask.setVerifyEnd(new Date());
            acTask.setVerifySize(sources.size());
            archiveTaskService.updateVerifyTaskStatus(acTask, ArchiveTaskStatus.VERIFIED);
        } catch (Exception e) {
            String ex = ExceptionUtils.getStackTrace(e);

            taskLog.setExecResult(ArchiveLogStatus.ERROR.getStatus());
            if (ex.length() > 2000) {
                ex = ex.substring(0, 2000);
            }
            taskLog.setErrorInfo(ex);

            log.info("归档任务校对出错：taskId= " + acTask.getId() + "：exception :" + ex);
            throw new DataArchiverException("归档校验任务taskId= " + acTask.getId() + "发生错误，校验失败", e);
        } finally {

            try {
                archiveLogService.saveArchiveLog(taskLog);
                log.info("执行归档数据校对日志保存成功！");
            } catch (Exception e) {
                String message = ExceptionUtils.getMessage(e);
                log.info("执行归档数据校对日志保存失败: {}", message);
            }

        }
    }

    @Override
    public boolean executeCheckVerify(ArchiveConf conf) {
        try {

            List<ArchiveConfDetailTask> taskList = archiveDao.queryArchiveConfDetailTaskList(conf, ArchiveTaskStatus.VERIFIED);

            if (CollectionUtils.isEmpty(taskList)) {
                throw new DataArchiverException("归档任务总数据校对失败！还有未执行完成的任务");
            }
            int totalSize = conf.getTotalSize();
            long sum = taskList.stream().mapToLong(t -> t.getVerifySize()).sum();
            if (totalSize != sum) {
                throw new DataArchiverException("归档任务总数据校对失败！当前批次【" + conf.getCurrentBatchNo() + "】归档数据总量对不上！");
            }
            return true;
        } catch (Exception e) {

            throw new DataArchiverException("归档任务总数据校对失败！当前批次【" + conf.getCurrentBatchNo() + "】归档数据总量对不上！");
        }
    }


}
