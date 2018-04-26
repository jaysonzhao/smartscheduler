package com.gzsolartech.schedule.quartz.task;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.schedule.service.BPMSendEmailService;
import com.gzsolartech.schedule.service.HCMService;


@Component
public class GetHCMTask extends BaseTask{
	private static final long serialVersionUID = 1L;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext=applicationContext;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(GetHCMTask.class);
	public void run(String jobId) {
		Date d = new Date();
		LOGGER.debug(d + "：获取码值任务执行");
		long startTime = System.currentTimeMillis();
		HCMService hcmService = (HCMService) applicationContext.getBean(HCMService.class);
		try {
			hcmService.ZHRPAFM021("");
			hcmService.exec();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(d + "：获取码值任务执行异常",e);
		}
		long endTime = System.currentTimeMillis();
		System.out.println(d + " 获取码值任务耗时：" + (endTime - startTime) + "ms");
		LOGGER.debug(d + "：获取码值任务执行结束，耗时" + (endTime - startTime) + "ms");
	}
}