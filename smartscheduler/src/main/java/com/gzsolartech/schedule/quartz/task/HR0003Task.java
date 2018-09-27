package com.gzsolartech.schedule.quartz.task;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.bpmportal.service.KronosService;
import com.gzsolartech.schedule.service.HR0003Service;
/**
 * 转正流程定时回写SAP
 * @author Tsy
   2018年5月9日
   下午6:00:53
 */
@Component
public class HR0003Task extends BaseTask{
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
		HR0003Service HR0003Service=applicationContext
				.getBean(HR0003Service.class);
		KronosService KronosService=applicationContext
				.getBean(KronosService.class);
		List<String> successIds = new ArrayList<String>();
		try {
			//System.out.println("定时任务");
			//获取写入数据
			List<Map<String, String>> list = HR0003Service.getFailDataList("Form_HR0003");
			for (Map<String, String> line : list)
		    {
	        	 JSONObject result = KronosService.qianka(line.get("SIGNDATE")
	        			 , line.get("SIGNTIME")
	        			 , line.get("SIGNTYPE")
	        			 , line.get("OVERRIDETYPENAME")
	        			 , line.get("EMPNUM"));
	        	 if(result.optString("Status", "").equals("Success"))
	        		 successIds.add(line.get("ROW_ID"));
		    }
			if(successIds.size() > 0)
				HR0003Service.updateRowStatus(successIds, "Success");
		} catch (Exception ex) {
			LOG.error("签卡回写Kronos执行异常！", ex);
		}
	}

}
