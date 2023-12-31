package com.small.archive.core.check;

import com.small.archive.core.emuns.ArchiveStrategyEnum;
import com.small.archive.pojo.ArchiveJobConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/11 011 23:05
 * @version: v1.0
 */

@Service
public class ArchiveModeCheckService {

    @Autowired
    private ArchiveModeCheckFactory archiveModeCheckFactory;



    public boolean archiveBeforeCheck(ArchiveJobConfig conf){
        ArchiveModeCheck archiveModeCheck = archiveModeCheckFactory.getArchiveModeCheckStrategy(ArchiveStrategyEnum.getStrategy(conf.getJobStrategy()));
        return archiveModeCheck.check(conf);
    }




}
