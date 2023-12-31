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
public class ArchiveJobConfParam {

    private long id ;
    private long jobId ;
    private String jobMode ;
    private String paramPk ;
    private String paramName ;
    private String paramType ;
    private int paramConvert ;
    private String paramValue ;
    private String paramExtVal ;
    private String paramDesc ;
    private int paramOrder ;
    private Date createTime ;
    private Date updateTime ;
    private int ifValid ;
    private String ext1 ;
    private String ext2 ;

    private long paramValueInt ;
    private Date paramValueDate ;

    public long getParamValueInt() {
        return Integer.parseInt(paramValue);
    }
}
