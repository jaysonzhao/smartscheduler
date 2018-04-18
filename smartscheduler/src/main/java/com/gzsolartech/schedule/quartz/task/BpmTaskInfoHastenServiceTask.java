package com.gzsolartech.schedule.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.schedule.service.BpmTaskInfoHastenService;
/**
 * 
* @ClassName: BpmTaskInfoHastenServiceTask 
* @Description: bpm 待办自动动催办调度任务
* @author wwd 
* @date 2017年8月18日 下午2:09:35 
*
 */
@Component
public class BpmTaskInfoHastenServiceTask extends BaseTask{
	private static final Logger LOGGER = LoggerFactory
			.getLogger(BpmTaskInfoHastenServiceTask.class);
	private static final long serialVersionUID = 1L;
@Override
public void setApplicationContext(ApplicationContext applicationContext)
		throws BeansException {
	// TODO Auto-generated method stub
	this.applicationContext=applicationContext;
}

@Override
public void run(String jobId) {
	LOGGER.debug("bpm 待办自动动催办调度任务执行开始");
	BpmTaskInfoHastenService bpmTaskInfoHastenService = (BpmTaskInfoHastenService) applicationContext
			.getBean("bpmTaskInfoHastenService");
	try {
		bpmTaskInfoHastenService.execute();
	} catch (Exception e) {
		e.printStackTrace();
		LOGGER.error("bpm 待办自动动催办调度任务执行异常",e);
	}
	LOGGER.debug("bpm 待办自动动催办调度任务执行结束");
}
}

