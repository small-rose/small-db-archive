
CREATE TABLE ARCHIVE_CONF
(
    ID NUMBER PRIMARY KEY,
    CONF_NAME VARCHAR2(100),
    CONF_DESC VARCHAR2(100),
    CONF_STATUS VARCHAR2(20),
    CONF_SOURCE_TAB VARCHAR2(40),
    CONF_TARGET_TAB VARCHAR2(40),
    CONF_WHERE VARCHAR2(2000),
    CONF_MODE VARCHAR2(100),
    CONF_PK VARCHAR2(100),
    CURRENT_BATCH_NO VARCHAR2(100),
    TOTAL_SIZE NUMBER,
    PAGE_SIZE NUMBER,
    CONF_PRIORITY NUMBER DEFAULT 5,
    CONF_CHECK_NUMS NUMBER, -- 校验次数
    CONF_DEL_CHECK NUMBER, -- 1校验,0-不校验
    CREATE_TIME DATE DEFAULT SYSDATE,
    UPDATE_TIME DATE,
    IF_VALID NUMBER(1) DEFAULT 1,
    EXT1 VARCHAR2(256),
    EXT2 VARCHAR2(256)
);

COMMENT ON TABLE ARCHIVE_CONF IS '归档配置表'
COMMENT ON COLUMN ON ARCHIVE_CONF.ID IS '主键ID';
COMMENT ON COLUMN ON ARCHIVE_CONF.CONF_NAME IS '配置名称';
COMMENT ON COLUMN ON ARCHIVE_CONF.CONF_DESC IS '配置描述';
COMMENT ON COLUMN ON ARCHIVE_CONF.CONF_STATUS IS '归档配置执行阶段';
COMMENT ON COLUMN ON ARCHIVE_CONF.CONF_SOURCE_TAB IS '归档配置源库表';
COMMENT ON COLUMN ON ARCHIVE_CONF.CONF_TARGET_TAB IS '归档配置目标库表';
COMMENT ON COLUMN ON ARCHIVE_CONF.CONF_WHERE IS '归档SQL条件';
COMMENT ON COLUMN ON ARCHIVE_CONF.CONF_MODE IS '归档模式';
COMMENT ON COLUMN ON ARCHIVE_CONF.CONF_PK IS '归档主键列';
COMMENT ON COLUMN ON ARCHIVE_CONF.CURRENT_BATCH_NO IS '归档任务当前批次号';
COMMENT ON COLUMN ON ARCHIVE_CONF.TOTAL_SIZE IS '归档任务当前批次号数据总量';
COMMENT ON COLUMN ON ARCHIVE_CONF.PAGE_SIZE IS '归档分批数量';
COMMENT ON COLUMN ON ARCHIVE_CONF.CONF_PRIORITY IS '归档优先级';
COMMENT ON COLUMN ON ARCHIVE_CONF.CONF_CHECK_NUMS IS '检查次数';
COMMENT ON COLUMN ON ARCHIVE_CONF.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN ON ARCHIVE_CONF.UPDATE_TIME IS '更新时间';
COMMENT ON COLUMN ON ARCHIVE_CONF.IF_VALID IS '是否生效1-启用，0-停用';
COMMENT ON COLUMN ON ARCHIVE_CONF.EXT1 IS '扩展字段1';
COMMENT ON COLUMN ON ARCHIVE_CONF.EXT2 IS '扩展字段2';

/*
    PREPARE("PREPARE", "归档配置准备完成"),
    CHECKING("CHECKING", "归档配置校验中"),
    CHECKED_SUCCESS("CHECKED_SUCCESS", "归档配置校验完成"),
    CHECKED_FAILED("CHECKED_FAILED", "归档配置校验失败"),
    MIGRATING("MIGRATING", "归档任务数搬运中"),
    MIGRATED("MIGRATED", "归档任务数搬运完成"),
    VERIFYING("VERIFYING", "归档任务数据校对中"),
    VERIFIED("VERIFIED", "归档任务数据校对完成"),
    DELETE("DELETE", "归档任务源表数据删除中"),
    SUCCESS("SUCCESS", "归档任务全部完成"),
    ERROR("ERROR", "归档任务出错");
 */
CREATE TABLE ARCHIVE_CONF_PARAM
(
    ID NUMBER PRIMARY KEY,
    CONF_ID NUMBER ,
    PARAM_PK VARCHAR2(100),
    PARAM_NAME VARCHAR2(100),
    PARAM_TYPE VARCHAR2(100),
    PARAM_CONVERT NUMBER,
    PARAM_VALUE VARCHAR2(100),
    PARAM_EXT_VAL VARCHAR2(100),
    PARAM_DESC VARCHAR2(100),
    PARAM_ORDER NUMBER,
    IF_VALID NUMBER(1) DEFAULT 1,
    EXT1 VARCHAR2(256),
    EXT2 VARCHAR2(256)
);

COMMENT ON TABLE  ARCHIVE_CONF_PARAM IS '归档任务参数表';

COMMENT ON COLUMN ON ARCHIVE_CONF_PARAM.ID IS '主键ID';
COMMENT ON COLUMN ON ARCHIVE_CONF_PARAM.CONF_ID IS '归档主键ID';
COMMENT ON COLUMN ON ARCHIVE_CONF_PARAM.PARAM_PK IS '主键列名';
COMMENT ON COLUMN ON ARCHIVE_CONF_PARAM.PARAM_NAME IS '归档参数名';
COMMENT ON COLUMN ON ARCHIVE_CONF_PARAM.PARAM_TYPE IS '归档参数类型';
COMMENT ON COLUMN ON ARCHIVE_CONF_PARAM.PARAM_CONVERT IS '归档参数转换';
COMMENT ON COLUMN ON ARCHIVE_CONF_PARAM.PARAM_VALUE IS '归档参数值';
COMMENT ON COLUMN ON ARCHIVE_CONF_PARAM.PARAM_EXT_VAL IS '归档扩展值';
COMMENT ON COLUMN ON ARCHIVE_CONF_PARAM.PARAM_DESC IS '归档参数描述';
COMMENT ON COLUMN ON ARCHIVE_CONF_PARAM.PARAM_ORDER IS '归档序号';
COMMENT ON COLUMN ON ARCHIVE_CONF_PARAM.IF_VALID IS '是否生效1-启用，0-停用';
COMMENT ON COLUMN ON ARCHIVE_CONF_PARAM.EXT1 IS '扩展字段1';
COMMENT ON COLUMN ON ARCHIVE_CONF_PARAM.EXT2 IS '扩展字段2';

CREATE TABLE ARCHIVE_CONF_DETAIL_TASK
(
    ID NUMBER PRIMARY KEY,
    CONF_ID NUMBER ,
    CURRENT_BATCH_NO VARCHAR2(100),
    TASK_ORDER NUMBER ,
    TASK_SOURCE_TAB VARCHAR2(40),
    TASK_TARGET_TAB VARCHAR2(40),
    TASK_SQL VARCHAR2(2000),
    EXPECT_SIZE NUMBER,
    ACTUAL_SIZE NUMBER,
    TASK_STATUS VARCHAR2(100),  --01-PREPARE/ 02-TRANSFER /03-VERIFY /04-DELETE /05-COMPLETED /06-ERROR
    TASK_START DATE,
    TASK_END DATE,
    VERIFY_SIZE NUMBER,
    VERIFY_START DATE,
    VERIFY_END DATE,
    DELETE_SIZE NUMBER,
    DELETE_START DATE,
    DELETE_END DATE,
    EXT1 VARCHAR2(256),
    EXT2 VARCHAR2(256)
);

COMMENT ON TABLE  ARCHIVE_CONF_DETAIL_TASK IS '归档任务执行表';

COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.ID IS '主键ID';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.CONF_ID IS '配置ID';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.CURRENT_BATCH_NO IS '任务当前批次号';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.TASK_ORDER IS '任务序号';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.TASK_SOURCE_TAB IS '任务源库表';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.TASK_TARGET_TAB IS '任务目标表';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.TASK_SQL IS '执行SQL的where开头的SQL';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.EXPECT_SIZE IS '任务预期归档数据记录数';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.ACTUAL_SIZE IS '任务实际归档数据记录数';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.TASK_STATUS IS '任务执行阶段';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.TASK_START IS '任务归档开始时间';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.TASK_END IS '任务归档结束时间';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.VERIFY_SIZE IS '任务校对数量';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.VERIFY_START IS '任务校对开始时间';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.VERIFY_END IS '任务校对结束时间';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.DELETE_SIZE IS '任务删除源表数据量';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.DELETE_START IS '任务删除源表数据开始时间';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.DELETE_END IS '任务删除源表数据结束时间';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.EXT1 IS '扩展字段1';
COMMENT ON COLUMN ON ARCHIVE_CONF_DETAIL_TASK.EXT2 IS '扩展字段2';


CREATE TABLE ARCHIVE_CONF_TASK_LOG
(
    ID NUMBER PRIMARY KEY,
    CONF_ID NUMBER ,
    TASK_ID NUMBER ,
    CURRENT_BATCH_NO VARCHAR2(100),
    TASK_PHASE VARCHAR(20) , --CHECK ARCHIVE-成功 VERIFY-失败 DELETE
    EXEC_RESULT VARCHAR(2) , --S-成功 E-失败
    CREATE_TIME DATE ,
    ERROR_INFO VARCHAR2(4000),
    EXT1 VARCHAR2(256),
    EXT2 VARCHAR2(256)
);

COMMENT ON TABLE  ARCHIVE_CONF_TASK_LOG IS '归档任务日志表';
COMMENT ON COLUMN ON ARCHIVE_CONF_TASK_LOG.ID IS '主键ID';
COMMENT ON COLUMN ON ARCHIVE_CONF_TASK_LOG.CONF_ID IS '归档配置ID';
COMMENT ON COLUMN ON ARCHIVE_CONF_TASK_LOG.TASK_ID IS '归档任务ID';
COMMENT ON COLUMN ON ARCHIVE_CONF_TASK_LOG.CURRENT_BATCH_NO IS '归档任务批次号';
COMMENT ON COLUMN ON ARCHIVE_CONF_TASK_LOG.TASK_PHASE IS '归档任务阶段';
COMMENT ON COLUMN ON ARCHIVE_CONF_TASK_LOG.EXEC_RESULT IS '执行结果';
COMMENT ON COLUMN ON ARCHIVE_CONF_TASK_LOG.CREATE_TIME IS '创建世界';
COMMENT ON COLUMN ON ARCHIVE_CONF_TASK_LOG.ERROR_INFO IS '错误日志';
COMMENT ON COLUMN ON ARCHIVE_CONF_TASK_LOG.EXT1 IS '扩展字段1';
COMMENT ON COLUMN ON ARCHIVE_CONF_TASK_LOG.EXT2 IS '扩展字段2';



CREATE SEQUENCE SEQ_ARC_CONF_ID MINVALUE 1 MAXVALUE 999999 START WITH 1 NOCACHE ;

CREATE SEQUENCE SEQ_ARC_CONF_PARAM_ID MINVALUE 1 MAXVALUE 999999 START WITH 1 NOCACHE ;

CREATE SEQUENCE SEQ_ARC_CONF_TASK_ID MINVALUE 1 MAXVALUE 999999 START WITH 1 NOCACHE ;

CREATE SEQUENCE SEQ_ARC_CONF_TASK_LOG_ID MINVALUE 1 MAXVALUE 999999999 START WITH 1 NOCACHE ;



--DEMO
INSERT INTO BVIS.ARCHIVE_CONF (ID, CONF_NAME, CONF_DESC, CONF_STATUS, CONF_SOURCE_TAB, CONF_TARGET_TAB, CONF_WHERE, CONF_MODE, CONF_PK, CURRENT_BATCH_NO, TOTAL_SIZE, PAGE_SIZE, CONF_PRIORITY, CONF_CHECK_NUMS, CONF_DEL_CHECK, CREATE_TIME, UPDATE_TIME, IF_VALID, EXT1, EXT2) VALUES (1, 'archive_test', '归档测试', 'PREPARE', 'archive_test', 'archive_test_bak', 'where id > :idstart and id < :idend and  create_time > :startdate and  create_time > :enddate and  conf_status =''1'' order by id ;', 'PK_NUM_MODE', 'id', null, null, 10, 5, null, null, SYSDATE, null, 1, null, null);