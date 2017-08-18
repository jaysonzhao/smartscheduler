package com.gzsolartech.schedule.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.gzsolartech.smartforms.entity.bpm.BpmGlobalConfig;
import com.gzsolartech.smartforms.service.bpm.BpmGlobalConfigService;

/**
 * 
 * @ClassName: ExecuteTransferTaskService
 * @Description: 调用平台的特殊转签接口进行待办转签
 * @author wwd
 * @date 2017年8月18日 下午2:35:01
 *
 */
@Service
public class ExecuteTransferTaskService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ExecuteTransferTaskService.class);
	@Autowired
	private BpmGlobalConfigService bpmGlobalConfigService;

	public void execute(String taskId,String documentId,String fromUserId,String targetUser,String note) throws Exception {
		// 获取平台应用的上下文
		String webContent = bpmGlobalConfigService.getWebContext();
		if (!webContent.endsWith("/")) {
			webContent += "/";
		}
		BpmGlobalConfig config=bpmGlobalConfigService.getFirstActConfig();
         transferTask(webContent,config.getBpmAdminName(),config.getBpmAdminPsw(),taskId,documentId,fromUserId,targetUser,note);
	}

	/**
	 * 进行模拟登录转签
	 *@param longinUrl
	 *@return
	 *@throws Exception
	 */
	public void transferTask(String webContent,String account,String password,String taskId,String documentId,String fromUserId,String targetUser,String note) throws Exception {
		String longinUrl = webContent + "console/user/login.action";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		/* 建立HTTP Post连线 */
		HttpPost login = new HttpPost(longinUrl);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// 用户名密码
		params.add(new BasicNameValuePair("username", account));
		params.add(new BasicNameValuePair("password", password));
		login.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpResponse  httpResponse = httpclient.execute(login);
        httpResponse.close();
		String transferUrl=webContent + "console/bpm/adminact/insteadOfTransfer.action";
		HttpPost httpPost = new HttpPost(transferUrl);
		List<NameValuePair> transferParams = new ArrayList<NameValuePair>();
		// 用户名密码
		transferParams.add(new BasicNameValuePair("taskId", taskId));
		transferParams.add(new BasicNameValuePair("note", note));
		transferParams.add(new BasicNameValuePair("documentId", documentId));
		transferParams.add(new BasicNameValuePair("fromUserId", fromUserId));
		transferParams.add(new BasicNameValuePair("targetUser", targetUser));
		httpPost.setEntity(new UrlEncodedFormEntity(transferParams, "UTF-8"));
		CloseableHttpResponse response = httpclient.execute(httpPost);
		String msg = EntityUtils.toString(response.getEntity(), "UTF-8");
		if(StringUtils.isNotBlank(msg)){
			JSONObject tmp=JSONObject.parseObject(msg);
			if(tmp.getBooleanValue("success")==false){
				LOGGER.error("SLA超时转签调用接口异常："+tmp.getString("msg"));
			}
		}
		response.close();
		httpclient.close();
		System.out.println(msg);
	}

}
