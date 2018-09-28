package com.gzsolartech.schedule.quartz.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gzsolartech.bpmportal.entity.ApplySycnFailRecord;
import com.gzsolartech.bpmportal.entity.ApplySycnUpdateRecord;
import com.gzsolartech.bpmportal.util.ApplySycnConstant;
//import com.gzsolartech.bpmportal.util.Xwpf;
import com.gzsolartech.schedule.service.PU52Service;
import com.gzsolartech.smartforms.constant.AppAclObjectType;
import com.gzsolartech.smartforms.entity.DatDocumentRight;
import com.gzsolartech.smartforms.service.DatDocumentRightService;
import com.gzsolartech.smartforms.utils.HttpClientUtils;
/**
 * PU52所有单调度
 * @author solar
 *
 */
@Component
@DisallowConcurrentExecution
public class PU52AllApplyTask  extends BaseTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext=applicationContext;
		
	}

	public final  static String  ALLAPPLYTYPE="ALL";
	public final  static String  PARTAPPLYTYPE="PART";
	
	@Override
	public void run(String jobId) {
     /*	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		System.out.println("执行任务调度"+df.format(new Date()));// new Date()为获取当前系统时间
*/	 
		PU52Service pu52Serivce = 
				applicationContext.getBean(PU52Service.class);
		
		
		//获取上次更新时间
		HttpClientUtils clientUtils=new HttpClientUtils();
		try {
		Boolean result=false;
		String uuid=UUID.randomUUID().toString();
		ApplySycnUpdateRecord  newRecord  =getRecord(result,uuid);
		
		List<ApplySycnFailRecord>  fail=new ArrayList<ApplySycnFailRecord>();
		ApplySycnUpdateRecord record=	pu52Serivce.getUpdateInfoByModular(ApplySycnConstant.PU52FROMNAME);
		List<Map<String,Object>>  list=new ArrayList<Map<String,Object>>();
		pu52Serivce.savePu52Record(newRecord);
		 
			if(record==null){
				 //如果上次更新时间为空的情况下则执行全表查询写入
				 list=pu52Serivce.getAllappLy(ALLAPPLYTYPE,null); 
			}else{
				//如果不为空的情况下，则安装更新时间更新，当时间大于记录表中的时间的内容执行更新/新增功能
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String updateTime=format.format(record.getUpdateTime());
				list=pu52Serivce.getAllappLy(PARTAPPLYTYPE,updateTime);
			}
			
			 for (int i = 0; i < list.size(); i++) {
				 	//docid 不可能为空，所以直接转换成字符串
				 	String docId=String.valueOf(list.get(i).get("DOCUMENT_ID"));
				 	//根据文档id获取文档内容
				 	//List<Map<String, String>> attachments = getAttachments(docId);
				 	//list.get(i).put("attachments", attachments);
					Map<String, List<String>>  acls=   getAcl(docId);
					list.get(i).put("acls", acls);
					 
					//写入搜索引擎
					String url=	ApplySycnConstant.PU52INDEXPATH+docId;
					String msg=clientUtils.doPut(url,  JSON.toJSONString(list.get(i))).getMsg();
					JSONObject info=JSONObject.parseObject(msg);
			    	if(info.getJSONObject("_shards").getInteger("successful")<1){
			    		result=false;
			    		fail.add(failRecord(docId));
			    	}
			    	
			 }
			
				
			if(fail.size()==0){
				result=true;
			}else{
				pu52Serivce.savePu52FailRecord(fail);
			}
			
			
		 	//添加更新时间日志
			newRecord.setUpdateTime(new Timestamp(System
				.currentTimeMillis()));
			newRecord.setState(result?"0":"1");
			pu52Serivce.updatePu52Record(newRecord);
			
			
		} catch (Exception e) {
//			e.printStackTrace();
			
		}
		
	}

	
	/**
	 * 生成新的同步记录
	 * @param result
	 * @param uuid
	 * @return
	 */
	public ApplySycnUpdateRecord getRecord(boolean result,String uuid){
		ApplySycnUpdateRecord  newRecord  =new ApplySycnUpdateRecord();
		newRecord.setId(uuid);
		newRecord.setModular(ApplySycnConstant.PU52FROMNAME);
		newRecord.setUpdateTime(new Timestamp(System
				.currentTimeMillis()));
		newRecord.setCreateTime(new Timestamp(System
				.currentTimeMillis()));
		newRecord.setState(result?"0":"1");
		return newRecord;
	}
	
	
	public ApplySycnFailRecord failRecord(String documentId){
		ApplySycnFailRecord  failInfo  =new ApplySycnFailRecord();
		failInfo.setId(UUID.randomUUID().toString());
		failInfo.setModular(ApplySycnConstant.PU52FROMNAME);
		failInfo.setDocumentId(documentId);
		failInfo.setCreateTime(new Timestamp(System
				.currentTimeMillis()));
		return failInfo;
	}
	
	
	/**
	 * 获取文件附件内容
	 * @param docfiles
	 * @return
	 */
	/*public List<Map<String, String>> getAttachments(String documentId){
		
		List<Map<String, String>> attachments = new ArrayList<Map<String, String>>();
		DatDocAttachmentService datDocAttachmentService=
				applicationContext.getBean(DatDocAttachmentService.class);
		List<DatDocAttachment> docfiles = datDocAttachmentService
				.getAttachmentByDocumentId(documentId);
		try {
			
		for (DatDocAttachment docatt : docfiles) {
			Map<String, String> attachment = new HashMap<String, String>();
			attachment.put(
					"fileName",
					StringUtils.isBlank(docatt.getAttachmentName()) ? ""
							: docatt.getAttachmentName());
			
			String filePath = docatt.getFilePath();
			// 要将filePath的“\”转为“/“，方便JSON处理
			filePath = StringUtils.isBlank(filePath) ? "" : filePath
					.replace("\\", "/");
			
			attachment.put("filePath", filePath);
			attachment.put("url", "console/file/download.action?attachmentId="+ docatt.getAttachmentId());
			
			String context=null;
			if(filePath.endsWith("docx")){
	    			context=Xwpf.readWord2007(getFilepath(filePath));
	    			attachment.put("fileContext", new sun.misc.BASE64Encoder().encode( context.getBytes("UTF-8")));
	    		}else if(filePath.endsWith("doc")){
	    			context=Xwpf.readWord2007(getFilepath(filePath));
	    			attachment.put("fileContext", new sun.misc.BASE64Encoder().encode( context.getBytes("UTF-8")));
	    		}else if(filePath.endsWith("pptx")){
	    			context=Xwpf.readPPT2007(getFilepath(filePath));
	    			attachment.put("fileContext", new sun.misc.BASE64Encoder().encode( context.getBytes("UTF-8")));
	    		}else if(filePath.endsWith("ppt")){
	    			context=Xwpf.readPPT2003(getFilepath(filePath));
	    			attachment.put("fileContext", new sun.misc.BASE64Encoder().encode( context.getBytes("UTF-8")));
	    		}else if(filePath.endsWith("xlsx")){
	    			context=Xwpf.readECLCE2007(getFilepath(filePath));
	    			attachment.put("fileContext", new sun.misc.BASE64Encoder().encode( context.getBytes("UTF-8")));
	    		}else if(filePath.endsWith("xls")){
	    			context=Xwpf.readECLCE2003(getFilepath(filePath));
	    			attachment.put("fileContext", new sun.misc.BASE64Encoder().encode( context.getBytes("UTF-8")));
	    		}else if(filePath.endsWith("pdf")){
	    			context=Xwpf.getPdfText(getFilepath(filePath));
	    			attachment.put("fileContext", new sun.misc.BASE64Encoder().encode( context.getBytes("UTF-8")));
	    		}else if(filePath.endsWith("txt")){
	    			attachment.put("fileContext", encodeBase64File(getFilepath(filePath)));
	    		}
			
			attachment.put("fileContext",new sun.misc.BASE64Encoder().
					encode( context.getBytes("UTF-8")));
			attachments.add(attachment);
		}
		
		} catch (Exception e) {
			// TODO: handle exception
		}
		return attachments;
		
	}*/
	
	/**
	 * 得到文档的权限信息
	 * @param documentId
	 * @return
	 */
	public Map<String, List<String>>  getAcl(String documentId){
		
		DatDocumentRightService datDocumentRightService=
				applicationContext.getBean(DatDocumentRightService.class);
		
		
		List<DatDocumentRight> docrights = datDocumentRightService
				.getDocumentRights(documentId);
		List<String> roleIds = new ArrayList<String>();
		List<String> userIds = new ArrayList<String>();
		List<String> deptIds = new ArrayList<String>();
		for (DatDocumentRight right : docrights) {
			String objId = right.getRightObjectId();
			String objType = right.getRightObjectType();
			if (StringUtils.isNotBlank(objId)
					&& StringUtils.isNotBlank(objType)) {
				switch (objType) {
				case AppAclObjectType.EMPLOYEE:
					userIds.add(objId);
					break;
				case AppAclObjectType.DEPARTMENT:
					deptIds.add(objId);
					break;
				case AppAclObjectType.ROLE:
					roleIds.add(objId);
					break;
				}
			}
		}
		Map<String, List<String>> acls = new HashMap<String, List<String>>();
		acls.put("empId", userIds);
		acls.put("depId", deptIds);
		acls.put("roleId", roleIds);
		return acls;
		
	}
	
	
	/**
	 * 获取文档附件路劲
	 * @param filePath
	 * @return
	 */
	public  static String getFilepath(String filePath) {
		String fileSeparator = System.getProperties().getProperty(
				"file.separator");
		if (filePath.indexOf(fileSeparator) < 0) {
			if (filePath.indexOf("\\") > 0) {// windows 系统
				filePath=filePath.replaceAll("\\", fileSeparator);
			} else {
				filePath=filePath.replaceAll("/", "\\\\");
			}
		}
		String basePath =ApplySycnConstant.FILEPATH;
		return basePath + filePath;

	}
	
	
	/*
	 * 编辑非三大办公软件文档
	 */
	public static   String  encodeBase64File(String path){
	       InputStream      fis =null;
	       String  encode="";
      try {
     	  File file = new File(path);;
          fis = new FileInputStream(file);
          InputStreamReader reader = new InputStreamReader(fis,"GBK"); //最后的"GBK"根据文件属性而定，如果不行，改成"UTF-8"试试
          BufferedReader br = new BufferedReader(reader);
          String line;
    
          while ((line = br.readLine()) != null) {
         	 encode+=line;
          }
          encode= new sun.misc.BASE64Encoder().encode( encode.getBytes("UTF-8"));
      
          br.close();
          reader.close();
      } catch (Exception e) {
          e.printStackTrace();
      } finally {
          if (fis != null) {
              try {
                  fis.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
		return encode;  

	}
	
}
