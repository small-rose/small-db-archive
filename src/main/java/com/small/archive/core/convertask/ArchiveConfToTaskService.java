package com.small.archive.core.convertask;

import com.small.archive.core.emuns.ArchiveStrategyEnum;
import com.small.archive.pojo.ArchiveJobConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/11 011 23:16
 * @version: v1.0
 */


@Component
public class ArchiveConfToTaskService {


    @Autowired
    private ArchiveConvertFactory archiveConvertFactory ;


    public boolean conf2Task(ArchiveJobConfig conf){
        ArchiveJobConvertTask archiveJobConvertTask = archiveConvertFactory.getConvertTaskModeService(ArchiveStrategyEnum.getStrategy(conf.getJobMode()));
        return archiveJobConvertTask.jobConvertTask(conf);
    }


}
