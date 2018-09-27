package com.gzsolartech.schedule.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gzsolartech.bpmportal.entity.AacEmployee;
import com.gzsolartech.bpmportal.util.RfcManager;
import com.gzsolartech.smartforms.constant.HttpSessionKey;
import com.gzsolartech.smartforms.entity.DatApplication;
import com.gzsolartech.smartforms.entity.DatDocument;
import com.gzsolartech.smartforms.entity.DatSystemMeta;
import com.gzsolartech.smartforms.entity.DatSystemMetaCata;
import com.gzsolartech.smartforms.entity.OrgEmployee;
import com.gzsolartech.smartforms.exceptions.SmartformsException;
import com.gzsolartech.smartforms.service.BaseDataService;
import com.gzsolartech.smartforms.service.DatApplicationService;
import com.gzsolartech.smartforms.service.DatDocumentService;
import com.gzsolartech.smartforms.service.DatSystemMetaService;
import com.gzsolartech.smartforms.service.OrgEmployeeService;
import com.gzsolartech.smartforms.utils.DatDocumentUtil;
import com.gzsolartech.smartforms.utils.XmlDataUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

/**
 * 同步PU0002物料组数据
 * @author 
 *
 */
@Service
public class PU0002Service extends BaseDataService {
	private static final Logger LOG = LoggerFactory
			.getLogger(PU0002Service.class);
	@Autowired
	private DatSystemMetaService datSystemMetaService;
	@Autowired
	private DatApplicationService datApplicationService;
	
	private String SAPFACTORY = "800";
	
	
	
	public Map<String, Object> asyncMaterielGroup() {
		String logMsg = "";
		String nodeName = "PU0002_materielGroup";
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DatSystemMetaCata cata = getCataByNodeName(nodeName);
			Map<String, Object> map = Z_BPM_MM_GET_MATKL("", "ZH'", "");
			String sapResult = (String) map.get("result");
			if("success".equals(sapResult) && cata != null) {
				String userNum = "Admin";
				// 1. 删除原有配置
				deleteSystemMeta(nodeName);
				// 2. 插入数据
				List<Map<String, Object>> rows = (List<Map<String, Object>>) map.get("rows");
				for(Map<String, Object> line: rows) {
					String MATKL = (String) line.get("MATKL");// 料组号
					String WGBEZ = (String) line.get("WGBEZ");// 料组名
					
					DatSystemMeta meta = new DatSystemMeta();
					meta.setMetaName(MATKL);
					meta.setMetaValue(WGBEZ);
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					meta.setUpdateTime(timestamp);
					meta.setCreateTime(timestamp);
					meta.setCreator(userNum);
					meta.setUpdateBy(userNum);
					meta.setMetaCataId(cata.getCataId());
					
					datSystemMetaService.create(meta);
				}
				gdao.getSession().flush();
			} else {
				if(cata == null)
					logMsg = "需同步的配置项不存在";
			}
		} catch(Exception e) {
			e.printStackTrace();
			logMsg = e.getMessage();
		} finally {
			if(StringUtils.isNotBlank(logMsg))
				LOG.info("PU0002同步物料组数据失败，" + logMsg);
		}
		return result;
	}
	
	/**
	 * 模糊查询获取物料组
	 * @param IM_WGBEZ 物料组名称
	 * @param IM_LANGU 语言
	 * @return
	 */
	public Map<String, Object> Z_BPM_MM_GET_MATKL(String IM_WGBEZ, String IM_LANGU, String sapFactory) {
		if(StringUtils.isBlank(sapFactory)) sapFactory = this.SAPFACTORY;
		
		Map<String, Object> result = new HashMap<String, Object>();
		// 返回表格参数
		int total = 0;
		List<Map<String, Object>> inveArray = new ArrayList<Map<String, Object>>();
		try {
			// 1. 初始化
			JCoFunction function = null;
			JCoDestination destination = null;
			JCoFieldIterator iterator = null;
			RfcManager rfcManager;
			rfcManager = RfcManager.getInstance(getSAPConfig(sapFactory));
			destination = rfcManager.getDestination();
			function = rfcManager.getFunction(destination, "Z_BPM_MM_GET_MATKL");
	
			// 2. 设置输入参数
			JCoParameterList importList = function.getImportParameterList();
			importList.setValue("IM_WGBEZ", IM_WGBEZ);
			importList.setValue("IM_LANGU", IM_LANGU);
	
			// 3. 调用
			function.execute(destination);
	
			// 4. 获取返回参数
			JCoParameterList exportList = function.getExportParameterList();
			String EX_OK = (String) exportList.getValue("EX_OK");
			String EX_MESSAGE = (String) exportList.getValue("EX_MESSAGE");
			result.put("result", "Y".equals(EX_OK) ? "success" : "fail");
			result.put("msg", EX_MESSAGE);
			if ("Y".equals(EX_OK)) {
				// 获取返回参数表
				JCoParameterList exportParameterList = function.getTableParameterList();
				JCoTable IT_RETURN = exportParameterList.getTable("ET_RESULT");
				// 获取总行数
				total = IT_RETURN.getNumRows();
	
				for (int i = 0; i < total; i++) {
					IT_RETURN.setRow(i);
					// 遍历输出参数表
					iterator = IT_RETURN.getFieldIterator();
					Map<String, Object> item = new HashMap<>();
					while (iterator.hasNextField()) {
						JCoField field = iterator.nextField();
						item.put(field.getName(), field.getValue().toString());
					}
					inveArray.add(item);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			result.put("result", "fail");
			result.put("msg", e.getMessage());
			e.printStackTrace();
		} finally {
			result.put("total", total);
			result.put("rows", inveArray);
		}
		return result;
	}
	
	/**
	 * 初始化sap配置
	 * @param SAPCode
	 * @return
	 */
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
	
	/**
	 * 根据nodeName删除meta数据
	 * @param nodeName
	 * @throws SmartformsException
	 */
	public void deleteSystemMeta(String nodeName) throws SmartformsException {
		List<String> metaIds = getMetaIdsByNodeName(nodeName);
		if(metaIds.size() > 0) {
			datSystemMetaService.delete(metaIds);
			gdao.getSession().flush();
		}
	}
	
	public List<String> getMetaIdsByNodeName(String nodeName) {
		List<String> list = new ArrayList<String>();
		if(StringUtils.isNotBlank(nodeName)) {
			String hql = "select meta.metaId from DatSystemMeta meta, DatSystemMetaCata cata where meta.metaCataId = cata.cataId and cata.cataName = ?";
			list = gdao.queryHQL(hql, nodeName);
		}
		return list;
	}
	
	public DatSystemMetaCata getCataByNodeName(String nodeName) {
		DatSystemMetaCata cata = null;
		String hql = "from DatSystemMetaCata cata where cata.cataName = ? ";
		List<Object> list = gdao.queryHQL(hql, nodeName);
		if(list.size() > 0) {
			cata = (DatSystemMetaCata) list.get(0);
		}
		return cata;
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
	
}
