package com.gzsolartech.schedule.quartz.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.util.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gzsolartech.bpmportal.entity.AacEmployee;
import com.gzsolartech.bpmportal.service.KronosService;
import com.gzsolartech.schedule.service.BpmRealityAuditRecordService;


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

			BpmRealityAuditRecordService bpmRealityAuditRecordService = applicationContext
					.getBean(BpmRealityAuditRecordService.class);
			
			//同步数据到bpm_reality_audit_record表
			bpmRealityAuditRecordService.createBpmInstanceExchangeRecord();
			//同步字段
			List<Map<String,Object>> AllList = bpmRealityAuditRecordService.getIsNull();
			System.out.println("begin...");
			for (Map<String, Object> map : AllList) {
				String RECORD_ID = (String) map.get("RECORD_ID");
				bpmRealityAuditRecordService.updateBpmInstanceExchangeRecord(RECORD_ID, map.get("TASK_ID") + "");
			}
			System.out.println("end...");
			//获取bpm_reality_audit_record
			List<Map<String,Object>> datas = bpmRealityAuditRecordService.getAll();
			//获取提交时间同步到bpm_reality_audit_record表
			for(Map<String,Object> map : datas) {
				String RECORD_ID = map.get("RECORD_ID") + "";
				String TASK_ID = map.get("TASK_ID") + "";
				//同步到达时间
				if(StringUtils.isNotBlank(TASK_ID)) {
					bpmRealityAuditRecordService.serTime(RECORD_ID,TASK_ID);
				}
			}
			
			List<Map<String, Object>> list = bpmRealityAuditRecordService.getID();
			for (Map map : list) {
				String INSTANCE_ID = (String) map.get("INSTANCE_ID");
				String SRC_NODE_ID = (String) map.get("SRC_NODE_ID");
				String RECORD_ID = (String) map.get("RECORD_ID");
				System.out.println("for in loop beging");
				//获取审批时长
				try {
					Map<String,Object> param = getNodeInFoById(SRC_NODE_ID,INSTANCE_ID);
					List<Map<String, Object>> lists = (List<Map<String, Object>>) param.get("rows");
					for(Map<String, Object> m : lists) {
						bpmRealityAuditRecordService.updateByID(RECORD_ID, m.get("consume").toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("for in loop end");
			}
			System.out.println("执行结束");
		} catch (Exception e) {
			e.printStackTrace();
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
		BpmRealityAuditRecordService bpmRealityAuditRecordService = applicationContext
				.getBean(BpmRealityAuditRecordService.class);
		Map<String, Object> data = new HashMap<String, Object>();
		System.out.println("begin getNodeInFoById ");
		List<Map<String, Object>>  infos = bpmRealityAuditRecordService.getNodeInfoById(nodeId, instanceId);
		System.out.println("request  sap begin ");
		//计算审批时长 
		for(Map<String, Object> info :infos){
			Date arrivalTime = (Date) info.get("arrivalTime");
			Date submitTime = (Date) info.get("submitTime");
			//到达时间早于提交时间
			if(arrivalTime.getTime() - submitTime.getTime() > 0){
				info.put("arrivalTime", submitTime);
				info.put("submitTime", arrivalTime);
			}
		}
		infos = countConsume(infos);
		for(Map<String, Object> map : infos) {
			String ErrorCode = (String) map.get("isVP");
			//如果是VP手动计算
			if("Y".equals(ErrorCode)) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒 

				String arrivalTime = df.format(map.get("arrivalTime"));
				String submitTime = df.format(map.get("submitTime"));
				String takeTime = countTime(arrivalTime,submitTime);
				map.put("consume", takeTime);
			}
		}
		System.out.println("request  sap end ");
		System.out.println("end getNodeInFoById ");
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
		KronosService kronosService = applicationContext
				.getBean(KronosService.class);
		BpmRealityAuditRecordService bpmRealityAuditRecordService = applicationContext
				.getBean(BpmRealityAuditRecordService.class);
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
			String ErrorCode = "";
			String isVP = "";
			try {
				System.out.println(" sap checkDataRound begin");
				JSONObject jo =kronosService.CheckDataNoRound(String.valueOf(info.get("jobNumber")), "123",
						startDate, startTime, endDate, endTime);
				System.out.println(" sap checkDataRound end");
				//如果查询成功，则取值
				if("Success".equals(String.valueOf(jo.get("Status")))){
					JSONObject request = jo.getJSONObject("CNLeaveRequest");
					//获取任务处理时长(小时)
					consume = String.valueOf(request.get("AmountInTime"));
				}else{
					JSONObject error = jo.getJSONObject("Error");
					//调用失败
					if("1201".equals(error.get("ErrorCode")+"")) {
						//判断是否vp
						List<AacEmployee> emps= bpmRealityAuditRecordService.getAACUser(String.valueOf(info.get("jobNumber")));
						if(emps.size()<1){
							LOGGER.debug("====找不到工号为===="+info.get("jobNumber")+"====的用户===！！");
							LOGGER.error("====找不到工号为===="+info.get("jobNumber")+"====的用户===！！");
						}
						//非vp找上级继续调用一次
						if(!"10103".equals(emps.get(0).getPositionlevel()) && !"10104".equals(emps.get(0).getPositionlevel())) {
							List<Map<String, Object>> list = bpmRealityAuditRecordService.getLleader(String.valueOf(getDirectLeader(emps.get(0))));
							if(list.size()>0){
								jo =kronosService.checkDataRound(String.valueOf(list.get(0).get("EMP_NUM")), "123",
										startDate, startTime, endDate, endTime);
							}else{
								LOGGER.debug("====该用户没有直接领导===="+info.get("jobNumber"));
								LOGGER.error("====该用户没有直接领导===="+info.get("jobNumber"));
							}
							if("Success".equals(String.valueOf(jo.get("Status")))){
								JSONObject request = jo.getJSONObject("CNLeaveRequest");
								//获取任务处理时长(小时)
								consume = String.valueOf(request.get("AmountInTime"));
							}else {
								consume = "调用考勤接口出错【"+error.toString()+"】";
								ErrorCode = error.get("ErrorCode") + "";
								isVP = "N";
							}
						}else {
							consume = "调用考勤接口出错【"+error.toString()+"】";
							ErrorCode = error.get("ErrorCode") + "";
							isVP = "Y";
						}
					}
				}
			} catch (Exception e) {
				consume = "调用考勤接口出错";
				LOGGER.error("任务处理时长计算失败！", e);
			}
			
			//环节处理时长
			info.put("consume", consume);
			info.put("ErrorCode", ErrorCode);
			info.put("isVP", isVP);
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
	/**
	 * 获取直接领导
	 * @param emp
	 * @return
	 */
	private static String getDirectLeader(AacEmployee emp){
		String directLeader="";
		directLeader = emp.getManagerNum();
		if(StringUtils.isEmpty(directLeader)){
			directLeader = emp.getSeniormanagerNum();
			if(StringUtils.isEmpty(directLeader)){
				directLeader = emp.getDirectorNum();
				if(StringUtils.isEmpty(directLeader)){
					directLeader = emp.getSeniordirectorNum();
					if(StringUtils.isEmpty(directLeader)){
						directLeader = emp.getVpNum();
						if(StringUtils.isEmpty(directLeader)){
							directLeader = emp.getSeniorvpNum();
							if(StringUtils.isEmpty(directLeader)){
								directLeader="";
							}
						}
					}
				}
			}
		}
		return directLeader;
	}
	
	public String countTime(String strDateStart,String strDateEnd) {
		try {  
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
            Date date_start = sdf.parse(strDateStart);  
            Date date_end = sdf.parse(strDateEnd);  
              
            BpmRealityAuditRecordTask app = new BpmRealityAuditRecordTask();  
            Calendar cal_start = Calendar.getInstance();  
            Calendar cal_end = Calendar.getInstance();  
            cal_start.setTime(date_start);  
            cal_end.setTime(date_end);  
            return String.valueOf(app.getWorkingDay(cal_start, cal_end)/(3600*1000*1.0));
        } catch (Exception e) {  
           e.printStackTrace();
        	return "";
        }  
		
    }
	
	/** 
     * 计算2个日期之间的相隔天数 
     *  
     * @param d1 
     * @param d2 
     * @return 
     */  
    public Long getWorkingDay(java.util.Calendar d1, java.util.Calendar d2) {  
          
        int result = -1;  
        long result1 = -1;  
        if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end  
            java.util.Calendar swap = d1;  
            d1 = d2;  
            d2 = swap;  
        }  
        /*//如果两个时间在同一周并且都不是周末日期，则直接返回时间差，增加执行效率   
        if(d1.get(Calendar.YEAR)==d2.get(Calendar.YEAR)   
                && d1.get(Calendar.WEEK_OF_YEAR)==d2.get(Calendar.WEEK_OF_YEAR)   
                && d1.get(Calendar.DAY_OF_WEEK)!=1 && d2.get(Calendar.DAY_OF_WEEK)!=7   
                && d1.get(Calendar.DAY_OF_WEEK)!=1 && d2.get(Calendar.DAY_OF_WEEK)!=7){   
            return new Long(d2.getTimeInMillis()-d1.getTimeInMillis());   
        }*/  
        long charge_start_date = 0;// 开始日期的日期偏移量  
        long charge_end_date = 0;// 结束日期的日期偏移量  
        // 日期不在同一个日期内  
        if (d1.get(Calendar.DAY_OF_WEEK)!= 1 && d1.get(Calendar.DAY_OF_WEEK)!=7) {// 开始日期为星期六和星期日时偏移量为0  
            charge_start_date += (7-d1.get(Calendar.DAY_OF_WEEK))*8*3600000;    
            System.out.println(d1.getTime().getHours()+":"+d1.getTime().getMinutes());  
            if(d1.getTime().getHours()>=8&&d1.getTime().getMinutes()>=30||d1.getTime().getHours()>=9){  
                if(d1.getTime().getHours()==13 && d1.getTime().getMinutes()>=30){  
                    charge_start_date -= (d1.getTime().getHours()-13+3.5)*60000*60;  
                    charge_start_date -= d1.get(Calendar.MINUTE)*60000-30*60000;    
                    charge_start_date -= d1.get(Calendar.SECOND)*1000;    
                    charge_start_date -= d1.get(Calendar.MILLISECOND);   
                }else if(d1.getTime().getHours()>=14){  
                	charge_start_date -= (d1.getTime().getHours()-14+4)*60000*60;  
//                    charge_start_date -= d1.get(Calendar.MINUTE)*60000-30*60000;    
                    charge_start_date -= d1.get(Calendar.SECOND)*1000;    
                    charge_start_date -= d1.get(Calendar.MILLISECOND);   
                }else if(d1.getTime().getHours()>=18){  
                    charge_start_date -= 8*3600000;  
                }else if((d1.getTime().getHours()>=12&&d1.getTime().getHours()<13)||
                		(d1.getTime().getHours()>=13&&d1.getTime().getHours()<14&&d1.getTime().getMinutes()<30)){  
                    charge_start_date -= 3.5*60000*60;  
                }else if(d1.getTime().getHours()<12){  
                    System.out.print("aaa<12");  
                    charge_start_date -= (d1.getTime().getHours()-8)*60000*60;  
                    charge_start_date -= d1.get(Calendar.MINUTE)*60000-30*60000;    
                    charge_start_date -= d1.get(Calendar.SECOND)*1000;    
                    charge_start_date -= d1.get(Calendar.MILLISECOND);   
                }  
                  
            }  
              
        }  
        if (d2.get(Calendar.DAY_OF_WEEK)!= 1 && d2.get(Calendar.DAY_OF_WEEK)!=7) {// 结束日期为星期六和星期日时偏移量为0  
             //只有在结束时间为非周末的时候才计算偏移量    
            charge_end_date += (7-d2.get(Calendar.DAY_OF_WEEK))*8*3600000;    
            if(d2.getTime().getHours()>=8&&d2.getTime().getMinutes()>=30||d2.getTime().getHours()>=9){  
                System.out.print("aaa>13");  
                if(d2.getTime().getHours()==13 && d2.getTime().getMinutes()>=30){  
                    System.out.print("aaa>13");  
                    charge_end_date -= (d2.getTime().getHours()-13+3.5)*60000*60;  
                    charge_end_date -= d2.get(Calendar.MINUTE)*60000-30*60000;    
                    charge_end_date -= d2.get(Calendar.SECOND)*1000;    
                      
                    charge_end_date -= d2.get(Calendar.MILLISECOND);   
                }else if(d2.getTime().getHours()>=14){  
                    charge_end_date -= (d2.getTime().getHours()-14+4)*60000*60;  
//                    charge_end_date -= d2.get(Calendar.MINUTE)*60000-30*60000;    
                    charge_end_date -= d2.get(Calendar.SECOND)*1000;    
                      
                    charge_end_date -= d2.get(Calendar.MILLISECOND);   
                }else if(d2.getTime().getHours()>=18){  
                    charge_end_date -= 8*3600000;  
                }else if((d2.getTime().getHours()>=12&&d2.getTime().getHours()<13)||
                		(d2.getTime().getHours()>=13&&d2.getTime().getHours()<14&&d2.getTime().getMinutes()<30)){  
                    System.out.print("aaa>12");  
                    charge_end_date -= 3.5*60000*60;  
                }else if(d2.getTime().getHours()<12){  
                    charge_end_date -= (d2.getTime().getHours()-8)*60000*60;  
                    charge_end_date -= d2.get(Calendar.MINUTE)*60000-30*60000;    
                    charge_end_date -= d2.get(Calendar.SECOND)*1000;    
                    charge_end_date -= d2.get(Calendar.MILLISECOND);   
                }  
                  
            }  
  
        }  
        result = (getDaysBetween(this.getNextMonday(d1), this.getNextMonday(d2)) / 7)* 5;  
        result1 =  result*8*3600000+charge_start_date-charge_end_date;   
          
        return result1;  
    }  
  
    public String getChineseWeek(Calendar date) {  
        final String dayNames[] = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",  
                "星期六" };  
  
        int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);  
  
        // System.out.println(dayNames[dayOfWeek - 1]);  
        return dayNames[dayOfWeek - 1];  
  
    }
    /** 
     * 获得日期的下一个星期一的日期 
     *  
     * @param date 
     * @return 
     */  
    public Calendar getNextMonday(Calendar date) {  
        Calendar result = null;  
        result = date;  
        do {  
            result = (Calendar) result.clone();  
            result.add(Calendar.DATE, 1);  
        } while (result.get(Calendar.DAY_OF_WEEK) != 2);  
        return result;  
    }  
    public int getDaysBetween(java.util.Calendar d1, java.util.Calendar d2) {  
        if (d1.after(d2)) {   
            java.util.Calendar swap = d1;  
            d1 = d2;  
            d2 = swap;  
        }  
        int days = d2.get(java.util.Calendar.DAY_OF_YEAR)  
                - d1.get(java.util.Calendar.DAY_OF_YEAR);  
        int y2 = d2.get(java.util.Calendar.YEAR);  
        if (d1.get(java.util.Calendar.YEAR) != y2) {  
            d1 = (java.util.Calendar) d1.clone();  
            do {  
                days += d1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);  
                d1.add(java.util.Calendar.YEAR, 1);  
            } while (d1.get(java.util.Calendar.YEAR) != y2);  
        }  
        return days;  
    }  
}
