package com.gzsolartech.schedule.quartz.task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.bpmportal.service.KronosService;
import com.gzsolartech.schedule.service.HR0002Service;
import com.gzsolartech.schedule.service.HR20WriteSapService;
import com.gzsolartech.smartforms.service.bpm.BpmInstanceSyncSignalService;
/**
 * 转正流程定时回写SAP
 * @author Tsy
   2018年5月9日
   下午6:00:53
 */
@Component
public class HR0002ServiceTask extends BaseTask{
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
		HR0002Service HR0002Service=applicationContext
				.getBean(HR0002Service.class);
		KronosService KronosService=applicationContext
				.getBean(KronosService.class);
		try {
			//System.out.println("定时任务");
			//获取写入数据
			List<Map<String, String>> list =	HR0002Service.hqxrsj();
			List<String> successIds = new ArrayList<String>();
			for (Map<String, String> k : list)
			{
				String startDate="";
				String startTime="";
				String endDate="";
				String endTime="";
				startDate =k.get("PERIODSTART").substring(0,10);
				startTime =k.get("PERIODSTART").substring(k.get("PERIODSTART").length()- 5);
				endDate =k.get("PERIODEND").substring(0,10);
				endTime =k.get("PERIODEND").substring(k.get("PERIODEND").length()- 5);
			   
				String temp = KronosService.xiujia(k.get("SAPNO"), k.get("LEAVETYPE"), startDate, startTime,endDate, endTime).toString();
				JSONObject result=JSONObject.fromObject(temp); 
			 
				//写入成功id
				String Status = result.getString("Status");
				if(Status.equals("Success")){
					successIds.add(k.get("DOCUID"));
				}
		    }
			//写入成功改变状态
			 if(successIds.size() > 0)
				 HR0002Service.updateRowStatus(successIds, "SUCCESS");
		} catch (Exception ex) {
			LOG.error("请假调休写入SAP失败！", ex);
		}
	}

}
