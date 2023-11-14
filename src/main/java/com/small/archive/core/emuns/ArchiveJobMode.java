package com.small.archive.core.emuns;

import com.small.archive.utils.SmallUtils;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ ArchiveJobMode ] 说明： 无
 * @Function: 功能描述： 无    配置策略： 用来区分配置用途、归档全流程操作、只清理数据操作
 * @Date: 2023/11/13 013 22:31
 * @Version: v1.0
 */
public enum ArchiveJobMode {

    ARCHIVE("Archive data","归档数据"),
    DELETE("Clean Up Data","清理数据"),
    NULL_MODE("Not supports ","不支持的作业模式");

    private String modeCode;
    private String modeDesc;

    public static ArchiveJobMode getModeCode(String confMode) {
        if (!SmallUtils.hasText(confMode)){
            return null;
        }
        for (ArchiveJobMode mode : ArchiveJobMode.values()){
            if (mode.getModeCode().equalsIgnoreCase(confMode)){
                return mode;
            }
        }
        return null;
    }

    public static String getAllModeName() {
        StringBuffer sb = new StringBuffer("[");
        for (ArchiveStrategyEnum modeType : ArchiveStrategyEnum.values()){
            if (!modeType.name().equals(NULL_MODE.name())){
                sb.append(modeType.name()).append(",");
            }
        }
        String result = sb.substring(0, sb.length()-1);
        result = result.concat("]");
        return result;
    }

    public String getModeCode() {
        return modeCode;
    }

    public String getModeDesc() {
        return modeDesc;
    }

    ArchiveJobMode(String enCode, String chDesc) {
        this.modeCode = enCode;
        this.modeDesc = chDesc;
    }
}
