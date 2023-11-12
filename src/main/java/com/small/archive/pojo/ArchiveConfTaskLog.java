package com.small.archive.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 2:05
 * @version: v1.0
 */
@Data
public class ArchiveConfTaskLog {

    private long id ;
    private long confId ;
    private long taskId ;
    private String currentBatchNo ;
    private String taskPhase ;
    private String execResult ;
    private Date createTime ;
    private String errorInfo ;
    private String ext1 ;
    private String ext2 ;

    public ArchiveConfTaskLog(){

    }

    public ArchiveConfTaskLog(ArchiveConfDetailTask task) {
        this.confId = task.getConfId();
        this.taskId = task.getId();
        this.currentBatchNo = task.getCurrentBatchNo();
        this.createTime = new Date();
    }
}
