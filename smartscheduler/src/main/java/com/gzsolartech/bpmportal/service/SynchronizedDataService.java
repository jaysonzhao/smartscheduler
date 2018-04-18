package com.gzsolartech.bpmportal.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gzsolartech.bpmportal.entity.FMoney;
import com.gzsolartech.smartforms.entity.DatApplication;
import com.gzsolartech.smartforms.entity.DatDocument;
import com.gzsolartech.smartforms.exceptions.SmartformsException;
import com.gzsolartech.smartforms.service.BaseDataService;
import com.gzsolartech.smartforms.service.DatApplicationService;
import com.gzsolartech.smartforms.service.DatDocumentService;
import com.gzsolartech.smartforms.utils.DatDocumentUtil;
import com.gzsolartech.smartforms.utils.XmlDataUtils;

/**
 * @description sql server to oracle
 * @author hhf
 * @date 2017年5月24日 下午6:47:55
 */
@Service
public class SynchronizedDataService extends BaseDataService{

	private static final long serialVersionUID = 5111237443230279743L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SynchronizedDataService.class);
	@Autowired
	private DatApplicationService datApplicationService;
	@Autowired
	private DatDocumentService datDocumentService;
	/**
	 * 同步sql server 汇率表中的数据到oracle
	 * @throws SmartformsException
	 */
	public void execute() throws SmartformsException{
		Connection con = null;
		Statement statement=null;
		ResultSet resultSet =null;
		try {
			DatApplication dat = datApplicationService.getDatApplicationByName("SystemMG");
			Map<String, Object> config = getDocumentByField(dat.getAppId(),"SAPConfig","SearchKey","SQLServer");
			String url = "jdbc:sqlserver://"+config.get("ashost")+";databaseName=FinanceSetting;IntegratedSecurity=False";
			con = DriverManager.getConnection(url,(String)config.get("user"),(String)config.get("passwd"));
			statement = con.createStatement();
			resultSet = statement.executeQuery("select * from f_Money");
			ArrayList<FMoney> data = new ArrayList<FMoney>();
			while (resultSet.next()) {
				FMoney fMoney = new FMoney();
				fMoney.setId(resultSet.getInt("Id"));
				fMoney.setMoneyType(resultSet.getString("MoneyType"));
				fMoney.setMoneyCode(resultSet.getString("MoneyCode"));
				fMoney.setMoneyRate(resultSet.getString("MoneyRate"));
				fMoney.setMoneySign(resultSet.getString("MoneySign"));
				fMoney.setSort(resultSet.getInt("Sort"));
				fMoney.setCreator(resultSet.getString("Creator"));
				fMoney.setCreationDate(resultSet.getTimestamp("CreationDate"));
				data.add(fMoney);
			}
			gdao.deleteByHql("delete from FMoney");
			gdao.save(data);
		} catch (Exception e) {
			LOGGER.error("同步数据时出现错误：",e);
			throw new SmartformsException(e);
		}finally {
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					statement=null;
				}
			}
			if(resultSet!=null){
				try {
					resultSet.close();
				} catch (SQLException e) {
					resultSet=null;
				}
			}
		}
	}
	
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
