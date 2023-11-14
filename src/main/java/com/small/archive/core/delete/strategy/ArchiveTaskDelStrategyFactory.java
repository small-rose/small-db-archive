package com.small.archive.core.delete.strategy;

import com.small.archive.core.emuns.ArchiveStrategyEnum;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ ArchiveTaskDelStrategyFactory ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/14 014 23:11
 * @Version: v1.0
 */
@Component
public class ArchiveTaskDelStrategyFactory {

    List<ArchiveTaskDeleteStrategy>  archiveTaskDeleteStrategyList ;


    public ArchiveTaskDeleteStrategy getArchiveTaskDeleteStrategy(ArchiveStrategyEnum strategy) {
        return archiveTaskDeleteStrategyList.stream().filter(s->(strategy.equals(s.getArchiveStrategy()))).findFirst().get();
    }
}
