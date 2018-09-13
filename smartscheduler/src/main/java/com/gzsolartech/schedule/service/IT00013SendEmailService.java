package com.gzsolartech.schedule.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gzsolartech.bpmportal.util.email.EmailNotificationUtil;
import com.gzsolartech.smartforms.service.BaseDataService;
import com.gzsolartech.smartforms.service.SysConfigurationService;

@Service
public class IT00013SendEmailService extends BaseDataService {

	@Autowired
	private SysConfigurationService sysConfigurationService;

	private static final Logger LOGGER = LoggerFactory.getLogger(IT00013SendEmailService.class);

	/**
	 * 发送给申请人的
	 * 
	 * @return address
	 */
	public void prepareSendEmailToAppliant() {
		List<Map<String, String>> datas = getOpeningPeriodEndDate();
		String title = null;
		String foldername = null;
		String address = null;
		String nickName = null;
		String documentid = null;
		String appid = null;
		String content = null;
		for (int i = 0; i < datas.size(); i++) {
			address = datas.get(i).get("EMAIL");
			nickName = datas.get(i).get("NICK_NAME");
			foldername = datas.get(i).get("FOLDERNAME");
			documentid = datas.get(i).get("DOCUMENTID");
			appid = datas.get(i).get("APPID");
			// 您申请的XX权限将于10日后到期，如需继续使用，请及时续申请。点此可查看上次申请情况：单号链接
			String url = "http://bpmtest.aact.com/console/template/engine/opendocument/" + appid + "/" + documentid + ".xsp";
			content = "您申请的" + foldername + "服务器文件夹访问权限将于10日后到期，如需继续使用，请及时续申请。点此可查看上次申请情况："
					+ "<a href="+url+">单号链接</a>";
			title = foldername + "文件夹访问权限即将到期提醒";
			sendEmail(address, title, content);
		}
	}

	/**
	 * 发送给服务器管理员的
	 * 
	 * @return address
	 */
	public void prepareSendEmailToManager() {
		
		List<Map<String, String>> datas = getEndDateForManager();
		String documentid = null;
		String appid = null;
		String address = null;
		String title = null;
		String content = null;
		String nickName = null;
		String jobnumber = null;
		String empnum = null;
		String empcompany = null;
		String empdept = null;
		String mobile = null;
		String applypermission = null;
		String starttime = null;
		String endtime = null;
		String foldername = null;
		for (int i = 0; i < datas.size(); i++) {
			documentid = datas.get(i).get("DOCUMENTID");
			appid = datas.get(i).get("APPID");
			address = datas.get(i).get("serverManagerEmail");
			nickName = datas.get(i).get("EMPNAME");
			jobnumber = datas.get(i).get("JOBNUMBER");
			empnum = datas.get(i).get("EMPNUM");
			empcompany = datas.get(i).get("EMPCOMPANY");
			empdept = datas.get(i).get("EMPDEPT");
			mobile = datas.get(i).get("MOBILE");
			applypermission = datas.get(i).get("APPLYPERMISSION");
			starttime = datas.get(i).get("STARTTIME");
			endtime = datas.get(i).get("ENDTIME");
			foldername = datas.get(i).get("FOLDERNAME");
			String url = "http://bpmtest.aact.com/console/template/engine/opendocument/" + appid + "/" + documentid + ".xsp";
			// 您申请的XX权限将于10日后到期，如需继续使用，请及时续申请。点此可查看上次申请情况：单号链接
			content = "	员工编号：" + jobnumber +"</br>"
					 +" 域帐号：" + empnum +"</br>"
					 +"	所属公司：" + empcompany +"</br>"
					+"	所属部门：" + empdept +"</br>"
					+"	联系方式：" + mobile +"</br>"
					+"	服务器文件夹权限：" + applypermission +"</br>"
					+"	起始时间：" + starttime +"</br>"
					+"	关闭时间：" + endtime +"</br>"
					+"	BPM申请单链接：</br>" 
					+ "<a href="+url+">请点击这里打开</a>";
			title = foldername + "文件夹访问权限关闭通知";
			sendEmail(address, title, content);

		}
	}

	/**
	 * 发送邮件
	 * 
	 * @param address
	 * @param title
	 * @param content
	 * @return
	 */
	public void sendEmail(String address, String title, String content) {
		Map<String, Object> results = new HashMap<String, Object>();
		try {
			Map<String, Object> config = sysConfigurationService.getSysConfiguration("syscfg");
			final String account = config.get("account") + "";
			final String password = config.get("password") + "";
			new EmailNotificationUtil().execute(account, password, address, title, content);
			results.put("state", true);
		} catch (Exception e) {
			LOGGER.error("发送邮件异常", e);
			results.put("state", false);
			results.put("error", e.getMessage());
		}
	}

	/**
	 * 获取发送给申请人的邮件信息
	 * 
	 * @return resList
	 */
	public List<Map<String, String>> getOpeningPeriodEndDate() {
		String sql = "select t.document_id DOCUMENTID,t.app_id APPID,extractvalue(t.document_data,'/root/IT00013_folderNamePath') folderName,"
				+ " extractvalue(t.document_data,'/root/email') email,"
				+ " extractvalue(t.document_data,'/root/empName') empName,"
				+ " extractvalue(t.document_data,'/root/IT00013_openingPeriodEnd') EndDate"
				+ " from dat_document t where t.document_id in"
				+ " (select d.document_id from dat_document d where d.form_name = 'IT00013')";
		List<Map<String, String>> list = gdao.executeJDBCSqlQuery(sql);
		List<Map<String, String>> resList = new ArrayList<Map<String, String>>();
		String EndDate = null;
		for (Map<String, String> m : list) {
			Map<String, String> tempMap = new HashMap<String, String>();
			String arrivalEndDate = m.get("ENDDATE");
			if (arrivalEndDate == null) {
				arrivalEndDate = "2018-1-1";
			}
			// 获取当前日期
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
			int days = 0;
			try {
				Date date3 = format.parse(arrivalEndDate);
				Date date2 = new Date(date3.getTime());
				Date date1 = new Date(date.getTime());
				days = differentDays(date1, date2);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (days == 10) {
				tempMap.put("NICK_NAME", m.get("EMPNAME"));
				tempMap.put("EMAIL", m.get("EMAIL"));
				tempMap.put("FOLDERNAME", m.get("FOLDERNAME"));
				tempMap.put("DOCUMENTID", m.get("DOCUMENTID"));
				tempMap.put("APPID", m.get("APPID"));
				resList.add(tempMap);
			}
		}
		return resList;
	}

	/**
	 * date2比date1多的天数 比较两个日期的相差天数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public int differentDays(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		int day1 = cal1.get(Calendar.DAY_OF_YEAR);
		int day2 = cal2.get(Calendar.DAY_OF_YEAR);

		int year1 = cal1.get(Calendar.YEAR);
		int year2 = cal2.get(Calendar.YEAR);
		if (year1 != year2) // 同一年
		{
			int timeDistance = 0;
			for (int i = year1; i < year2; i++) {
				if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) // 闰年
				{
					timeDistance += 366;
				} else // 不是闰年
				{
					timeDistance += 365;
				}
			}
			return (timeDistance + (day2 - day1));
		} else // 不同年
		{
			//System.out.println("判断day2 - day1 : " + (day2 - day1));
			return (day2 - day1);
		}
	}

	/**
	 * 获取发送给服务器管理员的邮件信息
	 * 
	 * @return resList
	 */
	public List<Map<String, String>> getEndDateForManager() {
		String sql = "select t.document_id DOCUMENTID,t.app_id APPID,"
				+ " extractvalue(t.document_data,'/root/IT00013_folderNamePath') FOLDERNAME,"
				+ " extractvalue(t.document_data,'/root/empName') EMPNAME,"
				+ " extractvalue(t.document_data,'/root/IT00013_employeeId') JOBNUMBER,"
				+ " extractvalue(t.document_data,'/root/empCompany') EMPCOMPANY,"
				+ " extractvalue(t.document_data,'/root/IT00013_regionalAccount') EMPNUM,"
				+ " extractvalue(t.document_data,'/root/empDept') EMPDEPT,"
				+ " extractvalue(t.document_data,'/root/mobile') MOBILE,"
				+ " extractvalue(t.document_data,'/root/text_IT00013_applyPermission_display') APPLYPERMISSION,"
				+ " extractvalue(t.document_data,'/root/IT00013_openingPeriodStart') STARTTIME,"
				+ " extractvalue(t.document_data,'/root/IT00013_openingPeriodEnd') ENDTIME,"
				+ "	EXTRACTVALUE(T.DOCUMENT_DATA,'/root/serverManagerName') SERVERMANAGERNAME"
				+ " from dat_document t where t.document_id in "
				+ " (select d.document_id from dat_document d where d.form_name = 'IT00013')";
		List<Map<String, String>> list = gdao.executeJDBCSqlQuery(sql);
		List<Map<String, String>> resList = new ArrayList<Map<String, String>>();
		for (Map<String, String> m : list) {
			Map<String, String> tempMap = new HashMap<String, String>();
			String EndDate = m.get("ENDTIME");
			if (EndDate == null) {
				EndDate = "2018-1-1";
			}
			// 获取当前日期
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String SendDate = df.format(new Date());
			String[] nowDate = SendDate.split("-");
			String StrnowDate = "";
			for (int i = 0; i < nowDate.length; i++) {
				StrnowDate += nowDate[i];
			}
			String[] ArrEndDate = EndDate.split("-");
			String StrEndDate = "";
			for (int i = 0; i < ArrEndDate.length; i++) {
				StrEndDate += ArrEndDate[i];
			}
			String org_employeeSql = "select t.email as EMAIL from org_employee t where t.emp_num = ?";
			List<Object> params = new ArrayList<Object>();
			List<Map<String, String>> data = new ArrayList<Map<String, String>>();
			if (StrnowDate.equals(StrEndDate)) {
				tempMap.put("DOCUMENTID", m.get("DOCUMENTID"));
				tempMap.put("APPID", m.get("APPID"));
				tempMap.put("FOLDERNAME", m.get("FOLDERNAME"));
				tempMap.put("EMPNAME", m.get("EMPNAME"));
				tempMap.put("JOBNUMBER", m.get("JOBNUMBER"));
				tempMap.put("EMPNUM", m.get("EMPNUM"));
				tempMap.put("EMPCOMPANY", m.get("EMPCOMPANY"));
				tempMap.put("EMPDEPT", m.get("EMPDEPT"));
				tempMap.put("MOBILE", m.get("MOBILE"));
				tempMap.put("APPLYPERMISSION", m.get("APPLYPERMISSION"));
				tempMap.put("STARTTIME", m.get("STARTTIME"));
				tempMap.put("ENDTIME", m.get("ENDTIME"));
				String SERVERMANAGERNAME = m.get("SERVERMANAGERNAME");
				String NewStr = SERVERMANAGERNAME.substring(SERVERMANAGERNAME.indexOf("(")+1, SERVERMANAGERNAME.lastIndexOf(")"));
				params.add(NewStr);
				data = gdao.executeJDBCSqlQuery(org_employeeSql, params);
				String serverManagerEmail = "";
				for(Map<String, String> email : data){
					serverManagerEmail = email.get("EMAIL");
				}
				tempMap.put("serverManagerEmail", serverManagerEmail);
				resList.add(tempMap);
			}
		}
		return resList;
	}
}
