package com.small.archive.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Project : small-db-archive
 * @Author : zhangzongyuan
 * @Description : [ ArchiveLog ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/10 19:23
 * @Version ： 1.0
 **/
@Data
public class ArchiveLog {

    private int id ;
    private String archiveName ;
    private String archiveStatus ;
    private String archiveMode ;
    private String selectSql ;
    private String insertSql ; //保留最后一条SQL
    private String deleteSql ;
    private Date startDate ;
    private Date endDate ;
    private String errorInfo ;
    private String includeColumns ;
    private String excludeColumns ;
    private String ext1 ;
    private String ext2 ;

}
