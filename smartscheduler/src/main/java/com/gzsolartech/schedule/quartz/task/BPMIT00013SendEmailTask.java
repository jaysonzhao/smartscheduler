package com.gzsolartech.schedule.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.schedule.service.BPMSendBirthdayEmailService;
import com.gzsolartech.schedule.service.IT00013SendEmailService;

@Component
public class BPMIT00013SendEmailTask extends BaseTask{

	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext=applicationContext;
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BPMIT00013SendEmailTask.class);
	@Override
	public void run(String jobId) {
		// TODO Auto-generated method stub
		LOGGER.debug("BPM中服务器文件夹权限申请到期定时邮件任务执行");
		IT00013SendEmailService IT00013SendEmailService = (IT00013SendEmailService) applicationContext.getBean("IT00013SendEmailService");
		try {
			//IT00013SendEmailService.prepareSendEmailToAppliant();
			IT00013SendEmailService.prepareSendEmailToManager();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("BPM中服务器文件夹权限申请到期定时邮件任务执行异常",e);
		}
		LOGGER.debug("BPM中服务器文件夹权限申请到期定时邮件任务执行结束");
	}

}
