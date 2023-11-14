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
public class ArchiveJobConfig {

    private long id ;
    private String jobName ;
    private String jobStatus ;
    private String sourceTable ;
    private String targetTable ;
    private String jobCondition ;
    /*
     *  配置模式： JOB_MODE : ARCHIVE 数据归档, CLEAN-数据清理
     */
    private String jobMode ;
    /**
     *  归档策略 PK_NUM_MODE 主键数字模式、PK_NUM_MODE 主键字符串模式、DATE_MODE  日期归档模式、
     *  删除策略 PK_NUM_MODE 主键删除  DATE_MODE  日期删除模式、
     */
    private String jobStrategy ;
    private String jobColumns ;
    private int doDistinct ;
    private String jobBatchNo ;
    /**
     * 本次归档/删除 预期总量
     */
    private long totalExpectSize ;
    /**
     * 本次已归档 总量
     */
    private long totalArchivedSize ;
    /**
     * 分批归档/删除大小
     */
    private long jobPageSize ;
    /**
     * 归档/删除任务优先级
     */
    private int jobPriority ;
    private int jobConfCheckNums ;
    private int jobDelCheck ;
    private Date createTime ;
    private Date updateTime ;
    private int ifValid ;
    private String ext1 ;
    private String ext2 ;


}
