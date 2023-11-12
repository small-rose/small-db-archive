package com.small.archive.service.task;

import com.small.archive.core.emuns.ArchiveConfStatus;
import com.small.archive.core.transfer.ArchiveTransferConfService;
import com.small.archive.dao.ArchiveDao;
import com.small.archive.pojo.ArchiveConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * @Project : small-db-archive
 * @description: TODO 功能角色说明： ArchiveTaskWatchService
 * TODO 描述：   数据搬运
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */

@Slf4j
@Service
public class ArchiveTransferTask {


    @Autowired
    private ArchiveDao archiveDao;

    @Autowired
    private ArchiveTransferConfService archiveTransferConfService;


    public void archiveTaskExec() {

        ArchiveConf query = new ArchiveConf();
        query.setConfStatus(ArchiveConfStatus.CONVERTED.getStatus());
        List<ArchiveConf> acList = archiveDao.queryArchiveConfList(query);
        if (CollectionUtils.isEmpty(acList)) {
            log.info("位检测到转换成功[CONVERTED]的任务，无法进行数据搬运!");
            return;
        }
        for (ArchiveConf conf : acList) {
            archiveTransferConfService.executeConfArchive(conf);
        }
        log.info("本次扫扫描执行数据归档搬运对应的表：" + acList.size() + " 张");
    }
}
