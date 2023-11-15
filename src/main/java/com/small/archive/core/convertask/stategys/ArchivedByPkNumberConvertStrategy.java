package com.small.archive.core.convertask.stategys;

import com.small.archive.core.constant.ArchiveConstant;
import com.small.archive.core.context.ArchiveContextHolder;
import com.small.archive.core.emuns.ArchiveJobStatus;
import com.small.archive.core.emuns.ArchiveStrategyEnum;
import com.small.archive.dao.ArchiveTaskDao;
import com.small.archive.exception.ArchiverConvertException;
import com.small.archive.pojo.ArchiveJobConfParam;
import com.small.archive.pojo.ArchiveJobConfig;
import com.small.archive.pojo.ArchiveJobDetailTask;
import com.small.archive.service.ArchiveJobConfService;
import com.small.archive.service.ArchiveSqlService;
import com.small.archive.service.ArchiveTaskLogService;
import com.small.archive.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
public class ArchivedByPkNumberConvertStrategy extends AbstractLogStrategy implements ArchiveJobConvertStrategy {

    @Autowired
    private ArchiveJobConfService archiveJobConfService;

    @Autowired
    private ArchiveTaskDao archiveTaskDao ;
    @Autowired
    private ArchiveSqlService archiveSqlService;

    @Override
    public ArchiveStrategyEnum getStrategyMode() {
        return ArchiveStrategyEnum.ARCHIVE_PK_NUMBER;
    }

    public ArchivedByPkNumberConvertStrategy(ArchiveTaskLogService archiveTaskLogService) {
        super(archiveTaskLogService);
    }

    @Override
    @Transactional(rollbackFor = ArchiverConvertException.class )
    public boolean jobConvertTask(ArchiveJobConfig jobConfig) {
        create();
        try {
            // 再次校验是否有数据需要归档
            Map<String, Object> archiveMap = ArchiveContextHolder.getArchiveMap();
            int sourceCount = archiveMap.get(ArchiveConstant.COUNT_SOURCE)!=null ? (int)archiveMap.get(ArchiveConstant.COUNT_SOURCE) : -1;
            if (sourceCount <= 0){
                log.info("本次无可归档数据，不生成归档任务！");
                return false;
            }
            double pageSize = jobConfig.getJobPageSize();
            int pageNo = 1;
            if (sourceCount> pageSize){
                pageNo = (int) Math.ceil(sourceCount / pageSize)  + 1 ;
            }
            // 提取SQL参数
            List<ArchiveJobConfParam> paramList = archiveJobConfService.queryArchiveConfParamListByConfId(jobConfig.getId());
            List<ArchiveJobConfParam> confParamList = null;
            if (!CollectionUtils.isEmpty(paramList)){
                confParamList = paramList.stream().collect(Collectors.groupingBy(ArchiveJobConfParam::getJobId)).get(jobConfig.getId());
            }

            String sql =  jobConfig.getJobCondition();

            List<ArchiveJobConfParam> idList = confParamList.stream().filter(s -> jobConfig.getJobColumns().toLowerCase().contains(s.getParamPk().toLowerCase())).collect(Collectors.toList());
            List<ArchiveJobConfParam> noIdList = confParamList.stream().filter(s -> !jobConfig.getJobColumns().toLowerCase().contains(s.getParamPk().toLowerCase())).collect(Collectors.toList());
            Optional<ArchiveJobConfParam> startIdParam = idList.stream().min(Comparator.comparing(ArchiveJobConfParam::getParamValueInt));
            if (ObjectUtils.isEmpty(startIdParam)){
                log.info("本次归档配置参数取最小ID有问题，无法生成归档任务！");
                return false;
            }
            Optional<ArchiveJobConfParam> endIdParam = idList.stream().max(Comparator.comparing(ArchiveJobConfParam::getParamValueInt));
            if (ObjectUtils.isEmpty(endIdParam)){
                log.info("本次归档配置参数取最大ID有问题，无法生成归档任务！");
                return false;
            }
            int idStart = Integer.parseInt(startIdParam.get().getParamValue());
            int idEnd = Integer.parseInt(endIdParam.get().getParamValue());
            if (idStart<= idEnd){
                log.info("本次归档配置参数取最大ID有问题，无法生成归档任务！");
                return false;
            }

            String batchNo = jobConfig.getSourceTable().toUpperCase().concat(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            jobConfig.setJobBatchNo(batchNo);

            long idTmpStart = idStart;
            long idTmpEnd = 0;
            long idSize = jobConfig.getJobPageSize();
            String selectSql = "";
            String deleteSel = "";
            // 计算DATE
            List<ArchiveJobConfParam> actualParamList = archiveSqlService.calculateDateParams(noIdList) ;
            //计算ID,并准备保证任务
            List<ArchiveJobDetailTask> taskList = new ArrayList<>(pageNo);
            ArchiveJobDetailTask task = null;
            for (int x=1 ; x <= pageNo; x++){


                if (x==1){
                    actualParamList.add(startIdParam.get());
                    ArchiveJobConfParam temEndId = new ArchiveJobConfParam();
                    BeanUtils.copyProperties(endIdParam.get(), temEndId);
                    // 修改第一个任务的结束id
                    idTmpEnd = idTmpStart + idSize ;
                    temEndId.setParamValue(String.valueOf(idTmpEnd));
                    actualParamList.add(temEndId);
                }else if (x>1 && x< pageNo){
                    // 修改开始id
                    ArchiveJobConfParam startTmpId = new ArchiveJobConfParam();
                    BeanUtils.copyProperties(startIdParam.get(), startTmpId);
                    startTmpId.setParamValue(String.valueOf(idTmpEnd));
                    // 修改结束id
                    ArchiveJobConfParam endTmpId = new ArchiveJobConfParam();
                    BeanUtils.copyProperties(endIdParam.get(), endTmpId);
                    idTmpEnd = idTmpStart + idSize ;
                    endTmpId.setParamValue(String.valueOf(idTmpEnd));
                    actualParamList.add(startTmpId);
                    actualParamList.add(endTmpId);
                }else if (x==pageNo){
                    // 修改最后一个个任务的开始id
                    ArchiveJobConfParam startTmpId = new ArchiveJobConfParam();
                    BeanUtils.copyProperties(startIdParam.get(), startTmpId);
                    startTmpId.setParamValue(String.valueOf(idTmpEnd));
                    actualParamList.add(startTmpId);

                    actualParamList.add(endIdParam.get());
                }
                //条件赋值
                sql = SqlUtils.buildTaskSql(sql, actualParamList);
                selectSql = SqlUtils.buildAppendSelectSql(jobConfig.getSourceTable(), sql);
                deleteSel = SqlUtils.buildAppendDeleteSql(jobConfig.getSourceTable(), sql);
                task = new ArchiveJobDetailTask();
                task.setJobId(jobConfig.getId());
                task.setJobBatchNo(batchNo);
                task.setCreateTime(new Date());
                task.setTaskOrder(x);
                task.setSourceTable(jobConfig.getSourceTable());
                task.setTargetTable(jobConfig.getTargetTable());
                task.setTaskSelSql(selectSql);
                task.setTaskDelSql(deleteSel);
                task.setTaskStatus(ArchiveJobStatus.PREPARE.getStatus());

                archiveSqlService.checkTaskSql(selectSql, task);
                taskList.add(task);
            }
            long total = archiveTaskDao.updateForBatch(taskList);
            if (pageNo!=total){
                throw new ArchiverConvertException("归档转换异常");
            }
            jobConfig.setJobBatchNo(batchNo);
            jobConfig.setJobStatus(ArchiveJobStatus.CONVERT_SUCCESS.getStatus());
            archiveJobConfService.updateArchiveConf(jobConfig);
            return true;
        }catch (Exception e){
            String ex = ExceptionUtils.getStackTrace(e);
            log.info("归档配置拆分子任务异常："+ex);
            jobConfig.setJobStatus(ArchiveJobStatus.CONVERT_FAILED.getStatus());
            archiveJobConfService.updateArchiveConfStatusFailed(jobConfig, ArchiveJobStatus.CONVERT_FAILED);
            setTaskLogProperties(jobConfig, ex);
            throw new ArchiverConvertException("归档转换异常");
        }finally {
            saveTaskLog();
        }
    }
}
