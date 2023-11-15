package com.small.archive.pojo;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 2:05
 * @version: v1.0
 */
@Builder
@Data
public class ArchiveTaskLog {

    private long id ;
    private long jobId ;
    private long taskId ;
    private String jobBatchNo ;
    private String taskPhase ;
    private String taskResult ;
    private Date createTime ;
    private String errorInfo ;
    private String ext1 ;
    private String ext2 ;


    public ArchiveTaskLog(){

    }

    public ArchiveTaskLog(ArchiveJobDetailTask task) {
        this.jobId = task.getJobId();
        this.taskId = task.getId();
        this.jobBatchNo = task.getJobBatchNo();
        this.createTime = new Date();
    }
}
