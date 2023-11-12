package com.small.archive.task;

import com.small.archive.base.SmallDbArchiveAppTests;
import com.small.archive.service.task.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ ArchiveAllTaskTests ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/12 012 20:10
 * @Version: v1.0
 */
public class ArchiveAllTaskTests extends SmallDbArchiveAppTests {

    @Autowired
    private ArchiveConfCheckTask archiveConfCheckTask;
    @Autowired
    private ArchiveConfConvertTask archiveConfConvertTask;


    @Autowired
    private ArchiveTransferTask archiveTransferTask;
    @Autowired
    private ArchiveVerifyTask archiveVerifyTask;
    @Autowired
    private ArchiveDeleteTask archiveDeleteTask;

    /**
     * 验证配置
     */
    @Test
    public void checkConf(){

        archiveConfCheckTask.archiveConfWatch();
    }

    /**
     * 生成任务
     */
    @Test
    public void convert2Task(){

        archiveConfConvertTask.archiveConfConvert2Task();
    }

    /**
     * 搬运数据
     */
    @Test
    public void transfer(){
        archiveTransferTask.archiveTaskExec();
    }

    /**
     * 校对数据
     */
    @Test
    public void verify(){
        archiveVerifyTask.archiveTaskVerify();
    }


    /**
     * 删除数据
     */
    @Test
    public void delete(){
        archiveDeleteTask.archiveTaskDeleteSourceData();
    }
}
