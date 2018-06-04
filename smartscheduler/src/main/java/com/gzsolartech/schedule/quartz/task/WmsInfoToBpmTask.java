package com.gzsolartech.schedule.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.schedule.service.WmsInfoToBpm;

@Component
public class WmsInfoToBpmTask  extends BaseTask{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(WmsInfoToBpmTask.class);
	@Override
	public void setApplicationContext(ApplicationContext application) throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext=application;
	}

	@Override
	public void run(String jobId) {
		// TODO Auto-generated method stub
//		WmsInfoToBpm wms = new WmsInfoToBpm();
		WmsInfoToBpm wms = applicationContext.getBean(WmsInfoToBpm.class);
		wms.getInfoFromWms();
	}

}
