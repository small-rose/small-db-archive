package com.small.archive.core.check;

import com.small.archive.core.emuns.ArchiveModeStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/11 011 23:06
 * @version: v1.0
 */

@Component
public class ArchiveCheckFactory {

    @Autowired
    private List<ArchiveBeforeCheck> checkList ;


    public ArchiveBeforeCheck getArchiveBeforeCheckStrategy(ArchiveModeStrategy checkRuleName) {

        return checkList.stream().filter(s->(checkRuleName.equals(s.getCheckType()))).findFirst().get();
    }

}
