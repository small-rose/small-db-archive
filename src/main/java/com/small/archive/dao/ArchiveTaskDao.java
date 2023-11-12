package com.small.archive.dao;

import com.small.archive.core.emuns.ArchiveTaskStatus;
import com.small.archive.pojo.ArchiveConfDetailTask;
import com.small.archive.service.jdbc.JdbcTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/11 011 22:35
 * @version: v1.0
 */
@Slf4j
@Repository
public class ArchiveTaskDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JdbcTemplate archiveJdbcTemplate;
    @Autowired
    private JdbcTemplateService jdbcTemplateService;


    public List<Map<String, Object>> queryForSoureList(String selectSql) {
        return jdbcTemplate.queryForList(selectSql);
    }

    public List<Map<String, Object>> queryForTargetList(String selectSql) {
        return archiveJdbcTemplate.queryForList(selectSql);
    }


    public int updateTaskStatus(ArchiveConfDetailTask acTask, ArchiveTaskStatus taskStatus) {
        String sql = "update archive_conf_detail_task a set  a.task_status=? " +
                ", task_start = ?  where id = ? ";
        return jdbcTemplate.update(sql, taskStatus.getStatus(), acTask.getTaskStart(), acTask.getId());
    }

    public int updateArchiveConfDetailTaskStatus(ArchiveConfDetailTask acTask, ArchiveTaskStatus taskStatus) {
        String sql = "update archive_conf_detail_task a set a.task_status=? , a.actual_size = ? ," +
                " task_end = ?  where id = ? ";
        return jdbcTemplate.update(sql, taskStatus.getStatus(),acTask.getActualSize(), acTask.getTaskEnd(), acTask.getId());
    }


    public int updateVerifyTaskStatus(ArchiveConfDetailTask acTask, ArchiveTaskStatus taskStatus) {
        String sql = "update archive_conf_detail_task a set a.verify_size=? , a.task_status='02' " +
                ", verify_start = ? , verify_end = ? where id = ? ";
        return jdbcTemplate.update(sql, acTask.getVerifySize(), taskStatus.getStatus(), acTask.getVerifyStart(),acTask.getVerifyEnd(), acTask.getId());
    }


    public Date queryForDate(String paramValue) {
        String sql = "select "+paramValue+" from dual ";
        return jdbcTemplate.queryForObject(sql, Date.class);
    }

    public long updateForBatch(List<ArchiveConfDetailTask> taskList) {
        String sql = "insert into archive_conf_detail_task (id, conf_id, current_batch_no, task_order, task_source_tab," +
                "task_target_tab, task_sql, expect_size, task_status, create_time) " +
                " values (sql.nextval,?,?,?,?)";
        List<Object[]> paramsList = new ArrayList<>(taskList.size());
        for (ArchiveConfDetailTask t : taskList) {
            Object[] params = new Object[]{
                    t.getConfId(), t.getCurrentBatchNo(), t.getTaskOrder(), t.getTaskSourceTab(),
                    t.getTaskTargetTab(), t.getTaskSql(), t.getExpectSize(), t.getTaskStart(), t.getCreateTime()
            };
            paramsList.add(params);
        }
        int total = 0;
        int[] hang = jdbcTemplate.batchUpdate(sql, paramsList);
        for (int h : hang){
            total+= h;
        }
        return total;
    }


    public int deleteSouceTab(String delSql) {
        return jdbcTemplate.update(delSql);
    }
}
