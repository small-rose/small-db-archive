package com.small.archive.core.transfer;

import com.small.archive.core.emuns.ArchiveConfStatus;
import com.small.archive.core.emuns.ArchiveTaskStatus;
import com.small.archive.dao.ArchiveDao;
import com.small.archive.pojo.ArchiveConf;
import com.small.archive.pojo.ArchiveConfDetailTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ ArchiveTransferConfService ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/12 012 19:37
 * @Version: v1.0
 */
@Slf4j
@Service
public class ArchiveTransferConfService implements ConfArchiver {

    @Autowired
    private ArchiveDao archiveDao ;
    @Autowired
    private ArchiveTransferService archiveTransferService ;


    @Override
    @Transactional
    public void executeConfArchive(ArchiveConf conf) {
        try {
            // 检测可搬运任务
            List<ArchiveConfDetailTask> taskList = archiveDao.queryArchiveConfDetailTaskList(conf, ArchiveTaskStatus.PREPARE);
            if (CollectionUtils.isEmpty(taskList)){
                log.info("未检测到可执行[PREPARE]的归档任务！");
                return;
            }

            // 标记搬运中完成
            archiveDao.updateArchiveConfStatus(conf.getId(), ArchiveConfStatus.MIGRATING);

            for (ArchiveConfDetailTask task : taskList) {
                //数据搬运，任务级事务
                archiveTransferService.executeArchive(task);
            }
            boolean check = archiveTransferService.executeCheckArchive(conf);
            if (check) {
                // 标记搬运完成
                archiveDao.updateArchiveConfStatus(conf.getId(), ArchiveConfStatus.MIGRATED);
            }
        }catch (Exception e){
            throw e;
        }
    }
}
