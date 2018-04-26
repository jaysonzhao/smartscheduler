package com.gzsolartech.schedule.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gzsolartech.bpmportal.entity.AacEmployee;
import com.gzsolartech.bpmportal.service.AACEmployeeService;
import com.gzsolartech.smartforms.entity.OrgEmployee;
import com.gzsolartech.smartforms.service.BaseDataService;
@Service
public class BpmRealityAuditRecordService extends BaseDataService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(BpmRealityAuditRecordService.class);
	@Autowired
	private AACEmployeeService aacEmployeeService;
	 
	public List<Map<String, Object>> getID(){
		String queryField = "select r.RECORD_ID,r.INSTANCE_ID,r.SRC_NODE_ID  " + 
				"from bpm_reality_audit_record r where r.TAKE_TIME is null";
		List<Map<String, Object>> data = gdao.executeJDBCSqlQuery(queryField);
		return data;
	}
	public void updateByID(String id,String Time){
		try {
			String queryField = "update bpm_reality_audit_record set TAKE_TIME = "+Time+" where "
					+ "RECORD_ID = '"+id+"'";
			int data = gdao.executeJDBCSql(queryField);
		System.out.println(data);
		} catch (Exception e) {
			
		}
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
		String sql = "select * from BPM_REALITY_AUDIT_RECORD "
				+ "where src_node_id = '"+nodeId+"' and instance_id = '"+instanceId+"' order by arrival_Time asc";
		List<Map<String,Object>> records = gdao.executeJDBCSqlQuery(sql);
		for(Map<String, Object> record : records){
			Map<String,Object> info = new HashMap<String, Object>();
			//获取环节处理人
			OrgEmployee emps= getUser(record.get("CREATOR") + "");
			String empNickName = "";
			if(emps!=null){
				empNickName = emps.getNickName();
			}
			
			//环节名称
			info.put("node",record.get("TASK_NAME"));
			//环节处理人
			info.put("handler",empNickName);
			//环节到达时间（取自BPM，加8小时转为北京时间）
			info.put("arrivalTime", toBeijingTime((Timestamp) record.get("ARRIVAL_TIME")));
			//环节处理时间
			info.put("submitTime", record.get("CREATE_TIME"));
			//环节处理动作
			info.put("handleAction", record.get("EXCHANGE_TYPE"));
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
	 * 获取用户对象
	 * 
	 * @author sujialin
	 * @param uid
	 *            用户UID
	 * @return
	 */
	public List<AacEmployee> getAACUser(String empNum) {
		String hql = "from AacEmployee t where emp_num= ? ";
		List<AacEmployee> emp = gdao.queryHQL(hql, empNum);
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
	/**
	 * 同步bpm_instance_exchange_record表到BPM_INSTANCE_EXCHANGE_RECORD
	 * @return
	 */
	public List<Map<String,Object>> createBpmInstanceExchangeRecord(){
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		//所有数据
/*		String sql = "insert into bpm_reality_audit_record " + 
				"(record_id, app_id, create_time, creator, description, document_id, exchange_type, from_user_id, from_user_name,  " + 
				"instance_id, src_node_id, target_node_id, task_id, task_name, to_user_id, to_user_name, snapshot_id, ip_address) " + 
				"select record_id, app_id, create_time, creator, description, document_id, exchange_type, from_user_id,  " + 
				"from_user_name, instance_id, src_node_id, target_node_id, task_id, task_name, to_user_id, to_user_name,  " + 
				"snapshot_id, ip_address from bpm_instance_exchange_record i where not exists (select 1 from bpm_reality_audit_record r where r.record_id = i.record_id)";
*/
		//采购订单评审的数据
		String sql = "insert into bpm_reality_audit_record " + 
				"(record_id, app_id, create_time, creator, description, document_id, exchange_type, from_user_id, from_user_name,   " + 
				"instance_id, src_node_id, target_node_id, task_id, task_name, to_user_id, to_user_name, snapshot_id, ip_address) " + 
				"select i.record_id, i.app_id, i.create_time, i.creator, i.description, i.document_id, i.exchange_type, i.from_user_id, " + 
				"i.from_user_name, i.instance_id, i.src_node_id, i.target_node_id, i.task_id, i.task_name, i.to_user_id, i.to_user_name,  " + 
				"i.snapshot_id, i.ip_address from bpm_instance_exchange_record i,dat_document d " + 
				"where i.DOCUMENT_ID = d.DOCUMENT_ID(+) and d.FORM_NAME = 'Form_PU01' and " + 
				" not exists (select 1 from bpm_reality_audit_record r where r.record_id = i.record_id)";
		int i = gdao.executeJDBCSql(sql);
		
		return datas;
	}
	
	/**
	 * 获取BPM_INSTANCE_EXCHANGE_RECORD
	 * @return
	 */
	public List<Map<String,Object>> getAll(){
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		String sql = "select RECORD_ID,TASK_ID from bpm_reality_audit_record where ARRIVAL_TIME is null";
		datas = gdao.executeJDBCSqlQuery(sql);
		return datas;
	}
	/**
	 * 同步bpm_task_info的提交时间到BPM_INSTANCE_EXCHANGE_RECORD
	 * @return
	 */
	public List<Map<String,Object>> serTime(String RECORD_ID,String TASK_ID){
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		List params = new ArrayList();
		params.add(TASK_ID);
		String sql = "select t2.create_time as ARRIVAL_TIME from bpm_instance_exchange_record t1 inner join  " + 
				"(select task_id, create_time, rownum from (select task_id, create_time from   bpm_task_info  " + 
				"where task_id= ? order by task_id, create_time ) t1 where rownum=1) t2  " + 
				"on t1.task_id=t2.task_id ";
		List<Map<String,Object>> list = gdao.executeJDBCSqlQuery(sql,params);
		//获取到达时间,判断taskid是否为空
		if(!list.isEmpty()) {
			String ARRIVAL_TIME = list.get(0).get("ARRIVAL_TIME").toString();
			
			String sql2 = "update bpm_reality_audit_record set ARRIVAL_TIME = to_timestamp('"+ARRIVAL_TIME+"','yyyy-mm-dd hh24:mi:ss.ff')  " + 
					"where RECORD_ID = ? ";
			List params2 = new ArrayList();
	//		params2.add(ARRIVAL_TIME);
			params2.add(RECORD_ID);
			int i = gdao.executeJDBCSql(sql2, params2);
			System.out.println(i);
		}
		return datas;
	}
	
	
	/**
	 * 获取BPM_INSTANCE_EXCHANGE_RECORD
	 * @return
	 */
	public List<Map<String,Object>> getIsNull(){
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		String sql = "select RECORD_ID,TASK_ID from bpm_reality_audit_record where bpd_id is null and process_app_id is  null";
		datas = gdao.executeJDBCSqlQuery(sql);
		return datas;
	}
	/**
	 * 同步两个字段到BPM_INSTANCE_EXCHANGE_RECORD
	 * @return
	 */
	public void updateBpmInstanceExchangeRecord(String RECORD_ID,String TASK_ID){
		
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		String sql = "select t2.bpd_id,t2.process_app_id from bpm_instance_exchange_record t1 inner join  "+
				"(select task_id, bpd_id,process_app_id, rownum from (select task_id, bpd_id,process_app_id from   bpm_task_info "+ 
				"where task_id= '"+TASK_ID+"' order by task_id, create_time ) t1 where rownum=1) t2  "+
				"on t1.task_id=t2.task_id ";
		List<Map<String,Object>> list = gdao.executeJDBCSqlQuery(sql);
		if(!list.isEmpty()){
			String BPD_ID = list.get(0).get("BPD_ID").toString();
			String PROCESS_APP_ID = list.get(0).get("PROCESS_APP_ID").toString();
			String sql2 = "update bpm_reality_audit_record set bpd_id = '"+BPD_ID+"',process_app_id = '"+PROCESS_APP_ID+"' " +
							"where RECORD_ID = '"+RECORD_ID+"'";
			int i  = gdao.executeJDBCSql(sql2);
		}
		
	}

	public void get(String RECORD_ID, String TASK_ID) {

		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		String sql = "select t2.bpd_id,t2.process_app_id from bpm_instance_exchange_record t1 inner join  "
				+ "(select task_id, bpd_id,process_app_id, rownum from (select task_id, bpd_id,process_app_id from   bpm_task_info "
				+ "where task_id= '" + TASK_ID + "' order by task_id, create_time ) t1 where rownum=1) t2  "
				+ "on t1.task_id=t2.task_id ";
		List<Map<String, Object>> list = gdao.executeJDBCSqlQuery(sql);
		if (!list.isEmpty()) {
			String BPD_ID = list.get(0).get("BPD_ID").toString();
			String PROCESS_APP_ID = list.get(0).get("PROCESS_APP_ID").toString();
			String sql2 = "update bpm_reality_audit_record set bpd_id = '" + BPD_ID + "',process_app_id = '"
					+ PROCESS_APP_ID + "' " + "where RECORD_ID = '" + RECORD_ID + "'";
			int i = gdao.executeJDBCSql(sql2);
		}

	}
	//查询上级
	public List<Map<String, Object>> getLleader(String jobNumber) {

		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		String sql = "select * from AAC_EMPLOYEE t where ad = '"+jobNumber+"'";
		List<Map<String, Object>> list = gdao.executeJDBCSqlQuery(sql);
		return list;
	}
}
