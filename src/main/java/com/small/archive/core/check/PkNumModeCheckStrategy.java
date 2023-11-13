package com.small.archive.core.check;

import com.small.archive.core.emuns.ArchiveModeStrategy;
import com.small.archive.dao.ArchiveDao;
import com.small.archive.exception.ArchiverCheckException;
import com.small.archive.pojo.ArchiveConf;
import com.small.archive.pojo.ArchiveConfParam;
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
public class PkNumModeCheckStrategy implements ArchiveBeforeCheck {

    @Autowired
    private ArchiveDao archiveDao ;

    @Override
    public ArchiveModeStrategy getCheckType() {
        return ArchiveModeStrategy.ARCHIVE_PK_NUM;
    }

    @Override
    public boolean check(ArchiveConf conf) {

        List<ArchiveConfParam> paramList = archiveDao.queryArchiveConfParamListByConfId(conf.getId());
        if (SmallUtils.isEmpty(paramList)){
            throw new ArchiverCheckException("归档使用[ARCHIVE_PK_NUM]策略必须配置主键赋值参数。");
        }
        if (SmallUtils.isNotEmpty(paramList) && ArchiveModeStrategy.ARCHIVE_PK_NUM.name().equals(conf.getConfArchiveStrategy())) {
            List<ArchiveConfParam> idParamList = paramList.stream().filter(s -> ":".concat(conf.getConfPk()).equalsIgnoreCase(s.getParamName())).collect(Collectors.toList());
            if (ObjectUtils.isEmpty(idParamList)) {
                log.info("归档使用[ARCHIVE_PK_NUM]策略必须配置对应配置主键的起始和终止值。");
                throw new ArchiverCheckException("归档使用[ARCHIVE_PK_NUM]策略必须配置对应配置主键的起始和终止值。");
            }
            if (!ObjectUtils.isEmpty(idParamList) && idParamList.size()!=2 ) {
                log.info("归档使用[ARCHIVE_PK_NUM]策略必须配置对应配置主键的起始和终止值,且必须是2条配置。");
                throw new ArchiverCheckException("归档使用[ARCHIVE_PK_NUM]策略必须配置对应配置主键参数的起始和终止值,且必须是2条配置。");
            }
            List numberList = Arrays.asList("number", "int","integer","long", "bigdecimal");
            for (ArchiveConfParam param : idParamList) {
                if (StringUtils.hasText(param.getParamType()) && !numberList.contains(param.getParamType().toLowerCase())) {
                    throw new ArchiverCheckException("归档使用[ARCHIVE_PK_NUM]策略必须配置对应配置主键参数类型:[number,int,integer,long,bigdecimal]。");
                }
            }
        }
        return true;
    }
}
