package com.small.archive.core.verify;

import com.small.archive.pojo.ArchiveConf;
import com.small.archive.pojo.ArchiveConfDetailTask;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ DataArchiverVerify ] 接口说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/12 012 16:20
 * @Version: v1.0
 */
public interface DataArchiverVerify {


    /**
     * 执行数据校对
     * @param conf
     * @param acTask
     */
    public  void executeVerify(ArchiveConf conf,  ArchiveConfDetailTask acTask);


    /**
     * 执行校对检查
     * @param conf
      */
    public boolean executeCheckVerify(ArchiveConf conf);
}
