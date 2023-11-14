package com.small.archive.core.convertask;

import com.small.archive.core.constant.ArchiveConstant;
import com.small.archive.core.context.ArchiveContextHolder;
import com.small.archive.core.emuns.ArchiveJobStatus;
import com.small.archive.core.emuns.ArchiveStrategyEnum;
import com.small.archive.core.emuns.ArchiveTaskStatus;
import com.small.archive.dao.ArchiveTaskDao;
import com.small.archive.exception.ArchiverConvertException;
import com.small.archive.pojo.ArchiveJobConfParam;
import com.small.archive.pojo.ArchiveJobConfig;
import com.small.archive.pojo.ArchiveJobDetailTask;
import com.small.archive.service.ArchiveJobConfService;
import com.small.archive.service.ArchiveSqlService;
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
public class ArchiveDateDayStrConvertStrategy implements ArchiveJobConvertTask {

    @Autowired
    private ArchiveJobConfService archiveJobConfService;
    @Autowired
    private ArchiveTaskDao archiveTaskDao ;

    @Autowired
    private ArchiveSqlService archiveSqlService;

    @Override
    public ArchiveStrategyEnum getMode() {
        return ArchiveStrategyEnum.ARCHIVE_DATE_DAY;
    }

    @Override
    @Transactional(rollbackFor = ArchiverConvertException.class )
    public boolean jobConvertTask(ArchiveJobConfig conf) {
        try {
            // 再次校验是否有数据需要归档
            Map<String, Object> archiveMap = ArchiveContextHolder.getArchiveMap();
            int sourceCount = archiveMap.get(ArchiveConstant.COUNT_SOURCE)!=null ? (int)archiveMap.get(ArchiveConstant.COUNT_SOURCE) : -1;
            if (sourceCount <= 0){
                log.info("本次无可归档数据，不生成归档任务！");
                return false;
            }
            double pageSize = conf.getJobPageSize();
            int pageNo = 1;
            if (sourceCount> pageSize){
                 pageNo = (int) Math.ceil(sourceCount / pageSize)  + 1 ;
            }
            // 提取SQL参数
            List<ArchiveJobConfParam> paramList = archiveJobConfService.queryArchiveConfParamList();
            List<ArchiveJobConfParam> confParamList = null;
            if (!CollectionUtils.isEmpty(paramList)){
                confParamList = paramList.stream().collect(Collectors.groupingBy(ArchiveJobConfParam::getJobId)).get(conf.getId());
            }

            String sql =  conf.getJobCondition();


            List<ArchiveJobConfParam> idList = confParamList.stream().filter(s -> conf.getJobColumns().toLowerCase().contains(s.getParamPk().toLowerCase())).collect(Collectors.toList());
            List<ArchiveJobConfParam> noIdList = confParamList.stream().filter(s -> !conf.getJobColumns().toLowerCase().contains(s.getParamPk().toLowerCase())).collect(Collectors.toList());
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

            String batchNo = conf.getSourceTable().toUpperCase().concat(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            conf.setJobBatchNo(batchNo);

            long idTmpStart = idStart;
            long idTmpEnd = 0;
            long idSize = conf.getJobPageSize();
            // 计算DATE
            List<ArchiveJobConfParam> actualParamList = archiveSqlService.calculateDateParams(noIdList) ;
            //计算ID,并准备保证任务
            List<ArchiveJobDetailTask> taskList = new ArrayList<>(pageNo);
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

                sql = SqlUtils.buildTaskSql(sql, actualParamList);
                ArchiveJobDetailTask task = new ArchiveJobDetailTask();
                task.setJobId(conf.getId());
                task.setJobBatchNo(batchNo);
                task.setCreateTime(new Date());
                task.setTaskOrder(x);
                task.setSourceTable(conf.getSourceTable());
                task.setTargetTable(conf.getTargetTable());
                task.setTaskSql(sql);
                archiveSqlService.checkTaskSql(sql, task);
                task.setTaskStatus(ArchiveTaskStatus.PREPARE.getStatus());
                task.setCreateTime(new Date());
                taskList.add(task);
            }
            long total = archiveTaskDao.updateForBatch(taskList);
            if (pageNo!=total){
                throw new ArchiverConvertException("归档转换异常");
            }
            conf.setJobBatchNo(batchNo);
            conf.setJobStatus(ArchiveJobStatus.CONVERT_SUCCESS.getStatus());
            archiveJobConfService.updateArchiveConf(conf);
        }catch (Exception e){
            String ex = ExceptionUtils.getStackTrace(e);
            log.info("归档配置拆分子任务异常："+ex);
            conf.setJobStatus(ArchiveJobStatus.CONVERT_FAILED.getStatus());
            archiveJobConfService.updateArchiveConfStatusFailed(conf, ArchiveJobStatus.CONVERT_FAILED);
            throw new ArchiverConvertException("归档转换异常");
        }
        return true;
    }
}
