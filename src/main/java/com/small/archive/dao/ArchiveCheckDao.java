package com.small.archive.dao;

import com.small.archive.core.constant.ArchiveConstant;
import com.small.archive.core.context.ArchiveContextHolder;
import com.small.archive.core.emuns.ArchiveModeType;
import com.small.archive.pojo.ArchiveConf;
import com.small.archive.pojo.ArchiveConfParam;
import com.small.archive.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    private JdbcTemplate jdbcTemplate ;
    @Autowired
    private JdbcTemplate archiveJdbcTemplate ;
    @Autowired
    private ArchiveDao archiveDao ;

    public boolean checkExistsSourceTable(String tableName){
        try {
            String sql = " select count(*) from USER_TABLES a where a.table_name = '"+tableName.toUpperCase()+"' ";
            Long aLong = jdbcTemplate.queryForObject(sql, Long.class);
            if (aLong>0){
                return true;
            }
        }catch (Exception e){
            log.info("校验源库表失败：{}", e.getMessage());
            return false;
        }
        return false;
    }

    public boolean checkExistsTargetTable(String tableName){
        try {
            String sql = " select count(*) from USER_TABLES a where a.table_name = '"+tableName.toUpperCase()+"' ";
            Long aLong = archiveJdbcTemplate.queryForObject(sql, Long.class);
            if (aLong>0){
                return true;
            }
        }catch (Exception e){
            log.info("校验目标表失败：{}", e.getMessage());
            return false;
        }
        return false;
    }

    public boolean checkTableSql(ArchiveConf conf){
        try {
            String archSql = SqlUtils.buildCheckSelectSql(conf.getConfSourceTab(), conf.getConfWhere());
            Long count1 = jdbcTemplate.queryForObject(archSql, Long.class);
            Long count2 = archiveJdbcTemplate.queryForObject(archSql, Long.class);

            if (ObjectUtils.isEmpty(count1) && ObjectUtils.isEmpty(count2)
                    && count1 > 0 && count2>-1){
                ArchiveContextHolder.setArchiveSql(archSql);
                Map<String, Object> archiveMap = new HashMap<>();
                archiveMap.put(ArchiveConstant.COUNT_SOURCE, count1);
                archiveMap.put(ArchiveConstant.COUNT_TARGET, count2);
                ArchiveContextHolder.setArchiveMap(archiveMap);
                return true;
            }
        }catch (Exception e){
            log.info("校验归档查询SQL失败：" +e);
        }
        return false ;

    }

    public boolean checkConfParams(ArchiveConf conf) {

        List<ArchiveConfParam> paramList = archiveDao.queryArchiveConfParamListByConfId(conf.getId());
        if (CollectionUtils.isEmpty(paramList)){
            log.info("归档使用PK_NUM_MODE模式必须配置对应配置主键的起始和终止值！");
            return false;
        }
        if (ArchiveModeType.PK_NUM_MODE.name().equals(conf.getConfMode())){
            ArchiveConfParam param = paramList.stream().filter(s -> ":".concat(conf.getConfPk()).equalsIgnoreCase(s.getParamName())).findFirst().get();
            if (ObjectUtils.isEmpty(param)){
                log.info("归档使用PK_NUM_MODE模式必须配置对应配置主键的起始和终止值！");
                return false;
            }
            List numberList = Arrays.asList("number","int","long","number","integer");
            if (StringUtils.hasText(param.getParamType()) && !numberList.contains(param.getParamType().toLowerCase())){
                return false;
            }
        }
        return true;
    }

    public Long checkSql(String sql) {
        Long count1 = jdbcTemplate.queryForObject(sql, Long.class);
        return count1;
    }

    public boolean checkConfMode(String confMode) {
        ArchiveModeType mode = ArchiveModeType.getMode(confMode);
        return !ArchiveModeType.NULL_MODE.name().equalsIgnoreCase(mode.name());
    }
}
