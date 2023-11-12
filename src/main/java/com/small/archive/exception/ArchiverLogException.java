package com.small.archive.exception;

/**
 * @Project : small-db-archive
 * @description: TODO 功能角色说明：
 * TODO 描述：
 * @author: 张小菜
 * @date: 2023/11/12 012 0:22
 * @version: v1.0
 */
public class ArchiverLogException extends RuntimeException{


    public ArchiverLogException() {
        super();
    }


    public ArchiverLogException(String message) {
        super(message);
    }


    public ArchiverLogException(String message, Throwable cause) {
        super(message, cause);
    }
}
