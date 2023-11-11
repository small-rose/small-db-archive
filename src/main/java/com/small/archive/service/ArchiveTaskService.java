package com.small.archive.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @Project : small-db-archive
 * @Author : zhangzongyuan
 * @Description : [ CheckService ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/10 21:39
 * @Version ： 1.0
 **/

@Component
public class ArchiveTaskService {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    private boolean check(){

       return true ;
    }
}
