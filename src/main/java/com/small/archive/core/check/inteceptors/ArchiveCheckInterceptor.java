package com.small.archive.core.check.inteceptors;

import com.small.archive.core.annotation.CustomInterceptors;
import com.small.archive.core.check.ArchiveCheckService;
import com.small.archive.core.emuns.ArchiveConfMode;
import com.small.archive.dao.ArchiveCheckDao;
import com.small.archive.exception.ArchiverCheckException;
import com.small.archive.pojo.ArchiveConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ CustomInterceptor ] 说明： 无
 * @Function: 功能描述： 无
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
    private ArchiveCheckService archiveCheckService ;
    @Override
    public boolean intercept(ArchiveConf conf) {
        try {
            boolean result = archiveCheckDao.checkExistsSourceTable(conf.getConfSourceTab());
            if (!result) {
                log.info(">>> 归档配置的源表在源数据库中不存在");
                throw new ArchiverCheckException("归档配置的源表[" + conf.getConfSourceTab() + "]在源数据库中不存在");
            }
            result = archiveCheckDao.checkExistsTargetTable(conf.getConfTargetTab());
            if (!result) {
                log.info(">>> 归档配置的目标表在归档库中不存在");
                throw new ArchiverCheckException("归档配置的目标表[" + conf.getConfTargetTab() + "]在归档库中不存在");
            }
            archiveCheckDao.checkSourceTableSql(conf);
            archiveCheckDao.checkTargetTableSql(conf);

            archiveCheckService.archiveBeforeCheck(conf);
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
    public boolean supports(ArchiveConfMode confStrategy) {
        return ArchiveConfMode.ARCHIVE.equals(confStrategy);
    }
}
