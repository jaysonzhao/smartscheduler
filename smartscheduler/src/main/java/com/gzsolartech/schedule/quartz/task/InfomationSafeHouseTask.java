package com.gzsolartech.schedule.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.schedule.service.InfomationSafeHouseService;
import com.gzsolartech.schedule.service.WmsInfoToBpm;

@Component
public class InfomationSafeHouseTask extends BaseTask{

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(InfomationSafeHouseTask.class);
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		
	}

	@Override
	public void run(String jobId) {
		InfomationSafeHouseService info =applicationContext.getBean(InfomationSafeHouseService.class);
		info.excute();
		
		
	}

}
