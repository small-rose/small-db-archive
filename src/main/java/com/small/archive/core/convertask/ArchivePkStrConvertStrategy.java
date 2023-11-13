package com.small.archive.core.convertask;

import com.small.archive.core.constant.ArchiveConstant;
import com.small.archive.core.context.ArchiveContextHolder;
import com.small.archive.core.emuns.ArchiveConfStatus;
import com.small.archive.core.emuns.ArchiveModeStrategy;
import com.small.archive.core.emuns.ArchiveTaskStatus;
import com.small.archive.dao.ArchiveDao;
import com.small.archive.dao.ArchiveTaskDao;
import com.small.archive.exception.ArchiverConvertException;
import com.small.archive.pojo.ArchiveConf;
import com.small.archive.pojo.ArchiveConfDetailTask;
import com.small.archive.pojo.ArchiveConfParam;
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
import java.util.*;
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
public class ArchivePkStrConvertStrategy implements ArchiveConfConvert {

    @Autowired
    private ArchiveDao archiveDao;
    @Autowired
    private ArchiveTaskDao archiveTaskDao ;
    @Autowired
    private ArchiveSqlService archiveSqlService;

    @Override
    public ArchiveModeStrategy getMode() {
        return ArchiveModeStrategy.ARCHIVE_PK_STR;
    }

    @Override
    @Transactional(rollbackFor = ArchiverConvertException.class )
    public boolean confConvertTask(ArchiveConf conf) {
        try {
            // 再次校验是否有数据需要归档
            Map<String, Object> archiveMap = ArchiveContextHolder.getArchiveMap();
            int sourceCount = archiveMap.get(ArchiveConstant.COUNT_SOURCE)!=null ? (int)archiveMap.get(ArchiveConstant.COUNT_SOURCE) : -1;
            if (sourceCount <= 0){
                log.info("本次无可归档数据，不生成归档任务！");
                return false;
            }
            double pageSize = conf.getPageSize();
            int pageNo = 1;
            if (sourceCount> pageSize){
                 pageNo = (int) Math.ceil(sourceCount / pageSize)  + 1 ;
            }
            // 提取SQL参数
            List<ArchiveConfParam> paramList = archiveDao.queryArchiveConfParamList();
            List<ArchiveConfParam> confParamList = null;
            if (!CollectionUtils.isEmpty(paramList)){
                confParamList = paramList.stream().collect(Collectors.groupingBy(ArchiveConfParam::getConfId)).get(conf.getId());
            }

            String sql =  conf.getConfWhere();


            List<ArchiveConfParam> idList = confParamList.stream().filter(s -> conf.getConfPk().toLowerCase().contains(s.getParamPk().toLowerCase())).collect(Collectors.toList());
            List<ArchiveConfParam> noIdList = confParamList.stream().filter(s -> !conf.getConfPk().toLowerCase().contains(s.getParamPk().toLowerCase())).collect(Collectors.toList());
            Optional<ArchiveConfParam> startIdParam = idList.stream().min(Comparator.comparing(ArchiveConfParam::getParamValueInt));
            if (ObjectUtils.isEmpty(startIdParam)){
                log.info("本次归档配置参数取最小ID有问题，无法生成归档任务！");
                return false;
            }
            Optional<ArchiveConfParam> endIdParam = idList.stream().max(Comparator.comparing(ArchiveConfParam::getParamValueInt));
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

            String batchNo = conf.getConfSourceTab().toUpperCase().concat(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            conf.setCurrentBatchNo(batchNo);

            int idTmpStart = idStart;
            int idTmpEnd = 0;
            int idSize = conf.getPageSize();
            // 计算DATE
            List<ArchiveConfParam> actualParamList = archiveSqlService.calculateDateParams(noIdList) ;
            //计算ID,并准备保证任务
            List<ArchiveConfDetailTask> taskList = new ArrayList<>(pageNo);
            for (int x=1 ; x <= pageNo; x++){


                if (x==1){
                    actualParamList.add(startIdParam.get());
                    ArchiveConfParam temEndId = new ArchiveConfParam();
                    BeanUtils.copyProperties(endIdParam.get(), temEndId);
                    // 修改第一个任务的结束id
                    idTmpEnd = idTmpStart + idSize ;
                    temEndId.setParamValue(String.valueOf(idTmpEnd));
                    actualParamList.add(temEndId);
                }else if (x>1 && x< pageNo){
                    // 修改开始id
                    ArchiveConfParam startTmpId = new ArchiveConfParam();
                    BeanUtils.copyProperties(startIdParam.get(), startTmpId);
                    startTmpId.setParamValue(String.valueOf(idTmpEnd));
                    // 修改结束id
                    ArchiveConfParam endTmpId = new ArchiveConfParam();
                    BeanUtils.copyProperties(endIdParam.get(), endTmpId);
                    idTmpEnd = idTmpStart + idSize ;
                    endTmpId.setParamValue(String.valueOf(idTmpEnd));
                    actualParamList.add(startTmpId);
                    actualParamList.add(endTmpId);
                }else if (x==pageNo){
                    // 修改最后一个个任务的开始id
                    ArchiveConfParam startTmpId = new ArchiveConfParam();
                    BeanUtils.copyProperties(startIdParam.get(), startTmpId);
                    startTmpId.setParamValue(String.valueOf(idTmpEnd));
                    actualParamList.add(startTmpId);

                    actualParamList.add(endIdParam.get());
                }

                sql = SqlUtils.buildTaskSql(sql, actualParamList);
                ArchiveConfDetailTask task = new ArchiveConfDetailTask();
                task.setConfId(conf.getId());
                task.setCurrentBatchNo(batchNo);
                task.setCreateTime(new Date());
                task.setTaskOrder(x);
                task.setTaskSourceTab(conf.getConfSourceTab());
                task.setTaskTargetTab(conf.getConfTargetTab());
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
            conf.setCurrentBatchNo(batchNo);
            conf.setConfStatus(ArchiveConfStatus.CONVERTED.getStatus());
            archiveDao.updateArchiveConf(conf);
        }catch (Exception e){
            String ex = ExceptionUtils.getStackTrace(e);
            log.info("归档配置拆分子任务异常："+ex);
            throw new ArchiverConvertException("归档转换异常");
        }
        return true;
    }
}
