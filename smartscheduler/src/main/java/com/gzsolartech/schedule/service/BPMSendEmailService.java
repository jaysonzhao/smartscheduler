package com.gzsolartech.schedule.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
public class BPMSendEmailService extends BaseDataService {
	@Autowired
	private SysConfigurationService sysConfigurationService;
	@Autowired
	private DocService docService;
	@Autowired
	private BPMSendEmailService bpmSendEmailService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BPMSendEmailService.class);
	
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
	
	public void getMyDocument() {
		IT30sendEmail();
		IT31sendEmail();
	}
	
	/**
	 * IT30笔记本准携证定时邮件
	 * @return
	 */
	public void IT30sendEmail() {
		//获得当前日期+1/7天
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.add(Calendar.DAY_OF_MONTH, 1);
		cal2.add(Calendar.WEEK_OF_YEAR, 1);
		String date1 = df.format(cal1.getTime());
		String date2 = df.format(cal2.getTime());
		String title = "您的准携证即将到期！";
		String content1 = "您的准携证将于" + date1 + "到期，如需续申请，请及时在BPM上提交申请单。";
		String content2 = "您的准携证将于" + date2 + "到期，如需续申请，请及时在BPM上提交申请单。";
		String keys1 = "{tosend:\"todo\",periodEnd:" + date1 + "}";
		String keys2 = "{tosend:\"todo\",periodEnd:" + date2 + "}";
		List<Map<String, Object>> missionlist1 = docService.getAllDocumentBykeys("APP_IT", null, "IT30", keys1, null);
		List<Map<String, Object>> missionlist2 = docService.getAllDocumentBykeys("APP_IT", null, "IT30", keys2, null);
		ArrayList<String> emails1 = new ArrayList<String>();
		ArrayList<String> emails2 = new ArrayList<String>();
		for (Map<String, Object> missionmap1 : missionlist1) {
			emails1.add(String.valueOf(missionmap1.get("email")));
		}
		for (Map<String, Object> missionmap2 : missionlist2) {
			emails2.add(String.valueOf(missionmap2.get("email")));
		}
		for(String address:emails1){
			bpmSendEmailService.sendEmail(address, title, content1);
        }
		for(String address:emails2){
			bpmSendEmailService.sendEmail(address, title, content2);
		}
	}
	
	/**
	 * IT31信息安全房账号及权限申请定时邮件
	 * @return
	 */
	public void IT31sendEmail() {
		//获得当前日期+1/7天
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Calendar cal3 = Calendar.getInstance();
		cal1.add(Calendar.DAY_OF_MONTH, 1);
		cal2.add(Calendar.WEEK_OF_YEAR, 1);
		cal3.add(Calendar.DAY_OF_MONTH, -1);
		String date1 = df.format(cal1.getTime());
		String date2 = df.format(cal2.getTime());
		String date3 = df.format(cal3.getTime());
		String title = "您的信息安全房使用权限即将到期！";
		String content1 = "您的信息安全房使用权限于" + date1 + "到期，如需续申请，请及时在EIP上提交申请单。若到期流程未申请完毕，将关闭您的信息安全房使用权限。";
		String content2 = "您的信息安全房使用权限于" + date2 + "到期，如需续申请，请及时在EIP上提交申请单。若到期流程未申请完毕，将关闭您的信息安全房使用权限。";
		String keys1 = "{emalTemp:\"hasFin\",periodEnd:" + date1 + "}";
		String keys2 = "{emalTemp:\"hasFin\",periodEnd:" + date2 + "}";
		String keys3 = "{emalTemp:\"hasFin\",periodEnd:" + date3 + "}";   
		List<Map<String, Object>> missionlist1 = docService.getAllDocumentBykeys("APP_IT", null, "IT31", keys1, null);
		List<Map<String, Object>> missionlist2 = docService.getAllDocumentBykeys("APP_IT", null, "IT31", keys2, null);
		List<Map<String, Object>> missionlist3 = docService.getAllDocumentBykeys("APP_IT", null, "IT31", keys3, null);
		ArrayList<String> emails1 = new ArrayList<String>();
		ArrayList<String> emails2 = new ArrayList<String>();
		ArrayList<String> emails3 = new ArrayList<String>();
		ArrayList<String> empNames = new ArrayList<String>();
		for (Map<String, Object> missionmap1 : missionlist1) {
			emails1.add(String.valueOf(missionmap1.get("email")));
		}
		for (Map<String, Object> missionmap2 : missionlist2) {
			emails2.add(String.valueOf(missionmap2.get("email")));
		}
		for (Map<String, Object> missionmap3 : missionlist3) {
			emails3.add(String.valueOf(missionmap3.get("accept_email")));
			empNames.add(String.valueOf(missionmap3.get("empName")));
		}
		for(String address:emails1){
			bpmSendEmailService.sendEmail(address, title, content1);
        }
		for(String address:emails2){
			bpmSendEmailService.sendEmail(address, title, content2);
		}
		for(int i=0;i<emails3.size();i++){
			    String address = emails3.get(i);
			    String empName = empNames.get(i);
			    String title3 = empName+"信息安全房使用权限已到期！";
			    String content3 = empName+"信息安全房使用权限已到期，请立即关闭！"; 
			    bpmSendEmailService.sendEmail(address, title3, content3);
		}
	}
}