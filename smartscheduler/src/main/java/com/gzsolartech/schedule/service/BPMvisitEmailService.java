package com.gzsolartech.schedule.service;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.drools.compiler.lang.dsl.DSLMapParser.variable_definition_return;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.sym.Name;
import com.gzsolartech.bpmportal.util.email.EmailNotificationUtil;
import com.gzsolartech.smartforms.service.BaseDataService;
import com.gzsolartech.smartforms.service.SysConfigurationService;

@Service
public class BPMvisitEmailService extends BaseDataService {
	@Autowired
	private SysConfigurationService sysConfigurationService;
	@Autowired
	private BPMSendEmailService bpmSendEmailService;
	@Autowired
	private Doc2Service doc2Service;
	private static final Logger LOGGER = LoggerFactory.getLogger(BPMvisitEmailService.class);
	
	
	/**
	 * 
	 * @return address
	 */
	public void IT0001sendEmail() {
		//获得当前日期
		String address = "w@aactechnologies.com";//发送邮件接收地址
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.DAY_OF_MONTH, 1);
		String date1 = df.format(cal1.getTime());
		String title = "访问人员到期";
		String employeeId;
		String endTime,orderNum;
		String employeeName;
		String content1 = "你好,\r";
		
//		String keys1 = "{ShowSTUendTime:" + date1 + ",__instanceStatus:\"STATE_FINISHED\"}"; // 获取时间条件
		List<Map<String, Object>> missionlist1 = doc2Service.getAllDocumentBykeys();
		
		ArrayList<String> emails1 = new ArrayList<String>();
		for (Map<String, Object> missionmap1 : missionlist1) {
			String appid,docuid,http;
			employeeId = String.valueOf(missionmap1.get("EMPLOYEEID"));
			employeeName = String.valueOf(missionmap1.get("EMPLOYEENAME"));
			endTime = String.valueOf(missionmap1.get("ENDTIME"));
			orderNum = String.valueOf(missionmap1.get("ORDERNUM"));
			appid = String.valueOf(missionmap1.get("APPID"));
			docuid = String.valueOf(missionmap1.get("DOCUID"));
			http="bpmtest.aact.com"+"/console/template/engine/opendocument"
					+ "/"+appid+"/"+docuid+".xsp";
			if (employeeName!=null) {
				content1 += "\r" + employeeName +"申请的访问时间"+ endTime + "到期，请及时关闭其访问权限："+
			"BPM申请单链接：请点击这里打开<a href='"+http+"'>" + orderNum + ""+"</a>"+"\r\r\r\r          ";
			}
					
		}
		   
			bpmSendEmailService.sendEmail(address, title, content1);
	
		
	}
	
	/**
	 * 发送邮件
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
}
