package com.small.archive.core.convertask.stategys;

import com.small.archive.core.emuns.ArchiveStrategyEnum;
import com.small.archive.pojo.ArchiveJobConfig;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */
public interface ArchiveJobConvertStrategy {

    public ArchiveStrategyEnum getStrategyMode();

    public boolean jobConvertTask(ArchiveJobConfig conf);


}
