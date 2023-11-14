package com.small.archive.core.delete;

import com.small.archive.core.delete.strategy.ArchiveTaskDelStrategyFactory;
import com.small.archive.core.delete.strategy.ArchiveTaskDeleteStrategy;
import com.small.archive.core.emuns.ArchiveStrategyEnum;
import com.small.archive.core.emuns.ArchiveTaskStatus;
import com.small.archive.dao.ArchiveDao;
import com.small.archive.pojo.ArchiveJobConfig;
import com.small.archive.pojo.ArchiveJobDetailTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ ArchiveTaskDeleteService ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/14 014 23:11
 * @Version: v1.0
 */
@Slf4j
@Service
public class ArchivedJobTaskDeleteService implements ArchivedJobTaskDelete {

    @Autowired
    private ArchiveDao archiveDao;
    @Autowired
    private ArchiveTaskDelStrategyFactory archiveTaskDelStrategyFactory ;


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean deleteArchivedData(ArchiveJobConfig conf, ArchiveJobDetailTask task) {
        // 根据不同归档策略执行不同的删除方式
        ArchiveTaskDeleteStrategy archiveModeCheck = archiveTaskDelStrategyFactory.getArchiveTaskDeleteStrategy(ArchiveStrategyEnum.getStrategy(conf.getJobStrategy()));
        return archiveModeCheck.deleteArchivedSourceData(conf, task);
    }


    public boolean executeCheckDelete(ArchiveJobConfig conf) {

        //检查当前批次任务是否出错
        List<ArchiveJobDetailTask> taskList = archiveDao.queryArchiveConfDetailTaskList(conf, ArchiveTaskStatus.ERROR);
        if (!CollectionUtils.isEmpty(taskList)) {
            log.info("当前批次任务存在出错任务！请人工检查！");
            return false;
        }
        return true;
    }


}
