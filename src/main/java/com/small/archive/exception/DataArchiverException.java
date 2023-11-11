package com.small.archive.exception;

/**
 * @Project : small-db-archive
 * @Author : zhangzongyuan
 * @Description : [ DataArchiverException ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/10 22:33
 * @Version ： 1.0
 **/
public class DataArchiverException extends RuntimeException{


    public DataArchiverException() {
        super();
    }


    public DataArchiverException(String message) {
        super(message);
    }


    public DataArchiverException(String message, Throwable cause) {
        super(message, cause);
    }
}
