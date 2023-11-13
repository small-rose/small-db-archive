package com.small.archive.core.check.inteceptors;

import com.small.archive.core.annotation.CustomInterceptors;
import com.small.archive.core.annotation.DefaultInterceptors;
import com.small.archive.core.emuns.ArchiveConfMode;
import com.small.archive.core.emuns.ArchiveConfStatus;
import com.small.archive.core.emuns.ArchiveLogPhase;
import com.small.archive.core.emuns.ArchiveLogStatus;
import com.small.archive.exception.ArchiverCheckException;
import com.small.archive.pojo.ArchiveConf;
import com.small.archive.pojo.ArchiveConfTaskLog;
import com.small.archive.service.ArchiveConfService;
import com.small.archive.service.ArchiveLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ CheckInterceptorChain ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2023/11/13 013 21:53
 * @Version: v1.0
 */

@Slf4j
@Component
public class CheckInterceptorManager {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    @DefaultInterceptors
    private List<CheckInterceptor> defaultInterceptors;
    @Autowired
    @CustomInterceptors
    private List<CheckInterceptor> strategyInterceptors;
    @Autowired
    private ArchiveConfService archiveConfService;
    @Autowired
    private ArchiveLogService archiveLogService;


    @Transactional( propagation = Propagation.REQUIRED, rollbackFor = ArchiverCheckException.class )
    public void applyCheck(ArchiveConf conf) {
        // 在责任链中先执行默认的拦截器
        //DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        //TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        archiveConfService.updateArchiveConfStatus(conf.getId(), ArchiveConfStatus.CHECKING);
        try {
            for (CheckInterceptor interceptor : defaultInterceptors) {
                interceptor.intercept(conf);
            }

            // 再根据策略执行对应的拦截器
            for (CheckInterceptor interceptor : strategyInterceptors) {
                if (interceptor.supports(ArchiveConfMode.getByCode(conf.getConfMode()))) {
                    interceptor.intercept(conf);
                }
            }
            archiveConfService.updateArchiveConfStatus(conf.getId(), ArchiveConfStatus.CHECKED_SUCCESS);
             // 所有拦截器执行完毕，提交事务
            //transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 发生异常，回滚事务
            //transactionManager.rollback(transactionStatus);
            String ex = ExceptionUtils.getStackTrace(e);
            ArchiveConfTaskLog taskLog = new ArchiveConfTaskLog();
            taskLog.setConfId(conf.getId());
            taskLog.setTaskPhase(ArchiveLogPhase.CHECK.getStatus());
            taskLog.setCreateTime(new Date());
            taskLog.setExecResult(ArchiveLogStatus.ERROR.getStatus());
            if (ex.length() > 2000) {
                ex = ex.substring(0, 2000);
            }
            taskLog.setErrorInfo(ex);
            archiveLogService.saveArchiveLog(taskLog);
            archiveConfService.updateArchiveConfStatusFailed(conf.getId(), ArchiveConfStatus.CHECKED_FAILED);


            throw new ArchiverCheckException("配置校验出错: " + ex);
        }finally {
            log.info(">>> archive conf checked over !");
        }


    }
}
