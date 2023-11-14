package com.small.archive.core.delete.strategy;

import com.small.archive.core.emuns.ArchiveStrategyEnum;
import com.small.archive.pojo.ArchiveJobConfig;
import com.small.archive.pojo.ArchiveJobDetailTask;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ ArchiveTaskDeleteStrategy ] 接口说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/12 012 17:30
 * @Version: v1.0
 */
public interface ArchiveTaskDeleteStrategy {


    public ArchiveStrategyEnum getArchiveStrategy();


    public boolean deleteArchivedSourceData(ArchiveJobConfig conf, ArchiveJobDetailTask acTask);
}
