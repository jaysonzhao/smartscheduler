package com.gzsolartech.schedule.quartz.task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.schedule.service.HR20WriteSapService;
import com.gzsolartech.smartforms.service.bpm.BpmInstanceSyncSignalService;
/**
 * 转正流程定时回写SAP
 * @author Tsy
   2018年5月9日
   下午6:00:53
 */
@Component
public class HR20WriteSapTask extends BaseTask{
	private static final Logger LOG = LoggerFactory
			.getLogger(HR20WriteSapTask.class);
	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext=applicationContext;
	}
	@Override
	public void run(String jobId) {
		// TODO Auto-generated method stub
		HR20WriteSapService hr20WriteSapService=applicationContext
				.getBean(HR20WriteSapService.class);
		try {
			//同步BPM实例状态
			LOG.error("转正人员信息回写SAP正在执行...");
			hr20WriteSapService.writeSap();
		} catch (Exception ex) {
			LOG.error("转正人员信息回写SAP执行异常！", ex);
		}
	}

}
