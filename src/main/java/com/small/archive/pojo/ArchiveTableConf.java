package com.small.archive.pojo;

import lombok.Data;

/**
 * @Project : small-db-archive
 * @Author : zhangzongyuan
 * @Description : [ ArchiveTableConf ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/10 19:19
 * @Version ： 1.0
 **/
@Data
public class ArchiveTableConf {

    private int id ;
    private String archiveName;
    private String archiveDesc;
    private String archiveStatus;
    /**
     * 正常模式 NORMAL/FORCE
     */
    private String archiveMode;
    private String sourceTable;
    private String targetTable;
    private String archiveCondition;
    private String archiveNums;
    private String forceSourceSql;
    private String forceTargetSql;
    private String pkColumn;
    private String excludeColumns;
    private String createDate ;

}
