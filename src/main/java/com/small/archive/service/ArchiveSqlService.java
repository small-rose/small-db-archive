package com.small.archive.service;

import com.small.archive.core.emuns.ArchiveParamType;
import com.small.archive.dao.ArchiveCheckDao;
import com.small.archive.dao.ArchiveTaskDao;
import com.small.archive.exception.ArchiverCheckException;
import com.small.archive.pojo.ArchiveConfDetailTask;
import com.small.archive.pojo.ArchiveConfParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:58
 * @version: v1.0
 */
@Slf4j
@Repository
public class ArchiveSqlService {


    @Autowired
    private ArchiveTaskDao archiveTaskDao ;
    @Autowired
    private ArchiveCheckDao archiveCheckDao ;

    public List<ArchiveConfParam> calculateDateParams(List<ArchiveConfParam> noIdList) {

        if (CollectionUtils.isEmpty(noIdList)){
            return Collections.emptyList();
        }
        ArchiveConfParam param = null;
        List<ArchiveConfParam> resultList = new ArrayList<>();
        for (ArchiveConfParam tmp : noIdList) {
            param = new ArchiveConfParam();
            BeanUtils.copyProperties(tmp, param);
            if (ArchiveParamType.DATE.name().equalsIgnoreCase(tmp.getParamType())){
                Date date = archiveTaskDao.queryForDate(tmp.getParamValue());
                 param.setParamValue(new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
            resultList.add(param);
        }
        return resultList;
    }

    public void checkTaskSql(String sql, ArchiveConfDetailTask task) {
        try {
            long nums  = archiveCheckDao.checkSql(sql);
            task.setExpectSize(nums);
        }catch (Exception e){
            throw new ArchiverCheckException("校验任务SQL执行失败", e);
        }
    }
}
