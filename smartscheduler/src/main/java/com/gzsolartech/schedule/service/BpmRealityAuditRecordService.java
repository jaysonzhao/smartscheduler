package com.gzsolartech.schedule.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gzsolartech.bpmportal.service.AACEmployeeService;
import com.gzsolartech.bpmportal.util.email.EmailNotificationUtil;
import com.gzsolartech.smartforms.constant.SysConfigurationTypeName;
import com.gzsolartech.smartforms.entity.OrgEmployee;
import com.gzsolartech.smartforms.entity.bpm.BpmAuditRecord;
import com.gzsolartech.smartforms.service.BaseDataService;
import com.gzsolartech.smartforms.service.SysConfigurationService;
import com.gzsolartech.smartforms.service.bpm.BpmTaskInfoService;
import com.gzsolartech.smartforms.utils.MailUtil;
@Service
public class BpmRealityAuditRecordService extends BaseDataService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(BpmRealityAuditRecordService.class);
	@Autowired
	private AACEmployeeService aacEmployeeService;
	 
	public List<Map<String, Object>> getID(){
		String queryField = "select r.RECORD_ID,r.INSTANCE_ID,r.task_node_id " + 
				"from bpm_reality_audit_record r";
		List<Map<String, Object>> data = gdao.executeJDBCSqlQuery(queryField);
		return data;
	}
	public int updateByID(String id,String Time){
		String queryField = "update bpm_reality_audit_record set TAKE_TIME = "+Time+" where "
				+ "RECORD_ID = '"+id+"'";
		int data = gdao.executeJDBCSql(queryField);
		gdao.commit();
		return data;
	}
	/**
	 * 通过流程实例ID和流程环节ID获取环节处理信息
	 * @param nodeId 环节ID
	 * @param instanceId 流程实例ID
	 * @author Lolipop
	 * @return
	 */
	public List<Map<String,Object>> getNodeInfoById(String nodeId,String instanceId){
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		//根据节点Id和实例Id，按任务到达时间排序，将所查节点的审批信息找出
		String nodeInfoHQL = "from BpmAuditRecord where taskNodeId = ? and  instanceId = ? order by arrivalTime asc";
		List<BpmAuditRecord> records = gdao.queryHQL(nodeInfoHQL,nodeId,instanceId);
		for(BpmAuditRecord record : records){
			Map<String,Object> info = new HashMap<String, Object>();
			//获取环节处理人
			OrgEmployee emps= getUser(record.getActionUserId());
			String empNickName = "";
			if(emps!=null){
				empNickName = emps.getNickName();
			}
			
			//环节名称
			info.put("node",record.getTaskName());
			//环节处理人
			info.put("handler",empNickName);
			//环节到达时间（取自BPM，加8小时转为北京时间）
			info.put("arrivalTime", toBeijingTime(record.getArrivalTime()));
			//环节处理时间
			info.put("submitTime", record.getSubmitTime());
			//环节处理动作
			info.put("handleAction", record.getResultType());
			//环节办理意见
			info.put("note", record.getNote());
			//人员jobNumber，用于考勤接口查请假天数
			info.put("jobNumber", emps.getJobNumber());
			
			datas.add(info);
		}
		return datas;
	}
	
	/**
	 * 获取用户对象
	 * 
	 * @author sujialin
	 * @param uid
	 *            用户UID
	 * @return
	 */
	public OrgEmployee getUser(String uid) {
		if (uid == null) {
			return null;
		}
		OrgEmployee emp = gdao.findById(OrgEmployee.class, uid);
		if (emp == null) {
			LOGGER.warn("找不到用户，用户ID：" + uid);
		}
		return emp;

	}
	/**
	 * 将BPM时间转化为北京时间
	 * @param time
	 * @return
	 */
	private Timestamp toBeijingTime(Timestamp  time){
		Calendar cc = Calendar.getInstance();
		cc.setTime(time);
		//增加8小时
		cc.add(Calendar.HOUR, 8);
		return new Timestamp(cc.getTimeInMillis());
	}
}
