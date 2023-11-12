package com.small.archive.exception;

/**
 * @Project : small-db-archive
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */
public class ArchiverConvertException extends RuntimeException{


    public ArchiverConvertException() {
        super();
    }


    public ArchiverConvertException(String message) {
        super(message);
    }


    public ArchiverConvertException(String message, Throwable cause) {
        super(message, cause);
    }
}
