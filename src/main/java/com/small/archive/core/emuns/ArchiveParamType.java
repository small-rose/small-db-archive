package com.small.archive.core.emuns;
/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */
public enum ArchiveParamType {

    NUMBER("Long"),
    VARCHAR("String"),
    DATE("DATE");

    private String type ;

    ArchiveParamType(String type) {
        this.type = type;
    }

    public static ArchiveParamType getParamType(String confMode) {
         for (ArchiveParamType modeType : ArchiveParamType.values()){
            if (modeType.type.equals(confMode)){
                return modeType;
            }
        }
         return null;
    }
}
