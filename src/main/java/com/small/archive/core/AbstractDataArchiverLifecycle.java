package com.small.archive.core;

import com.small.archive.pojo.ArchiveTableConf;

/**
 * @Project : small-db-archive
 * @Author : zhangzongyuan
 * @Description : [ AbstractDataArchiverLifecycle ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/10 22:31
 * @Version ： 1.0
 **/
public  abstract class AbstractDataArchiverLifecycle implements DataArchiverLifecycle {



    public abstract void watchArchiveConf(ArchiveTableConf conf);


    public abstract void initTaskBatchData(ArchiveTableConf conf);


    public  abstract void processTransportData(ArchiveTableConf tableConf);


    public  abstract void verificationData(ArchiveTableConf tableConf);


    public  abstract void finalize(ArchiveTableConf tableConf);


}
