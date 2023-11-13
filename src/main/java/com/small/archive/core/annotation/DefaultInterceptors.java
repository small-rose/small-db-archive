package com.small.archive.core.annotation;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ DefaultInterceptors ] 注解类
 * @Function: 注解类功能描述：
 * @Date: 2023/11/13 013 22:24
 * @Version: v1.0
 */

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE, ElementType.METHOD})
@Retention( RetentionPolicy.RUNTIME)
@Documented
@Qualifier
public @interface DefaultInterceptors {
}
