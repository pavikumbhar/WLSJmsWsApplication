package com.pavikumbhar.javaheart.config;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author pavikumbhar
 */

@Slf4j
public abstract class QuartzScheduler {
    
    @Autowired
    private Environment environment;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private PlatformTransactionManager transactionManager;
    
    /**
     * 
     * @param key
     * @return
     */
    protected final String getProperty(String key) {
        return environment.getProperty(key);
    }
    
    protected SchedulerFactoryBean createScheduler(String schedulerName, Trigger trigger) {
        SchedulerFactoryBean quartzScheduler = new SchedulerFactoryBean();
        
        /**
         * Naturally, Quartz with the DB requires references to the data source and transaction
         * manager beans
         */
        quartzScheduler.setDataSource(dataSource);
        quartzScheduler.setTransactionManager(transactionManager);
        
        /**
         * This name is persisted as SCHED_NAME in db. for local testing could change to unique name
         * to avoid collision with dev server
         */
        quartzScheduler.setSchedulerName(schedulerName);
        
        /**
         * Will update database cron triggers to what is in this jobs file on each deploy. Replaces
         * all previous trigger and job data that was in the database. YMMV
         */
        quartzScheduler.setOverwriteExistingJobs(true);
        
        // custom job factory of spring with DI support for @Autowired!
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        jobFactory.setIgnoredUnknownProperties("applicationContext");
        quartzScheduler.setJobFactory(jobFactory);
        
        quartzScheduler.setAutoStartup(true);
        quartzScheduler.setOverwriteExistingJobs(true);
        quartzScheduler.setWaitForJobsToCompleteOnShutdown(false);
        
        quartzScheduler.setApplicationContextSchedulerContextKey("applicationContext");
        
        quartzScheduler.setQuartzProperties(quartzProperties());
        
        quartzScheduler.setTriggers(trigger);
        
        return quartzScheduler;
    }
    
    public Properties quartzProperties() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/properties/quartz.properties"));
        Properties properties = null;
        try {
            propertiesFactoryBean.afterPropertiesSet();
            properties = propertiesFactoryBean.getObject();
            
        } catch (IOException e) {
            log.warn("Cannot load quartz.properties.");
        }
        
        return properties;
    }
    
    protected JobDetailFactoryBean createJobDetail(String jobDetail, Class<? extends Job> jobClass) {
        
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(jobClass);
        jobDetailFactory.setDescription(jobClass.getName() + "Invoke  Job service...");
        jobDetailFactory.setDurability(true);
        jobDetailFactory.afterPropertiesSet();
        jobDetailFactory.setName(jobDetail);
        return jobDetailFactory;
    }
    
    protected CronTriggerFactoryBean createCronTrigger(JobDetail job, String cronTriggerName, String cronEpression) {
        CronTriggerFactoryBean cronTrigger = new CronTriggerFactoryBean();
        cronTrigger.setName(cronTriggerName);
        cronTrigger.setJobDetail(job);
        cronTrigger.setStartDelay(0);
        cronTrigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        cronTrigger.setCronExpression(cronEpression);
        return cronTrigger;
    }
    
}
