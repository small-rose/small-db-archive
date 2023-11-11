package com.small.archive.exception;

/**
 * @Project : small-db-archive
 * @Author : zhangzongyuan
 * @Description : [ QueryException ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/11 15:54
 * @Version ： 1.0
 **/
public class QueryException extends RuntimeException{


    public QueryException() {
        super();
    }


    public QueryException(String message) {
        super(message);
    }


    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }

}
