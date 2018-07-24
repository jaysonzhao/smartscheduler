package com.gzsolartech.schedule.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gzsolartech.bpmportal.entity.BpmActivityCostTime;
import com.gzsolartech.bpmportal.service.KronosService;
import com.gzsolartech.smartforms.service.BaseDataService;

@Service
public class BpmActivityCostTimeService  extends BaseDataService{
	private static final Logger LOG = LoggerFactory
			.getLogger(BpmActivityCostTimeService.class);
	
	
	
	@Autowired
	private KronosService ronosService;
	
	public void save (BpmActivityCostTime cost){
		if(cost!=null){
			if(StringUtils.isBlank(cost.getCostId())){
				cost.setCostId("costid:" + UUID.randomUUID().toString());
			}
			gdao.saveOrUpdate(cost);
		}
	}
	
	public void saveAll (List<BpmActivityCostTime> costList){
		for(BpmActivityCostTime cost :costList){
			if(cost!=null){
				if(StringUtils.isBlank(cost.getCostId())){
					cost.setCostId("costid:" + UUID.randomUUID().toString());
				}
			}
		}
		gdao.saveOrUpdate(costList);
	}
	
	public void update (BpmActivityCostTime cost){
		gdao.update(cost);
	}
	
	public void updateAll (List<BpmActivityCostTime> costList){
		gdao.update(costList);
	}
	
	/**
	 * 批量调取SAP考勤接口获取审批时长
	 * @param infos
	 * @return
	 */
	public BpmActivityCostTime saveCountCostTime(BpmActivityCostTime info){
		//任务到达时间（取自BPM，加8小时转为北京时间，已经转化）
		Calendar startCal = Calendar.getInstance();  
		startCal.setTime((Date) info.getCreateTime());
//					startCal.add(Calendar.HOUR, 8);
		//任务提交时间
		Calendar endCal = Calendar.getInstance();  
		endCal.setTime((Date) info.getSubmitTime());
		//开始时间(任务到达时间)
		String startDate  = startCal.get(Calendar.YEAR)+"-"+
							this.addZero(startCal.get(Calendar.MONTH)+1,2)+"-"+
							this.addZero(startCal.get(Calendar.DATE),2);
		String startTime = 	this.addZero(startCal.get(Calendar.HOUR_OF_DAY),2)+":"+
							this.addZero(startCal.get(Calendar.MINUTE),2);
							//不支持秒
							//this.addZero(startCal.get(Calendar.MILLISECOND), 3);
//										startCal.get(Calendar.SECOND);
		//结束时间(任务提交时间)
		String endDate  = 	endCal.get(Calendar.YEAR)+"-"+
							this.addZero(endCal.get(Calendar.MONTH)+1,2)+"-"+
							this.addZero(endCal.get(Calendar.DATE),2);
		String endTime = 	this.addZero(endCal.get(Calendar.HOUR_OF_DAY),2)+":"+
							this.addZero(endCal.get(Calendar.MINUTE),2);
							//不支持秒
							//this.addZero(endCal.get(Calendar.MILLISECOND), 3);
//										endCal.get(Calendar.SECOND); 
		//通过调用考勤接口的排班接口得出任务处理时长
		String consume = "";
		String errorConsume = "";
		try {
			System.out.println("开始查询");
			JSONObject jo =ronosService.checkDataRound(info.getTaskOwner(), "123",
					startDate, startTime, endDate, endTime);
			//如果查询成功，则取值
			if("Success".equals(String.valueOf(jo.get("Status")))){
				JSONObject request = jo.getJSONObject("CNLeaveRequest");
				//获取任务处理时长(小时)
				consume = String.valueOf(request.get("AmountInTime"));
				info.setIsCheckSuccess("Y");
				System.out.println(startDate+"  "+startTime +"----" +endDate+"   "+endTime+"---"+"cost:"+consume);
			}else{
				Object errorObject = jo.get("Error");
				System.out.println(errorObject.getClass());
				
				JSONObject error = jo.getJSONObject("Error");
				JSONObject detailErrors = error.getJSONObject("DetailErrors");
				if(JSONArray.class.equals(detailErrors.get("Error").getClass())){
					JSONArray dError = detailErrors.getJSONArray("Error");
					JSONObject  dJSON = dError.getJSONObject(0);
					String Message = String.valueOf(dJSON.get("Message"));
					errorConsume = "调用考勤接口出错【"+Message+"】";
				}else{
					JSONObject dError = detailErrors.getJSONObject("Error");
					JSONObject  dJSON = dError;
					String Message = String.valueOf(dJSON.get("Message"));
					errorConsume = "调用考勤接口出错【"+Message+"】";
					info.setMsg(errorConsume);
					info.setIsCheckSuccess("N");

				}
			}
		} catch (Exception e) {
			errorConsume = "调用考勤接口出错";
			e.printStackTrace();
			LOG.error("任务处理时长计算失败！", e);
		}
		System.out.println(errorConsume);
		//环节处理时长
		info.setSapCostTime(consume);
		//查询数据库是否存在，存在则删除
		String isExists = "from BpmActivityCostTime where taskId  = ? ";
		List<BpmActivityCostTime> elist = gdao.queryHQL(isExists, info.getTaskId());
		try {
			if(elist.size()>0){
				gdao.delete(elist);
				gdao.getSession().flush();
			}
			save(info);
			
		} catch (Exception e) {
			for(BpmActivityCostTime a :elist){
				System.out.println("old+"+a.getCostId()+"===="+a.getTaskId());
			}
			System.out.println(info.getCostId());
			System.out.println(info.getTaskId());
			e.printStackTrace();
			
			// TODO: handle exception
		}
		return info;
	}
	/**
	 * 批量调取SAP考勤接口获取审批时长
	 * @param infos
	 * @return
	 */
	public List<BpmActivityCostTime> countCostTimeList(List<BpmActivityCostTime> infos){
		//遍历获取审批时长
		int now = 1;
		for(BpmActivityCostTime info :infos){
			//任务到达时间（取自BPM，加8小时转为北京时间，已经转化）
			Calendar startCal = Calendar.getInstance();  
			startCal.setTime((Date) info.getCreateTime());
//					startCal.add(Calendar.HOUR, 8);
			//任务提交时间
			Calendar endCal = Calendar.getInstance();  
			endCal.setTime((Date) info.getSubmitTime());
			//开始时间(任务到达时间)
			String startDate  = startCal.get(Calendar.YEAR)+"-"+
					this.addZero(startCal.get(Calendar.MONTH)+1,2)+"-"+
					this.addZero(startCal.get(Calendar.DATE),2);
			String startTime = 	this.addZero(startCal.get(Calendar.HOUR_OF_DAY),2)+":"+
					this.addZero(startCal.get(Calendar.MINUTE),2);
			//不支持秒
			//this.addZero(startCal.get(Calendar.MILLISECOND), 3);
//										startCal.get(Calendar.SECOND);
			//结束时间(任务提交时间)
			String endDate  = 	endCal.get(Calendar.YEAR)+"-"+
					this.addZero(endCal.get(Calendar.MONTH)+1,2)+"-"+
					this.addZero(endCal.get(Calendar.DATE),2);
			String endTime = 	this.addZero(endCal.get(Calendar.HOUR_OF_DAY),2)+":"+
					this.addZero(endCal.get(Calendar.MINUTE),2);
			//不支持秒
			//this.addZero(endCal.get(Calendar.MILLISECOND), 3);
//										endCal.get(Calendar.SECOND); 
			//通过调用考勤接口的排班接口得出任务处理时长
			String consume = "";
			try {
				System.out.println("开始查询"+now+"行");
				JSONObject jo =ronosService.checkDataRound(info.getTaskOwner(), "123",
						startDate, startTime, endDate, endTime);
				//如果查询成功，则取值
				if("Success".equals(String.valueOf(jo.get("Status")))){
					JSONObject request = jo.getJSONObject("CNLeaveRequest");
					//获取任务处理时长(小时)
					consume = String.valueOf(request.get("AmountInTime"));
					System.out.println("第"+now+"数据  "+startDate+"  "+startTime +"----" +endDate+"   "+endTime+"---"+"cost:"+consume);
				}else{
					Object errorObject = jo.get("Error");
					System.out.println(errorObject.getClass());
					
					JSONObject error = jo.getJSONObject("Error");
					JSONObject detailErrors = error.getJSONObject("DetailErrors");
					JSONArray dError = detailErrors.getJSONArray("Error");
					JSONObject  dJSON = dError.getJSONObject(0);
					String Message = String.valueOf(dJSON.get("Message"));
					consume = "调用考勤接口出错【"+Message+"】";
				}
			} catch (Exception e) {
				consume = "调用考勤接口出错";
				e.printStackTrace();
				LOG.error("任务处理时长计算失败！", e);
			}
			
			//环节处理时长
			info.setSapCostTime(consume);
			save(info);
			now++;
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
	
	/*
	 * 找出所有环节审批信息
	 */
	public List<BpmActivityCostTime> listAll(){
		String hql ="from BpmActivityCostTime";
		List<BpmActivityCostTime> costList = gdao.queryHQL(hql);
		return costList;
	}
	
	/*
	 * 找出不存在审批记录表及任务更新时间为当天的任务
	 */
	public List<Map<String, Object>> listNeedUpdate(){
		String today = "";
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		today =  sdf.format(d);
		
		String sql ="select  tt.CREATE_TIME ,tt.DOCUMENT_ID,"
				+ "tt.INSTANCE_ID,tt.SUBMIT_TIME ,tt.TASK_ID,tt.TASK_NAME,'' SAP_COST_TIME , "
				+ "(select JOB_NUMBER from org_employee where emp_num = tt.ASSIGNED_TO) TASK_OWNER ,"
				+ "tt.BPD_ID,tt.NODE_ID from "
				+ "(  select * from  BPM_ACTIVITY_COST_TIME_VIEW tt where  not exists ("
				+ " select task_id from  BPM_ACTIVITY_COST_TIME where  task_id =  tt.task_id ) union  "
				+ " select * from  BPM_ACTIVITY_COST_TIME_VIEW tt2 where tt2.create_time > to_date('"+today+" 00:00:00','yyyy-MM-dd hh24:mi:ss') "
				+ " ) tt ";
		List<Map<String,Object>> costList = gdao.executeJDBCSqlQuery(sql);
		return costList;
	}
	
	/**
	 * 找出不存在审批记录表及任务更新时间为当天的任务
	 * @return
	 */
	public List<BpmActivityCostTime> listNeedUpdateObejct(){
		List<Map<String, Object>> lists = listNeedUpdate();
		List<BpmActivityCostTime> listObjects = new ArrayList<BpmActivityCostTime>(); 
		for(Map<String, Object> list :lists){
			String CREATE_TIME = String.valueOf(list.get("CREATE_TIME"));
			String SUBMIT_TIME = String.valueOf(list.get("SUBMIT_TIME"));
			
			BpmActivityCostTime temp = new BpmActivityCostTime();
			temp.setBpdId(String.valueOf(list.get("BPD_ID")));
			temp.setCreateTime(toTimestamp(CREATE_TIME));
			temp.setDocumentId(String.valueOf(list.get("DOCUMENT_ID")));
			temp.setInstanceId(String.valueOf(list.get("INSTANCE_ID")));
			temp.setIsCheckSuccess("Y");
			temp.setMsg("");
			temp.setNodeId(String.valueOf(list.get("NODE_ID")));
			temp.setSubmitTime(toTimestamp(CREATE_TIME));
			temp.setTaskId(String.valueOf(list.get("TASK_ID")));
			temp.setTaskName(String.valueOf(list.get("TASK_NAME")));
			temp.setTaskOwner(String.valueOf(list.get("TASK_OWNER")));
			listObjects.add(temp);
		}
		return listObjects;
	}
	
	public Timestamp toTimestamp(String time){
		Timestamp ts = null;  
        try {  
            ts = Timestamp.valueOf(time);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }
		return ts;  
	}
	
	/**
	 * 获取审批时长为空的数据
	 * @return
	 */
	public List<BpmActivityCostTime> getCostTimeEmpty(){
		String hql = "from BpmActivityCostTime where sapCostTime is null";
		List<BpmActivityCostTime> infos = gdao.queryHQL(hql);
		return infos;
		
	}
}
