package com.small.archive.core.emuns;
/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */
public enum ArchiveLogStatus {

    SUCCESS("S","归档任务成功") ,
    ERROR("E","归档任务出错");

    private String status ;
    private String desc ;

    public String getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    ArchiveLogStatus(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static ArchiveLogStatus getParamType(String confMode) {
         for (ArchiveLogStatus modeType : ArchiveLogStatus.values()){
            if (modeType.status.equals(confMode)){
                return modeType;
            }
        }
         return null;
    }
}
