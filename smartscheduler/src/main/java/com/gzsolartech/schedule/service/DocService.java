package com.gzsolartech.schedule.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.gzsolartech.smartforms.constant.EntityIdPrefix;
import com.gzsolartech.smartforms.constant.HttpSessionKey;
import com.gzsolartech.smartforms.entity.DatApplication;
import com.gzsolartech.smartforms.entity.DatDocument;
import com.gzsolartech.smartforms.service.BaseDataService;
import com.gzsolartech.smartforms.service.DatApplicationService;
import com.gzsolartech.smartforms.utils.DatDocumentUtil;
import com.gzsolartech.smartforms.utils.XmlDataUtils;

@Service
public class DocService extends BaseDataService{
	@Autowired
	private DocService docService;
	@Autowired
	private DatApplicationService datApplicationService;
	
	public List<Map<String, Object>> getAllDocumentBykeys(String appName,String appId,String formName,String keys,String orderBy){
		if(StringUtils.isEmpty(appName)){
			DatApplication dat = datApplicationService.load(appId);
			if(dat!=null){
				appName = dat.getAppName();
			}
		}
		String viewName = "view_"+appName+"_"+formName;
		viewName = viewName.toUpperCase();
		JSONObject json = new JSONObject(keys);
		Iterator<String> iterator = json.keys();
		Map<String, String> parms = new HashMap<String, String>();
		while(iterator.hasNext()){
			String fieldName = iterator.next();
			String fieldValue = json.optString(fieldName);
			if(!StringUtils.isEmpty(fieldName)&&!StringUtils.isEmpty(fieldValue)){
				parms.put(fieldName, fieldValue);
			}
		}
		List<Map<String, Object>> data = docService.getDocumentByField(viewName, parms ,orderBy);
		return data; 
	}
	
	/**
	 * 按条件搜索视图
	 * @param viewName
	 * @param parms
	 * @return
	 */
	public List<Map<String, Object>> getDocumentByField(String viewName,Map parms,String orderBy) {
		List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
		String situation="";
		List<DatDocument> docList = null;
		String sql = "select doc.* from "+viewName+" tview,DAT_DOCUMENT doc where tview.document_Id=doc.document_Id ";
		List params = new ArrayList();
		if(parms!=null){
			Set<String> set = parms.keySet();
			Iterator<String> iterator = set.iterator();
			while(iterator.hasNext()){
				String fieldName = iterator.next();
				String fieldValue = parms.get(fieldName).toString();
				sql += " and EXTRACTVALUE(doc.DOCUMENT_DATA,'//root/"+fieldName+"')=?";
				params.add(fieldValue);
			}
		}
		if(!StringUtils.isEmpty(orderBy)){
			sql += " order by EXTRACTVALUE(doc.DOCUMENT_DATA,'//root/"+orderBy+"')";
		}
		docList = gdao.executeJDBCSqlQuery(sql, DatDocument.class, params);
		for(int i=0;i<docList.size();i++){
			DatDocument doc = docList.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
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
								map.put(key, ((ArrayList<?>) data).get(0));
							} else {
								map.put(key, data);
							}
						}
					}
				}
			}
			results.add(map);
		}
		return results;
	}
}