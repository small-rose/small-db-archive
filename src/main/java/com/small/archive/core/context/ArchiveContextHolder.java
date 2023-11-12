package com.small.archive.core.context;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:05
 * @version: v1.0
 */
@Slf4j
public class ArchiveContextHolder {

    private static final ThreadLocal<String> archiveSqlHolder = new ThreadLocal<>();

    private static final ThreadLocal<Map<String,Object>> archiveMapHolder = new ThreadLocal<>();


    public static void setArchiveSql(String sql) {
        archiveSqlHolder.set(sql);
    }

    public static String getArchiveSql() {
        String key = archiveSqlHolder.get();
        return  key;
    }

    public static void setArchiveMap(Map<String,Object> archiveMap) {
        archiveMapHolder.set(archiveMap);
    }

    public static Map<String,Object> getArchiveMap() {
        return  archiveMapHolder.get();
    }
}
