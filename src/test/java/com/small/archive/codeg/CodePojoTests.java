package com.small.archive.codeg;

import com.small.archive.base.SmallDbArchiveAppTests;
import com.small.archive.service.jdbc.JdbcTemplateService;
import com.small.archive.utils.CamelCaseUtils;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Project : small-db-archive
 * @Author : zhangzongyuan
 * @Description : [ CodePojoTests ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2023/11/11 15:49
 * @Version ： 1.0
 **/
public class CodePojoTests extends SmallDbArchiveAppTests {

    @Autowired
    private JdbcTemplateService jdbcTemplateService ;

    @Test
    public void test01(){
        String sql = "SELECT  TC.TABLE_NAME,  TC.COLUMN_NAME, DATA_TYPE\n" +
                "FROM USER_TAB_COLUMNS TC\n" +
                "WHERE  TC.TABLE_NAME in ('ARCHIVE_CONF','ARCHIVE_CONF_PARAM','ARCHIVE_CONF_DETAIL_TASK'，'ARCHIVE_CONF_TASK_LOG')\n" +
                "ORDER BY TC.TABLE_NAME  , TC.COLUMN_ID ASC ";
        List<TbColumn> tbColumns = jdbcTemplateService.queryForList(sql, TbColumn.class);
        Map<String, List<TbColumn>> collect = tbColumns.stream().collect(Collectors.groupingBy(TbColumn::getTableName));
        String line = "private %s %s ;" ;
        for (String tableName : collect.keySet()) {
            List<TbColumn> columnList = collect.get(tableName);
            System.out.println("--------------------------------"+tableName);
            columnList.forEach(t->{
                String l = String.format(line, getType(t.getDataType()), CamelCaseUtils.toCamelCase(t.getColumnName()));
                System.out.println(l);
            });
        }

    }


    private String getType(String type){
        String result = "";
        switch (type){
            case "DATE":
                result = "Date"; break;
            case "NUMBER":
                result = "int"; break;
            case "VARCHAR2":
            case "ARCHAR2":
                result = "String"; break;
            default:
                result = ""; break;
        }
        return result ;
    }


}
@Data
class TbColumn {

    private String tableName ;
    private String columnName ;
    private String dataType ;
}
