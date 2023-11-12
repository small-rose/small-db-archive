package com.small.archive.core.emuns;
/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */
public enum ArchiveTaskStatus {

    PREPARE("10","归档任务准备完成"),
    HANDING("20","归档任务数据搬运进行中"),
    MIGRATED("29","归档任务数据搬运完成"),
    VERIFYING("30","归档任务数据校对中"),
    VERIFIED("39","归档任务数据校对完成"),
    DELETE("40","归档任务源表数据删除中"),
    COMPLETED("99", "归档任务全部完成"),
    ERROR("00","归档任务出错");

    private String status ;
    private String desc ;

    public String getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    ArchiveTaskStatus(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static ArchiveTaskStatus getParamType(String confMode) {
         for (ArchiveTaskStatus modeType : ArchiveTaskStatus.values()){
            if (modeType.status.equals(confMode)){
                return modeType;
            }
        }
         return null;
    }
}
