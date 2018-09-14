package com.gzsolartech.schedule.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.schedule.service.PU0002Service;

/**
 * 同步PU0002物料组数据
 * @author 69990013
 *
 */
@Component
public class PU0002AsyncDataTask extends BaseTask {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PU0002AsyncDataTask.class);
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
	}

	@Override
	public void run(String jobId) {
		PU0002Service pu0002Service = applicationContext.getBean(PU0002Service.class);
		pu0002Service.asyncMaterielGroup();
	}
	
	

}
