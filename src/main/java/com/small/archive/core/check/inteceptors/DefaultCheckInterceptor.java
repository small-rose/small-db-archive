package com.small.archive.core.check.inteceptors;

import com.small.archive.core.annotation.DefaultInterceptors;
import com.small.archive.core.emuns.ArchiveJobMode;
import com.small.archive.core.emuns.ArchiveStrategyEnum;
import com.small.archive.exception.ArchiverCheckException;
import com.small.archive.pojo.ArchiveJobConfig;
import com.small.archive.utils.SmallUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ DefaultCheckInterceptor ] 说明： 无
 * @Function: 功能描述： 进行一些必须的默认配置校验
 * @Date: 2023/11/13 013 21:52
 * @Version: v1.0
 */
@Slf4j
@Component
@DefaultInterceptors
public class DefaultCheckInterceptor implements CheckInterceptor, Ordered {


    @Override
    public boolean intercept(ArchiveJobConfig conf) {
        // check the field conf_source_table in the ARCHIVE_JOB_CONFIG table
        if (!SmallUtils.hasText(conf.getSourceTable())) {
            log.info("归档作业配置检查，源库表[SOURCE_TABLE]不允许为空");
            throw new ArchiverCheckException("归档配置中归档源库表[SOURCE_TABLE]不允许为空");
        }
        //check the field job_mode in the ARCHIVE_JOB_CONFIG table
        if (!SmallUtils.hasText(conf.getJobMode())) {
            log.info("归档作业配置检查，归档作业模式[JOB_MODE]不允许为空");
            throw new ArchiverCheckException("归档配置中归档源库表[JOB_MODE]不允许为空");
        }
        // check job mode enum
        ArchiveJobMode confMode = ArchiveJobMode.getModeCode(conf.getJobMode());
        if (ArchiveJobMode.NULL_MODE.equals(confMode)) {
            log.info("归档作业配置检查，配置的作业模式【" + conf.getJobMode() + "】暂不支持，可用模式有" + ArchiveJobMode.getAllModeName());
            throw new ArchiverCheckException("归档配置的目标表配置的归档模式【" + conf.getJobMode() + "】暂不支持，可用模式有" + ArchiveStrategyEnum.getAllModeName());
        }
        ArchiveStrategyEnum strategyEnum = ArchiveStrategyEnum.getStrategy(conf.getJobStrategy());
        if (ArchiveStrategyEnum.NULL_MODE.equals(strategyEnum)) {
            log.info("归档作业配置检查，归档模式下配置的归档策略【" + conf.getJobStrategy() + "】暂不支持，可用归档策略有" + ArchiveStrategyEnum.getAllModeName());
            throw new ArchiverCheckException("归档作业配置检查，归档模式下配置的归档策略【" + conf.getJobStrategy() + "】暂不支持，可用归档策略有" + ArchiveStrategyEnum.getAllModeName());
        }
        String s = ArchiveJobMode.ARCHIVE.name().equals(conf.getJobMode()) ? ArchiveJobMode.ARCHIVE.getModeDesc() : ArchiveJobMode.DELETE.getModeDesc();

        //check the field conf_where in the ARCHIVE_JOB_CONFIG table
        if (!SmallUtils.hasText(conf.getJobCondition())) {
            log.info("归档配置中" + s + "条件[JOB_CONDITION]不允许为空");
            throw new ArchiverCheckException("归档配置中" + s + "条件[JOB_CONDITION]不允许为空");
        }
        if (SmallUtils.hasText(conf.getJobCondition()) && conf.getJobCondition().toLowerCase().startsWith("where")) {
            log.info("归档配置中" + s + "条件[JOB_CONDITION]必须以[WHERE]开头的SQL字符串");
            throw new ArchiverCheckException("归档配置中" + s + "条件[JOB_CONDITION]必须以[WHERE]开头的SQL字符串");
        }
        return true;
    }


    @Override
    public int getOrder() {
        return -99;
    }


    /**
     * 不支持策略 永远返回false
     *
     * @param type
     * @return
     */
    @Override
    public boolean supports(ArchiveJobMode type) {
        return false;
    }
}
