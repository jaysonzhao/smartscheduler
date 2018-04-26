package com.gzsolartech.schedule.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.schedule.service.BPMSendEmailService;

@Component
public class SendEmailTask extends BaseTask{
	private static final long serialVersionUID = 1L;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext=applicationContext;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailTask.class);
	public void run(String jobId) {
		LOGGER.debug("BPM定时邮件任务执行");
		BPMSendEmailService BPMsendEmailService = (BPMSendEmailService) applicationContext.getBean("BPMSendEmailService");
		try {
			BPMsendEmailService.getMyDocument();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("BPM定时邮件任务执行异常",e);
		}
		LOGGER.debug("BPM定时邮件任务执行结束");
	}
}