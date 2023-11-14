package com.small.archive.dao;

import com.small.archive.core.emuns.ArchiveTaskStatus;
import com.small.archive.pojo.ArchiveJobConfParam;
import com.small.archive.pojo.ArchiveJobConfig;
import com.small.archive.pojo.ArchiveJobDetailTask;
import com.small.archive.pojo.ArchiveTaskLog;
import com.small.archive.service.jdbc.JdbcTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Project : small-db-archive
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */
@Slf4j
@Repository
public class ArchiveLogDao {

    @Autowired
    private JdbcTemplateService jdbcTemplateService ;
    @Autowired
    private JdbcTemplate jdbcTemplate ;




    public int saveArchiveLog(ArchiveTaskLog taskLog){
        String sql = "insert into ARCHIVE_JOB_CONFIG_task_log (id, conf_id, task_id, current_batch_no, task_phase, exec_result, create_time, error_info)  "+
                " values(seq_arc_conf_task_log_id.nextval, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, taskLog.getJobId(), taskLog.getTaskId(), taskLog.getJobBatchNo(), taskLog.getTaskPhase(),
                taskLog.getTaskResult(), taskLog.getCreateTime(), taskLog.getErrorInfo());
    }


    public List<ArchiveJobConfParam> queryArchiveConfParamListByConfId(int confId){
        String sql = " select * from ARCHIVE_JOB_CONFIG_PARAM a where a.conf_status = '1' and a.conf_id="+confId+" order by a.confId ";
        return jdbcTemplateService.queryForList(sql, ArchiveJobConfParam.class);
    }

    public List<ArchiveJobDetailTask> queryArchiveConfDetailTaskList(ArchiveJobConfig conf, ArchiveTaskStatus taskStatus){
        String sql = " select t.* from  ARCHIVE_JOB_CONFIG_DETAIL_TASK t  where t.conf_id "+conf.getId()+
                " and a.task_batch_no = '"+conf.getJobBatchNo()+"' a.task_status = '"+taskStatus.getStatus()+"' order by a.task_order ";
        return jdbcTemplateService.queryForList(sql, ArchiveJobDetailTask.class);
    }

    public ArchiveJobConfig queryArchiveConfByTask(ArchiveJobDetailTask task){
        String sql = " select t.* from  ARCHIVE_JOB_CONFIG t  where t.id = "+task.getJobId(); ;
        return jdbcTemplate.queryForObject(sql, ArchiveJobConfig.class);
    }


}
