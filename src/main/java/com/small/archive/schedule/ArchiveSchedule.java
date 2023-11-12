package com.small.archive.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Project : small-db-archive
 * @Author : zhangzongyuan
 * @Description : [ ArchiveSchedule ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/10 19:38
 * @Version ： 1.0
 **/

@Component
public class ArchiveSchedule {


    @Scheduled
    public void initTaskStep(){

    }



    @Scheduled
    public void processData(){

    }



    @Scheduled
    public void validateData(){

    }


    @Scheduled
    public void deleteData(){

    }
}
