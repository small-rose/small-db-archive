package com.small.archive.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @Project : small-db-archive
 * @Author : zhangzongyuan
 * @Description : [ DataSourceConfig ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/10 19:13
 * @Version ： 1.0
 **/
@Configuration
@PropertySource("classpath:application.yml")
public class DataSourceConfig {


    @Bean
    public JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(dataSource());
    }

    @Bean("archiveJdbcTemplate")
    public JdbcTemplate archiveJdbcTemplate(){
        return new JdbcTemplate(archiveDataSource());
    }



    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid.master")
    public DataSource dataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "secondDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.archive")
    public DataSource archiveDataSource() {
        return DruidDataSourceBuilder.create().build();
    }
}
