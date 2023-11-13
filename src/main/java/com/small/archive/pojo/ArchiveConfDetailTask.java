package com.small.archive.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Project : small-db-archive
 * @Author : zhangzongyuan
 * @Description : [ ArchiveConf ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/11 15:22
 * @Version ： 1.0
 **/
@Data
public class ArchiveConfDetailTask {

    private Long id ;
    private Long confId ;
    private String currentBatchNo ;
    private Long taskOrder ;
    private String taskSourceTab ;
    private String taskTargetTab ;
    private String taskSql ;
    private Long expectSize ;
    private Long actualSize ;
    private String taskStatus ;
    private Date createTime ;
    private Date taskStart ;
    private Date taskEnd ;
    private Long verifySize ;
    private Date verifyStart ;
    private Date verifyEnd ;
    private Long deleteSize ;
    private Date deleteStart ;
    private Date deleteEnd ;
    private String errorInfo ;
    private String ext1 ;
    private String ext2 ;

}
