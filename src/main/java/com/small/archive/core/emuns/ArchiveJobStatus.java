package com.small.archive.core.emuns;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */
public enum ArchiveJobStatus {

    PREPARE("PREPARE", "归档作业配置准备完成"),
    CHECKING("CHECKING", "归档作业配置校验中"),
    CHECKED_SUCCESS("CHECKED_SUCCESS", "归档作业配置校验完成"),
    CHECKED_FAILED("CHECKED_FAILED", "归档作业配置校验失败"),
    CONVERTING("CONVERTING", "归档作业任务转换中"),
    CONVERT_SUCCESS("CONVERT_SUCCESS", "归档作业任务转换成功"),
    CONVERT_FAILED("CONVERT_FAILED", "归档作业任务转换失败"),
    MIGRATING("MIGRATING", "归档作业任务数搬运中"),
    MIGRATED_SUCCESS("MIGRATED", "归档作业任务数搬运完成"),
    MIGRATED_FAILED("MIGRATED", "归档作业任务数搬运完成"),
    VERIFYING("VERIFYING", "归档作业任务数据校对中"),
    VERIFY_SUCCESS("VERIFY_SUCCESS", "归档作业任务数据校对完成"),
    VERIFY_FAILED("VERIFY_FAILED", "归档作业任务数据校对完成"),
    DELETE("DELETE", "归档作业任务源表数据删除中"),
    DELETE_FAILED("DELETE_FAILED", "归档作业任务源表数据删除中"),
    SUCCESS("SUCCESS", "归档作业任务全部完成"),
    ERROR("ERROR", "归档作业任务出错");

    private String status;
    private String desc;

    public String getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    ArchiveJobStatus(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static ArchiveJobStatus getParamType(String confMode) {
        for (ArchiveJobStatus modeType : ArchiveJobStatus.values()) {
            if (modeType.status.equals(confMode)) {
                return modeType;
            }
        }
        return null;
    }
}
