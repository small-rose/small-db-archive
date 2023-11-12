package com.small.archive.core.check;

import com.small.archive.core.emuns.ArchiveCheckModeType;
import com.small.archive.pojo.ArchiveConf;
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
public class ArchiveCheckService {

    @Autowired
    private ArchiveCheckFactory archiveCheckFactory;

    public boolean archiveBeforeCheck(ArchiveConf conf){
        ArchiveBeforeCheck archiveBeforeCheck = archiveCheckFactory.getArchiveBeforeCheckStrategy(ArchiveCheckModeType.DEFAULT);
        return archiveBeforeCheck.check(conf);
    }

    public boolean archiveBeforeCheck(ArchiveConf conf, ArchiveCheckModeType archiveCheckModeType){
        ArchiveBeforeCheck archiveBeforeCheck = archiveCheckFactory.getArchiveBeforeCheckStrategy(archiveCheckModeType);
        return archiveBeforeCheck.check(conf);
    }
}
