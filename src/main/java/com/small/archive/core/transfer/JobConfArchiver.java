package com.small.archive.core.transfer;

import com.small.archive.pojo.ArchiveJobConfig;

/**
 * @Project : small-db-archive
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */
public  interface JobConfArchiver {

    public  void executeJobConfigArchive(ArchiveJobConfig conf);


}
