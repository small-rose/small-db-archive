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
public class ArchiveByPkStringCheckStrategy implements ArchiveModeCheck {

    @Autowired
    private ArchiveDao archiveDao;

    @Override
    public ArchiveStrategyEnum getArchiveStrategy() {
        return ArchiveStrategyEnum.ARCHIVE_PK_STRING;
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
        if (SmallUtils.isNotEmpty(paramList) && ArchiveStrategyEnum.ARCHIVE_PK_STRING.name().equals(conf.getJobStrategy())) {
            List<ArchiveJobConfParam> idParamList = paramList.stream().filter(s -> ":".concat(conf.getJobColumns()).equalsIgnoreCase(s.getParamName())).collect(Collectors.toList());
            if (ObjectUtils.isEmpty(idParamList)) {
                log.info("归档使用[{}]策略必须配置对应配置主键的Like范围。", getArchiveStrategy());
                throw new ArchiverCheckException("归档使用[" + getArchiveStrategy() + "]策略必须配置对应配置主键的Like范围。");
            }
            if (!ObjectUtils.isEmpty(idParamList) && idParamList.size() != 1) {
                log.info("归档使用[{}]策略必须配置对应参数主键Like,且必须是1条配置，检索数据使用LIKE模式。", getArchiveStrategy());
                throw new ArchiverCheckException("归档使用[" + getArchiveStrategy() + "]策略必须配置对应参数主键Like,且必须是1条配置，检索数据使用LIKE模式。");
            }
            List numberList = Arrays.asList("string");
            for (ArchiveJobConfParam param : idParamList) {
                if (StringUtils.hasText(param.getParamType()) && !numberList.contains(param.getParamType().toLowerCase())) {
                    log.info("归档使用[" + getArchiveStrategy() + "]策略必须配置对应配置主键参数类型:[string]。");
                    throw new ArchiverCheckException("归档使用[" + getArchiveStrategy() + "]策略必须配置对应配置主键参数类型:[string]。");
                }
            }
        }
        return true;
    }
}
