package com.small.archive.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Project : small-db-archive
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */
@Data
public class ArchiveConf {

    private long id ;
    private String confName ;
    private String confDesc ;
    private String confStatus ;
    private String confSourceTab ;
    private String confTargetTab ;
    private String confWhere ;
    private String confMode ;
    private String confPk ;
    private String currentBatchNo ;
    // 本次归档总量
    private int totalSize ;
    // 分配归档大小
    private int pageSize ;
    // 归档任务优先级
    private int confPriority ;
    // 归档校验次数
    private int confCheckNums ;
    private int confDelCheck ;
    private Date createTime ;
    private Date updateTime ;
    private int ifValid ;
    private String ext1 ;
    private String ext2 ;


}
