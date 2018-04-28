package com.gzsolartech.schedule.quartz.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.bpmportal.service.SynchronizedDataService;
import com.gzsolartech.bpmportal.util.email.EmailNotificationUtil;
import com.gzsolartech.schedule.service.HR19Service;
import com.gzsolartech.smartforms.dao.GenericDao;
import com.gzsolartech.smartforms.exceptions.SmartformsException;

@Component
public class HR19ServiceTask extends BaseTask {
	/**
	 * 每个月1号定时发送一次邮件
	 */
	@Autowired
	protected GenericDao gdao;
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(HR19ServiceTask.class);
	
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext = applicationContext;
	}
	@Override
	public void run(String jobId) {
		HR19Service hr19  = (HR19Service) applicationContext
				.getBean("HR19Service");
			
		hr19.sendEmailService();
		
	}

}
