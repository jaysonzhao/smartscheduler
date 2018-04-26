package com.gzsolartech.schedule.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.bpmportal.service.PU01ScheWrite;
import com.gzsolartech.bpmportal.service.SynchronizedDataService;
import com.gzsolartech.smartforms.exceptions.SmartformsException;
/**
 * @description 调度写入sap 
 * @author hhf
 * @date 2018年4月25日 下午9:42:55
 */
@Component
public class PU01ScheWriteTask extends BaseTask{
	
	private static final long serialVersionUID = 4690002209618844165L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PU01ScheWriteTask.class);

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext=applicationContext;
	}

	@Override
	public void run(String jobId) {
		PU01ScheWrite pu01sw = 
				applicationContext.getBean(PU01ScheWrite.class);
		try {
			pu01sw.execute();
		} catch (SmartformsException e) {
			LOGGER.error("写入订单时出现异常：",e);
			e.printStackTrace();
		}
		
	}
}
