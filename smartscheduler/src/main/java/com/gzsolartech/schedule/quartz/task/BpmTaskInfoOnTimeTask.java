package com.gzsolartech.schedule.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.schedule.service.BpmTaskInfoOnTimeService;

/**
 * 
 * @ClassName: BpmTaskInfoOntimeTask
 * @Description: 邮件定时通知
 * @author wwd
 * @date 2017年12月11日 上午11:56:41
 *
 */
@Component
public class BpmTaskInfoOnTimeTask extends BaseTask {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(BpmTaskInfoOnTimeTask.class);
	private static final long serialVersionUID = 1L;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext = applicationContext;
	}

	@Override
	public void run(String jobId) {
		LOGGER.debug("邮件定时通知调度任务执行开始");
		BpmTaskInfoOnTimeService bpmTaskInfoOnTimeService = (BpmTaskInfoOnTimeService) applicationContext
				.getBean("bpmTaskInfoOnTimeService");
		try {
			bpmTaskInfoOnTimeService.execute();
		} catch (Exception e) {
			LOGGER.error("邮件定时通知调度任务执行异常", e);
		}
		LOGGER.debug("邮件定时通知调度任务执行结束");
	}
}
