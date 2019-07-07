package com.pavikumbhar.javaheart.config.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
@DisallowConcurrentExecution
@Slf4j
public class JobTwo extends QuartzJobBean {
    
    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {
        log.debug("### Job :  {}  invoked at : {}", JobTwo.class.getSimpleName(), new java.util.Date());
    }
}