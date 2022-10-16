package com.applory.pictureserver.config;

import com.applory.pictureserver.quartz.LockAdminUserJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Configuration
public class QuartzSchedulerConfiguration {

    private final Logger logger = LoggerFactory.getLogger(QuartzSchedulerConfiguration.class);

    private final SchedulerFactoryBean schedulerFactory;

    public QuartzSchedulerConfiguration(SchedulerFactoryBean schedulerFactory) {
        this.schedulerFactory = schedulerFactory;
    }

//    @PostConstruct
//    private void run() {
//        logger.info("QUARTZ POST CONSTRUCT");
//        JobDataMap map = new JobDataMap(Collections.singletonMap("1", "1"));
//        JobDetail jobDetail = JobBuilder.newJob(LockAdminUserJob.class)
//            .withIdentity("lock", "admin-user")
//            .usingJobData(map)
//            .build();
//
//        CronTrigger trigger = TriggerBuilder.newTrigger()
//            .withIdentity("trigger", "trigger-group")
//            .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
//            .build();
//        try {
//            schedulerFactory.getObject().scheduleJob(jobDetail, trigger);
//        } catch (SchedulerException e) {
//            logger.error(e.getMessage());
//        }
//
//    }

}
