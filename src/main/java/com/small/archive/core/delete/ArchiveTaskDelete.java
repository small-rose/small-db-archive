package com.small.archive.core.delete;

import com.small.archive.pojo.ArchiveConf;
import com.small.archive.pojo.ArchiveConfDetailTask;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ ArchiveTaskDelete ] 接口说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/12 012 17:30
 * @Version: v1.0
 */
public interface ArchiveTaskDelete {


    public void deleteSourceData(ArchiveConf conf, ArchiveConfDetailTask task);
}
