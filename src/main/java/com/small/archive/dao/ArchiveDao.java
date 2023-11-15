package com.small.archive.dao;

import com.small.archive.core.emuns.ArchiveJobMode;
import com.small.archive.core.emuns.ArchiveJobStatus;
import com.small.archive.core.emuns.ArchiveTaskStatusEnum;
import com.small.archive.pojo.ArchiveJobConfParam;
import com.small.archive.pojo.ArchiveJobConfig;
import com.small.archive.pojo.ArchiveJobDetailTask;
import com.small.archive.service.jdbc.JdbcTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
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
public class ArchiveDao {

    @Autowired
    private JdbcTemplateService jdbcTemplateService ;
    @Autowired
    private JdbcTemplate jdbcTemplate ;

    public ArchiveJobConfig queryArchiveConfById(long id, ArchiveJobStatus confStatus){
        String sql = " select * from ARCHIVE_JOB_CONF a where a.id = "+id+" and a.job_status='"+confStatus.getStatus()+"' ";
        return jdbcTemplateService.queryForObject(sql, ArchiveJobConfig.class);
    }
    public List<ArchiveJobConfig> queryArchiveConfList(ArchiveJobConfig conf){
        String sql = " select * from ARCHIVE_JOB_CONFIG a where a.job_status = '"+conf.getJobStatus()+"' and if_valid=1 order by conf_priority desc ";
        return jdbcTemplateService.queryForList(sql, ArchiveJobConfig.class);
    }


    public List<ArchiveJobConfParam> queryArchiveConfParamList(){
        String sql = " select * from ARCHIVE_JOB_CONFIG_PARAM a where if_valid=1 order by a.confId ";
        return jdbcTemplate.queryForList(sql, ArchiveJobConfParam.class);
    }

    public List<ArchiveJobConfParam> queryArchiveJobConfParamListByJobId(long jobId){
        String sql = " select * from ARCHIVE_JOB_CONFIG_PARAM a where a.job_id=? and a.if_valid=1 order by a.confId ";
        List params = new ArrayList();
        params.add(jobId);
        return jdbcTemplate.queryForList(sql, ArchiveJobConfParam.class, params.toArray());
    }

    public List<ArchiveJobConfParam> queryArchiveConfParamListByConfId(long confId){
        String sql = " select * from ARCHIVE_JOB_CONFIG_PARAM a where a.job_status = '1' and a.conf_id="+confId+" order by a.confId ";
        return jdbcTemplateService.queryForList(sql, ArchiveJobConfParam.class);
    }

    public List<ArchiveJobDetailTask> queryArchiveConfDetailTaskList(ArchiveJobConfig conf, ArchiveTaskStatusEnum taskStatus){
        String sql = " select t.* from  ARCHIVE_JOB_CONFIG_DETAIL_TASK t  where t.job_id "+conf.getId()+
                " and a.job_batch_no = '"+conf.getJobBatchNo()+"' a.task_status = '"+taskStatus.getStatus()+"' order by a.task_order ";
        return jdbcTemplateService.queryForList(sql, ArchiveJobDetailTask.class);
    }

    public ArchiveJobConfig queryArchiveConfByTask(ArchiveJobDetailTask task){
        String sql = " select t.* from  ARCHIVE_JOB_CONFIG t  where t.id = "+task.getJobId() ;
        return jdbcTemplate.queryForObject(sql, ArchiveJobConfig.class);
    }


    public int updateArchiveConfStatus(ArchiveJobConfig conf, ArchiveJobStatus confStatus) {
        ArchiveJobConfig confRecord = null;
        try {
            confRecord = queryArchiveConfById(conf.getId(), confStatus);
        }catch (EmptyResultDataAccessException e){
            log.info("没有查到CHECKING配置，ARCHIVE_JOB_CONFIG即将更新到"+confStatus.getStatus());
        }
        if (!ObjectUtils.isEmpty(confRecord)){
            return 1;
        }
        StringBuffer sb = new StringBuffer("update archive_job_config t set t.job_status = ");
        sb.append(" '").append(confStatus.getStatus()).append("' ");
        if (ArchiveJobStatus.CHECKED_SUCCESS.getDesc().equals(confStatus)){
            sb.append(" , t.total_expect_size = ").append(conf.getTotalExpectSize());
            if (ArchiveJobMode.ARCHIVE.name().equalsIgnoreCase(conf.getJobMode())){
                sb.append(" , t.target_archived_size = ").append(conf.getTotalArchivedSize());
            }
        }
        sb.append(" , update_time = sysdate  where t.id = ").append(conf.getId()) ;
        log.info(">>> update conf >>> " + sb.toString());
        return jdbcTemplate.update(sb.toString());
    }

    public int updateArchiveConf(ArchiveJobConfig conf) {
        String sql = "update ARCHIVE_JOB_CONFIG a set  a.job_status= ? ," +
                " job_batch_no = ? , update_time = sysdate  where id = ? ";
        return jdbcTemplate.update(sql, conf.getJobStatus(), conf.getJobBatchNo(), conf.getId());
    }
}
