package com.small.archive.service;

import com.small.archive.dao.ArchiveLogDao;
import com.small.archive.exception.ArchiverLogException;
import com.small.archive.pojo.ArchiveConfTaskLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ ArchiveConfService ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/12 012 19:49
 * @Version: v1.0
 */

@Slf4j
@Service
public class ArchiveLogService {

    @Autowired
    private ArchiveLogDao archiveLogDao ;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int saveArchiveLog(ArchiveConfTaskLog taskLog){
        if (ObjectUtils.isEmpty(taskLog)){
            throw new ArchiverLogException("执行日志参数为空，执行失败!");
        }
        return archiveLogDao.saveArchiveLog(taskLog);
    }
}
