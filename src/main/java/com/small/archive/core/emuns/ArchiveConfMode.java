package com.small.archive.core.emuns;

import com.small.archive.utils.SmallUtils;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ ArchiveConfMode ] 说明： 无
 * @Function: 功能描述： 无    配置策略： 用来区分配置用途、归档全流程操作、只清理数据操作
 * @Date: 2023/11/13 013 22:31
 * @Version: v1.0
 */
public enum ArchiveConfMode {

    ARCHIVE("Archive data","归档数据"),
    DELETE("Clean Up Data","清理数据");

    private String encode;
    private String chDesc;

    public static ArchiveConfMode getByCode(String confMode) {
        if (!SmallUtils.hasText(confMode)){
            return null;
        }
        for (ArchiveConfMode mode : ArchiveConfMode.values()){
            if (mode.getEncode().equalsIgnoreCase(confMode)){
                return mode;
            }
        }
        return null;
    }

    public String getEncode() {
        return encode;
    }

    public String getChDesc() {
        return chDesc;
    }

    ArchiveConfMode(String enCode, String chDesc) {
        this.encode = enCode;
        this.chDesc = chDesc;
    }
}
