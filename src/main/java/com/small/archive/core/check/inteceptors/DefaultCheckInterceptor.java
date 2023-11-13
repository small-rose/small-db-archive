package com.small.archive.core.check.inteceptors;

import com.small.archive.core.annotation.DefaultInterceptors;
import com.small.archive.core.emuns.ArchiveConfMode;
import com.small.archive.exception.ArchiverCheckException;
import com.small.archive.pojo.ArchiveConf;
import com.small.archive.utils.SmallUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ DefaultCheckInterceptor ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/13 013 21:52
 * @Version: v1.0
 */
@Slf4j
@Component
@DefaultInterceptors
public class  DefaultCheckInterceptor implements CheckInterceptor, Ordered {


    @Override
    public boolean intercept(ArchiveConf conf) {

        if (!SmallUtils.hasText(conf.getConfSourceTab())){
            log.info("归档配置中源库表[CONF_SOURCE_TAB]不允许为空");
            throw  new ArchiverCheckException("归档配置中归档源库表conf_source_tab不允许为空");
        }
        if (!SmallUtils.hasText(conf.getConfMode())){
            log.info("归档配置中配置模式[CONF_MODE]不允许为空");
            throw  new ArchiverCheckException("归档配置中归档源库表[CONF_MODE]不允许为空");
        }

        if (!SmallUtils.hasText(conf.getConfWhere())){
            String s = ArchiveConfMode.ARCHIVE.name().equals(conf.getConfMode()) ?"数据归档": "数据清理";
            log.info("归档配置中"+s+"条件[CONF_WHERE]不允许为空");
            throw  new ArchiverCheckException("归档配置中"+s+"条件[CONF_WHERE]不允许为空");
        }

        return true;
    }



    @Override
    public int getOrder() {
        return -99;
    }



    /**
     * 不支持策略 永远返回false
     * @param type
     * @return
     */
    @Override
    public boolean supports(ArchiveConfMode type) {
        return false;
    }
}
