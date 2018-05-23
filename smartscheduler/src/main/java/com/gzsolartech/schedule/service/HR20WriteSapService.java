package com.gzsolartech.schedule.service;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.gzsolartech.bpmportal.util.RfcManager;
import com.gzsolartech.smartforms.entity.DatApplication;
import com.gzsolartech.smartforms.entity.DatDocument;
import com.gzsolartech.smartforms.service.BaseDataService;
import com.gzsolartech.smartforms.utils.DatDocumentUtil;
import com.gzsolartech.smartforms.utils.XmlDataUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
/**
 * 转正人员信息回写SAP
 * @author Tsy
   2018年5月9日
   下午6:22:15
 */
@Service
public class HR20WriteSapService extends  BaseDataService{
	private static final Logger LOG = LoggerFactory
			.getLogger(HR20WriteSapService.class);
	/**
	 * 批量回写转正人员信息开始
	   Tsy
	 * 2018年5月9日下午6:25:53
	 */
	public void writeSap(){
		Map<String, String> map=getHR20_AppId();
		String appId=String.valueOf(map.get("appId"));//应用库Id
		String formName=String.valueOf(map.get("formName"));//表单名
		List<Map<String, Object>> maps=getDocumentIsEnd(appId,formName);
		for (int i = 0; i < maps.size(); i++) {
			Map<String, Object> docData=maps.get(i);//文档数据
			if(!docData.isEmpty()){
			    if(docData.containsKey("responseJson")){//如果已回写
			    	JSONObject json=new JSONObject(String.valueOf(docData.get("responseJson")));
			    	if(json.getString("FLAG").equals("Y")){//且状态已成功
			    		continue;
			    	}
			    }
				JSONObject responseJson=new JSONObject();
				Map<String, Object> writeSap=getData(docData);
				JCoFunction function = null;
				JCoDestination destination = null;
				RfcManager rfcManager;
				try {
					rfcManager = RfcManager.getInstance(getSAPUserInfo());
					destination = rfcManager.getDestination();
					function = rfcManager.getFunction(destination, "Z_BPM_HR_POSTPRODEG");// 调用回写接口
					JCoParameterList Import = function.getImportParameterList();
					String saveGrading=String.valueOf(writeSap.get("saveGrading"));//是否分阶段调薪
					Import.setValue("I_FLAG", "3");
					Import.setValue("I_CHSALARY", "X");
					Import.setValue("I_STAGE",saveGrading);
					JCoParameterList tableparameterList = function.getTableParameterList();// 获取表集合
					JCoTable table = tableparameterList.getTable("IT_DATA");// 具体获取某个表
					table.appendRow();
					table.setRow(1);
					
					String FLAG="";//返回标识
					String MESS="";//返回信息
					String TIME="";//回写时间
					table.setValue("PERNR", String.valueOf(writeSap.get("jobNumber")));// 员工ID
					table.setValue("BEGDA", String.valueOf(writeSap.get("positiveDate")));//转正日期
					table.setValue("MASSG", String.valueOf(writeSap.get("become")));//转正原因
					table.setValue("ZHRFZCMC",String.valueOf(writeSap.get("positiveLaterProfessional")));//转正后职称
					table.setValue("TXBL", String.valueOf(writeSap.get("positiveLaterPayProportion")));//薪资百分比
					table.setValue("BETRG", String.valueOf(writeSap.get("later")));//转正后薪资
					table.setValue("PERSG", String.valueOf(writeSap.get("staffgroup")));//员工组
					table.setValue("TRFAR", String.valueOf(writeSap.get("salary")));//工资等级类型
					table.setValue("ZHRFZD", String.valueOf(writeSap.get("zhrzd")));//职等
					table.setValue("ZBETRG", String.valueOf(writeSap.get("before")));// 写入转正前薪资
					function.execute(destination);
					JCoTable tableRETURN = tableparameterList.getTable("IT_RETURN");// 具体获取某个表
					FLAG = String.valueOf(tableRETURN.getValue("FLAG"));
					MESS= String.valueOf(tableRETURN.getValue("MESS"));
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					TIME=sdf.format(new Date());
					if(docData.containsKey("responseJson")){//更新状态
						responseJson.put("FLAG", FLAG);
						responseJson.put("MESS", MESS);
						responseJson.put("TIME", TIME);
						updateSapState(responseJson.toString(), String.valueOf(docData.get("__docuid")));
					}else{//增加状态节点
						responseJson.put("FLAG", FLAG);
						responseJson.put("MESS", MESS);
						responseJson.put("TIME", TIME);
						addSapState(responseJson.toString(), String.valueOf(docData.get("__docuid")));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	/**
     * 获取所有结束的单据
       Tsy
     * 2018年5月9日下午7:29:21
     */
    public List<Map<String, Object>> getDocumentIsEnd(String appId,String formName){
    	List<Map<String, Object>> maps=new ArrayList<>();
    	List<Map<String, Object>> documentIds=getAllApplication(appId,formName);
    	if(!documentIds.isEmpty()){
    		for (int i = 0; i < documentIds.size(); i++) {
    			Map<String, Object> map=documentIds.get(i);
    			if(!map.isEmpty()){
    				String documentId=String.valueOf(map.get("DOCUMENT_ID"));
    				if(isEnd(documentId)==0){//代表单据结束
    					Map<String, Object> data=queryForm(documentId);
    					maps.add(data);
    				}
    			}
			}
    	}
    	return maps;
    }
	/**
	 * 获取单据是否结束
	   Tsy
	 * 2018年5月9日下午7:27:33
	 */
    public int isEnd(String documentId){
    	int finished=-1;
    	List<Object> params = new ArrayList<Object>();
		params.add(documentId);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		// 获取最新一条数据，如果INSTANCE_STATE为STATE_FINISHED,则该流程已经结束
		String sql = "SELECT * FROM BPM_INSTANCE_INFO where DOCUMENT_ID = ?";
		List<Map<String, Object>> datas = gdao.executeJDBCSqlQuery(sql, params);
		if (datas.size() > 0) {
			String instance_state = String.valueOf(datas.get(0).get("INSTANCE_STATE"));
			if ("STATE_FINISHED".equals(instance_state)) {
				finished=0;
			}
		}
		return finished;
    }
    /**
     * 获取所有单据
       Tsy
     * 2018年5月9日下午7:31:27
     */
    public List<Map<String, Object>> getAllApplication(String appId,String formName){
    	List<Map<String, Object>>  maps = null;
    	String sql="select DOCUMENT_ID from dat_document where app_id=? and form_name=?";	
    	List params = new ArrayList();
        params.add(appId);
        params.add(formName);
        maps = gdao.executeJDBCSqlQuery(sql,params);
        return maps;
	}
    /**
	 * 解析XML
	   Tsy
	 * 2018年5月9日下午6:35:43
	 */
	@RequestMapping("/queryForm")
	@ResponseBody
	public Map<String, Object> queryForm(String docuId){//传入文档ID
		List<DatDocument>  docList = null;
		String sql="select * from dat_document where DOCUMENT_ID=?";	
		List params = new ArrayList();
		params.add(docuId);
		docList = gdao.executeJDBCSqlQuery(sql,DatDocument.class,params);
		List<DatDocument> datDocument=docList;
		Map<String, Object> formData = new TreeMap<String, Object>();
		if(datDocument.size()>0){
			DatDocument document=datDocument.get(0);
			if(document!=null){
				String xmldata = XmlDataUtils.toString(document.getDocumentData());
				JSONObject jsoDocData = XML.toJSONObject(xmldata);
				if (jsoDocData.has("root")) {
					JSONObject root = jsoDocData.getJSONObject("root");
					Iterator it = root.keys();
					while (it.hasNext()) {
						String key = (String) it.next();
						Object value = (Object) DatDocumentUtil.parseValue(root.get(key));
						formData.put(key, value);
					}
				}
			}
		}
		return formData;
	}
	
    /**
	 * 获取SAP连接
	   Tsy
	 * 2018年5月9日下午6:27:00
	 */
	public JSONObject getSAPUserInfo() {
		DatApplication dat= getDatApplicationByName("SystemMG");
		Map<String, Object> config = getDocumentByField(dat.getAppId(), "SAPConfig", "SearchKey",
				"HCMSAP");
		Set keys = config.keySet();
		Iterator<String> iterator = keys.iterator();
		JSONObject SAPConfig = new JSONObject();
		while (iterator.hasNext()) {
			String key = iterator.next();
			SAPConfig.put(key, config.get(key).toString());
		}
		return SAPConfig;
	}
	public DatApplication getDatApplicationByName(String appName){
        List applist = gdao.queryHQL("from DatApplication where appName=?", new Object[] {
            appName
        });
        if(applist.isEmpty())
            return null;
        else
            return (DatApplication)applist.get(0);
    }
	public Map<String, Object> getDocumentByField(String appId,String formName,String fieldName,String fieldValue) {
		Map<String, Object> results = new HashMap<String, Object>();
		String sql = "select * from DAT_DOCUMENT where app_id=? and form_Name=? and EXTRACTVALUE(DOCUMENT_DATA,'//root/"+fieldName+"')=?";
//		List<DatDocument> docList = gdao.queryHQL(hql, formName);
		List params = new ArrayList();
		params.add(appId);
		params.add(formName);
		params.add(fieldValue);
		List<DatDocument> docList = gdao.executeJDBCSqlQuery(sql, DatDocument.class, params);
		DatDocument doc = null;
		if (docList.isEmpty()) {
			LOG.warn("在应用库中没有文档！");
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
	/**
	 * 获取系统应用库配置的ID
	   Tsy
	 * 2018年5月9日下午7:27:42
	 */
	private Map<String, String> getHR20_AppId() {
		Map<String, String> HR20_AppId=new TreeMap<>();
		DatApplication dat = getDatApplicationByName("SystemMG");
		Map<String, Object> HR20_App = getDocumentByField(dat.getAppId(), "MultiValue",
				"ThisLevelMenu", "HR20_App");
		String query=String.valueOf(HR20_App.get("MutilData"));
		String[] queryArr=query.split(",");
		if(queryArr.length>0){
			HR20_AppId.put("appId",queryArr[0]);
			HR20_AppId.put("formName",queryArr[1]);
		}
		return HR20_AppId;
	}
	/**
	 * 处理字符串
	   Tsy
	 * 2018年5月11日上午9:11:11
	 */
	private String trimStr(String str){
	  if((str!=null)||(!str.equals(""))){
	    if(str.indexOf("###@@@@###")>=0&&str.indexOf(";")>=0){
	    	str=str.split(";")[0];
	    }
	    if(str.indexOf("###@@@@###")>=0&&str.indexOf(";")<0){
	    	str=str.split("###@@@@###")[0];
	    }
	    if(str.indexOf("###@@@@###")<0&&str.indexOf(";")>=0){
	    	str=str.split(";")[0];
	    }
	  }
	  return str;
	}
	private Map<String,Object> getData(Map<String,Object> docData){
		Map<String,Object> doc=new HashMap<>();
		//工号
		if(docData.containsKey("jobNumber")){
			doc.put("jobNumber", trimStr(String.valueOf(docData.get("jobNumber"))));
		}
		//转正日期
		if(docData.containsKey("positiveDate")){
			doc.put("positiveDate",trimStr(String.valueOf(docData.get("positiveDate")).replace("-", "")));
		}
		//转正原因
		if(docData.containsKey("become")){
			doc.put("become", trimStr(String.valueOf(docData.get("become"))));
		}
		//转正后职称
		if(docData.containsKey("positiveLaterProfessional")){
			doc.put("positiveLaterProfessional", trimStr(String.valueOf(docData.get("positiveLaterProfessional"))));
		}
		//工资等级类型
		if(docData.containsKey("salary")){
			doc.put("salary", trimStr(String.valueOf(docData.get("salary"))));
		}
		//员工组
		if(docData.containsKey("staffgroup")){
			String staffgroup=trimStr(String.valueOf(docData.get("staffgroup")));
			if(staffgroup.equals("D")){
				staffgroup="A";
			}else if(staffgroup.equals("E")){
				staffgroup="B";
			}
			doc.put("staffgroup", staffgroup);
		}
		//是否调薪
		if(docData.containsKey("saveGrading")){
			doc.put("saveGrading", trimStr(String.valueOf(docData.get("saveGrading"))));
		}
		//薪资百分比
		if(docData.containsKey("positiveLaterPayProportion")){
			doc.put("positiveLaterPayProportion", trimStr(String.valueOf(docData.get("positiveLaterPayProportion")).replace("%", "")));
		}
		//职等
		if(docData.containsKey("zhrzd")){
			doc.put("zhrzd", trimStr(String.valueOf(docData.get("zhrzd"))));
		}
		//转正前薪资
		if(docData.containsKey("before")){
			doc.put("before", trimStr(String.valueOf(docData.get("before"))));
		}
		//转正后薪资
		if(docData.containsKey("later")){
			doc.put("later", trimStr(String.valueOf(docData.get("later"))));
		}
	    return doc;
	}
	/**
	 * 增加SAP返回的信息状态
	   Tsy
	 * 2018年5月11日上午10:12:51
	 */
	private void addSapState(String responseJson,String docId){
	  String sql="UPDATE DAT_DOCUMENT T set T.DOCUMENT_DATA=APPENDCHILDXML(T.DOCUMENT_DATA, '/root',sys.xmlType.CREATEXML('<responseJson>"+responseJson+"</responseJson>'))"
	  		+ " WHERE T.DOCUMENT_ID='"+docId+"'";
	  gdao.executeJDBCSql(sql);
	}
	/**
	 * 更新SAP返回的信息状态
	   Tsy
	 * 2018年5月11日上午10:12:51
	 */
	private void updateSapState(String responseJson,String docId){
	  String sql="UPDATE DAT_DOCUMENT T set T.DOCUMENT_DATA = UPDATEXML(T.DOCUMENT_DATA, '/root/responseJson','<responseJson>"+responseJson+"</responseJson>')"
            + " WHERE T.DOCUMENT_ID='"+docId+"'";
	  gdao.executeJDBCSql(sql);
	}
}
