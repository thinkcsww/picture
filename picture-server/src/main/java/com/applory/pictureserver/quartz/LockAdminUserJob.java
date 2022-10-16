package com.applory.pictureserver.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockAdminUserJob implements Job {

    private final Logger logger = LoggerFactory.getLogger(LockAdminUserJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("QUARTZ 실행중");
    }
}
