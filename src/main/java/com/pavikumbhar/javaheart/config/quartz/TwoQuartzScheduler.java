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
@ConditionalOnProperty(prefix = "quartz.two.scheduler", name = "enabled", matchIfMissing = true)
public class TwoQuartzScheduler extends QuartzScheduler {

	private static final String TWO_TRIGGER = "TWO_TRIGGER";
	private static final String TWO_JOB_DETAIL = "TWO_JOB_DETAIL";

	@PostConstruct
	public void init() {
		log.info("Loading {} Scheduler...",TwoQuartzScheduler.class.getSimpleName());
	}

	@Bean
	public SchedulerFactoryBean twoScheduler() {
		return createScheduler(TwoQuartzScheduler.class.getSimpleName(), twoCronTrigger().getObject());
	}

	@Bean
	public JobDetailFactoryBean twoQuartzJob() {
		return createJobDetail(TWO_JOB_DETAIL, JobTwo.class);
	}

	@Bean
	public CronTriggerFactoryBean twoCronTrigger() {
		return createCronTrigger(twoQuartzJob().getObject(), TWO_TRIGGER,getProperty("quartz.two.scheduler.cron.expression"));
	}

}
