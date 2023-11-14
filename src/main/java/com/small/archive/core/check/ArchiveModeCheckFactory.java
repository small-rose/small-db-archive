package com.small.archive.core.check;

import com.small.archive.core.emuns.ArchiveStrategyEnum;
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
public class ArchiveModeCheckFactory {

    @Autowired
    private List<ArchiveModeCheck> checkList ;


    public ArchiveModeCheck getArchiveModeCheckStrategy(ArchiveStrategyEnum strategyEnum) {
        return checkList.stream().filter(s->(strategyEnum.equals(s.getArchiveStrategy()))).findFirst().get();
    }

}
