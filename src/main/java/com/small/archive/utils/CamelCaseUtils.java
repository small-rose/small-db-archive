package com.small.archive.utils;

/**
 * @Project : small-db-archive
 * @Author : zhangzongyuan
 * @Description : [ CamelCaseUtils ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/11 15:55
 * @Version ： 1.0
 **/
public class CamelCaseUtils {

    private static final char SEPARATOR = '_';

    private CamelCaseUtils() {
    }

    public static String toCamelCase(String input) {
        if (input == null) {
            return null;
        }
        input = input.toLowerCase();
        int length = input.length();

        StringBuilder sb = new StringBuilder(length);
        boolean upperCase = false;
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

}
