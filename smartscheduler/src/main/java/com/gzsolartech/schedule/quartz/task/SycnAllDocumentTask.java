package com.gzsolartech.schedule.quartz.task;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gzsolartech.bpmportal.entity.DocSycnFailRecord;
import com.gzsolartech.bpmportal.entity.DocSycnUpdateRecord;
import com.gzsolartech.bpmportal.entity.ESIndex;
import com.gzsolartech.schedule.service.SycnAllDocumentService;
import com.gzsolartech.smartforms.constant.SysConfigurationTypeName;
import com.gzsolartech.smartforms.entity.DatDocument;
import com.gzsolartech.smartforms.entity.DatDocumentRight;
import com.gzsolartech.smartforms.service.DatDocumentRightService;
import com.gzsolartech.smartforms.service.DatDocumentService;
import com.gzsolartech.smartforms.service.DatTableRowService;
import com.gzsolartech.smartforms.service.SysConfigurationService;
import com.gzsolartech.smartforms.utils.HttpClientUtils;
import com.gzsolartech.smartforms.utils.XmlDataUtils;

/**
 * 同步所有文档信息
 * @author IT
 *
 */
@Component
public class SycnAllDocumentTask extends BaseTask  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String PREFIX="sycn";
	private static final Logger LOG = LoggerFactory
			.getLogger(SycnAllDocumentTask.class);

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		this.applicationContext=applicationContext;
		
	}

	@Override
	public void run(String jobId) {
		// TODO Auto-generated method stub
		
		SycnAllDocumentService serivce = 
				applicationContext.getBean(SycnAllDocumentService.class);
		
		SysConfigurationService sysConfigurationService = 
				applicationContext.getBean(SysConfigurationService.class);
		
		DatTableRowService datTableRowService = 
				applicationContext.getBean(DatTableRowService.class);
		
		DatDocumentRightService datDocumentRightService = 
				applicationContext.getBean(DatDocumentRightService.class);
		DatDocumentService datDocumentService = 
				applicationContext.getBean(DatDocumentService.class);
		
		Map<String, Object> config = sysConfigurationService
				.getSysConfiguration(SysConfigurationTypeName.SYSTEM_CONFIG);
		
		
		 
		
		
		HttpClientUtils clientUtils=new HttpClientUtils();
		String uuid=UUID.randomUUID().toString();

		
		List<DatDocument> docs=new ArrayList<DatDocument>();
		List<DocSycnFailRecord>  fail=new ArrayList<DocSycnFailRecord>();
		//获取上次更新时间
		DocSycnUpdateRecord record=	serivce.getUpdateInfo();
		if(record==null){
			//获取整个doc信息
			 docs=serivce.getAllDocument();
		}else{
			//根据上一次更新时间，获取之间文档信息
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String updateTime=format.format(record.getUpdateTime());
			docs=serivce.getDocumentByupdateTime(updateTime);
			
		}
		
		for (DatDocument datDocument : docs) {
				
				
				//得到docdate
				 String docdata = XmlDataUtils.toString(datDocument
						.getDocumentData());
				 DatDocument datDocument_= datDocumentService.loadWithDatApp(datDocument.getDocumentId());
				 String appName=datDocument_.getDatApplication().getAppName();
				 //清空docdate
				 datDocument.setDocumentData(null);
				 datDocument.setDatApplication(null);
				 JSONObject  jsonObject= JSONObject.parseObject(datDocument.toString());	
				 jsonObject.put("create_Time", datDocument.getCreateTime().getTime());
				 jsonObject.put("update_Time", datDocument.getUpdateTime().getTime());
				 
				 JSONObject jsoDocData = XmlDataUtils.xmlToJson(docdata);
				 recJson(jsoDocData);
				 if (jsoDocData.containsKey("root")) {
							jsonObject.put("root", JSONObject.parse(jsoDocData.get("root").toString()));
				 }
				
				
				 Map<String, Object>  tableRows= datTableRowService.getDatas(datDocument.getDocumentId());
				 JSONObject itemJSONObj = JSONObject.parseObject(JSON.toJSONString(tableRows));
				 recJson(itemJSONObj);
			     jsonObject.put("tableRows", itemJSONObj);
			     
			     
				Map<String, Object> acl = new HashMap<String, Object>();
				List<String> empId = new ArrayList<String>();
				List<String> roleId = new ArrayList<String>();
				List<String>   depId =new ArrayList<String>();
			     
				List<DatDocumentRight> acls=		datDocumentRightService.getDocumentRights(datDocument.getDocumentId());
				for (int i = 0; i < acls.size(); i++) {
					if(acls.get(i).getRightObjectType().equals("employee")){
						empId.add(acls.get(i).getRightObjectId());
					}else if(acls.get(i).getRightObjectType().equals("department")){
						depId.add(acls.get(i).getRightObjectId());
					}else{
						roleId.add(acls.get(i).getRightObjectId());
					}
				}
				acl.put("empId", empId);
				acl.put("depId", depId);
				acl.put("roleId", roleId);
				jsonObject.put("acls",acl);
				
			    
			 //索引必须为小写
				 //得到请求的地址ES
			 String url=config.get("searchCfg").toString()+"/"+
					PREFIX+(appName+datDocument.getFormName()).toLowerCase();
			  String msg="";
			 if(serivce.getIndex(datDocument.getFormName().toLowerCase())){
				 url+="/docinfo/"+datDocument.getDocumentId();
				 //新增文档
				 msg=clientUtils.doPut(url,  JSON.toJSONString(jsonObject)).getMsg();
			 }else{
				 //删除es索引
				String  delUrl=url+"/docinfo";
				clientUtils.doDel(delUrl).getMsg();
				 //新建
				 clientUtils.doPut(url,"{}").getMsg();
				 //关闭索引
				 String closeUrl=url+"/_close";
				clientUtils.doPut(closeUrl,"{}").getMsg();
				 //基本映射
				 String settingsUrl=url+"/_settings";
				 //todo映射文
				 String mapping = 
						 "{\"settings\":"
								 + "{"
								 + " \"index.mapping.total_fields.limit\":30000,"
								 + "\"index\":{\"max_result_window\":100000000 }"
								 + "},"
						 + "\"mappings\":"
						 + "	{\"docinfo\":"
						 + "			{\"dynamic\":true,"
						 + "			 \"properties\":"
						 + "					{\"attachments.fileContext\":"
						 + "						{\"type\":\"attachment\","
						 + "						 \"fields\":"
						 + "								{\"content\":{"
						 + "									\"language\":\"zh_cn\","
						 + "									\"type\":\"text\","
						 + "									\"analyzer\":\"ik_smart\","
						 + "									\"search_analyzer\":\"ik_smart\","
						 + "									\"include_in_all\":\"true\","
						 + "									\"boost\":8,"
						 + "									\"term_vector\":\"with_positions_offsets\","
						 + "									\"store\":true}"
						 + "								}"
						 + "						},"
						 + "			   \"create_Time\":{\"type\":\"date\"},"
						 + "			   \"update_Time\":{\"type\":\"date\"}"
						 + "				   }"
						 + "			}"
						 + "	}"
						 + "}";
				clientUtils.doPut(settingsUrl,mapping).getMsg();
				 
				//打开索引
				 String openUrl=url+"/_open";
				 clientUtils.doPut(openUrl,"{}").getMsg();
				 //新增文档
				 url+="/docinfo/"+datDocument.getDocumentId();
				 msg=clientUtils.doPut(url,  JSON.toJSONString(jsonObject)).getMsg();
				 
				 //新增索引记录
				 ESIndex index=new ESIndex();
				 index.setId(UUID.randomUUID().toString());
				 index.setCreateTime(new Timestamp(System
							.currentTimeMillis()));
				 index.setIndexs(datDocument.getFormName().toLowerCase());
				 serivce.saveIndex(index);
			 }
			 
			 try {
				   JSONObject info=JSONObject.parseObject(msg);
				    if(info.containsKey("_shards")){
				    	if(info.getJSONObject("_shards").getInteger("successful")<1){
				    		fail.add(failRecord(datDocument.getDocumentId()));
				    	}
				    }else{
				    	fail.add(failRecord(datDocument.getDocumentId()));
				    }
			} catch (Exception e) {
				fail.add(failRecord(datDocument.getDocumentId()));
				 try {
						Thread.sleep(5000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}
		}
		
		 //新增记录
			DocSycnUpdateRecord  newRecord  =new DocSycnUpdateRecord();
			newRecord.setId(uuid);
			newRecord.setUpdateTime(new Timestamp(System
					.currentTimeMillis()));
			newRecord.setCreateTime(new Timestamp(System
					.currentTimeMillis()));
		    serivce.save(newRecord);
		 //记录失败的记录
			 if(fail.size()>0){
				 serivce.saveFail(fail);
			 }
	}

	
	

	public static void recJson(JSONObject jso){
		for (String key :jso.keySet()) {
			Object obj=jso.get(key);
			if (obj instanceof JSONArray) {
				recJsonArray(obj);
			} else if (obj instanceof JSONObject) {
				recJson((JSONObject)obj);
			} else  {
				jso.put(key, " "+obj);
			}
		}
	}
	
	
	public static void recJsonArray(Object obj){
		
		if (obj instanceof JSONArray) {
		JSONArray temp=	(JSONArray)obj;
			for (int i = 0; i < temp.size(); i++) {
				if (temp.get(i) instanceof JSONArray) {
					recJsonArray(temp.get(i));
				}else if( temp.get(i) instanceof JSONObject){
					recJson((JSONObject)temp.get(i));
				}
			}
		} else if (obj instanceof JSONObject) {
			recJson((JSONObject)obj);
		} 
		
	}
	
	
	
	public DocSycnFailRecord failRecord(String documentId){
		DocSycnFailRecord  failInfo  =new DocSycnFailRecord();
		failInfo.setId(UUID.randomUUID().toString());
		failInfo.setDocumentId(documentId);
		failInfo.setCreateTime(new Timestamp(System
				.currentTimeMillis()));
		return failInfo;
	}
	
	
	
}
