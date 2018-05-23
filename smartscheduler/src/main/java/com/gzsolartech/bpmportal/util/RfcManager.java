package com.gzsolartech.bpmportal.util;


import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gzsolartech.smartforms.entity.DatApplication;
import com.gzsolartech.smartforms.service.DatApplicationService;
import com.gzsolartech.smartforms.service.DatDocumentService;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;

/**
 * 连接SAP
 */

public class RfcManager {
	private JCoFunction function;

	// 输入参数列表
//	private JCoParameterList inPara = null;

	// 输出参数列表
//	private JCoParameterList outPara = null;
//
//	private JCoParameterList tabPara = null;

//	private String functionName;
	@Autowired
	private DatDocumentService datDocumentService;
	@Autowired
	private DatApplicationService datApplicationService;

	private static String ABAP_AS = "ABAP_AS_WITHOUT_POOL";

	private JCoDestination destination;

	private RfcManager(JSONObject SAPConfig) throws Exception {
		connect(SAPConfig); // 连接SAP
	}

	// 调用RfcManager
	public static RfcManager getInstance(JSONObject SAPConfig)
			throws Exception, JCoException {
		// RFC接口调用开始 ==========
		RfcManager common = new RfcManager(SAPConfig);
		return common;
	}

	// 连接 SAP
	public void connect(JSONObject SAPConfig) throws Exception {
		// set properties参数，

		String host = SAPConfig.get("ashost").toString();
		String clientName = SAPConfig.get("client").toString();
		String language = SAPConfig.get("langu").toString();
		String userid = SAPConfig.get("user").toString();
		String password = SAPConfig.get("passwd").toString();
		String system = SAPConfig.get("sysnr").toString();
		// 设置SAP的连接参数
		Properties connectProperties = new Properties();
		connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, host);
		connectProperties
				.setProperty(DestinationDataProvider.JCO_SYSNR, system);
		connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT,
				clientName);
		connectProperties.setProperty(DestinationDataProvider.JCO_USER, userid);
		connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD,
				password);
		connectProperties.setProperty(DestinationDataProvider.JCO_LANG,
				language);

		try {
			// 创建DestinationDataProvider，
			createDataFile(ABAP_AS, "jcoDestination", connectProperties);
			destination = JCoDestinationManager.getDestination(ABAP_AS);

		} catch (JCoException ex) {
			throw new Exception("SAP连接失败" + ex.getMessage());
		}
	}

	/*
	 * 设置参数 name - the name of the field to set value - the value to set for the
	 * field
	 */
//	public RfcManager addParameter(String name, String value) {
//		inPara.setValue(name, value);
//		return this;
//	}
	public JCoDestination getDestination(){
		try {
			destination = JCoDestinationManager.getDestination(ABAP_AS);
		} catch (JCoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return destination;
	}

//	public RfcManager addParameter(int name, String value) {
//		inPara.setValue(name, value);
//		return this;
//	}

	// 执行方法
//	public RfcManager prepare(String functionName) throws Exception {
//		this.functionName = functionName;
//		try {
//			// 取得要执行的方法
//			function = destination.getRepository().getFunction(functionName);
//		} catch (JCoException e) {
//			throw new Exception("SAP获取方法" + functionName + "失败："
//					+ e.getMessage());
//		}
//		if (function == null) {
//			throw new Exception(functionName + "方法不存在");
//		}
//		// 取得参数列表
//		inPara = function.getImportParameterList();
//		outPara = function.getExportParameterList();
//		tabPara = function.getTableParameterList();
//		return this;
//	}

	// 执行方法
//	public JCoParameterList execCall() throws JCoException {
//		// Execute
//		function.execute(destination);
//		return outPara;
//	}

	// 取得返回结果
//	public JCoTable getResultTable(String tableName) {
//		return tabPara.getTable(tableName);
//	}

	// //取得参数列表
	// public JCoTable getParamTable(String tableName) {
	// return tabPara.getTable(tableName);
	// }

	// SAP传入参数为列表
	public JCoTable getParamTableList(String tableName) {
		return function.getTableParameterList().getTable(tableName);
	}

	// DisConnect
	public void close() {
		// if (client != null)
		// client.disconnect();
//		clie
	}

	// Creates a connection configuration file based on parameters given above
	static void createDataFile(String name, String suffix, Properties properties)
			throws Exception {
		File cfg = new File(name + "." + suffix);
		// if (!cfg.exists()) {
		try {
			FileOutputStream fos = new FileOutputStream(cfg, false);
			properties.store(fos, "ABAP_AS_WITHOUT_POOL");
			fos.close();
		} catch (Exception e) {
			throw new Exception("不能创建SAP连接需要的Destination文件" + cfg.getName());
		}
		// }
	}

//	public String convertNull(String str) {
//		if (str == null)
//			return "";
//		return str;
//	}
	public JCoFunction getFunction(JCoDestination destination,String functionName) {
		JCoFunction function = null;
		 try {
			function = destination.getRepository()  
			         .getFunctionTemplate(functionName).getFunction();
		} catch (JCoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return function;
	}

	public static void main(String[] args) {
		try {
			JSONObject SAPConfig = new JSONObject();
			/*SAPConfig.put("ashost", "10.128.64.130");
			SAPConfig.put("client", "200");
			SAPConfig.put("langu", "en");
			SAPConfig.put("user", "bpm");
			SAPConfig.put("passwd", "Bpm123456");
			SAPConfig.put("sysnr", "00");*/
			SAPConfig.put("ashost", "10.128.96.132");
			SAPConfig.put("client", "800");
			SAPConfig.put("langu", "en");
			SAPConfig.put("user", "QWEB");
			SAPConfig.put("passwd", "Qweb12345");
			SAPConfig.put("sysnr", "00");
//			测试机<ashost>10.128.96.132</ashost><client>800</client><langu>en</langu><user>BPM</user><passwd>Bpm2#use</passwd><sysnr>00</sysnr>
//			生产机<ashost>10.128.64.130</ashost><client>800</client><langu>zh</langu><user>web</user><passwd>AACeip2015</passwd><sysnr>01</sysnr>
//			SAPConfig.put("ashost", "10.128.64.130");
//			SAPConfig.put("client", "800");
//			SAPConfig.put("langu", "zh");
//			SAPConfig.put("user", "web");
//			SAPConfig.put("passwd", "AACeip2015");
//			SAPConfig.put("sysnr", "00");
			
//			SAPConfig.put("ashost", "10.161.1.70");
//			SAPConfig.put("client", "301");
//			SAPConfig.put("langu", "en");
//			SAPConfig.put("user", "HCM_HRBPM");
//			SAPConfig.put("passwd", "123456");
//			SAPConfig.put("sysnr", "00");
			RfcManager rfc = getInstance(SAPConfig);
			JCoDestination destination = rfc.getDestination();
//			JCoFunction function = rfc.getFunction(destination, "Z_BPM_PP_MRPINFO");//调用接口方法
			JCoFunction function = rfc.getFunction(destination, "Z_BI_BUKRS_WERKS");//调用接口方法
			System.out.println(function);
			JCoParameterList importParameterList = function.getImportParameterList();
			JCoParameterList tableParameterList = function.getTableParameterList();
			JCoTable table = tableParameterList.getTable("PT_RETURN");
			int i=0;
//			System.out.println(function);
//			JCoTable table = parameterList.getTable("T_PERNR");//获取具体哪个表
//			table.appendRow();
//			table.setRow(1);
////			table.setValue("ZHRDGZTS","5.5");//获取员工编号
//			System.out.println(table);
////			table.setValue("PERNR","60001347");//获取员工编号
//			function.execute(destination);//执行这个方法
//			System.out.println(function.getExportParameterList());
//			JCoParameterList exportParameterList = function.getChangingParameterList(); 
//			String EP_ISOK = (String) exportParameterList.getValue("EP_ISOK");
//			System.out.println(exportParameterList);
//			data.put("EP_ISOK", EP_ISOK);
//			data.put("EP_MESS", exportParameterList.getValue("EP_MESS"));
//			if ("Y".equals(EP_ISOK)) {
//				table = parameterList.getTable("T_INFO");//获取人员信息表格
//				JCoFieldIterator  iterator = table.getFieldIterator();
//				while (iterator.hasNextField()) {
//					JCoField field = iterator.nextField();
//					data.put(field.getName(), field.getValue());
//				}
//				System.out.println(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}