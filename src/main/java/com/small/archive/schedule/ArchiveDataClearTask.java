package com.small.archive.schedule;

import com.small.archive.core.delete.ArchivedJobDelete;
import com.small.archive.core.emuns.ArchiveJobStatus;
import com.small.archive.dao.ArchiveDao;
import com.small.archive.pojo.ArchiveJobConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * @Project : small-db-archive
 * @description: TODO 功能角色说明： ArchiveDataClearTask
 * TODO 描述：   step 5 数据已归档数据清理
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */

@Slf4j
@Service
public class ArchiveDataClearTask {


    @Autowired
    private ArchiveDao archiveDao;

    @Autowired
    private ArchivedJobDelete archivedJobDelete;


    public void archiveTaskDeleteSourceData() {

        ArchiveJobConfig query = new ArchiveJobConfig();
        query.setJobStatus(ArchiveJobStatus.VERIFY_SUCCESS.getStatus());
        List<ArchiveJobConfig> archiveJobConfigs = archiveDao.queryArchiveConfList(query);
        if (CollectionUtils.isEmpty(archiveJobConfigs)) {
            log.info("位找到搬运完成配置，不执行数据搬运");
            return;
        }
        for (ArchiveJobConfig conf : archiveJobConfigs) {
            //数据校对
            archivedJobDelete.jobArchivedDataDelete(conf);

        }

    }
}
