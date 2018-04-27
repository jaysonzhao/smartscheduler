package com.gzsolartech.schedule.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gzsolartech.bpmportal.util.email.EmailNotificationUtil;
import com.gzsolartech.smartforms.constant.SysConfigurationTypeName;
import com.gzsolartech.smartforms.dao.GenericDao;
import com.gzsolartech.smartforms.service.SysConfigurationService;
@Service
public class HR19DepartmentService {
	@Autowired
	private GenericDao gdao;
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(HR19DepartmentService.class);
	@Autowired
	private SysConfigurationService sysConfigurationService;
	public void sendEmailDeparementService(){
		StringBuilder head = new StringBuilder();
		head.append("<table width='100%'>").append("<td>姓名</td>")
				.append("<td>工号</td>").append("<td>一级部门</td>").append("<td>二级部门</td>").append("<td>三级部门</td>")
				.append("<td>部门总监</td>").append("<td>部门VP</td>").append("<td>提出日期</td>").append("<td>预计离职日期</td>")
				.append("<td>离职办理日期</td>").append("<td>离职类型</td>").append("<td>离职原因</td>").append("<td>HR综合意见</td>")
				.append("<td>是否竞业</td>").append("<td>竞业限制开始时间</td>").append("<td>竞业限制结束时间</td>")
				.append("<td>离职竞业期(月)</td></tr></thead>");
		head.append("<tbody>");
		String sql_place = "  SELECT  distinct A.meta_value,a.eaa1 from dat_system_meta A,dat_system_meta_cata B "
				+ " WHERE b.cata_id=a.meta_cata_id AND b.cata_name='HR19_AddForName'";
		List<Map<String, Object>> list_place = gdao.executeJDBCSqlQuery(sql_place);
		for (int x = 0; x < list_place.size(); x++) {
			Map<String, Object> map_place = list_place.get(x);
			String	 sql = "select extractvalue(x.document_data,'/root/orderNum') as orderNum,"
					    + "extractvalue(x.document_data,'/root/empAdName') as empAdName,"
						+ "extractvalue(x.document_data,'/root/empName') as empName,"
						+ "extractvalue(x.document_data,'/root/empNum') as empNum,"
						+ "extractvalue(x.document_data,'/root/stairDepartment') as stairDepartment,"
						+ "extractvalue(x.document_data,'/root/secondDepartment') as secondDepartment,"
						+ "extractvalue(x.document_data,'/root/threeDepartment') as threeDepartment,"
						+ "to_char(x.create_time,'yyyy-mm-dd') as createtime,"
						+ "extractvalue(x.document_data,'/root/vacateinfodate') as vacateinfodate,"
						+ "extractvalue(x.document_data,'/root/termDate') as termDate, "
						+ "extractvalue(x.document_data,'/root/competitionStartTime') as competitionStartTime,"
						+ "extractvalue(x.document_data,'/root/resigncause_info_n') as resigncause_info_n,"
						+ "extractvalue(x.document_data,'/root/resigncause') as resigncause,"
						+ "extractvalue(x.document_data,'/root/text_agreeornotthat_display') as text_agreeornotthat_display,"
						+ "extractvalue(x.document_data,'/root/HR19_overall') as HR19_overall,"
						+ "extractvalue(x.document_data,'/root/continuesignmonth') as continuesignmonth,"
						+ "extractvalue(x.document_data,'/root/competitionEndTime') as competitionEndTime from dat_document x, bpm_instance_info b "
						+ " where x.form_name='Form_HR19' "
						+ " and extractvalue(x.document_data,'/root/__docuid') = b.document_id "
						+ " and b.INSTANCE_STATE ='STATE_FINISHED'"
						+ "  and b.update_time between  trunc(next_day(to_date(?,'yyyy-mm-dd') - 8, 1)-6) and trunc(next_day(to_date(?,'yyyy-mm-dd') - 8, 1))    "
						+ "  and extractvalue(x.document_data,'/root/stairDepartment') in ('研发部','射频结构研发','MEMS研发')"
						+ " and extractvalue(x.document_data,'/root/managerArea') = ?";
			
				List<Object> params = new ArrayList<>();
				//String date = "2018-2-8";
				
				 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				 String date = sdf.format(new Date());
				 
				params.add(date);
				params.add(date);
				params.add(map_place.get("META_VALUE").toString());
				List<Map<String, Object>> list = gdao.executeJDBCSqlQuery(sql, params);
				StringBuilder body = new StringBuilder();
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> map = list.get(i);
					body.append("<tr>");
					System.out.println("map=" + map);
					String value = map.get("EMPADNAME").toString();
					String sql_director = "select nachn||vorna as director from aac_employee where ad= (select upper(director_num) from aac_employee where ad=?)";
					String sql_vp = "select nachn||vorna as vp from aac_employee where ad= (select upper(vp_num) from aac_employee where ad=?)";
					List<Object> params_value = new ArrayList<>();
					params_value.add(value);
					List<Map<String,Object>> list_director = gdao.executeJDBCSqlQuery(sql_director, params_value);
					List<Map<String,Object>> list_vp = gdao.executeJDBCSqlQuery(sql_vp, params_value);
					//content.append("<td>").append(map.get("ORDERNUM").toString()).append("</td>");// 单号
					body.append("<td>").append(map.get("EMPNAME")!=null?map.get("EMPNAME").toString():"").append("</td>");// 姓名
					body.append("<td>").append(map.get("EMPNUM")!=null?map.get("EMPNUM").toString():"").append("</td>");// 工号
					body.append("<td>").append(map.get("STAIRDEPARTMENT")!=null?map.get("STAIRDEPARTMENT").toString():"").append("</td>");// 一级部门
					body.append("<td>").append(map.get("SECONDDEPARTMENT")!=null?map.get("SECONDDEPARTMENT").toString():"").append("</td>");// 二级部门
					body.append("<td>").append(map.get("THREEDEPARTMENT")!=null?map.get("THREEDEPARTMENT").toString():"").append("</td>");// 三级部门
					body.append("<td>").append(list_director.size()>0?list_director.get(0).get("DIRECTOR").toString():"").append("</td>");// 部门总监
					body.append("<td>").append(list_vp.size()>0?list_vp.get(0).get("VP").toString():"").append("</td>");// 部门VP
					body.append("<td>").append(map.get("CREATETIME")!=null?map.get("CREATETIME").toString():"").append("</td>");// 提出日期
					body.append("<td>").append(map.get("VACATEINFODATE")!=null?map.get("VACATEINFODATE").toString():"").append("</td>");// 预计离职日期
					body.append("<td>").append(map.get("TERMDATE")!=null?map.get("TERMDATE").toString():"").append("</td>");// 离职办理日期
					body.append("<td>").append(map.get("RESIGNCAUSE_INFO_N")!=null?map.get("RESIGNCAUSE_INFO_N").toString():"").append("</td>");// 离职类型
					body.append("<td>").append(map.get("RESIGNCAUSE")!=null?map.get("RESIGNCAUSE").toString():"").append("</td>");// 离职原因
					body.append("<td>").append(map.get("HR19_OVERALL")!=null?map.get("HR19_OVERALL").toString():"").append("</td>");// HR综合意见
					body.append("<td>").append(map.get("TEXT_AGREEORNOTTHAT_DISPLAY")!=null?map.get("TEXT_AGREEORNOTTHAT_DISPLAY"):"").append("</td>");// 是否竞业
					body.append("<td>").append(map.get("COMPETITIONSTARTTIME")!=null?map.get("COMPETITIONSTARTTIME").toString():"").append("</td>");// 竞业限制开始时间
					body.append("<td>").append(map.get("COMPETITIONENDTIME")!=null?map.get("COMPETITIONENDTIME").toString():"").append("</td>");// 竞业限制结束时间
					body.append("<td>").append(map.get("CONTINUESIGNMONTH")!=null?map.get("CONTINUESIGNMONTH").toString():"").append("</td>");// 离职竞业期(月)
					body.append("</tr>");

				}
				body.append("</tbody></table>");
				String[] split = map_place.get("EAA1").toString().split(";");
				Map<String, Object> config = sysConfigurationService
						.getSysConfiguration(SysConfigurationTypeName.SYSTEM_CONFIG);
				String account = config.get("account") + "";
				String password = config.get("password") + "";
				if (list.size() > 0) {
					for (int y = 0; y < split.length; y++) {
						String empid = split[y].substring(split[y].indexOf("(")+1, split[y].indexOf(")"));
						String sql_emp = "select email,nick_name from org_employee where emp_num = ?";
						List<Object> params_emp = new ArrayList<>();
						params_emp.add(empid);
						List<Map<String,Object>> list_emp = gdao.executeJDBCSqlQuery(sql_emp, params_emp);
						Map<String, Object> map_emp = list_emp.get(0);
						if(map_emp.get("email")==null){
							new EmailNotificationUtil().execute(account, password, "pengyuhuan@aactechnologies.com",
									map_emp.get("NICK_NAME").toString()+",员工表对应的邮件为空", head.toString()+body.toString());
						}else{
						new EmailNotificationUtil().execute(account, password, map_emp.get("EMAIL").toString(),
								map_place.get("META_VALUE").toString()+"地区,"+"离职汇总报表发送表格内容", head.toString()+body.toString());
						}
					}
				}
		}
	}
}
