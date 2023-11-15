package com.small.archive.service;

import com.small.archive.core.emuns.ArchiveJobStatus;
import com.small.archive.core.emuns.ArchiveTaskStatusEnum;
import com.small.archive.dao.ArchiveDao;
import com.small.archive.pojo.ArchiveJobConfParam;
import com.small.archive.pojo.ArchiveJobConfig;
import com.small.archive.pojo.ArchiveJobDetailTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ ArchiveJobConfService ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/12 012 19:49
 * @Version: v1.0
 */

@Slf4j
@Service
public class ArchiveJobConfService {

    @Autowired
    private ArchiveDao archiveDao ;

    public List<ArchiveJobConfig> queryArchiveConfList(ArchiveJobConfig query) {
        return archiveDao.queryArchiveConfList(query);
    }


    public List<ArchiveJobDetailTask> queryArchiveConfDetailTaskList(ArchiveJobConfig conf, ArchiveTaskStatusEnum prepare) {
        return archiveDao.queryArchiveConfDetailTaskList(conf,prepare);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int updateArchiveConf(ArchiveJobConfig conf){
        return archiveDao.updateArchiveConf(conf);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int updateArchiveConfStatus(ArchiveJobConfig conf, ArchiveJobStatus confStatus){
        return archiveDao.updateArchiveConfStatus(conf, confStatus);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int updateArchiveConfStatusFailed(ArchiveJobConfig conf, ArchiveJobStatus checkedFailed) {
        return archiveDao.updateArchiveConfStatus(conf, checkedFailed);
    }


    public List<ArchiveJobConfParam> queryArchiveConfParamList() {
        return archiveDao.queryArchiveConfParamList();
    }

    public List<ArchiveJobConfParam> queryArchiveConfParamListByConfId(long jobId) {
        return archiveDao.queryArchiveConfParamListByConfId(jobId);
    }
}
