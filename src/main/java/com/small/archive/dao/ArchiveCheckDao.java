package com.small.archive.dao;

import com.small.archive.core.constant.ArchiveConstant;
import com.small.archive.core.context.ArchiveContextHolder;
import com.small.archive.core.emuns.ArchiveModeStrategy;
import com.small.archive.exception.ArchiverCheckException;
import com.small.archive.pojo.ArchiveConf;
import com.small.archive.utils.SmallUtils;
import com.small.archive.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

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
public class ArchiveCheckDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JdbcTemplate archiveJdbcTemplate;
    @Autowired
    private ArchiveDao archiveDao;

    public boolean checkExistsSourceTable(String tableName) {
        try {
            String sql = " select count(*) from USER_TABLES a where a.table_name = '" + tableName.toUpperCase() + "' ";
            Long aLong = jdbcTemplate.queryForObject(sql, Long.class);
            if (aLong > 0) {
                return true;
            }
        } catch (Exception e) {
            log.info("校验源库表失败：归档配置的源表可能在源数据库中不存在,或无权限查询[USER_TABLES]表: 错误提示 {}", e.getMessage());
            throw new ArchiverCheckException("校验源库表失败：归档配置的源表可能在源数据库中不存在,或无权限查询[USER_TABLES]表: 错误提示：" + ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    public boolean checkExistsTargetTable(String tableName) {
        try {
            String sql = " select count(*) from USER_TABLES a where a.table_name = '" + tableName.toUpperCase() + "' ";
            Long aLong = archiveJdbcTemplate.queryForObject(sql, Long.class);
            if (aLong > 0) {
                return true;
            }
        } catch (Exception e) {
            log.info("校验目标表失败：归档配置的目标表在归档库中不存在,或无权限查询[USER_TABLES]表: 错误提示 {}", e.getMessage());
            throw new ArchiverCheckException("校验目标表失败：归档配置的目标表在归档库中不存在,或无权限查询[USER_TABLES]表: 错误提示：" + ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    public boolean checkSourceTableSql(ArchiveConf conf) {
        Map<String, Object> archiveMap = new HashMap<>();
        try {
            String archSql = SqlUtils.buildCheckSelectSql(conf.getConfSourceTab(), conf.getConfWhere());
            log.info(">>> source table count sql >>> " + archSql);
            Long count = jdbcTemplate.queryForObject(archSql, Long.class);
            archiveMap.put(ArchiveConstant.COUNT_SOURCE, count);
            Map<String, Object> contextMap = ArchiveContextHolder.getArchiveMap();
            if (!SmallUtils.isEmpty(contextMap)) {
                contextMap.putAll(archiveMap);
            } else {
                contextMap = new HashMap<>();
                contextMap.putAll(archiveMap);
            }
            ArchiveContextHolder.setArchiveMap(contextMap);
            return true;

        } catch (Exception e) {
            log.info("校验归档查询SQL失败：" + e);
            throw new ArchiverCheckException("校验归档源库查询SQL失败: " + ExceptionUtils.getStackTrace(e));
        }
    }

    public boolean checkTargetTableSql(ArchiveConf conf) {
        Map<String, Object> archiveMap = new HashMap<>();
        try {
            String archSql = SqlUtils.buildCheckSelectSql(conf.getConfTargetTab(), conf.getConfWhere());
            log.info(">>> target table count sql >>> " + archSql);
            Long count = archiveJdbcTemplate.queryForObject(archSql, Long.class);
            archiveMap.put(ArchiveConstant.COUNT_TARGET, count);
            Map<String, Object> contextMap = ArchiveContextHolder.getArchiveMap();
            if (!SmallUtils.isEmpty(contextMap)) {
                contextMap.putAll(archiveMap);
            } else {
                contextMap = new HashMap<>();
                contextMap.putAll(archiveMap);
            }
            ArchiveContextHolder.setArchiveMap(contextMap);
            return true;

        } catch (Exception e) {
            log.info("校验归档查询SQL失败：" + e);
            throw new ArchiverCheckException("校验归档目标库查询SQL失败: " + ExceptionUtils.getStackTrace(e));
        }
    }



    public Long checkSql(String sql) {
        Long count1 = jdbcTemplate.queryForObject(sql, Long.class);
        return count1;
    }

    public boolean checkConfMode(String confMode) {
        ArchiveModeStrategy mode = ArchiveModeStrategy.getMode(confMode);
        return !ArchiveModeStrategy.NULL_MODE.name().equalsIgnoreCase(mode.name());
    }
}
