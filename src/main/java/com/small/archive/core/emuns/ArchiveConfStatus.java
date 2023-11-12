package com.small.archive.core.emuns;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */
public enum ArchiveConfStatus {

    PREPARE("PREPARE", "归档配置准备完成"),
    CHECKING("CHECKING", "归档配置校验中"),
    CHECKED_SUCCESS("CHECKED_SUCCESS", "归档配置校验完成"),
    CHECKED_FAILED("CHECKED_FAILED", "归档配置校验失败"),
    CONVERTED("CONVERTED", "归档任务转换成功"),
    MIGRATING("MIGRATING", "归档任务数搬运中"),
    MIGRATED("MIGRATED", "归档任务数搬运完成"),
    VERIFYING("VERIFYING", "归档任务数据校对中"),
    VERIFIED("VERIFIED", "归档任务数据校对完成"),
    DELETE("DELETE", "归档任务源表数据删除中"),
    SUCCESS("SUCCESS", "归档任务全部完成"),
    ERROR("ERROR", "归档任务出错");

    private String status;
    private String desc;

    public String getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    ArchiveConfStatus(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static ArchiveConfStatus getParamType(String confMode) {
        for (ArchiveConfStatus modeType : ArchiveConfStatus.values()) {
            if (modeType.status.equals(confMode)) {
                return modeType;
            }
        }
        return null;
    }
}
