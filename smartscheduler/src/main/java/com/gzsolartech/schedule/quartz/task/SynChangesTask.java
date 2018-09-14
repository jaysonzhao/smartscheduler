package com.gzsolartech.schedule.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import com.gzsolartech.schedule.service.SynChangerService;
@Component
public class SynChangesTask extends BaseTask {
	private static final long serialVersionUID = 1L;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext=applicationContext;
	}
	private static final Logger LOGGER = LoggerFactory.getLogger(SynChangesTask.class);
	@Override
	public void run(String jobId) {
		SynChangerService synChangerService=applicationContext.getBean(SynChangerService.class);
		try {
			LOGGER.error("定时获取SAP异动人员数据开始");
			synChangerService.saveSapChanger();
			LOGGER.error("定时获取SAP异动人员数据结束");
			LOGGER.error("定时获取BPM异动人员数据开始");
			synChangerService.saveBpmChanger();
			LOGGER.error("定时获取BPM异动人员数据结束");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("定时获取异动数据任务执行异常",e);
		}
	}
}
