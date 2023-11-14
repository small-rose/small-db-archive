package com.small.archive.core.verify;

import com.small.archive.core.emuns.ArchiveJobPhase;
import com.small.archive.core.emuns.ArchiveLogResult;
import com.small.archive.core.emuns.ArchiveTaskStatus;
import com.small.archive.core.transfer.ArchiveJobTaskService;
import com.small.archive.dao.ArchiveDao;
import com.small.archive.exception.DataArchiverException;
import com.small.archive.pojo.ArchiveJobConfig;
import com.small.archive.pojo.ArchiveJobDetailTask;
import com.small.archive.pojo.ArchiveTaskLog;
import com.small.archive.service.ArchiveTaskLogService;
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
import java.util.stream.Collectors;

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
    private ArchiveJobTaskService archiveJobTaskService;
    @Autowired
    private ArchiveDao archiveDao;

    @Autowired
    private ArchiveTaskLogService archiveTaskLogService;


    @Override
    @Transactional( rollbackFor = DataArchiverException.class )
    public void executeVerify(ArchiveJobConfig conf, ArchiveJobDetailTask acTask) {
        ArchiveTaskLog taskLog = new ArchiveTaskLog(acTask);

        try {
            taskLog.setJobId(acTask.getJobId());
            taskLog.setTaskId(acTask.getId());
            taskLog.setTaskPhase(ArchiveJobPhase.VERIFY.getStatus());
            taskLog.setCreateTime(new Date());


            acTask.setVerifyStart(new Date());
            acTask.setVerifySize(0);
            archiveJobTaskService.updateVerifyTaskStatus(acTask, ArchiveTaskStatus.VERIFYING);

            String sql = acTask.getTaskSql();
            if (acTask.getExpectSize() != acTask.getActualSize()) {
                throw new DataArchiverException("归档任务校验taskId= " + acTask.getId() + "搬运数据记录预期数量和实际数量不符，校验失败");
            }
            List<Map<String, Object>> sourceList = archiveJobTaskService.querySourceList(sql);
            List<Map<String, Object>> targetList = archiveJobTaskService.queryTargetList(sql);
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
            List<String> pkList = columnsSet.stream().filter(s -> conf.getJobColumns().contains(s)).map(String::toUpperCase).collect(Collectors.toList());

            Map<String, String> sources = DigestUtils.messageDigestConvert(sourceList, pkList);
            Map<String, String> targets = DigestUtils.messageDigestConvert(sourceList, pkList);
            boolean total1 = sources.keySet().containsAll(targets.keySet());
            boolean total2 = targets.keySet().containsAll(sources.keySet());
            if (!total1 || !total2) {
                log.info("归档任务两侧库数据校对出现差异！");
                throw new DataArchiverException("归档任务两侧库数据校对出现差异！");
            }
            log.info("归档任务两侧库数据校对成功！");

            acTask.setVerifyEnd(new Date());
            acTask.setVerifySize(Long.valueOf(sources.size()));
            archiveJobTaskService.updateVerifyTaskStatus(acTask, ArchiveTaskStatus.VERIFIED);
        } catch (Exception e) {
            String ex = ExceptionUtils.getStackTrace(e);

            taskLog.setTaskResult(ArchiveLogResult.ERROR.getStatus());
            if (ex.length() > 2000) {
                ex = ex.substring(0, 2000);
            }
            taskLog.setErrorInfo(ex);

            log.info("归档任务校对出错：taskId= " + acTask.getId() + "：exception :" + ex);
            throw new DataArchiverException("归档校验任务taskId= " + acTask.getId() + "发生错误，校验失败", e);
        } finally {

            try {
                archiveTaskLogService.saveArchiveLog(taskLog);
                log.info("执行归档数据校对日志保存成功！");
            } catch (Exception e) {
                String message = ExceptionUtils.getMessage(e);
                log.info("执行归档数据校对日志保存失败: {}", message);
            }

        }
    }

    @Override
    public boolean executeCheckVerify(ArchiveJobConfig conf) {
        try {

            List<ArchiveJobDetailTask> taskList = archiveDao.queryArchiveConfDetailTaskList(conf, ArchiveTaskStatus.VERIFIED);

            if (CollectionUtils.isEmpty(taskList)) {
                throw new DataArchiverException("归档任务总数据校对失败！还有未执行完成的任务");
            }
            long totalSize = conf.getTotalExpectSize();
            long sum = taskList.stream().mapToLong(t -> t.getVerifySize()).sum();
            if (totalSize != sum) {
                throw new DataArchiverException("归档任务总数据校对失败！当前批次【" + conf.getJobBatchNo() + "】归档数据总量对不上！");
            }
            return true;
        } catch (Exception e) {

            throw new DataArchiverException("归档任务总数据校对失败！当前批次【" + conf.getJobBatchNo() + "】归档数据总量对不上！");
        }
    }


}
