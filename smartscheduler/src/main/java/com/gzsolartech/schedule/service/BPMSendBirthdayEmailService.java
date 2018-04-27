package com.gzsolartech.schedule.service;

/*
 * BY DYK
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.ArrayList;
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
public class BPMSendBirthdayEmailService extends BaseDataService {
    /** 中国公民身份证号码最小长度。 */
    public  final int CHINA_ID_MIN_LENGTH = 15;
    /** 中国公民身份证号码最大长度。 */
    public  final int CHINA_ID_MAX_LENGTH = 18;
	@Autowired
	private SysConfigurationService sysConfigurationService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BPMSendBirthdayEmailService.class);
	
	
	/**
	 * 
	 * @return address
	 */
	public void prepareSendEmail() {
		//获取当前日期
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String SendDate = df.format(new Date());
		String[] nowDate = SendDate.split("-");
		String birthdayDate = "";
		for ( int i=1;i<nowDate.length;i++ ) {
			birthdayDate += nowDate[i];
		}
		List<Map<String, String>> datas = getAdressesByNowDate(birthdayDate);
		String title = null;
		String content = "birthDayCard";
		String address = null;
		String nickName = null;
		for (int i=0;i<datas.size();i++){
			address = datas.get(i).get("EMAIL");
			nickName =  datas.get(i).get("NICK_NAME");
			title = "【"+nickName+"生日快乐】特别的日子，特别的祝福！";
			sendEmail(address, title, content);
		}
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
	
	/**
	 * 获取生日在今天的员工的adress
	 * @param birthdayDate
	 * @return addresss
	 */
	public List<Map<String, String>> getAdressesByNowDate (String birthdayDate){
		String sql = "select nick_name,email,identity_card from org_employee";
		List<Map<String, String>> list = gdao.executeJDBCSqlQuery(sql);
		List<Map<String, String>> resList = new ArrayList<Map<String, String>>();
		String identityCard = null;
		for (int i=0;i<list.size();i++) {
			Map<String, String> tempMap = new HashMap<String, String>();
			identityCard = (String) list.get(i).get("IDENTITY_CARD");
			if ( identityCard != null && getBirthByIdCard(identityCard).equals(birthdayDate)){
				tempMap.put("NICK_NAME", (String) list.get(i).get("NICK_NAME"));
				tempMap.put("EMAIL", (String) list.get(i).get("EMAIL"));
				resList.add(tempMap);
			}
		}
		return resList;
	}
	
    /**
     * 根据身份编号获取生日
     *
     * @param idCard 身份编号
     * @return 生日(yyyyMMdd)
     */
    public static String getBirthByIdCard(String idCard) {
    	String birthDay = null;
    	if ( idCard.length() == 18 ){
    		birthDay = idCard.substring(10, 14);
    	} else if ( idCard.length() == 15 ) {
    		birthDay = "19" + idCard.substring(9, 12);
    	}
    	return birthDay;
    }
}
