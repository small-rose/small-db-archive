package com.small.archive.core.convertask;

import com.small.archive.core.convertask.stategys.ArchiveJobConvertStrategy;
import com.small.archive.core.emuns.ArchiveStrategyEnum;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:23
 * @version: v1.0
 */
@Component
public class ArchiveConvertFactory {

    List<ArchiveJobConvertStrategy> confConvertList ;


    public ArchiveJobConvertStrategy getConvertTaskModeService(ArchiveStrategyEnum strategyEnum) {

        return confConvertList.stream().filter(s->(strategyEnum.equals(s.getStrategyMode()))).findFirst().get();
    }
}
