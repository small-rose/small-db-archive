package com.small.archive.dao;

import com.small.archive.core.emuns.ArchiveConfStatus;
import com.small.archive.core.emuns.ArchiveTaskStatus;
import com.small.archive.pojo.ArchiveConf;
import com.small.archive.pojo.ArchiveConfDetailTask;
import com.small.archive.pojo.ArchiveConfParam;
import com.small.archive.service.jdbc.JdbcTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

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

    public ArchiveConf queryArchiveConfById(long id, ArchiveConfStatus confStatus){
        String sql = " select * from ARCHIVE_CONF a where a.id = "+id+" and a.conf_status='"+confStatus.getStatus()+"' ";
        return jdbcTemplateService.queryForObject(sql, ArchiveConf.class);
    }
    public List<ArchiveConf> queryArchiveConfList(ArchiveConf conf){
        String sql = " select * from ARCHIVE_CONF a where a.conf_status = '"+conf.getConfStatus()+"' and if_valid=1 order by conf_priority desc ";
        return jdbcTemplateService.queryForList(sql, ArchiveConf.class);
    }


    public List<ArchiveConfParam> queryArchiveConfParamList(){
        String sql = " select * from ARCHIVE_CONF_PARAM a where if_valid=1 order by a.confId ";
        return jdbcTemplateService.queryForList(sql, ArchiveConfParam.class);
    }


    public List<ArchiveConfParam> queryArchiveConfParamListByConfId(long confId){
        String sql = " select * from ARCHIVE_CONF_PARAM a where a.conf_status = '1' and a.conf_id="+confId+" order by a.confId ";
        return jdbcTemplateService.queryForList(sql, ArchiveConfParam.class);
    }

    public List<ArchiveConfDetailTask> queryArchiveConfDetailTaskList(ArchiveConf conf, ArchiveTaskStatus taskStatus){
        String sql = " select t.* from  ARCHIVE_CONF_DETAIL_TASK t  where t.conf_id "+conf.getId()+
                " and a.task_batch_no = '"+conf.getCurrentBatchNo()+"' a.task_status = '"+taskStatus.getStatus()+"' order by a.task_order ";
        return jdbcTemplateService.queryForList(sql, ArchiveConfDetailTask.class);
    }

    public ArchiveConf queryArchiveConfByTask(ArchiveConfDetailTask task){
        String sql = " select t.* from  ARCHIVE_CONF t  where t.id = "+task.getConfId() ;
        return jdbcTemplate.queryForObject(sql, ArchiveConf.class);
    }


    public int updateArchiveConfStatus(long id, ArchiveConfStatus confStatus) {
        ArchiveConf conf = null;
        try {
             conf = queryArchiveConfById(id, confStatus);
        }catch (EmptyResultDataAccessException e){
            log.info("没有查到CHECKING配置，ARCHIVE_CONF即将更新到"+confStatus.getStatus());
        }
        if (!ObjectUtils.isEmpty(conf)){
            return 1;
        }
        String sql = " update ARCHIVE_CONF t set t.conf_status = '"+confStatus.getStatus()+"',update_time = sysdate  where t.id = "+id ;
        return jdbcTemplate.update(sql);
    }

    public int updateArchiveConf(ArchiveConf conf) {
        String sql = "update ARCHIVE_CONF a set  a.conf_Status= ? ," +
                " current_batch_no = ? , update_time = sysdate  where id = ? ";
        return jdbcTemplate.update(sql, conf.getConfStatus(), conf.getCurrentBatchNo(), conf.getId());
    }
}
