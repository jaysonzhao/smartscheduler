package com.gzsolartech.schedule.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.schedule.service.IT0002Service;

@Component
public class IT0002SendEmailTask extends BaseTask {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(IT0002SendEmailTask.class);
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext=applicationContext;
	}

	@Override
	public void run(String jobId) {
		IT0002Service service = applicationContext.getBean(IT0002Service.class);
		try {
			service.execute();
		} catch (Exception e) {
			LOGGER.error("IT0002发送邮件时出现异常：", e);
			e.printStackTrace();
		}
	}
}
