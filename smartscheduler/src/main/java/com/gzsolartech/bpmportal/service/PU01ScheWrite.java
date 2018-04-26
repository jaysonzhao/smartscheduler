package com.gzsolartech.bpmportal.service;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gzsolartech.bpmportal.util.RfcManager;
import com.gzsolartech.smartforms.entity.DatApplication;
import com.gzsolartech.smartforms.entity.DatDocument;
import com.gzsolartech.smartforms.exceptions.SmartformsException;
import com.gzsolartech.smartforms.service.BaseDataService;
import com.gzsolartech.smartforms.service.DatApplicationService;
import com.gzsolartech.smartforms.utils.DatDocumentUtil;
import com.gzsolartech.smartforms.utils.XmlDataUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

/**
 * @description 调度写入已结束的单号 
 * @author hhf
 * @date 2018年4月25日 下午6:35:34
 */
@Service
public class PU01ScheWrite extends BaseDataService{

	private static final long serialVersionUID = 5111237443230279743L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PU01ScheWrite.class);
	
	@Autowired
	private DatApplicationService datApplicationService;
	
	@Transactional
	public void execute() throws SmartformsException{
		try {
			
			List<Map<String, Object>> data = loadRecords();
			for (Map<String, Object> map : data) {
				
				String SAPCLIENT=map.get("SAPCLIENT")==null?"":map.get("SAPCLIENT").toString();
				String ORDERNUM=map.get("ORDERNUM")==null?"":map.get("ORDERNUM").toString();
				String HTEXT=map.get("HTEXT")==null?"":map.get("HTEXT").toString();
				
				JSONObject SAPConfig = getSAPConfig(SAPCLIENT);
				RfcManager rfcManager = RfcManager.getInstance(SAPConfig);
				JCoDestination destination = rfcManager.getDestination();
				JCoFunction function = rfcManager.getFunction(destination, "Z_RFC_MM_PO_RELEASE");
				JCoParameterList importList = function.getImportParameterList();
				importList.setValue("IP_EBELN", ORDERNUM);
				function.execute(destination);
				JCoParameterList exportParameterList = function.getExportParameterList();
				String EP_SUBRC = exportParameterList.getString("EP_SUBRC");
				String EP_MSG = exportParameterList.getString("EP_MSG");
				if("0".equals(EP_SUBRC)){
					updateWriteRecord(map, "true", EP_SUBRC, EP_MSG);
					Z_BPM_MM_UPOTEXT(ORDERNUM,HTEXT,SAPCLIENT);
				}else{
					updateWriteRecord(map, "false", EP_SUBRC, EP_MSG);
				}
			}
		} catch (Exception e) {
			LOGGER.error("采购订单评审调度写入时出现异常：",e);
			throw new SmartformsException(e);
		}
	}
	
	public void updateWriteRecord(Map<String, Object> temp,String STATE,String RETURNCODE,String RETURNMSG){
		String DOCID=temp.get("DOCID")==null?"":temp.get("DOCID").toString();
		String ORDERNUM=temp.get("ORDERNUM")==null?"":temp.get("ORDERNUM").toString();
		String APPLYNUM=temp.get("APPLYNUM")==null?"":temp.get("APPLYNUM").toString();
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("select * from PU01_SCHEWRITERECORD where DOCID='"+DOCID+"'");
			List<Map<String, Object>> data = gdao.executeJDBCSqlQuery(sb.toString());
			//存在数据时更新，没有时插入
			if (data!=null&&!data.isEmpty()) {
				for (Map<String, Object> map : data) {
					int WRITECOUNT=map.get("WRITECOUNT")==null?0:Integer.parseInt(map.get("WRITECOUNT").toString());
					StringBuilder usb = new StringBuilder();
					usb.append("update PU01_SCHEWRITERECORD set RETURNCODE='"+RETURNCODE+"',"
							+ "RETURNMSG='"+RETURNMSG+"',WRITECOUNT="+(++WRITECOUNT)+","
							+ "EXECTIME='"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"' "
							+ "where DOCID='"+DOCID+"'");
					gdao.executeJDBCSql(usb.toString());
					StringBuilder usb2 = new StringBuilder();
					usb2.append("update PU01_RECORD set STATE='"+STATE+"' where DOCID='"+DOCID+"'");
					gdao.executeJDBCSql(usb2.toString());
				}
			}else{
				StringBuilder isb = new StringBuilder();
				isb.append("INSERT INTO PU01_SCHEWRITERECORD (DOCID,ORDERNUM,APPLYNUM,RETURNCODE,"
						+ "RETURNMSG,EXECTIME,WRITECOUNT) "
						+ "VALUES ('"+DOCID+"','"+ORDERNUM+"','"+APPLYNUM+"','"+RETURNCODE+"','"+RETURNMSG+"',"
						+ "'"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"',1)");
				gdao.executeJDBCSql(isb.toString());
				StringBuilder isb2 = new StringBuilder();
				isb2.append("update PU01_RECORD set STATE='"+STATE+"' where DOCID='"+DOCID+"'");
				gdao.executeJDBCSql(isb2.toString());
			}
		} catch (NumberFormatException e) {
			LOGGER.error("更新数据时出现异常：DOCID",e);
			e.printStackTrace();
		}
	}
	
	/**
	 * 抬头信息
	 * @param EBELN
	 * @param HTEXT
	 * @param sapcomp
	 * @return
	 */
	public Map<String, Object> Z_BPM_MM_UPOTEXT(String EBELN,String HTEXT,String sapcomp){
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			JCoFunction function = null;
			JCoDestination destination = null;
			RfcManager rfcManager = RfcManager.getInstance(getSAPConfig(sapcomp));
			destination = rfcManager.getDestination();
			function = rfcManager.getFunction(destination, "Z_BPM_MM_UPOTEXT");
			JCoParameterList tableParameterList = function.getTableParameterList();
			JCoTable table = tableParameterList.getTable("IT_INPUT");
			table.appendRow();
			table.setValue("EBELN", EBELN);
			table.setValue("HTEXT", HTEXT);
			function.execute(destination);
			JCoTable outPutTable = tableParameterList.getTable("IT_OUTPUT");
			String FLAG = outPutTable.getString("FLAG");
			HTEXT = outPutTable.getString("HTEXT");
			String MESS = outPutTable.getString("MESS");
			EBELN = outPutTable.getString("EBELN");
			result.put("FLAG", FLAG);
			result.put("HTEXT", HTEXT);
			result.put("MESS", MESS);
			result.put("EBELN", EBELN);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获取结束单未写入的数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> loadRecords() throws Exception{
		List<Map<String,Object>> result = new ArrayList<Map<String, Object>>();
		StringBuilder sb = new StringBuilder();
		sb.append("select * from PU01_RECORD where STATE='false' ");
		result = gdao.executeJDBCSqlQuery(sb.toString());
		return result;
	}
	
	public JSONObject getSAPConfig(String SAPCode) {
		DatApplication dat = datApplicationService.getDatApplicationByName("SystemMG");
		Map<String, Object> config = getDocumentByField(dat.getAppId(), "SAPConfig", "client",
				SAPCode);
		Set<String> keys = config.keySet();
		Iterator<String> iterator = keys.iterator();
		JSONObject SAPConfig = new JSONObject();
		while (iterator.hasNext()) {
			String key = iterator.next();
			SAPConfig.put(key, config.get(key).toString());
		}
		return SAPConfig;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> getDocumentByField(String appId,String formName,String fieldName,String fieldValue) {
		
		Map<String, Object> results = new HashMap<String, Object>();
		String sql = "select * from DAT_DOCUMENT where app_id=? and form_Name=? and EXTRACTVALUE(DOCUMENT_DATA,'//root/"+fieldName+"')=?";
		List params = new ArrayList();
		params.add(appId);
		params.add(formName);
		params.add(fieldValue);
		List<DatDocument> docList = gdao.executeJDBCSqlQuery(sql, DatDocument.class, params);
		DatDocument doc = null;
		if (docList.isEmpty()) {
//			LOG.warn("在应用库中没有文档！");
		}else{
			doc = docList.get(0);
		}
		if (doc != null) {
			String xmldata = XmlDataUtils.toString(doc.getDocumentData());
			JSONObject jsoDocData = XML.toJSONObject(xmldata);
			if (jsoDocData.has("root")) {
				JSONObject root = jsoDocData.getJSONObject("root");
				Iterator it = root.keys();
				while (it.hasNext()) {
					String key = (String) it.next();
					Object value = root.get(key);
					Object data = DatDocumentUtil.parseValue(value);
					if (data != null) {
						if (data instanceof ArrayList) {
							results.put(key, ((ArrayList<?>) data).get(0));
						} else {
							results.put(key, data);
						}
					}
				}
			}
		}
		return results;
	}
	
}
