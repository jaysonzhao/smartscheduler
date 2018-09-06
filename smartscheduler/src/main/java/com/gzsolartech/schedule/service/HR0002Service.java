package com.gzsolartech.schedule.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gzsolartech.smartforms.constant.EntityIdPrefix;
import com.gzsolartech.smartforms.entity.DatApplication;
import com.gzsolartech.smartforms.entity.OrgCompany;
import com.gzsolartech.smartforms.entity.OrgRole;
import com.gzsolartech.smartforms.entity.OrgRoleMember;
import com.gzsolartech.smartforms.service.BaseDataService;
import com.gzsolartech.smartforms.service.DatApplicationService;

import net.sf.json.JSONObject;

@Service("HR0002Service")
public class HR0002Service extends BaseDataService{
	
	private static final long serialVersionUID = -1027110740142333151L;
	
	@Autowired
	private DatApplicationService datApplicationService;

	/**
	 * 获取公司信息
	 * @param num
	 * @return
	 */
	public List<OrgCompany> getCompany(String num) {
		String hql = null;
		List<OrgCompany> cp = new ArrayList<OrgCompany>();
		if (StringUtils.isBlank(num)) {
			hql = " from OrgCompany";
			cp = gdao.queryHQL(hql);
		} else {
			hql = " from OrgCompany where companyNum= ? ";
			cp = gdao.queryHQL(hql, num);
		}
		if(cp.isEmpty()){
			return null;
		}else{
			return cp;
		}
	}
	
	/**
	 * 获取部门全称
	 * @param num
	 * @return
	 */
	public Map<String, String> getDepartmentFullName(String num) {
		String sql = "SELECT to_char(replace(wm_concat(dp.DEPTNAME), ',', '_')) deptFullName "
				+ "FROM (select dept_name deptName, LEVEL lv from org_department d "
				+ "start WITH d.dept_num = ? "
				+ "CONNECT BY NOCYCLE prior d.parent_dept_num = d.dept_num "
				+ "ORDER BY lv DESC) dp";
		List<Object> params = new ArrayList<Object>();
		params.add(num);
		List<Map<String, String>> dp = gdao.executeJDBCSqlQuery(sql, params);
		if(dp.isEmpty()){
			return null;
		}else{
			return dp.get(0);
		}
	}
	
	
	public String getToSendTimes(String leaveType, String empid) {
		String sql = "SELECT sum(leaveTime) allTime FROM hr_leave"
				+ " WHERE status<>'SUCCESS' AND leaveType= ? AND empid= ? ";
		List<Object> params = new ArrayList<Object>();
		params.add(leaveType);
		params.add(empid);
		List<Map<String, BigDecimal>> dp = gdao.executeJDBCSqlQuery(sql, params);
		if(dp.get(0).get("ALLTIME") == null){
			return "0";
		}else{
			return dp.get(0).get("ALLTIME").toString();
		}
	}
	
	/**
	 * 
	 * @param leaveId
	 * @param orderNum
	 * @param empName
	 * @param district
	 * @param type
	 * @param leaveType
	 * @param periodStart
	 * @param periodEnd
	 * @param startOrEnd
	 * @param leaveTime
	 * @param status
	 * @param page
	 * @param rows
	 * @return
	 */
	public List<Map<String, String>> getLeave(String empid, List<String> districtNum, String queryDoc, int page, int rows) {
		List<Object> params = new ArrayList<Object>();
		JSONObject js = JSONObject.fromObject(queryDoc);
		StringBuilder sql = new StringBuilder("select orderNum, docuid, empName, sapNo, type, leaveType, district, periodStart, periodEnd,"
				+ " leaveTime, startOrEnd, status, lastDate ,isout,TASKNAME,DEPARTMENT,LEAVEREASON,CREATEDATE from HR_Leave where status IS NOT NULL");
		
		String company = js.getString("company").trim();
		String empName = js.getString("empName").trim();
		String periodstartstart = js.getString("periodstartstart").trim();
		String periodstartend = js.getString("periodstartend").trim();
		String orderNum = js.getString("orderNum").trim();
		String sapNo = js.getString("sapNo").trim();
		String isend = js.getString("isend").trim();
		String leaveType = js.getString("leaveType").trim();
		String department = js.getString("department").trim();
		String status = js.getString("status").trim();
		String applyStart = js.getString("applyStart").trim();
		String applyEnd = js.getString("applyEnd").trim();
		String lastStart = js.getString("lastStart").trim();
		String lastEnd = js.getString("lastEnd").trim();
		String isout = js.getString("isout").trim();
		if (StringUtils.isNotBlank(empid)) {
			sql.append(" and empid like ? ");
			params.add(empid);
		}
		if (StringUtils.isNotBlank(orderNum)) {
			sql.append(" and orderNum like '%"+orderNum+"%' ");
		}
		if (StringUtils.isNotBlank(empName)) {
			sql.append(" and empName = ? ");
			params.add(empName);
		}
		if (StringUtils.isNotBlank(sapNo)) {
			sql.append(" and sapNo like '%"+sapNo+"%' ");
		}
		if (StringUtils.isNotBlank(company)) {
			sql.append(" and company = ? ");
			params.add(company);
		}
		if (StringUtils.isNotBlank(isend)) {
			if(isend.equals("SUCCESS")) {
			sql.append(" and status = ? ");
			params.add(isend);
			}else {
				sql.append(" and not status = ? ");
				params.add("SUCCESS");
			}
		}
		if (StringUtils.isNotBlank(isout)) {
			sql.append(" and isout = ? ");
			params.add(isout);
		}
		if (StringUtils.isNotBlank(status)) {
			if(status.equals("UNDO")) {
			sql.append(" and status = ? ");
			params.add(status);
			}else {
				sql.append(" and not status = ? ");
				params.add("UNDO");
			}
		}
		if (StringUtils.isNotBlank(department)) {
			sql.append(" and department = ? ");
			params.add(department);
		}
		if (StringUtils.isNotBlank(leaveType)) {
			sql.append(" and leaveType = ? ");
			params.add(leaveType);
		}
		if (StringUtils.isNotBlank(periodstartstart)) {
			sql.append(" and to_date(PERIODSTART,'yyyy-MM-dd hh24:mi:ss') >= to_date( ? ,'yyyy-MM-dd hh24:mi:ss') ");
			params.add(periodstartstart);
		}
		if (StringUtils.isNotBlank(periodstartend)) {
			sql.append(" and to_date(PERIODSTART,'yyyy-MM-dd hh24:mi:ss') <= to_date( ? ,'yyyy-MM-dd hh24:mi:ss') ");
			params.add(periodstartend);
		}
		
		if (StringUtils.isNotBlank(applyStart)) {
			sql.append(" and to_date(createDate,'yyyy-MM-dd hh24:mi:ss') >= to_date( ? ,'yyyy-MM-dd hh24:mi:ss') ");
			params.add(applyStart);
		}
		if (StringUtils.isNotBlank(applyEnd)) {
			sql.append(" and to_date(createDate,'yyyy-MM-dd hh24:mi:ss') <= to_date( ? ,'yyyy-MM-dd hh24:mi:ss') ");
			params.add(applyEnd);
		}
		
		if (StringUtils.isNotBlank(lastStart)) {
			sql.append(" and to_date(lastDate,'yyyy-MM-dd hh24:mi:ss') >= to_date( ? ,'yyyy-MM-dd hh24:mi:ss') ");
			params.add(lastStart);
		}
		if (StringUtils.isNotBlank(lastEnd)) {
			sql.append(" and to_date(lastDate,'yyyy-MM-dd hh24:mi:ss') >= to_date( ? ,'yyyy-MM-dd hh24:mi:ss') ");
			params.add(lastEnd);
		}
		if (districtNum!=null && districtNum.size()>0) {
			sql.append(" and districtNum in ( :districtNum ) ");
		}
		SQLQuery rs = gdao.getSession().createSQLQuery(sql.toString());
		rs.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		if (!params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				rs.setParameter(i, params.get(i));
			}
		}
		if (districtNum!=null && districtNum.size() > 0) {
			rs.setParameterList("districtNum", districtNum);
		}
		if (page <= 1) {
			page = 1;
		}

		if (rows <= 0) {
			rows = 10;
		}

		page = (page - 1);
		if (page != 0) {
			page = rows * page;
		}
		rs.setFirstResult(page);
		rs.setMaxResults(rows);
		return rs.list();
	}
	
	/**
	 * 批量更新状态
	 * @param leaveIds
	 * @param status
	 */
	public int updateLeaveStatus(List<String> ids, String status){
		String sql = "update dat_document t "
				+ "set t.document_data = updatexml(t.document_data, '//root/status','<status vtype=\"value\" datatype=\"text\">" + status + "</status>'), "
				+ "t.update_time = SYSDATE where t.document_id in ( :ids )";
		SQLQuery rs = gdao.getSession().createSQLQuery(sql);
		rs.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		rs.setParameterList("ids", ids);
		return rs.executeUpdate();
	}
	
	/**
	 * 批量更新状态2
	 * @param leaveIds
	 * @param status
	 */
	public int updateRowStatus(List<String> ids, String status){
		String sql = "update dat_document t "
				+ " set t.document_data = updatexml(t.document_data, '//root/status','<status>" + status + "</status>') "
				+ " where t.document_id in ( :ids )";
		SQLQuery rs = gdao.getSession().createSQLQuery(sql);
		rs.setParameterList("ids", ids);
		return rs.executeUpdate();
	}
	
	public OrgRole getRoleMember(String code) {
		String hql = " from OrgRole WHERE roleCode= ? ";
		Object[] param = {code};
		List<OrgRole> lr = gdao.queryHQL(hql, param);
		if(lr.isEmpty()){
			return null;
		}else{
			return lr.get(0);
		}
	}
	
	public List<String> getMembers(String roleId, String empid) {
		String sql = " from OrgRoleMember WHERE roleNum= ? AND empNum = ?";
		Object[] param = {roleId, empid};
		List<OrgRoleMember> lo = gdao.queryHQL(sql, param);
		if(lo.isEmpty()) {
			return null;
		}else{
			List<String> dis = new ArrayList<String>();
			for (int i = 0; i < lo.size(); i++) {
				dis.add(lo.get(i).getDeptNum());
			}
			return dis;
		}
	}
	
	/**
	 * 
	 * @param employeeId
	 * @param page
	 * @param rows
	 * @param appId
	 * @param formName
	 * @param queryDoc
	 * @param order
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> bpmTaskInfo(String employeeId, Integer page, Integer rows,
			String appId, String formName, String queryDoc, String order) throws Exception {
		String drafTaskName = "起草";
		DatApplication datApplication = datApplicationService.load(appId);
		String formViewName = EntityIdPrefix.SQL_FORM_VIEW
				+ datApplication.getAppName() + "_" + formName;
		String drafQuery = "";
		// 添加查询条件
		List<Object> params = new ArrayList<Object>();
		// 如果不是管理员，则获取对应的人员信息
		Boolean isManager = false;
		if ("manamger".equals(employeeId)) {
			isManager = true;
		}
		if (!isManager) {
			params.add(employeeId);
		}
		params.add(appId);
		params.add(formName);
		// 查询sql
		StringBuffer sql = new StringBuffer();
		sql.append(" from BPM_TASK_INFO task ," + formViewName + " fv , dat_document doc, ");

		// 用于判断流程状态，增加待办表单的由来（提交，退回等）
		sql.append("  (select * from ( select DOCUMENT_ID,RESULT_TYPE,"
				+ "rank()over(PARTITION by DOCUMENT_ID order by"
				+ " CREATE_TIME desc) mm  "
				+ "from BPM_AUDIT_RECORD ) where mm=1) re ");
		sql.append(" where task.task_state!='STATE_FINISHED' and task.task_status='Received'");
		sql.append(" and task.instance_state='STATE_RUNNING' and task.instance_exec_state='Active'  ");
		sql.append(" and task.document_id=fv.document_id ");
		// 若非管理员，则查询对应的人的信息
		sql.append(isManager ? "" : " and task.assigned_to=? ");
		sql.append(" and fv.app_id=?");

		// 用于判断流程状态，增加待办表单的由来（提交，退回等）
		sql.append(" and re.document_id=fv.document_id");
		sql.append(" and fv.form_name=? ");
		// 获取采购编码
		sql.append(" and doc.document_id=task.document_id ");
		
		// 如果有条件查询，则增加
		if (queryDoc != null && !"".equals(queryDoc)) {
			org.json.JSONObject jo = new org.json.JSONObject(queryDoc);
			Set<String> keys = jo.keySet();
			keys.remove("formName");
			keys.remove("appId");
			if (keys.contains("startDate")
					&& keys.contains("endDate")
					&& (!"".equals(jo.get("startDate")) || !"".equals(jo
							.get("endDate")))) {
				String tmp = "  and fv.CREATE_TIME between to_date('"
						+ jo.get("startDate")
						+ " 00:00:00','YYYY-MM-DD HH24:MI:SS') and to_date('"
						+ jo.get("endDate")
						+ " 23:59:59','YYYY-MM-DD HH24:MI:SS') ";
				sql.append(tmp);
				// 草稿查询条件
				drafQuery += "  and df.CREATE_TIME between to_date('"
						+ jo.get("startDate")
						+ " 00:00:00','YYYY-MM-DD HH24:MI:SS') and to_date('"
						+ jo.get("endDate")
						+ " 23:59:59','YYYY-MM-DD HH24:MI:SS') ";

				keys.remove("startDate");
				keys.remove("endDate");
			}
			for (String key : keys) {
				if (!"".equals(jo.get(key))) {
					sql.append(" and lower(fv." + key + ") like lower(?)");
					params.add("%" + jo.get(key) + "%");
					// 草稿查询条件
					drafQuery += " and lower(df." + key + ") like lower('%" + jo.get(key)
							+ "%') ";
				}
			}
		}

		String queryFields = "fv.empName,fv.DOCUMENT_ID, fv.FORM_NAME, fv.PARENT_DOCUMENT_ID, fv.CREATE_TIME, fv.CREATOR,fv.UPDATE_TIME,fv.APP_ID ,fv.orderNum";
		String drafFields = "df.empName,df.DOCUMENT_ID, df.FORM_NAME, df.PARENT_DOCUMENT_ID, df.CREATE_TIME, df.CREATOR,df.UPDATE_TIME,df.APP_ID ,df.orderNum";
		// 查询结果返回的列
		String viewSql = "select re.RESULT_TYPE, " + queryFields
				+ ",task.task_name,task.assigned_to,task.task_id ,task.create_time taskGet,"
				+ " to_char((select wmsys.wm_concat(DISTINCT EXTRACTVALUE(ROW_DATA, '/root/row/PurchaseOrder')) from DAT_TABLE_ROW WHERE doc.DOCUMENT_ID=DOCUMENT_ID and table_Id='table2')) as  purchcode,"
				+ "  to_char(( select wmsys.wm_concat( DISTINCT  EXTRACTVALUE(ROW_DATA, '/root/row/Brand') ) from  DAT_TABLE_ROW  WHERE doc.DOCUMENT_ID=DOCUMENT_ID and table_Id='table2'))as BRANd"
//				+ " doc.document_Data docData"
				+ sql.toString();

		// 特殊的草稿置为待办处理开始
		String drafView = "select 'submit' RESULT_TYPE," + drafFields + ",'"
				+ drafTaskName + "' task_name,'' assigned_to,'' task_id,df.create_time taskGet,"
						+ "to_char((select wmsys.wm_concat(DISTINCT EXTRACTVALUE(ROW_DATA, '/root/row/PurchaseOrder')) from DAT_TABLE_ROW WHERE doc.DOCUMENT_ID=DOCUMENT_ID and table_Id='table2')) as  purchcode,"
						+ " to_char(( select wmsys.wm_concat( DISTINCT  EXTRACTVALUE(ROW_DATA, '/root/row/Brand') ) from  DAT_TABLE_ROW  WHERE doc.DOCUMENT_ID=DOCUMENT_ID and table_Id='table2'))as BRANd";
		
		
		String draf = "  from "
				+ formViewName
				+ "  df ,dat_document doc ";

		draf+=" where  df.document_id not in(select  document_id from BPM_INSTANCE_INFO where creator='"
				+ employeeId
				+ "')"
				+ "   and df.document_id  in(select  document_id from DAT_DOCUMENT  where DOCUMENT_STATUS='drafttodo') "
				+ " and df.app_id='" + appId + "'" + "  and df.form_name='"
				+ formName + "'" + "  and df.creator='" + employeeId + "' and doc.document_id=df.document_id";
			
		draf+= drafQuery;
		// + "  order by create_time desc";
		drafView = drafView + draf;
		// 特殊的草稿置为待办处理结束

		if (order == null) {
			order = "desc";
		}
		String tmpSql = " from (select * from ( " + viewSql
				+ " ) union all select * from ( " + drafView + " )) tmptable ,DAT_DOCUMENT doc ";
		tmpSql= tmpSql +" where 1=1 and  doc.document_id=tmptable.document_id ";

//	   if(StringUtils.isNotEmpty(purchcode)){
//		   tmpSql+=" and purchcode  like '%"+purchcode+"%' ";
//		}
		List<Map<String, Object>> datas = gdao.executeJDBCSqlQuery("select tmptable.* "
			+ tmpSql,
				page, rows, params);
		
		Long count = gdao.countSql(tmpSql, params);
		Map<String, Object> map = new HashMap<String, Object>();
		// 总条数
		map.put("total", count);
		// 查询的数据
		map.put("rows", datas);  
		return map;
	}
	
	/**
	 *  获得外勤人员
	 * 
	 * @return
	 */
	public Map getMetaByCataNameIndex(String cataName,String serialNum) {
		Map map = new HashMap();
		String sql1 ="select EMP_NAME,EMP_NUM from ORG_ROLE_MEMBER where role_num in\r\n" + 
				"(select role_num from ORG_ROLE where role_code='"+cataName+"') and DISTRICT_NUM='"+serialNum+"'";
		List<Map<String, Object>>  list1 = gdao.executeJDBCSqlQuery(sql1);
		map.put("EMP_NUM", list1);
		return map;
	}
	
	/**
	 * 获取补遗日期
	 * 
	 * @return
	 */
	public Map HR0002_CorporateCurrency(String cataName,String searchKey) {
		Map map = new HashMap();
		String sql1 ="  select description from DAT_SYSTEM_META where\r\n" + 
				"         META_CATA_ID in(  \r\n" + 
				"         select CATA_ID from DAT_SYSTEM_META_CATA where cata_name='"+cataName+"') \r\n" + 
				"		 AND meta_name='"+searchKey+"' ";
		List list1 = gdao.executeJDBCSqlQuery(sql1);
		map= (Map) list1.get(0);
		String Currency=(String) map.get("description");
		map.put("Currency", Currency);
		return map;
	}
	
	/**
	 * 获取写入数据
	 * @param leaveId
	 * @param orderNum
	 * @param empName
	 * @param district
	 * @param type
	 * @param leaveType
	 * @param periodStart
	 * @param periodEnd
	 * @param startOrEnd
	 * @param leaveTime
	 * @param status
	 * @param page
	 * @param rows
	 * @return
	 */
	public List<Map<String, String>> hqxrsj() {
		List<Object> params = new ArrayList<Object>();
		String sql = "select DOCUID, SAPNO, LEAVETYPE, PERIODSTART, PERIODEND,STATUS,STARTOREND from HR_Leave where status2 IS NOT NULL and  taskName = '已完成' and  status ='否' ";
		List<Map<String, String>> list1 = gdao.executeJDBCSqlQuery(sql);
//		System.out.println(list1);
		return list1;
	}
}
