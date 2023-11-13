package com.small.archive.core.emuns;
/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */
public enum ArchiveModeStrategy {

    ARCHIVE_PK_NUM("ARCHIVE_PK_NUM"),
    ARCHIVE_PK_STR("ARCHIVE_PK_STR"),
    ARCHIVE_DATE_DAY("ARCHIVE_DATE_DAY"),
    ARCHIVE_DATE_MOTH("ARCHIVE_DATE_MOTH"),
    ARCHIVE_DATE_YEAR("ARCHIVE_DATE_YEAR"),
    NULL_MODE("NULL_MODE");

    private String mode ;

    ArchiveModeStrategy(String mode) {
        this.mode = mode;
    }

    public static ArchiveModeStrategy getMode(String confMode) {
         for (ArchiveModeStrategy modeType : ArchiveModeStrategy.values()){
            if (modeType.mode.equals(confMode)){
                return modeType;
            }
        }
         return NULL_MODE;
    }

    public static String getAllModeName() {
        StringBuffer sb = new StringBuffer("[");
        for (ArchiveModeStrategy modeType : ArchiveModeStrategy.values()){
            if (!modeType.name().equals(NULL_MODE.name())){
                sb.append(modeType.name()).append(",");
            }
        }
        String result = sb.substring(0, sb.length()-1);
        result = result.concat("]");
        return result;
    }

}
