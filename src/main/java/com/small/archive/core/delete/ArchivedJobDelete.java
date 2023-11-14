package com.small.archive.core.delete;

import com.small.archive.pojo.ArchiveJobConfig;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ JobArchivedDelete ] 接口说明： 无
 * @Function: 功能描述：  作业归档删除
 * @Date: 2023/11/14 014 23:23
 * @Version: v1.0
 */
public interface ArchivedJobDelete {

    public void jobArchivedDataDelete(ArchiveJobConfig conf);
}
