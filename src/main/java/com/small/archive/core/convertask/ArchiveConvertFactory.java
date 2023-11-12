package com.small.archive.core.convertask;

import com.small.archive.core.emuns.ArchiveModeType;
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

    List<ArchiveConfConvert> confConvertList ;


    public ArchiveConfConvert getConvertTaskModeService(ArchiveModeType convertMode) {

        return confConvertList.stream().filter(s->(convertMode.equals(s.getMode()))).findFirst().get();
    }
}
