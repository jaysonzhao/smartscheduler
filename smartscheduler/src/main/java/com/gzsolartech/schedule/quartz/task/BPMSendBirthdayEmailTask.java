package com.gzsolartech.schedule.quartz.task;
/*
 * By DYK
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.schedule.service.BPMSendBirthdayEmailService;
import com.gzsolartech.schedule.service.DocService;

@Component
public class BPMSendBirthdayEmailTask extends BaseTask{
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext=applicationContext;
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BPMSendBirthdayEmailTask.class);
	public void run(String jobId) {
		LOGGER.debug("BPM定时生日邮件任务执行");
		BPMSendBirthdayEmailService BPMsendBirthdayEmailService = (BPMSendBirthdayEmailService) applicationContext.getBean("BPMSendBirthdayEmailService");
		try {
			BPMsendBirthdayEmailService.prepareSendEmail();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("BPM定时生日邮件任务执行异常",e);
		}
		LOGGER.debug("BPM定时生日邮件任务执行结束");
	}
}
