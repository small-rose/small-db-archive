package com.small.archive.task;

import com.small.archive.base.SmallDbArchiveAppTests;
import com.small.archive.schedule.ArchiveDataClearTask;
import com.small.archive.schedule.ArchiveDataTransferTask;
import com.small.archive.schedule.ArchiveDataVerifyTask;
import com.small.archive.schedule.ArchiveJobConfCheckTask;
import com.small.archive.schedule.ArchiveJobConfConvertTask;
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
    private ArchiveJobConfCheckTask archiveJobConfCheckTask;
    @Autowired
    private ArchiveJobConfConvertTask archiveJobConfConvertTask;


    @Autowired
    private ArchiveDataTransferTask archiveDataTransferTask;
    @Autowired
    private ArchiveDataVerifyTask archiveDataVerifyTask;
    @Autowired
    private ArchiveDataClearTask archiveDataClearTask;

    /**
     * 验证配置
     */
    @Test
    public void checkConf(){

        archiveJobConfCheckTask.archiveConfWatch();
    }

    /**
     * 生成任务
     */
    @Test
    public void convert2Task(){

        archiveJobConfConvertTask.archiveConfConvert2Task();
    }

    /**
     * 搬运数据
     */
    @Test
    public void transfer(){
        archiveDataTransferTask.archiveTaskExec();
    }

    /**
     * 校对数据
     */
    @Test
    public void verify(){
        archiveDataVerifyTask.archiveTaskVerify();
    }


    /**
     * 删除数据
     */
    @Test
    public void delete(){
        archiveDataClearTask.archiveTaskDeleteSourceData();
    }
}
