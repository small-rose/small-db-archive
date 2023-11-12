package com.small.archive.core.check;

import com.small.archive.exception.ArchiverCheckException;
import com.small.archive.pojo.ArchiveConf;
import org.springframework.stereotype.Service;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/11 011 22:52
 * @version: v1.0
 */

@Service
public abstract class AbstractArchiveCheck implements ArchiveBeforeCheck{



    protected abstract boolean checkConf(ArchiveConf conf) throws ArchiverCheckException;


    protected abstract boolean checkMetaData(ArchiveConf conf) throws ArchiverCheckException;


    protected abstract boolean checkSql(ArchiveConf conf) throws ArchiverCheckException;
}
