package com.small.archive.service;

import com.small.archive.core.AbstractDataArchiverLifecycle;
import com.small.archive.exception.DataArchiverException;
import com.small.archive.pojo.ArchiveTableConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Project : small-db-archive
 * @Author : zhangzongyuan
 * @Description : [ ArchiveService ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/10 19:17
 * @Version ： 1.0
 **/
@Service
public class ArchiveBusService extends AbstractDataArchiverLifecycle {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JdbcTemplate archiveJdbcTemplate;

    @Override
    public void executeArchive() throws DataArchiverException {

    }


    public void transportData(ArchiveTableConf tableConf) {
        String sourceTable = tableConf.getSourceTable();
        String archiveTable = tableConf.getTargetTable();
        String archiveCondition = tableConf.getArchiveCondition();

        // 查询满足归档条件的数据
        String selectSql = "SELECT * FROM " + sourceTable + " WHERE " + archiveCondition;
        List<Map<String, Object>> data = jdbcTemplate.queryForList(selectSql);

        // 将数据插入归档表
        String insertSql = "INSERT INTO " + archiveTable + " VALUES (?, ?, ...)";
        for (Map<String, Object> row : data) {
            Object[] params = row.values().toArray();
            archiveJdbcTemplate.update(insertSql, params);
        }
    }


    private String buildInsertSql(String tableName, Map<String, Object> rowData) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("INSERT INTO ").append(tableName).append(" (");

        List<String> columns = new ArrayList<>(rowData.keySet());
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                sqlBuilder.append(", ");
            }
            sqlBuilder.append(columns.get(i));
        }
        sqlBuilder.append(") VALUES (");

        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                sqlBuilder.append(", ");
            }
            sqlBuilder.append("?");
        }

        sqlBuilder.append(")");
        return sqlBuilder.toString();
    }


    @Override
    public void watchArchiveConf(ArchiveTableConf conf) {

    }

    @Override
    public void initTaskBatchData(ArchiveTableConf conf) {

    }

    @Override
    public void processTransportData(ArchiveTableConf tableConf) {

    }

    @Override
    public void verificationData(ArchiveTableConf tableConf) {

    }

    @Override
    public void finalize(ArchiveTableConf tableConf) {

    }
}
