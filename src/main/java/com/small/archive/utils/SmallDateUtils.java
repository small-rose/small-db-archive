package com.small.archive.utils;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @Project : small-db-archive
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */
public class SmallDateUtils {

    public static final String YYYY_MM_DD = "yyyy-MM-dd";


    public static String now(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static Date now() {
        return new Date();
    }

    public static String format(Date date, String pattern) {
        if (SmallUtils.isEmpty(date)) {
            return null;
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    public static Date parse(String strDate, String pattern) {
        if (!SmallUtils.hasText(strDate)) {
            return null;
        }
        try {
           return new SimpleDateFormat(pattern).parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String addDays(String date, String pattern, int amount) {
        if (!SmallUtils.hasText(date)) {
            return "";
        }
        Date date1 = parse(date, pattern);
        Date newDate = DateUtils.addDays(date1, amount);
        return format(newDate,pattern);
    }


    public static String addMonths(String date, String pattern, int amount) {
        if (!SmallUtils.hasText(date)) {
            return "";
        }
        Date date1 = parse(date, pattern);
        Date newDate = DateUtils.addMonths(date1, amount);
        return format(newDate,pattern);
    }

    public static String addYears(String date, String pattern, int amount) {
        if (!SmallUtils.hasText(date)) {
            return "";
        }
        Date date1 = parse(date, pattern);
        Date newDate = DateUtils.addYears(date1, amount);
        return format(newDate,pattern);
    }

    public static String beginOfDay(String date, String pattern) {
        if (!SmallUtils.hasText(date)) {
            return "";
        }
        Date date1 = parse(date, pattern);
        Date newDate = DateUtil.beginOfDay(date1);
        return format(newDate,pattern);
    }

    public static String endOfDay(String date, String pattern) {
        if (!SmallUtils.hasText(date)) {
            return "";
        }
        Date date1 = parse(date, pattern);
        Date newDate = DateUtil.endOfDay(date1);
        return format(newDate,pattern);
    }


    public static String beginOfMonth(String date, String pattern) {
        if (!SmallUtils.hasText(date)) {
            return "";
        }
        Date date1 = parse(date, pattern);
        Date newDate = DateUtil.beginOfMonth(date1);
        return format(newDate,pattern);
    }

    public static String endOfMonth(String date, String pattern) {
        if (!SmallUtils.hasText(date)) {
            return "";
        }
        Date date1 = parse(date, pattern);
        Date newDate = DateUtil.endOfMonth(date1);
        return format(newDate,pattern);
    }

    public static String beginOfYear(String date, String pattern) {
        if (!SmallUtils.hasText(date)) {
            return "";
        }
        Date date1 = parse(date, pattern);
        Date newDate = DateUtil.beginOfYear(date1);
        return format(newDate,pattern);
    }


    public static String endOfYear(String date, String pattern) {
        if (!SmallUtils.hasText(date)) {
            return "";
        }
        Date date1 = parse(date, pattern);
        Date newDate = DateUtil.endOfYear(date1);
        return format(newDate,pattern);
    }
}
