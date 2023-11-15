package com.small.archive.core.convertask.stategys;

import com.small.archive.core.constant.ArchiveConstant;
import com.small.archive.core.context.ArchiveContextHolder;
import com.small.archive.core.emuns.ArchiveJobStatus;
import com.small.archive.core.emuns.ArchiveStrategyEnum;
import com.small.archive.core.emuns.ArchiveTaskStatusEnum;
import com.small.archive.dao.ArchiveTaskDao;
import com.small.archive.exception.ArchiverConvertException;
import com.small.archive.pojo.ArchiveJobConfParam;
import com.small.archive.pojo.ArchiveJobConfig;
import com.small.archive.pojo.ArchiveJobDetailTask;
import com.small.archive.service.ArchiveJobConfService;
import com.small.archive.service.ArchiveSqlService;
import com.small.archive.service.ArchiveTaskLogService;
import com.small.archive.utils.SmallUtils;
import com.small.archive.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */
@Slf4j
@Component
public class ArchivedByDateDayConvertStrategy extends AbstractLogStrategy implements ArchiveJobConvertStrategy {

    @Autowired
    private ArchiveJobConfService archiveJobConfService;
    @Autowired
    private ArchiveTaskDao archiveTaskDao;
    @Autowired
    private ArchiveSqlService archiveSqlService;

    @Override
    public ArchiveStrategyEnum getStrategyMode() {
        return ArchiveStrategyEnum.ARCHIVE_DATE_DAY;
    }

    public ArchivedByDateDayConvertStrategy(ArchiveTaskLogService archiveTaskLogService) {
        super(archiveTaskLogService);
    }

    @Override
    @Transactional( rollbackFor = ArchiverConvertException.class )
    public boolean jobConvertTask(ArchiveJobConfig jobConfig) {
        create();
        try {
            // 再次校验是否有数据需要归档
            Map<String, Object> archiveMap = ArchiveContextHolder.getArchiveMap();
            int sourceCount = archiveMap.get(ArchiveConstant.COUNT_SOURCE) != null ? (int) archiveMap.get(ArchiveConstant.COUNT_SOURCE) : -1;
            if (sourceCount <= 0) {
                log.info("本次无可归档数据，不生成归档任务！");
                return false;
            }
            // 提取SQL条件
            String sql = jobConfig.getJobCondition();
            // 提取对应的配置的SQL参数
            List<ArchiveJobConfParam> paramList = archiveJobConfService.queryArchiveConfParamListByConfId(jobConfig.getId());

            // 按日归档 无配置
            if (SmallUtils.isEmpty(paramList)) {
                // 自动查询对应表字段的最小日期
                paramList = Collections.emptyList();
                paramList.add(archiveSqlService.queryMinDate(jobConfig));
            }

            // 提取归档键 【按日为 日期字段 】如果没有配置就自动取最小的
            List<ArchiveJobConfParam> dateList = paramList.stream().filter(s -> jobConfig.getJobColumns().toLowerCase().contains(s.getParamPk().toLowerCase())).collect(Collectors.toList());
            // 提取非日期字段-赋值使用
            List<ArchiveJobConfParam> noDateListList = paramList.stream().filter(s -> !jobConfig.getJobColumns().toLowerCase().contains(s.getParamPk().toLowerCase())).collect(Collectors.toList());
            // 计算除此以外的DATE，没有则返回空集合
            List<ArchiveJobConfParam> actualParamList = archiveSqlService.calculateDateParams(dateList);
            if (SmallUtils.isNotEmpty(noDateListList)) {
                actualParamList.addAll(noDateListList);
            }
            String batchNo = jobConfig.getSourceTable().toUpperCase().concat(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            jobConfig.setJobBatchNo(batchNo);

             String selectSql = "";
            String deleteSel = "";

            //计算分页,并准备保证任务
            double pageSize = jobConfig.getJobPageSize();
            int pageNo = 1;
            if (sourceCount > pageSize) {
                pageNo = (int) Math.ceil(sourceCount / pageSize);
            }
            List<ArchiveJobDetailTask> taskList = new ArrayList<>(pageNo);
            ArchiveJobDetailTask task = null;
            long numStart = 1 ;
            long numEnd = 0 ;
            long taskPageSize = jobConfig.getJobPageSize();
            for (int x = 1; x <= pageNo; x++) {
                if (x>1){
                    numStart = numEnd ;
                }
                numEnd = x * taskPageSize ;
                // 拼接date类型
                sql = SqlUtils.buildTaskSql(sql, actualParamList);
                selectSql = SqlUtils.buildAppendSelectSqlPages(jobConfig, sql, numStart, numEnd);
                deleteSel = SqlUtils.buildAppendDeleteSqlPages(jobConfig, sql, numEnd);
                task = new ArchiveJobDetailTask();
                task.setJobId(jobConfig.getId());
                task.setJobBatchNo(batchNo);
                task.setCreateTime(new Date());
                task.setTaskOrder(x);
                task.setSourceTable(jobConfig.getSourceTable());
                task.setTargetTable(jobConfig.getTargetTable());
                task.setTaskSelSql(selectSql);
                task.setTaskDelSql(deleteSel);
                task.setTaskStatus(ArchiveTaskStatusEnum.PREPARED.getStatus());
                task.setCreateTime(new Date());

                archiveSqlService.checkTaskSql(sql, task);

                taskList.add(task);
            }
            long total = archiveTaskDao.updateForBatch(taskList);
            if (pageNo != total) {
                throw new ArchiverConvertException("归档作业转换任务异常");
            }
            jobConfig.setJobBatchNo(batchNo);
            jobConfig.setJobStatus(ArchiveJobStatus.CONVERT_SUCCESS.getStatus());
            archiveJobConfService.updateArchiveConf(jobConfig);
        } catch (Exception e) {
            String ex = ExceptionUtils.getStackTrace(e);
            log.info("归档作业转换任务异常：" + ex);
            jobConfig.setJobStatus(ArchiveJobStatus.CONVERT_FAILED.getStatus());
            archiveJobConfService.updateArchiveConfStatusFailed(jobConfig, ArchiveJobStatus.CONVERT_FAILED);

            setTaskLogProperties(jobConfig, ex);
            throw new ArchiverConvertException("归档作业转换任务异常");
        }finally {
            saveTaskLog();
        }
        return true;
    }
}
