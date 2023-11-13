package com.small.archive.core.check.inteceptors;

import com.small.archive.core.emuns.ArchiveConfMode;
import com.small.archive.pojo.ArchiveConf;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ CheckInterceptor ] 接口说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/13 013 21:51
 * @Version: v1.0
 */
public interface CheckInterceptor {


    public boolean supports(ArchiveConfMode type);


    public boolean intercept(ArchiveConf conf);


}
