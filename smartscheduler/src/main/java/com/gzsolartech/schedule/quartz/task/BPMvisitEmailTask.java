package com.gzsolartech.schedule.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.schedule.service.BPMvisitEmailService;
import com.gzsolartech.schedule.service.DocService;

@Component
public class BPMvisitEmailTask extends BaseTask{
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext=applicationContext;
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BPMvisitEmailTask.class);
	public void run(String jobId) {
		LOGGER.debug("BPM访问人员截至时间邮件任务执行");
		BPMvisitEmailService BPMvisitEmailService = (BPMvisitEmailService) applicationContext.getBean("BPMvisitEmailService");
		try {
			BPMvisitEmailService.IT0001sendEmail();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("BPM访问人员截至时间邮件任务执行",e);
		}
		LOGGER.debug("BPM访问人员截至时间邮件任务执行结束");
	}
}
