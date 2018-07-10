package com.gzsolartech.schedule.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gzsolartech.bpmportal.util.email.EmailNotificationUtil;
import com.gzsolartech.smartforms.constant.SysConfigurationTypeName;
import com.gzsolartech.smartforms.dao.GenericDao;
import com.gzsolartech.smartforms.service.BaseDataService;
import com.gzsolartech.smartforms.service.SysConfigurationService;
import com.gzsolartech.smartforms.service.bpm.BpmGlobalConfigService;

@Service
public class InfomationSafeHouseService extends BaseDataService {
	@Autowired
	private GenericDao gdao;
	@Autowired
	private SysConfigurationService sysConfigurationService;
	@Autowired
	private BpmGlobalConfigService bpmGlobalConfigService;

	public void excute() {
		// 获取一周后数据
		//weekInfo();
		// 获取一天后数据
		//oneDayAfter();
		// 获取一天前数据
		oneDayAgo();
	}

	public void weekInfo() {
		String sql_week = " select extractvalue(x.document_data,'/root/empName') as empName,"
				+ " extractvalue(x.document_data,'/root/ADName') as ADName,"
				+ " extractvalue(x.document_data,'/root/periodEnd') as now "
				+ " from dat_document x ,bpm_instance_info b where   " + " x.form_name='IT0005'  "
				+ " and extractvalue(x.document_data,'/root/__docuid') = b.document_id   "
				+ " and b.INSTANCE_STATE ='STATE_FINISHED' "
				+ " and extractvalue(x.document_data,'/root/periodEnd')=(select  to_char((sysdate + interval '7' day),'yyyy-mm-dd')  from dual  )";
		List<Map<String, Object>> list_weekAfter = gdao.executeJDBCSqlQuery(sql_week);
		if (!list_weekAfter.isEmpty()) {
			for (Map<String, Object> map : list_weekAfter) {
				String Adname = map.get("ADNAME") != null ? map.get("ADNAME").toString() : "";
				String date = map.get("NOW") != null ? map.get("NOW").toString() : "";
				sendEmail(Adname, "您的安全信息房使用权限即将到期!",
						"您的信息安全房使用权限于" + date + "到期,如需续申请,请及时在EIP上提交申请单."+"<br>"+"若到期流程未申请完毕,请关闭您的信息安全房使用权限.");

			}
		}
	}
	public void oneDayAfter() {
		String sql_week = " select extractvalue(x.document_data,'/root/empName') as empName,"
				+ " extractvalue(x.document_data,'/root/ADName') as ADName,"
				+ " extractvalue(x.document_data,'/root/periodEnd') as NOW "
				+ " from dat_document x ,bpm_instance_info b where    x.form_name='IT0005'  "
				+ " and extractvalue(x.document_data,'/root/__docuid') = b.document_id   "
				+ " and b.INSTANCE_STATE ='STATE_FINISHED' "
				+ " and extractvalue(x.document_data,'/root/periodEnd')=(select  to_char((sysdate + interval '1' day),'yyyy-mm-dd')  from dual  )";
		List<Map<String, Object>> list_weekAfter = gdao.executeJDBCSqlQuery(sql_week);
		if (!list_weekAfter.isEmpty()) {
			for (Map<String, Object> map : list_weekAfter) {
				String Adname = map.get("ADNAME") != null ? map.get("ADNAME").toString() : "";
				String date = map.get("NOW") != null ? map.get("NOW").toString() : "";
				sendEmail(Adname, "您的安全信息房使用权限即将到期!",
						"您的信息安全房使用权限于" + date + "到期,如需续申请,请及时在EIP上提交申请单.+"+"<br>"+"若到期流程未申请完毕,请关闭您的信息安全房使用权限.");

			}
		}
	}
	public void oneDayAgo() {
		String sql_week = " select extractvalue(x.document_data,'/root/orderNum') as ordernum,"
				+ " extractvalue(x.document_data,'/root/responeser_num') as manId,"
				+ " extractvalue(x.document_data,'/root/responeser') as man, "
				+ " extractvalue(x.document_data,'/root/empName') as empName,"
				+ " extractvalue(x.document_data,'/root/ADName') as adname,"
				+ " extractvalue(x.document_data,'/root/safehousearea') as area, "
				+ " extractvalue(x.document_data,'/root/__appid') as appid,"
				+ " extractvalue(x.document_data,'/root/__docuid') as docid,"
				+ " extractvalue(x.document_data,'/root/__taskId') as taskid"
				+ " from dat_document x ,bpm_instance_info b where   " + " x.form_name='IT0005'  "
				+ " and extractvalue(x.document_data,'/root/__docuid') = b.document_id   "
				+ " and b.INSTANCE_STATE ='STATE_FINISHED' "
				+ " and extractvalue(x.document_data,'/root/periodEnd')=(select  to_char((sysdate - interval '1' day),'yyyy-mm-dd')  from dual  )";
		List<Map<String, Object>> list_weekAfter = gdao.executeJDBCSqlQuery(sql_week);
		if (!list_weekAfter.isEmpty()) {
			for (Map<String, Object> map : list_weekAfter) {
				String manId = map.get("MANID") != null ? map.get("MANID").toString() : "";
				String man = map.get("MAN") != null ? map.get("MAN").toString() : "";
				String ordernum = map.get("ORDERNUM") != null ? map.get("ORDERNUM").toString() : "";
				String empname = map.get("EMPNAME") != null ? map.get("EMPNAME").toString() : "";
				String adname =  map.get("ADNAME") != null ? map.get("ADNAME").toString() : "";
				String area =  map.get("AREA") != null ? map.get("AREA").toString() : "";
				String appid = map.get("APPID") != null ? map.get("APPID").toString() : "";
				String docid = map.get("DOCID") != null ? map.get("DOCID").toString() : "";
				String taskid = map.get("TASKID") != null ? map.get("TASKID").toString() : "";
				String href = taskinfo(appid,docid,taskid);
				//System.out.println(href);
				System.out.println("<ul><li>"+"<a href='"+href+"'>"+ordernum+"</a></li>"
									+ "<li>"+empname+"</li>"
									+ "<li>"+adname+"</li>"
									+ "<li>"+area+"</li></ul>");
				sendEmail(manId, empname+"信息安全房使用权限已到期!",
						empname+"信息安全房使用期限已到期,请立即关闭!"+"<br>"
								+"<ul><li>"+"<a href='"+href+"'>"+ordernum+"</a></li>"
								+ "<li>"+empname+"</li>"
								+ "<li>"+adname+"</li>"
								+ "<li>"+area+"</li></ul>");

			}
		}
	}
	public String taskinfo(String appid,String docid,String taskId){
		String host=bpmGlobalConfigService.getWebContext();
		return host +"/console/template/engine/opendocument/"+appid+"/"+docid+".xsp?mode=edit&taskId="+taskId;
		
	}
	public void sendEmail(String Adname, String title, String context) {
		Map<String, Object> config = sysConfigurationService
				.getSysConfiguration(SysConfigurationTypeName.SYSTEM_CONFIG);
		String account = config.get("account") + "";
		String password = config.get("password") + "";
		String sql_emp = "select email,nick_name from org_employee where emp_name = ?";
		List<Object> params_emp = new ArrayList<>();
		params_emp.add(Adname);
		List<Map<String, Object>> list_emp = gdao.executeJDBCSqlQuery(sql_emp, params_emp);
		Map<String, Object> map_emp = list_emp.get(0);
		if (map_emp.get("EMAIL") == null) {
			new EmailNotificationUtil().execute(account, password, "pengyuhuan@aactechnologies.com", map_emp.get("NICK_NAME")+"为空,"+title, context);
		} else {
			
			  new EmailNotificationUtil().execute(account, password,
			  map_emp.get("EMAIL").toString(), title,context);
			 
			//new EmailNotificationUtil().execute(account, password, "pengyuhuan@aactechnologies.com", title, context);
		}
	}
}
