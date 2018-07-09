package com.gzsolartech.schedule.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.gzsolartech.smartforms.dao.GenericDao;
import com.gzsolartech.smartforms.entity.DatSystemMeta;
import com.gzsolartech.smartforms.service.BaseDataService;
import com.gzsolartech.smartforms.service.DatSystemMetaService;


@Service
public class WmsInfoToBpm extends BaseDataService {
	
	@Autowired
	protected GenericDao gdao;
	@Autowired
	protected DatSystemMetaService datSystemMetaService;
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(WmsInfoToBpm.class);
	
	public void getInfoFromWms(){
		String result="";
		try {
			
			Map<String, Object> param = new HashMap<String, Object>();
			Map<String, Object> param_key = new HashMap<String, Object>();
			param.put("tenantId", 49);
			param.put("processCode", "BPM2WMS_WERKS_WHSEID");
			param_key.put("dataSet", "{}");
			param.put("paramDataMap", param_key);
			HttpPost httppost = new HttpPost("http://wmstest.wmstest.aact.com:18087/ibus/api/process/syncPb.json");
			httppost.addHeader("Content-type","application/json; charset=utf-8");
			httppost.addHeader("I-DataSource-TenantId", "49");
			HttpClient client = new DefaultHttpClient();
			httppost.setEntity(new StringEntity(JSONObject.toJSONString(param)));
			HttpResponse response = client.execute(httppost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity());
			}
			JSONObject json = JSONObject.parseObject(result);
			JSONObject jsonObject = json.getJSONObject("content");
			com.alibaba.fastjson.JSONArray jsonArray = jsonObject.getJSONArray("result");
			for(int i=0;i<jsonArray.size();i++){
				JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
				String sql="select max(meta_name) as max from dat_system_meta where meta_cata_id='sysmetacata:fb9685a0-9629-4b79-aef7-73726e146b24'";
				List<Map<String,Object>> list =gdao.executeJDBCSqlQuery(sql);
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				
				Integer integerr = new Integer(list.get(0).get("MAX").toString());
				String insert_sql = "INSERT INTO dat_system_meta(META_ID,CREATE_TIME,CREATOR,DESCRIPTION,EXT_META,META_CATA_ID,META_NAME,META_VALUE,UPDATE_BY,UPDATE_TIME) values(?,?,?,?,?,?,?,?,?,?) ";
				List<Object> params = new ArrayList<>();
				params.add("sysmeta:" + UUID.randomUUID().toString());
				params.add(timestamp);
				params.add("Admin");
				params.add(jsonObject2.get("description").toString());
				params.add(jsonObject2.get("udf1").toString());
				params.add("sysmetacata:fb9685a0-9629-4b79-aef7-73726e146b24");
				params.add(integerr+1);
				params.add(jsonObject2.get("udf2").toString());
				params.add("Admin");
				params.add(timestamp);
				gdao.executeJDBCSql(insert_sql, params);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
