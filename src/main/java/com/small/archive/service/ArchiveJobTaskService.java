package com.small.archive.service;

import com.small.archive.core.emuns.ArchiveTaskStatusEnum;
import com.small.archive.dao.ArchiveTaskDao;
import com.small.archive.pojo.ArchiveJobDetailTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */

@Slf4j
@Service
public class ArchiveJobTaskService {

    @Autowired
    private ArchiveTaskDao archiveTaskDao;
    @Autowired
    private JdbcTemplate archiveJdbcTemplate;


    public List<Map<String, Object>> querySourceList(String selectSql) {
        return archiveTaskDao.queryForSoureList(selectSql);
    }

    public List<Map<String, Object>> queryTargetList(String selectSql) {
        return archiveTaskDao.queryForTargetList(selectSql);
    }

    public int batchUpdate(String insertSql, List<Object[]> paramsList) {
        int total = 0 ;
        int[] result = archiveJdbcTemplate.batchUpdate(insertSql, paramsList);
        for (int r : result){
            total += r ;
        }
        log.info("本地批量归档存入归档库数量：total = "+total);
        return total ;
    }

    @Transactional
    public int updateJobTaskStatus(ArchiveJobDetailTask acTask, ArchiveTaskStatusEnum taskStatus) {
        return archiveTaskDao.updateArchiveConfDetailTaskStatus(acTask, taskStatus);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int updateJobTaskStatusError(ArchiveJobDetailTask acTask, ArchiveTaskStatusEnum taskStatus) {
        return archiveTaskDao.updateArchiveConfDetailTaskStatus(acTask, taskStatus);
    }

 


    public int updateVerifyTaskStatus(ArchiveJobDetailTask acTask, ArchiveTaskStatusEnum taskStatus) {
        return archiveTaskDao.updateVerifyTaskStatus(acTask, taskStatus);
    }

    public int deleteSouceTab(String delSql) {
        return archiveTaskDao.deleteSouceTab(delSql);
    }


}
