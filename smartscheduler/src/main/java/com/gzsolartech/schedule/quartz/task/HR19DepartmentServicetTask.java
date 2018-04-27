package com.gzsolartech.schedule.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.schedule.service.HR19DepartmentService;
import com.gzsolartech.smartforms.dao.GenericDao;
@Component
public class HR19DepartmentServicetTask extends BaseTask{
	@Autowired
	protected GenericDao gdao;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(HR19DepartmentServicetTask.class);
	private static final long serialVersionUID = 1L;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
		
	}

	@Override
	public void run(String jobId) {
		HR19DepartmentService hr19 = (HR19DepartmentService) 
				applicationContext.getBean("HR19DepartmentService");
		hr19.sendEmailDeparementService();
		
	}
	
}
