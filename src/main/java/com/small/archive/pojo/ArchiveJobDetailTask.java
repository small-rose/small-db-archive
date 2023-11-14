package com.small.archive.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Project : small-db-archive
 * @Author : zhangzongyuan
 * @Description : [ ArchiveJobConfig ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/11 15:22
 * @Version ： 1.0
 **/
@Data
public class ArchiveJobDetailTask {

    private long id ;
    private long jobId ;
    private String jobBatchNo ;
    private String sourceTable ;
    private String targetTable ;
    private String taskSql ;
    private String taskStatus ;
    private int taskOrder ;
    private long expectSize ;
    private long actualSize ;
    private Date taskStart ;
    private Date taskEnd ;
    private long verifySize ;
    private Date verifyStart ;
    private Date verifyEnd ;
    private long deleteSize ;
    private Date deleteStart ;
    private Date deleteEnd ;
    private String deleteSql ;

    private Date createTime ;
    private Date lastUpdateTime ;
    private String ext1 ;
    private String ext2 ;

}
