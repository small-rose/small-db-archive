package com.small.archive.service;

import com.small.archive.core.emuns.ArchiveConfStatus;
import com.small.archive.core.emuns.ArchiveTaskStatus;
import com.small.archive.dao.ArchiveDao;
import com.small.archive.pojo.ArchiveConf;
import com.small.archive.pojo.ArchiveConfDetailTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ ArchiveConfService ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/12 012 19:49
 * @Version: v1.0
 */

@Slf4j
@Service
public class ArchiveConfService {

    @Autowired
    private ArchiveDao archiveDao ;

    public List<ArchiveConf> queryArchiveConfList(ArchiveConf query) {
        return archiveDao.queryArchiveConfList(query);
    }


    public List<ArchiveConfDetailTask> queryArchiveConfDetailTaskList(ArchiveConf conf, ArchiveTaskStatus prepare) {
        return archiveDao.queryArchiveConfDetailTaskList(conf,prepare);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public int updateArchiveConfStatus(ArchiveConf conf, ArchiveConfStatus confStatus){
        return archiveDao.updateArchiveConfStatus(conf, confStatus);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int updateArchiveConfStatusFailed(ArchiveConf conf, ArchiveConfStatus checkedFailed) {
        return archiveDao.updateArchiveConfStatus(conf, checkedFailed);
    }




}
