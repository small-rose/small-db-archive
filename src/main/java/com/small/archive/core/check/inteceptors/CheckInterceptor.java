package com.small.archive.core.check.inteceptors;

import com.small.archive.core.emuns.ArchiveJobMode;
import com.small.archive.pojo.ArchiveJobConfig;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ CheckInterceptor ] 接口说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/13 013 21:51
 * @Version: v1.0
 */
public interface CheckInterceptor {

    /**
     * 检查对应的拦截器是否支持传入的配置模式的 校验策略
     * @param type
     * @return
     */
    public boolean supports(ArchiveJobMode type);

    /**
     * 执行拦截器功能
     * @param conf
     * @return
     */
    public boolean intercept(ArchiveJobConfig conf);


}
