package com.small.archive.core.emuns;
/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */
public enum LogResultEnum {

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

    LogResultEnum(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static LogResultEnum getParamType(String confMode) {
         for (LogResultEnum modeType : LogResultEnum.values()){
            if (modeType.status.equals(confMode)){
                return modeType;
            }
        }
         return null;
    }
}
