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
    /*
     *  配置模式： CONF_MODE : ARCHIVE 数据归档, CLEAN-数据清理
     */
    private String confMode ;
    /**
     *  归档策略 PK_NUM_MODE 主键数字模式、PK_NUM_MODE 主键字符串模式、DATE_MODE  日期归档模式、
     */
    private String confArchiveStrategy ;
    private String confPk ;
    private String currentBatchNo ;
    /**
     * 本次归档/删除总量
     */
    private Long sourceTotalSize ;
    private Long targetTotalSize ;
    /**
     * 分批归档/删除大小
     */
    private int pageSize ;
    /**
     * 归档/删除任务优先级
     */
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
