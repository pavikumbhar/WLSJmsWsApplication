package com.pavikumbhar.javaheart.config.quartz;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.pavikumbhar.javaheart.config.QuartzScheduler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "quartz.one.scheduler", name = "enabled", matchIfMissing = true)
public class OneQuartzScheduler extends QuartzScheduler {
    
    private static final String ONE_TRIGGER = "ONE_TRIGGER";
    private static final String ONE_JOB_DETAIL = "ONE_JOB_DETAIL";
    
    @PostConstruct
    public void init() {
        log.info("Loading {} Scheduler...", OneQuartzScheduler.class.getSimpleName());
    }
    
    @Bean
    public SchedulerFactoryBean oneScheduler() {
        return createScheduler(OneQuartzScheduler.class.getSimpleName(), oneCronTrigger().getObject());
    }
    
    @Bean
    public JobDetailFactoryBean oneQuartzJob() {
        return createJobDetail(ONE_JOB_DETAIL, JobOne.class);
    }
    
    @Bean
    public CronTriggerFactoryBean oneCronTrigger() {
        return createCronTrigger(oneQuartzJob().getObject(), ONE_TRIGGER, getProperty("quartz.one.scheduler.cron.expression"));
    }
    
}
