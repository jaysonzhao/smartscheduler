package com.gzsolartech.schedule.quartz.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gzsolartech.bpmportal.service.KronosService;
import com.gzsolartech.schedule.service.BpmRealityAuditRecordService;
import com.gzsolartech.schedule.service.BpmTaskInfoEmailRemindService;


/**
 * 
 * @ClassName: BpmRealityAuditRecordTask
 * @Description: bpm 获取环节实际处理时长
 *
 */
@Component
public class BpmRealityAuditRecordTask extends BaseTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(BpmRealityAuditRecordTask.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	{

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext = applicationContext;
	}


	@Override
	public void run(String jobId) {
		LOGGER.debug("获取环节实际处理时长执行开始");
		// TODO Auto-generated method stub
		try {

			BpmRealityAuditRecordService bpmRealityAuditRecordService = (BpmRealityAuditRecordService) applicationContext
					.getBean("BpmRealityAuditRecordService");
			List<Map<String, Object>> list = bpmRealityAuditRecordService.getID();
			for (Map map : list) {
				String INSTANCE_ID = (String) map.get("INSTANCE_ID");
				String TASK_NODE_ID = (String) map.get("TASK_NODE_ID");
				String RECORD_ID = (String) map.get("RECORD_ID");
				//获取审批时长
				Map<String,Object> param = getNodeInFoById(TASK_NODE_ID,INSTANCE_ID);
				List<Map<String, Object>> lists = (List<Map<String, Object>>) param.get("rows");
				bpmRealityAuditRecordService.updateByID(RECORD_ID, lists.get(0).get("consume").toString());
			}
		} catch (Exception e) {
			LOGGER.debug("获取环节实际处理时长执行异常", e);
		}
		LOGGER.debug("获取环节实际处理时长执行结束");
	}
	/**
	 * 通过流程实例ID和流程环节ID获取环节处理信息
	 * @param nodeId 环节ID
	 * @param instanceId 流程实例ID
	 * @author Lolipop
	 * @return
	 */
	public Map<String, Object> getNodeInFoById(String nodeId,String instanceId){
		BpmRealityAuditRecordService bpmRealityAuditRecordService = (BpmRealityAuditRecordService) applicationContext
				.getBean("BpmRealityAuditRecordService");
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>>  infos = bpmRealityAuditRecordService.getNodeInfoById(nodeId, instanceId);
		//计算审批时长
		infos = countConsume(infos);
		data.put("total", infos.size());
		data.put("rows", infos);
		return data;
	}
	/**
	 * 计算审批时长
	 * @param infos 审批记录
	 * @return
	 */
	private List<Map<String,Object>> countConsume(List<Map<String,Object>> infos){
		KronosService kronosService = (KronosService) applicationContext
				.getBean("KronosService");
		//遍历获取审批时长
		for(Map<String, Object> info :infos){
			//任务到达时间（取自BPM，加8小时转为北京时间，已经转化）
			Calendar startCal = Calendar.getInstance();  
			startCal.setTime((Date) info.get("arrivalTime"));
//			startCal.add(Calendar.HOUR, 8);
			//任务提交时间
			Calendar endCal = Calendar.getInstance();  
			endCal.setTime((Date) info.get("submitTime"));
			//开始时间(任务到达时间)
			String startDate  = startCal.get(Calendar.YEAR)+"-"+
								this.addZero(startCal.get(Calendar.MONTH)+1,2)+"-"+
								this.addZero(startCal.get(Calendar.DATE),2);
			String startTime = 	this.addZero(startCal.get(Calendar.HOUR_OF_DAY),2)+":"+
								this.addZero(startCal.get(Calendar.MINUTE),2);
								//不支持秒
								//this.addZero(startCal.get(Calendar.MILLISECOND), 3);
//								startCal.get(Calendar.SECOND);
			//结束时间(任务提交时间)
			String endDate  = 	endCal.get(Calendar.YEAR)+"-"+
								this.addZero(endCal.get(Calendar.MONTH)+1,2)+"-"+
								this.addZero(endCal.get(Calendar.DATE),2);
			String endTime = 	this.addZero(endCal.get(Calendar.HOUR_OF_DAY),2)+":"+
								this.addZero(endCal.get(Calendar.MINUTE),2);
								//不支持秒
								//this.addZero(endCal.get(Calendar.MILLISECOND), 3);
//								endCal.get(Calendar.SECOND); 
			//通过调用考勤接口的排班接口得出任务处理时长
			String consume = "";
			try {
				JSONObject jo =kronosService.checkDataRound(String.valueOf(info.get("jobNumber")), "123",
						startDate, startTime, endDate, endTime);
				//如果查询成功，则取值
				if("Success".equals(String.valueOf(jo.get("Status")))){
					JSONObject request = jo.getJSONObject("CNLeaveRequest");
					//获取任务处理时长(小时)
					consume = String.valueOf(request.get("AmountInTime"));
				}else{
					JSONObject error = jo.getJSONObject("Error");
					JSONObject detailErrors = error.getJSONObject("DetailErrors");
					org.json.JSONArray dError = detailErrors.getJSONArray("Error");
					JSONObject  dJSON = dError.getJSONObject(0);
					String Message = String.valueOf(dJSON.get("Message"));
					consume = "调用考勤接口出错【"+Message+"】";
				}
			} catch (Exception e) {
				consume = "调用考勤接口出错";
				LOGGER.error("任务处理时长计算失败！", e);
			}
			
			//环节处理时长
			info.put("consume", consume);
		}
		return infos;
	}
	/**
	 * 为时间不足0
	 * @param num 时间
	 * @param len 时间应有长度
	 * @return
	 */
	private String addZero(int num, int len) {
		StringBuffer buf = new StringBuffer();
		buf.append(num);
		while (buf.length() < len) {
			buf.insert(0, 0);
		}
		return buf.toString();
	}
}
