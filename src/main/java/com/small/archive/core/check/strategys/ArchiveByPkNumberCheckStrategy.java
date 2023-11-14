package com.small.archive.core.check.strategys;

import com.small.archive.core.check.ArchiveModeCheck;
import com.small.archive.core.emuns.ArchiveStrategyEnum;
import com.small.archive.dao.ArchiveDao;
import com.small.archive.exception.ArchiverCheckException;
import com.small.archive.pojo.ArchiveJobConfParam;
import com.small.archive.pojo.ArchiveJobConfig;
import com.small.archive.utils.SmallUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ PkNumModeCheckStrategy ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/13 013 23:38
 * @Version: v1.0
 */

@Slf4j
@Service
public class ArchiveByPkNumberCheckStrategy implements ArchiveModeCheck {

    @Autowired
    private ArchiveDao archiveDao;

    @Override
    public ArchiveStrategyEnum getArchiveStrategy() {
        return ArchiveStrategyEnum.ARCHIVE_PK_NUMBER;
    }

    @Override
    public boolean check(ArchiveJobConfig conf) {

        if (!SmallUtils.hasText(conf.getJobColumns())){
            log.info("归档使用[{}]策略必须配置主键列不允许为空。", getArchiveStrategy());
            throw new ArchiverCheckException("归档使用[" + getArchiveStrategy() + "]策略必须配置主键列不允许为空。");
        }

        String jobColumns = conf.getJobColumns();
        jobColumns = jobColumns.replaceAll("，",",");
        if (jobColumns.contains(",")){
            List<String> pkList = Arrays.asList(jobColumns.split(","));
            // TODO 暂时无法处理联合主键
            if (pkList.size()>1){
                log.info("归档使用[{}]策略必须配置主键列暂无法处理联合主键。", getArchiveStrategy());
                throw new ArchiverCheckException("归档使用[" + getArchiveStrategy() + "]策略必须配置主键列暂无法处理联合主键。");
            }
        }

        List<ArchiveJobConfParam> paramList = archiveDao.queryArchiveConfParamListByConfId(conf.getId());
        if (SmallUtils.isEmpty(paramList)) {
            log.info("归档使用[{}]策略必须配置主键赋值参数。", getArchiveStrategy());
            throw new ArchiverCheckException("归档使用[" + getArchiveStrategy() + "]策略必须配置主键赋值参数。");
        }
        if (SmallUtils.isNotEmpty(paramList) && ArchiveStrategyEnum.ARCHIVE_PK_NUMBER.name().equals(conf.getJobStrategy())) {
            List<ArchiveJobConfParam> idParamList = paramList.stream().filter(s -> ":".concat(conf.getJobColumns()).equalsIgnoreCase(s.getParamName())).collect(Collectors.toList());
            if (ObjectUtils.isEmpty(idParamList)) {
                log.info("归档使用[{}]策略必须配置对应配置主键的起始和终止值。", getArchiveStrategy());
                throw new ArchiverCheckException("归档使用[" + getArchiveStrategy() + "]策略必须配置对应配置主键的起始和终止值。");
            }
            if (!ObjectUtils.isEmpty(idParamList) && idParamList.size() != 2) {
                log.info("归档使用[{}]策略必须配置对应配置主键的起始和终止值,且必须是2条配置。", getArchiveStrategy());
                throw new ArchiverCheckException("归档使用[" + getArchiveStrategy() + "]策略必须配置对应配置主键参数的起始和终止值,且必须是2条配置。");
            }
            List numberList = Arrays.asList("number", "int", "integer", "long", "bigdecimal");
            for (ArchiveJobConfParam param : idParamList) {
                if (StringUtils.hasText(param.getParamType()) && !numberList.contains(param.getParamType().toLowerCase())) {
                    throw new ArchiverCheckException("归档使用[" + getArchiveStrategy() + "]策略必须配置对应配置主键参数类型:[number,int,integer,long,bigdecimal]。");
                }
            }
        }
        return true;
    }
}
