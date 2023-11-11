package com.small.archive.codeg;

import com.small.archive.base.SmallDbArchiveApplicationTests;
import com.small.archive.service.jdbc.JdbcTemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Project : small-db-archive
 * @Author : zhangzongyuan
 * @Description : [ CodePojoTests ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/11 15:49
 * @Version ： 1.0
 **/
public class CodePojoTests extends SmallDbArchiveApplicationTests {

    @Autowired
    private JdbcTemplateService jdbcTemplateService ;

    @Test
    public void test01(){
        String sql = "";
        List<TbColumn> tbColumns = jdbcTemplateService.queryForList(sql, TbColumn.class);


    }


    class TbColumn {

    }
}
