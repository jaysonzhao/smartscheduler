package com.gzsolartech.schedule.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gzsolartech.smartforms.service.BaseDataService;

@Service
public class IT0002Service extends BaseDataService {

	private static final long serialVersionUID = 1L;

	@Autowired
	private SendEmailService sendEmailService;
	
	static final int START_DAY = 1;//过期N天后开始发邮件
	static final int INTERVAL_DAY = 1;//每N天发一次邮件
	static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	static final String TITLE = "物品放行单物品归还提醒";
	
	public void execute() {
		Calendar day = Calendar.getInstance();
		List<Map<String, Object>> doc = getDocs(day);
		List<Map<String, String>> email = getEmail(doc);
		sendEmail(email);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getDocs(Calendar day) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		List<Object> params = new ArrayList<Object>();
		try {
			String strDate = FORMAT.format(day.getTime());
			String sql = "select dat.* from"
					+ "(select doc.DOCUMENT_ID documentId,"
					+ "doc.APP_ID appId,"
					+ "extractvalue(doc.document_data, '//root/orderNum') orderNum,"
					+ "extractvalue(doc.document_data, '//root/empName') empName,"
					+ "extractvalue(doc.document_data, '//root/email') creatorAddress,"
					+ "e.email directLeaderAddress,"
					+ "extractvalue(doc.document_data, '//root/TakeOutMan') takeOutMan,"
					+ "extractvalue(tab.row_data, '//root/row/PlanBackDate') planBackDate,"
					+ "extractvalue(tab.row_data, '//root/row/BackDate') backDate,"
					+ "ceil(to_date(? , 'yyyy-MM-dd') -"
					+ " to_date(extractvalue(tab.row_data, '//root/row/PlanBackDate') ||"
					+ " ' ' || '01:01:01', 'yyyy-MM-dd hh24:mi:ss')) daydiff,"
					+ "row_number() over(partition by doc.DOCUMENT_ID order by doc.DOCUMENT_ID) rn"
					+ " FROM dat_document doc, dat_table_row tab, org_employee e"
					+ " where doc.DOCUMENT_ID = tab.DOCUMENT_ID"
					+ " AND instr(extractvalue(doc.document_data, '//root/DirectLeader_Num'), e.EMP_NUM) > 0"
					+ " AND doc.FORM_NAME = 'FM_IT0002'"
					+ " AND extractvalue(doc.document_data, '//root/InSecurity') = 'Yes'"
					+ " AND extractvalue(doc.document_data, '//root/IsNeedBack') = 'Yes'"
					+ " AND extractvalue(tab.row_data, '//root/row/text_IsNeedBack_display') = '是'"
					+ " AND extractvalue(tab.row_data, '//root/row/BackDate') IS NULL"
					+ " AND isdate(extractvalue(tab.row_data, '//root/row/PlanBackDate'), 'yyyy-MM-dd') = 1) dat"
					+ " where dat.daydiff >= ?"
					+ " AND mod(dat.daydiff - ?, ?+1) = 0"
					+ " AND rn = 1";
			params.add(strDate);
			params.add(START_DAY);
			params.add(START_DAY);
			params.add(INTERVAL_DAY);
			data = gdao.executeJDBCSqlQuery(sql, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public List<Map<String, String>> getEmail(List<Map<String, Object>> data) {
		List<Map<String, String>> mail = new ArrayList<Map<String, String>>();
		Map<String, String> detail = new HashMap<String, String>();
		String orderNum = null;
		String url = null;
		String appId = null;
		String documentId = null;
		String address = null;
		String empName = null;
		String takeOutMan = null;
		String content = null;
		String copy = null;
		for (int i = 0; i < data.size(); i++) {
			detail = new HashMap<String, String>();
			orderNum = (String)data.get(i).get("ORDERNUM");
			appId = (String)data.get(i).get("APPID");
			documentId = (String)data.get(i).get("DOCUMENTID");
			address = (String)data.get(i).get("CREATORADDRESS");
			copy = (String)data.get(i).get("DIRECTLEADERADDRESS");
			empName = (String)data.get(i).get("EMPNAME");
			takeOutMan = (String)data.get(i).get("TAKEOUTMAN");
			if (appId==null || documentId==null || address==null || copy==null) {
				continue;
			}
			url = "http://bpm.aac.com/console/template/engine/opendocument/" + appId + "/" + documentId + ".xsp";
			content = setContent(content, empName, takeOutMan, url, orderNum);
			detail.put("orderNum", orderNum);
			detail.put("content", content);
			detail.put("address", address);
			detail.put("copy", copy);
			mail.add(detail);
		}
		return mail;
	}
	
	public String setContent(String content, String empName, String takeOutMan, String url, String orderNum) {
		String temp = "";
		if (content != null) {
			temp = content;
		}
		String result = temp + empName + " 申请的物品放行单部分物品 " + takeOutMan + " 携带出去后还未归还，请及时归还，可点击如下链接查看："
				+ "<a href='" + url + "'>" + orderNum + "</a><br />";
		return result;
	}
	
	public void sendEmail(List<Map<String, String>> mail) {
		String address = null;
		String content = null;
		String copy = null;
		for (Map<String, String> m : mail) {
			address = m.get("address");
			content = m.get("content");
			copy = m.get("copy");
			sendEmailService.sendEmail(address, TITLE, content, copy);
		}
	}
}
