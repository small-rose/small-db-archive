package com.small.archive.schedule;

import com.small.archive.core.emuns.ArchiveJobStatus;
import com.small.archive.core.transfer.JobConfArchiverService;
import com.small.archive.dao.ArchiveDao;
import com.small.archive.pojo.ArchiveJobConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * @Project : small-db-archive
 * @description: TODO 功能角色说明： ArchiveDataTransferTask
 * TODO 描述：   数据搬运
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */

@Slf4j
@Service
public class ArchiveDataTransferTask {


    @Autowired
    private ArchiveDao archiveDao;

    @Autowired
    private JobConfArchiverService jobConfArchiverService;


    public void archiveTaskExec() {

        ArchiveJobConfig query = new ArchiveJobConfig();
        query.setJobStatus(ArchiveJobStatus.CONVERT_SUCCESS.getStatus());
        List<ArchiveJobConfig> acList = archiveDao.queryArchiveConfList(query);
        if (CollectionUtils.isEmpty(acList)) {
            log.info("位检测到转换成功[CONVERTED]的任务，无法进行数据搬运!");
            return;
        }
        for (ArchiveJobConfig conf : acList) {
            jobConfArchiverService.executeJobConfigArchive(conf);
        }
        log.info("本次扫扫描执行数据归档搬运对应的表：" + acList.size() + " 张");
    }
}
