package com.small.archive.core.check.inteceptors;

import com.small.archive.core.annotation.CustomInterceptors;
import com.small.archive.core.check.ArchiveModeCheckService;
import com.small.archive.core.emuns.ArchiveJobMode;
import com.small.archive.dao.ArchiveCheckDao;
import com.small.archive.exception.ArchiverCheckException;
import com.small.archive.pojo.ArchiveJobConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ CustomInterceptor ] 说明： 无
 * @Function: 功能描述：  ArchiveJobMode.ARCHIVE 归档数据配置模式使用的校验
 * @Date: 2023/11/13 013 22:09
 * @Version: v1.0
 */
@Slf4j
@Component
@CustomInterceptors
public class ArchiveCheckInterceptor implements CheckInterceptor {


    @Autowired
    private ArchiveCheckDao archiveCheckDao;
    @Autowired
    private ArchiveModeCheckService archiveModeCheckService;
    @Override
    public boolean intercept(ArchiveJobConfig conf) {
        try {
            // check the table of source exists when CONF_MODE = ARCHIVE
            boolean result = archiveCheckDao.checkExistsSourceTable(conf.getSourceTable());
            if (!result) {
                log.info(">>> 归档配置的源表在源数据库中不存在");
                throw new ArchiverCheckException("归档配置的源表[" + conf.getSourceTable() + "]在源数据库中不存在");
            }
            // check the table of target exists when CONF_MODE = ARCHIVE
            result = archiveCheckDao.checkExistsTargetTable(conf.getTargetTable());
            if (!result) {
                log.info(">>> 归档配置的目标表在归档库中不存在");
                throw new ArchiverCheckException("归档配置的目标表[" + conf.getTargetTable() + "]在归档库中不存在");
            }
            // check the sql of source table can or not execute
            archiveCheckDao.checkSourceTableSql(conf);
            // check the sql of target table can or not execute
            archiveCheckDao.checkTargetTableSql(conf);
            // check some conf of ARCHIVE_MODE
            archiveModeCheckService.archiveBeforeCheck(conf);
        }catch (Exception e){
            throw e ;
        }
        return true;
    }


    /**
     * CONF_MODE = ARCHIVE 数据归档时执行此校验
     * @param confStrategy
     * @return
     */
    @Override
    public boolean supports(ArchiveJobMode confStrategy) {
        return ArchiveJobMode.ARCHIVE.equals(confStrategy);
    }
}
