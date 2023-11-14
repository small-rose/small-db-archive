package com.small.archive.core.check.inteceptors;

import com.small.archive.core.annotation.CustomInterceptors;
import com.small.archive.core.annotation.DefaultInterceptors;
import com.small.archive.core.constant.ArchiveConstant;
import com.small.archive.core.context.ArchiveContextHolder;
import com.small.archive.core.emuns.ArchiveJobMode;
import com.small.archive.core.emuns.ArchiveJobPhase;
import com.small.archive.core.emuns.ArchiveJobStatus;
import com.small.archive.core.emuns.ArchiveLogResult;
import com.small.archive.exception.ArchiverCheckException;
import com.small.archive.pojo.ArchiveJobConfig;
import com.small.archive.pojo.ArchiveTaskLog;
import com.small.archive.service.ArchiveJobConfService;
import com.small.archive.service.ArchiveTaskLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Project: small-db-archive
 * @Author: 张小菜
 * @Description: [ CheckInterceptorChain ] 说明： 无
 * @Function: 功能描述： 配置校验管理器，整个校验逻辑入口
 * @Date: 2023/11/13 013 21:53
 * @Version: v1.0
 */

@Slf4j
@Component
public class CheckInterceptorManager {

    //@Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    @DefaultInterceptors
    private List<CheckInterceptor> defaultInterceptors;
    @Autowired
    @CustomInterceptors
    private List<CheckInterceptor> strategyInterceptors;
    @Autowired
    private ArchiveJobConfService archiveJobConfService;
    @Autowired
    private ArchiveTaskLogService archiveTaskLogService;


    @Transactional( propagation = Propagation.REQUIRED, rollbackFor = ArchiverCheckException.class )
    public void applyCheck(ArchiveJobConfig conf) {
        // 在责任链中先执行默认的拦截器
        //DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        //TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        archiveJobConfService.updateArchiveConfStatus(conf, ArchiveJobStatus.CHECKING);
        try {
            for (CheckInterceptor interceptor : defaultInterceptors) {
                interceptor.intercept(conf);
            }

            // 再根据策略执行对应的拦截器
            for (CheckInterceptor interceptor : strategyInterceptors) {
                if (interceptor.supports(ArchiveJobMode.getModeCode(conf.getJobMode()))) {
                    interceptor.intercept(conf);
                }
            }

            Map<String, Object> archiveMap = ArchiveContextHolder.getArchiveMap();
            Long count1 = (Long) archiveMap.get(ArchiveConstant.COUNT_SOURCE);
            conf.setTotalExpectSize(count1);
            if (ArchiveJobMode.ARCHIVE.name().equalsIgnoreCase(conf.getJobMode())){
                //相同SQL已归档数
                Long count2 = (Long) archiveMap.get(ArchiveConstant.COUNT_SOURCE);
                conf.setTotalArchivedSize(count2);
            }
            archiveJobConfService.updateArchiveConfStatus(conf, ArchiveJobStatus.CHECKED_SUCCESS);
             // 所有拦截器执行完毕，提交事务
            //transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 发生异常，回滚事务
            //transactionManager.rollback(transactionStatus);
            String ex = ExceptionUtils.getStackTrace(e);
            ArchiveTaskLog taskLog = new ArchiveTaskLog();
            taskLog.setJobId(conf.getId());
            taskLog.setTaskPhase(ArchiveJobPhase.CHECK.getStatus());
            taskLog.setCreateTime(new Date());
            taskLog.setTaskResult(ArchiveLogResult.ERROR.getStatus());
            if (ex.length() > 2000) {
                ex = ex.substring(0, 2000);
            }
            taskLog.setErrorInfo(ex);
            archiveTaskLogService.saveArchiveLog(taskLog);
            archiveJobConfService.updateArchiveConfStatusFailed(conf, ArchiveJobStatus.CHECKED_FAILED);


            throw new ArchiverCheckException("配置校验出错: " + ex);
        }finally {
            log.info(">>> archive conf checked over !");
        }


    }
}
