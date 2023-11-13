package com.small.archive.core.annotation;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ CustomInterceptors ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/13 013 22:19
 * @Version: v1.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE, ElementType.METHOD})
@Retention( RetentionPolicy.RUNTIME)
@Documented
@Qualifier
public @interface CustomInterceptors {
}
