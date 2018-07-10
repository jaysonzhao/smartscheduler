package com.gzsolartech.schedule.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gzsolartech.bpmportal.entity.ApplySycnFailRecord;
import com.gzsolartech.bpmportal.entity.ApplySycnUpdateRecord;
import com.gzsolartech.bpmportal.util.ApplySycnConstant;
import com.gzsolartech.smartforms.entity.OrgEmployee;
import com.gzsolartech.smartforms.entity.bpm.BpmTaskInfo;
import com.gzsolartech.smartforms.service.BaseDataService;
import com.gzsolartech.smartforms.service.OrgEmployeeService;
import com.gzsolartech.smartforms.service.bpm.BpmTaskInfoService;

@Service
public class PU52Service extends BaseDataService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	private  BpmTaskInfoService bpmTaskInfoService;
	
	@Autowired
	private OrgEmployeeService orgEmployeeService;
	
	public final  static String  ALLAPPLYTYPE="ALL";
	public final  static String  PARTAPPLYTYPE="PART";
	
	/**
	 * 根据模块名获取模块更新时间
	 * @param modularName
	 * @return
	 */
	public ApplySycnUpdateRecord getUpdateInfoByModular(String modularName){
		
		List<ApplySycnUpdateRecord> updateInfo = gdao.queryHQL(
				"from ApplySycnUpdateRecord where modular=?  order by createTime desc ",
				modularName);
		return updateInfo.size()>0?updateInfo.get(0):null;
		
	}
	
	
	
	
	/**
	 * 获取PU52所有单，即为管理员单
	 */
public List<Map<String,Object>> getAllappLy(String type,String updateTime){
		
		String appId=ApplySycnConstant.PU52APPID;
		String fromName=ApplySycnConstant.PU52FROMNAME;
		
		String doneColumn="r.INSTANCE_ID,fv.empName,fv.DOCUMENT_ID, fv.FORM_NAME, fv.PARENT_DOCUMENT_ID, "
				+ "fv.CREATE_TIME, fv.CREATOR,fv.UPDATE_TIME,fv.APP_ID ,fv.orderNum,'' task_id,r.CREATE_TIME taskGet ,"
				+ "to_char((select wmsys.wm_concat(DISTINCT EXTRACTVALUE(ROW_DATA, '/root/row/ReturnMsg')) from DAT_TABLE_ROW WHERE doc.DOCUMENT_ID=DOCUMENT_ID)) as  purchcode,"
				+ "(case when info.instance_Exec_state='Completed' then  info.UPDATE_TIME  else null  end )instanceEndTime ,"
				+ "task.task_name, to_char(( select wmsys.wm_concat( DISTINCT  EXTRACTVALUE(ROW_DATA, '/root/row/recommendedbrand') ) from  DAT_TABLE_ROW  WHERE doc.DOCUMENT_ID=DOCUMENT_ID ))as BRANd,"
				+ "to_char((fv.model||','||(select listagg(EXTRACTVALUE(ROW_DATA, '/root/row/materialname'),',') within group (order by EXTRACTVALUE(ROW_DATA, '/root/row/materialname')) from  DAT_TABLE_ROW ddttrr WHERE doc.DOCUMENT_ID=ddttrr.DOCUMENT_ID))) as MODEL, "
				//+ "extract(rowl.ROW_DATA, '/root').getClobVal() as rowData,"
				+ "extract(doc.document_Data, '/root').getClobVal() as docData";
		
		
		String doneTable="(select document_id,max(INSTANCE_ID) INSTANCE_ID, max(CREATE_TIME) CREATE_TIME  "
				+ "from BPM_INSTANCE_EXCHANGE_RECORD  group by document_id) r ,dat_document doc , "
				+ "BPM_INSTANCE_INFO info, "
				+ "bpm_allorder_pu52 fv,"
				+ "(select * from (select INSTANCE_ID,TASK_ID,ASSIGNED_TO,TASK_NAME,INSTANCE_EXEC_STATE,TASK_STATUS,document_id, row_number() over(PARTITION by INSTANCE_ID order by	CREATE_TIME desc)  mm from BPM_TASK_INFO) where mm=1)task ";
				//+ ",(select t.* from(select a.*,row_number() over(partition by document_id order by create_Time desc) rw from DAT_TABLE_ROW a)t where t.rw=1 )  rowl   ";
		
		String doneWhere="r.document_id=fv.document_id"
				+ " and r.document_id=task.document_id  "
				+ " and r.document_id=doc.document_id   "
				+ " and r.INSTANCE_ID=info.INSTANCE_ID    "
				+ " and fv.app_id=? and fv.form_name=?  "
				//+ " and doc.DOCUMENT_ID=rowl.DOCUMENT_ID  "
				+ " and (fv.DOCUMENT_STATUS !='deleted' or fv.DOCUMENT_STATUS is null)  "
				+ "order by fv.create_time desc";
		
		String rowsTable=" (select t.DOCUMENT_ID as  rowsDocId ,extract(t.ROW_DATA, '/root').getClobVal()"
				+ "  as rowData from(select a.*,row_number() over(partition by document_id order by create_Time desc) rw"
				+ " from DAT_TABLE_ROW a)t where t.rw=1)  row2 on a.DOCUMENT_ID=row2.rowsDocId";
	
		String doneSql="select * from ( select * from ( select " +doneColumn +" from "+doneTable +" where "+ doneWhere+" ) a LEFT JOIN  "+rowsTable+") ";
		String doneCount="select * from ( select fv.UPDATE_TIME from "+doneTable +" where "+ doneWhere+" )";
		
		
		String todoColumn=" task.INSTANCE_ID,fv.empName,fv.DOCUMENT_ID, fv.FORM_NAME, fv.PARENT_DOCUMENT_ID, fv.CREATE_TIME,"
				+ "fv.CREATOR,fv.UPDATE_TIME,fv.APP_ID ,fv.orderNum,task.task_id, task.CREATE_TIME  taskGet ,to_char((select wmsys.wm_concat(DISTINCT EXTRACTVALUE(ROW_DATA, '/root/row/ReturnMsg'))"
				+ "from DAT_TABLE_ROW WHERE doc.DOCUMENT_ID=DOCUMENT_ID)) as  purchcode,null instanceEndTime,task.task_name ,"
				+ "to_char(( select wmsys.wm_concat( DISTINCT  EXTRACTVALUE(ROW_DATA, '/root/row/recommendedbrand') ) from  DAT_TABLE_ROW  WHERE doc.DOCUMENT_ID=DOCUMENT_ID ))as BRANd,"
				+ "fv.model as MODEL   ,"
				//+ "extract(rowl.ROW_DATA, '/root').getClobVal() as rowData,"
				+ "extract(doc.document_Data, '/root').getClobVal() as docData";
		
		String todoTable="BPM_TASK_INFO task,"
				+ "dat_document doc,"
				+ "bpm_allorder_pu52 fv  ";
			//	+ ", (select t.* from(select a.*,row_number() over(partition by document_id order by create_Time desc) rw from DAT_TABLE_ROW a)t where t.rw=1 )  rowl  ";
		
		
		String todoWhere="task.task_state!='STATE_FINISHED' and task.task_status='Received'  "
				+ "and task.instance_state='STATE_RUNNING' and task.instance_exec_state='Active'    "
				+ "and task.document_id=fv.document_id   and task.document_id=doc.document_id "
				//+ "and doc.DOCUMENT_ID=rowl.DOCUMENT_ID  "
				+ "and  fv.app_id=? and fv.form_name=?  "
				+ "and  not EXISTS(select  'G'   from   (select document_id,max(INSTANCE_ID) INSTANCE_ID   from BPM_INSTANCE_EXCHANGE_RECORD "
				+ "group by document_id ) r ,bpm_allorder_pu52  fv    where   r.document_id=fv.document_id   and "
				+ "  fv.app_id=? and fv.form_name=? ) "
				+ " and (fv.DOCUMENT_STATUS !='deleted' or fv.DOCUMENT_STATUS is null)  "
				+ "order by fv.create_time desc";
		
		
		String todoSql="select * from ( select * from ( select " +todoColumn +" from "+todoTable +" where "+ todoWhere+") a LEFT JOIN  "+rowsTable+")";
		String todoCount="select * from ( select fv.UPDATE_TIME from "+todoTable +" where "+ todoWhere+")";
		
		
		String sql ="select * from ("+ doneSql+" union all "+todoSql +")　tmp where 1=1";
		String countSql ="from ("+ doneCount+" union all "+todoCount +")　 where 1=1  ";
		
		if(PARTAPPLYTYPE.equals(type)){
			String sqlTemp=" and UPDATE_TIME >to_date('"+updateTime+"','YYYY-MM-DD HH24:MI:SS') ";
			sql+=sqlTemp;
			countSql+=sqlTemp;
		}
		
		
		List<Object> params=new ArrayList<Object>();
		params.add(appId);
		params.add(fromName);
		params.add(appId);
		params.add(fromName);
		params.add(appId);
		params.add(fromName);
		
		Long count = gdao.countSql(countSql, params);
		int pagesize=200;	
		Long pageTmp=count/pagesize;
		Long pages=count%pagesize==0?(pageTmp<=0?1:pageTmp):(pageTmp)+1;
	
		List<Map<String, Object>> dataResult =new ArrayList<Map<String,Object>>();
		for (int k = 1; k <= pages; k++) {
			List<Map<String, Object>> datas = gdao.executeJDBCSqlQuery(sql,k,pagesize,params);
			List<String> instanceIds = new ArrayList<String>();
			for (int i = 0; i < datas.size(); i++) {
				String instanceId = (String) datas.get(i).get("INSTANCE_ID");
				if (StringUtils.isNotBlank(instanceId)) {
					instanceIds.add(instanceId);
				}
				
				String mode="";
				String[] modes= String.valueOf(datas.get(i).get("MODEL")).split(",");
				for (int j = 0; j < modes.length; j++) {
					if(!mode.contains(modes[j])){
					    mode+=modes[j]+",";
					}
				}
				if(mode.length()>0){
					mode=mode.substring(0, mode.length()-1);
					datas.get(i).put("MODEL", mode);
				}else{
					datas.get(i).put("MODEL", "");
				}
				
			}
			if (!instanceIds.isEmpty()) {
				mateTaskByInstanceId(datas, instanceIds);
			}
			dataResult.addAll(datas);
		}
		return dataResult;
	}
	
	
	public void mateTaskByInstanceId(List<Map<String, Object>> datas,
			List<String> instanceIds) {

		// 按时间顺序对INSTANCE_ID 相同的BPM_TASK_INFO表 进行排序
		String taskInfoSortSql = " select INSTANCE_ID,TASK_ID,ASSIGNED_TO,TASK_NAME,"
				+ "INSTANCE_EXEC_STATE,TASK_STATUS,"
				+ "	row_number()over(PARTITION by INSTANCE_ID order by"
				+ "	CREATE_TIME desc) mm  " + "	from BPM_TASK_INFO ";
		// 获取每条INSTANCE_ID的最新一条数据
		String latestTaskInfoSQL = "select * from (" + taskInfoSortSql
				+ ") where mm=1";
		// 获取人员表的人员编号和名称
		String empInfo = "select emp.nick_name,emp.emp_NUM  from ORG_EMPLOYEE emp";
		// 对数据进行匹配，得到环节处理人名称
		String taskInfo = "select * from (" + latestTaskInfoSQL
				+ ") info left join  ( " + empInfo
				+ " ) empp on empp.emp_NUM = info.ASSIGNED_TO "
				+ "where INSTANCE_ID in (:instanceId)";

		Query taskInfoQuery = gdao.getSession().createSQLQuery(taskInfo);
		taskInfoQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		taskInfoQuery.setParameterList("instanceId", instanceIds);
		// 获取对应实例ID的最新一条数据
		List<Map<String, Object>> infos = taskInfoQuery.list();
		// 遍历，匹配对应的实例ID的当前处理人和当前环节名称
		for (Map<String, Object> tmp : datas) {
			String instanceId = String.valueOf(tmp.get("INSTANCE_ID"));
			Map<String, Object> latestTaskInfo = new HashMap<String, Object>();
			// 将对应的实例ID的信息提取出来
			for (Map<String, Object> info : infos) {
				if (instanceId.equals(info.get("INSTANCE_ID"))) {
					latestTaskInfo = info;
				}
			}
			// 如果task_info表中存在，那么匹配其当前环节及当前环节处理人
			if (!latestTaskInfo.isEmpty()) {

				String snapshotBpdId = String.valueOf(latestTaskInfo
						.get("SNAPSHOT_BPD_ID"));

				// 如果流程结束了,则无当前处理人
				if ("Completed".equals(String.valueOf(latestTaskInfo
						.get("INSTANCE_EXEC_STATE")))) {
					tmp.put("currentOwnerName", "---");
					tmp.put("currentTaskName", "结束");
				} else {
					// 流程未结束，则获取其最新的处理人，可能有多个
					//List<BpmTaskInfo> receivedData = bpmTaskInfoService
					//		.getTaskInfosByInstanceId(instanceId, "Received");
					  List<BpmTaskInfo> receivedData = bpmTaskInfoService.getReceivedTaskInfoByInstId(instanceId);
					// 遍历组合获取当前处理人及环节名称
					Set<String> currentTaskName = new HashSet<String>();
					Set<String> assignedTo = new HashSet<String>();
					for (BpmTaskInfo result : receivedData) {
						OrgEmployee org = orgEmployeeService.getUser(result
								.getAssignedTo());
						assignedTo.add(org == null ? "" : org.getNickName());
						currentTaskName.add(result.getTaskName());
					}
					// 若最新环节存在Received，则获取其处理人和环节名称
					if (receivedData.size() != 0) {
						tmp.put("currentOwnerName", assignedTo);
						tmp.put("currentTaskName", currentTaskName);
					} else {
						// 若最新环节无Received，则为出错，则获取数据库中最新一条数据，流程处理人设置为Admin
						String hql = "from BpmTaskInfo  where instanceId=? order by createTime";
						List<BpmTaskInfo> latestdata = gdao.queryHQL(hql,
								instanceId);
						if (!latestdata.isEmpty()) {
							tmp.put("currentOwnerName", "Admin");
							tmp.put("currentTaskName", latestdata.get(0)
									.getTaskName());
						}
						
					}
				}

			} else {
				// 如果找不到，说明为起草环节，未提交
				String creator = String.valueOf(tmp.get("CREATOR"));
				OrgEmployee org = orgEmployeeService.getUser(creator);
				tmp.put("currentOwnerName",
						org == null ? "---" : org.getNickName());
				tmp.put("currentTaskName", "新建");
			}
		}
	}
	
	
	
	public void savePu52Record(ApplySycnUpdateRecord  record){
		gdao.save(record);
	}
	
	public void savePu52FailRecord(List<ApplySycnFailRecord>  failRecords){
		gdao.save(failRecords);
	}
	
	public void updatePu52Record(ApplySycnUpdateRecord  record){
		gdao.update(record);
	}
	public ApplySycnUpdateRecord getRecordById(String id){
		ApplySycnUpdateRecord record=gdao.findById(ApplySycnUpdateRecord.class, id);
		return record;
	}
	
}
