package com.small.archive.core.check.inteceptors;

import com.small.archive.core.annotation.CustomInterceptors;
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
 * @Function: 功能描述： ArchiveJobMode.DELETE清理数据配置模式使用的校验
 * @Date: 2023/11/13 013 22:09
 * @Version: v1.0
 */
@Slf4j
@Component
@CustomInterceptors
public class DeleteCheckInterceptor implements CheckInterceptor {


    @Autowired
    private ArchiveCheckDao archiveCheckDao;

    @Override
    public boolean intercept(ArchiveJobConfig conf) {
        try {
            // check the table of source exists when CONF_MODE = DELETE
            boolean result = archiveCheckDao.checkExistsSourceTable(conf.getSourceTable());
            if (!result) {
                log.info("归档配置的源表在源数据库中不存在");
                throw new ArchiverCheckException("归档配置的源表[" + conf.getSourceTable() + "]在源数据库中不存在");
            }
            // check the sql of source table can or not execute when CONF_MODE = DELETE
            archiveCheckDao.checkSourceTableSql(conf);
         }catch (Exception e){
            throw e ;
        }
        return true;
    }


    /**
     *  CONF_MODE = DELETE 数据清理 时执行校验
     * @param confStrategy
     * @return
     */
    @Override
    public boolean supports(ArchiveJobMode confStrategy) {
        return ArchiveJobMode.DELETE.equals(confStrategy);
    }
}
