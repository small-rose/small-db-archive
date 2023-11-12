package com.small.archive.core.check;

import com.small.archive.core.emuns.*;
import com.small.archive.dao.ArchiveCheckDao;
import com.small.archive.exception.ArchiverCheckException;
import com.small.archive.pojo.ArchiveConf;
import com.small.archive.pojo.ArchiveConfTaskLog;
import com.small.archive.service.ArchiveConfService;
import com.small.archive.service.ArchiveLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/11 011 23:02
 * @version: v1.0
 */
@Slf4j
@Component
public class DefaultArchiveBeforeCheckHandler extends AbstractArchiveCheck {

    @Autowired
    private ArchiveCheckDao archiveCheckDao;
    @Autowired
    private ArchiveLogService archiveLogService;
    @Autowired
    private ArchiveConfService archiveConfService;

    @Override
    public ArchiveCheckModeType getCheckType() {
        return ArchiveCheckModeType.DEFAULT;
    }

    @Override
    @Transactional( rollbackFor = ArchiverCheckException.class )
    public boolean check(ArchiveConf conf) {
        archiveConfService.updateArchiveConfStatus(conf.getId(), ArchiveConfStatus.CHECKING);

        try {
            if (checkConf(conf) && checkMetaData(conf) && checkSql(conf)) {
                archiveConfService.updateArchiveConfStatus(conf.getId(), ArchiveConfStatus.CHECKED_SUCCESS);
                return true;
            }
        } catch (ArchiverCheckException e) {
            String ex = ExceptionUtils.getStackTrace(e);
            ArchiveConfTaskLog taskLog = new ArchiveConfTaskLog();
            taskLog.setConfId(conf.getId());
            taskLog.setTaskPhase(ArchiveLogPhase.CHECK.getStatus());
            taskLog.setCreateTime(new Date());
            taskLog.setExecResult(ArchiveLogStatus.ERROR.getStatus());
            if (ex.length() > 2000) {
                ex = ex.substring(0, 2000);
            }
            taskLog.setErrorInfo(ex);
            archiveLogService.saveArchiveLog(taskLog);
            archiveConfService.updateArchiveConfStatusFailed(conf.getId(), ArchiveConfStatus.CHECKED_FAILED);

            throw new ArchiverCheckException("归档配置校验失败: "+ex);
        }
        return false;
    }


    @Override
    protected boolean checkConf(ArchiveConf conf) throws ArchiverCheckException {
        Assert.hasText(conf.getConfSourceTab(), "归档配置中归档源库表conf_source_tab不允许为空");
        Assert.hasText(conf.getConfTargetTab(), "归档配置中归档目标表conf_target_tab不允许为空");
        Assert.hasText(conf.getConfMode(), "归档配置中归档模式conf_mode不允许为空");
        boolean result = archiveCheckDao.checkConfMode(conf.getConfMode());
        if (!result) {
            log.info("归档配置的目标表配置的归档模式【" + conf.getConfMode() + "】暂不支持，可用模式有" + ArchiveModeType.getAllModeName());
            return result;
        }
        if (ArchiveModeType.PK_NUM_MODE.name().equalsIgnoreCase(conf.getConfMode())) {
            Assert.hasText(conf.getConfPk(), "归档配置中归档目标表conf_target_tab不允许为空");
        }
        return true;
    }

    @Override
    protected boolean checkMetaData(ArchiveConf conf) throws ArchiverCheckException {

        boolean result = archiveCheckDao.checkExistsSourceTable(conf.getConfSourceTab());
        if (!result) {
            log.info("归档配置的源表在源数据库中不存在");
            throw new ArchiverCheckException("归档配置的源表[" + conf.getConfSourceTab() + "]在源数据库中不存在");
        }
        result = archiveCheckDao.checkExistsTargetTable(conf.getConfTargetTab());
        if (!result) {
            log.info("归档配置的目标表在归档库中不存在");
            throw new ArchiverCheckException("归档配置的目标表[" + conf.getConfTargetTab() + "]在归档库中不存在");
        }

        return true;
    }

    @Override
    protected boolean checkSql(ArchiveConf conf) throws ArchiverCheckException {

        boolean result = archiveCheckDao.checkTableSql(conf);
        if (!result) {
            log.info("SQL校验未通过！");
        }
        archiveCheckDao.checkConfParams(conf);
        return result;
    }
}
