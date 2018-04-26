package com.gzsolartech.schedule.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.schedule.service.IT35Service;
import com.gzsolartech.smartforms.service.bpm.BpmArchiveStrategyService;



@Component
public class BpmToSccmData extends BaseTask {
	private static final long serialVersionUID = 1L;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext=applicationContext;
	}
	//IT35Service server=(IT35Service)applicationContext.getBean(IT35Service.class);
	private static final Logger LOGGER = LoggerFactory.getLogger(BpmToSccmData.class);
	@Override
	public void run(String jobId) {
		// TODO Auto-generated method stub
		LOGGER.debug("BPM定时获取SCCM的数据");
		IT35Service server = (IT35Service) applicationContext.getBean("IT35Service");
		try {
			server.instComData();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("BPM定时获取SCCM的数据任务执行异常",e);
		}
		LOGGER.debug("BPM定时获取SCCM的数据任务执行结束");
	}
}
