package com.small.archive.dao;

import com.small.archive.core.emuns.ArchiveTaskStatus;
import com.small.archive.pojo.ArchiveJobDetailTask;
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


    public int updateTaskStatus(ArchiveJobDetailTask acTask, ArchiveTaskStatus taskStatus) {
        String sql = "update ARCHIVE_JOB_DETAIL_TASK a set  a.task_status=? " +
                ", task_start = ?  where id = ? ";
        return jdbcTemplate.update(sql, taskStatus.getStatus(), acTask.getTaskStart(), acTask.getId());
    }

    public int updateArchiveConfDetailTaskStatus(ArchiveJobDetailTask acTask, ArchiveTaskStatus taskStatus) {
        String sql = "update ARCHIVE_JOB_DETAIL_TASK a set a.task_status=? , a.actual_size = ? ," +
                " task_end = ?  where id = ? ";
        return jdbcTemplate.update(sql, taskStatus.getStatus(),acTask.getActualSize(), acTask.getTaskEnd(), acTask.getId());
    }


    public int updateVerifyTaskStatus(ArchiveJobDetailTask acTask, ArchiveTaskStatus taskStatus) {
        String sql = "update ARCHIVE_JOB_DETAIL_TASK a set a.verify_size=? , a.task_status='02' " +
                ", verify_start = ? , verify_end = ? where id = ? ";
        return jdbcTemplate.update(sql, acTask.getVerifySize(), taskStatus.getStatus(), acTask.getVerifyStart(),acTask.getVerifyEnd(), acTask.getId());
    }


    public Date queryForDate(String paramValue) {
        String sql = "select "+paramValue+" from dual ";
        return jdbcTemplate.queryForObject(sql, Date.class);
    }

    public long updateForBatch(List<ArchiveJobDetailTask> taskList) {
        String sql = "insert into ARCHIVE_JOB_DETAIL_TASK (id, job_id, job_batch_no, task_order, source_table," +
                "target_table, task_sql, expect_size, task_status, create_time) " +
                " values (sql.nextval, ?,?,?,?,  ?,?,?,?, ?)";
        List<Object[]> paramsList = new ArrayList<>(taskList.size());
        for (ArchiveJobDetailTask t : taskList) {
            Object[] params = new Object[]{
                    t.getJobId(), t.getJobBatchNo(), t.getTaskOrder(), t.getSourceTable(),
                    t.getTargetTable(), t.getTaskSql(), t.getExpectSize(), t.getTaskStart(), t.getCreateTime()
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
