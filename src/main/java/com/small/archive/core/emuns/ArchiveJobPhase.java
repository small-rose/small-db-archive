package com.small.archive.core.emuns;
/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */
public enum ArchiveJobPhase {

    CHECK("CHECK","检查") ,
    CONVERT("CONVERT","转换") ,
    MIGRATE("MIGRATE","搬运") ,
    VERIFY("VERIFY","校对") ,
    DELETE("DELETE","删除");

    private String status ;
    private String desc ;

    public String getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    ArchiveJobPhase(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static ArchiveJobPhase getParamType(String confMode) {
         for (ArchiveJobPhase modeType : ArchiveJobPhase.values()){
            if (modeType.status.equals(confMode)){
                return modeType;
            }
        }
         return null;
    }
}
