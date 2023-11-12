package com.small.archive.core.emuns;
/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */
public enum ArchiveModeType {

    PK_NUM_MODE("PK_NUM_MODE"),
    NULL_MODE("NULL_MODE");

    private String mode ;

    ArchiveModeType(String mode) {
        this.mode = mode;
    }

    public static ArchiveModeType getMode(String confMode) {
         for (ArchiveModeType modeType : ArchiveModeType.values()){
            if (modeType.mode.equals(confMode)){
                return modeType;
            }
        }
         return NULL_MODE;
    }

    public static String getAllModeName() {
        StringBuffer sb = new StringBuffer("[");
        for (ArchiveModeType modeType : ArchiveModeType.values()){
            if (!modeType.name().equals(NULL_MODE.name())){
                sb.append(modeType.name()).append(",");
            }
        }
        String result = sb.substring(0, sb.length()-1);
        result = result.concat("]");
        return result;
    }

}
