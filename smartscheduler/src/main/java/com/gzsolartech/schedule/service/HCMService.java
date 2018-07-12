package com.gzsolartech.schedule.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.ParameterMode;

import org.hibernate.procedure.ProcedureCall;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gzsolartech.bpmportal.entity.HcmDictionariesTemp;
import com.gzsolartech.bpmportal.util.HcmDataTypeName;
import com.gzsolartech.bpmportal.util.RfcManager;
import com.gzsolartech.smartforms.entity.DatApplication;
import com.gzsolartech.smartforms.entity.DatDocument;
import com.gzsolartech.smartforms.service.BaseDataService;
import com.gzsolartech.smartforms.service.DatApplicationService;
import com.gzsolartech.smartforms.service.DatDocumentService;
import com.gzsolartech.smartforms.utils.DatDocumentUtil;
import com.gzsolartech.smartforms.utils.XmlDataUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

@Service
public class HCMService extends BaseDataService {
	private static final long serialVersionUID = -5970368654841010830L;
	
	private static final Logger LOG = LoggerFactory.getLogger(HCMService.class);
	
	@Autowired
	private DatDocumentService datDocumentService;
	@Autowired
	private DatApplicationService datApplicationService;
	
	/**
	 * 级联关系接口
	 * @author chao quan
	 * @param FLAG 入职情况
	 * @return
	 */
	public int rowNum = 0;
	
	public void ZHRPAFM021 (String tbname) {
		try {
			rowNum = 0;
			JCoFunction function = null;
			JCoDestination destination = null;
			RfcManager rfcManager = RfcManager.getInstance(getSAPConfig("HCMSAP"));
			destination = rfcManager.getDestination();
			function = rfcManager.getFunction(destination, "ZHRPAFM021");//获取级联关系
			JCoParameterList importParameterList = function.getImportParameterList();
			if (!"".equals(tbname)) {
				importParameterList.setValue("I_INPUT", tbname);
			}
			function.execute(destination);
			JCoParameterList tableparameterList = function.getTableParameterList();
			JCoTable T_CSKT = tableparameterList.getTable("T_CSKT");
			JCoTable T_DD07T = tableparameterList.getTable("T_DD07T");
			JCoTable T_T001P = tableparameterList.getTable("T_T001P");
			JCoTable T_T005T = tableparameterList.getTable("T_T005T");
			JCoTable T_T001 = tableparameterList.getTable("T_T001");
			JCoTable T_T500P = tableparameterList.getTable("T_T500P");
			JCoTable T_T501T = tableparameterList.getTable("T_T501T");
			JCoTable T_T502T = tableparameterList.getTable("T_T502T");
			JCoTable T_T503T = tableparameterList.getTable("T_T503T");
			JCoTable T_T505S = tableparameterList.getTable("T_T505S");
			JCoTable T_T516T = tableparameterList.getTable("T_T516T");
			JCoTable T_T517T = tableparameterList.getTable("T_T517T");
			JCoTable T_T526 = tableparameterList.getTable("T_T526");
//			JCoTable T_T527O = tableparameterList.getTable("T_T527O");
			JCoTable T_T527X = tableparameterList.getTable("T_T527X");
//			JCoTable T_T528B = tableparameterList.getTable("T_T528B");
			JCoTable T_T528T = tableparameterList.getTable("T_T528T");
			JCoTable T_T529T = tableparameterList.getTable("T_T529T");
			JCoTable T_T529U = tableparameterList.getTable("T_T529U");
			JCoTable T_T530T = tableparameterList.getTable("T_T530T");
			JCoTable T_T531S = tableparameterList.getTable("T_T531S");
			JCoTable T_T538T = tableparameterList.getTable("T_T538T");
			JCoTable T_T547S = tableparameterList.getTable("T_T547S");
			JCoTable T_T548T = tableparameterList.getTable("T_T548T");
			JCoTable T_T549T = tableparameterList.getTable("T_T549T");
			JCoTable T_T591S = tableparameterList.getTable("T_T591S");
			JCoTable T_T5R06 = tableparameterList.getTable("T_T5R06");
			JCoTable T_T7CN4W = tableparameterList.getTable("T_T7CN4W");
			JCoTable T_ZHR019 = tableparameterList.getTable("T_ZHR019");
			JCoTable T_ZHR021 = tableparameterList.getTable("T_ZHR021");
			JCoTable T_ZHRTINSTY = tableparameterList.getTable("T_ZHRTINSTY");
			JCoTable T_ZHRTTRMODE = tableparameterList.getTable("T_ZHRTTRMODE");
			JCoTable T_ZHRTZC = tableparameterList.getTable("T_ZHRTZC");
			JCoTable T_ZHRTZCCX = tableparameterList.getTable("T_ZHRTZCCX");
			JCoTable T_ZHRTZCLB = tableparameterList.getTable("T_ZHRTZCLB");
			JCoTable T_ZHRTZD = tableparameterList.getTable("T_ZHRTZD");
			JCoTable T_ZHRTZPGYS = tableparameterList.getTable("T_ZHRTZPGYS");
			JCoTable T_ZHRT_GZDD = tableparameterList.getTable("T_ZHRT_GZDD");
			JCoTable T_T503Z = tableparameterList.getTable("T_T503Z");
			JCoTable T_ZHR016 = tableparameterList.getTable("T_ZHR016");
			JCoTable T_ZHR024 = tableparameterList.getTable("T_ZHR024");
			JCoTable T_ZHR007 = tableparameterList.getTable("T_ZHR007");
			storeList(T_CSKT);
			storeList(T_DD07T);
			storeList(T_T001P);
			storeList(T_T005T);
			storeList(T_T001);
			storeList(T_T500P);
			storeList(T_T501T);
			storeList(T_T502T);
			storeList(T_T505S);
			storeList(T_T516T);
			storeList(T_T517T);
			storeList(T_T526);
//			storeList(T_T527O);
			storeList(T_T527X);
//			storeList(T_T528B);
			storeList(T_T528T);
			storeList(T_T529T);
			storeList(T_T529U);
			storeList(T_T530T);
			storeList(T_T531S);
			storeList(T_T538T);
			storeList(T_T547S);
			storeList(T_T548T);
			storeList(T_T549T);
			storeList(T_T591S);
			storeList(T_T5R06);
			storeList(T_T7CN4W);
			storeList(T_ZHR019);
			storeList(T_ZHR021);
			storeList(T_ZHRTINSTY);
			storeList(T_ZHRTTRMODE);
			storeList(T_ZHRTZC);
			storeList(T_ZHRTZCCX);
			storeList(T_ZHRTZCLB);
			storeList(T_ZHRTZD);
			storeList(T_ZHRTZPGYS);
			storeList(T_ZHRT_GZDD);
			storeList(T_ZHR016);
			storeList(T_ZHR024);
			storeList(T_ZHR007);
			storeList2(T_T503T,T_T503Z);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("获取码值共计:" + rowNum + "条");
	}
	
	public void storeList(Object table) {
		JCoTable extable = (JCoTable) table;
		String tbname = extable.getRecordMetaData().getName();
		List<HcmDictionariesTemp> data = new ArrayList<HcmDictionariesTemp>();
		if (extable.getNumRows() > 0) {
			for (int i = 0; i < extable.getNumRows(); i++) {
				extable.setRow(i);
				HcmDictionariesTemp temp = new HcmDictionariesTemp();
				temp.setTbName(tbname);
				switch (tbname) {
					case HcmDataTypeName.CBZX :
						temp.setDetailCode(extable.getString("KOSTL"));
						temp.setDetailName(extable.getString("KTEXT"));
						break;
					case HcmDataTypeName.YU :
						temp.setParentCode(extable.getString("DOMNAME"));
						temp.setDetailCode(extable.getString("DOMVALUE_L"));
						temp.setDetailName(extable.getString("DDTEXT"));
						break;
					case HcmDataTypeName.RSZFW :
						temp.setParentCode(extable.getString("WERKS"));
						temp.setDetailCode(extable.getString("BTRTL"));
						temp.setDetailName(extable.getString("BTEXT"));
						break;
					case HcmDataTypeName.GJ :
						temp.setDetailCode(extable.getString("LAND1"));
						temp.setDetailName(extable.getString("NATIO"));
						break;
					case HcmDataTypeName.GSDM :
						temp.setDetailCode(extable.getString("BUKRS"));
						temp.setDetailName(extable.getString("BUTXT"));
						break;
					case HcmDataTypeName.RYFW :
						temp.setDetailCode(extable.getString("BUKRS"));
						temp.setDetailName(extable.getString("NAME1"));
						break;
					case HcmDataTypeName.GYZ :
						temp.setDetailCode(extable.getString("PERSG"));
						temp.setDetailName(extable.getString("PTEXT"));
						break;
					case HcmDataTypeName.HY :
						temp.setDetailCode(extable.getString("FAMST"));
						temp.setDetailName(extable.getString("FTEXT"));
						break;
					case HcmDataTypeName.GYZZ :
						temp.setDetailCode(extable.getString("PERSK"));
						temp.setDetailName(extable.getString("PTEXT"));
						break;
					case HcmDataTypeName.MZ :
						temp.setDetailCode(extable.getString("RACKY"));
						temp.setDetailName(extable.getString("LTEXT"));
						break;
					case HcmDataTypeName.ZJ :
						temp.setDetailCode(extable.getString("KONFE"));
						temp.setDetailName(extable.getString("KTEXT"));
						break;
					case HcmDataTypeName.JYLX :
						temp.setDetailCode(extable.getString("SLART"));
						temp.setDetailName(extable.getString("STEXT"));
						break;
					case HcmDataTypeName.DQ :
						temp.setDetailCode(extable.getString("SACHX"));
						temp.setDetailName(extable.getString("SACHN"));
						break;
					case HcmDataTypeName.ZZDM :
						temp.setDetailCode(extable.getString("ORGKY"));
						temp.setDetailName(extable.getString("TEXT2"));
						break;
					case HcmDataTypeName.ZZDW :
						temp.setDetailCode(extable.getString("ORGEH"));
						temp.setDetailName(extable.getString("ORGTX"));
						break;//T528B
					case HcmDataTypeName.ZW :
						temp.setDetailCode(extable.getString("PLANS"));
						temp.setDetailName(extable.getString("PLSTX"));
						break;
					case HcmDataTypeName.RYXD :
						temp.setDetailCode(extable.getString("MASSN"));
						temp.setDetailName(extable.getString("MNTXT"));
						break;
					case HcmDataTypeName.ZT :
						temp.setDetailCode(extable.getString("STATV"));
						temp.setDetailName(extable.getString("TEXT1"));
						break;
					case HcmDataTypeName.XDYY :
						temp.setParentCode(extable.getString("MASSN"));
						temp.setDetailCode(extable.getString("MASSG"));
						temp.setDetailName(extable.getString("MGTXT"));
						break;
					case HcmDataTypeName.YWTX :
						temp.setDetailCode(extable.getString("TMART"));
						temp.setDetailName(extable.getString("TMTXT"));
						break;
					case HcmDataTypeName.DW :
						temp.setDetailCode(extable.getString("ZEINH"));
						temp.setDetailName(extable.getString("ETEXT"));
						break;
					case HcmDataTypeName.HTLX :
						temp.setDetailCode(extable.getString("CTTYP"));
						temp.setDetailName(extable.getString("CTTXT"));
						break;
					case HcmDataTypeName.TSRQLX :
						temp.setDetailCode(extable.getString("DATAR"));
						temp.setDetailName(extable.getString("DTEXT"));
						break;
					case HcmDataTypeName.GZFW :
						temp.setDetailCode(extable.getString("ABKRS"));
						temp.setDetailName(extable.getString("ATEXT"));
						break;
					case HcmDataTypeName.ZLX :
						temp.setParentCode(extable.getString("INFTY"));
						temp.setDetailCode(extable.getString("SUBTY"));
						temp.setDetailName(extable.getString("STEXT"));
						break;
					case HcmDataTypeName.ZJLX :
						temp.setDetailCode(extable.getString("ICTYP"));
						temp.setDetailName(extable.getString("ICTXT"));
						break;
					case HcmDataTypeName.ZJZT :
						temp.setDetailCode(extable.getString("ASTAT"));
						temp.setDetailName(extable.getString("CASTX"));
						break;
					case HcmDataTypeName.CQLDLC :
						temp.setDetailCode(extable.getString("ZCS"));
						temp.setDetailName(extable.getString("ZCSMC"));
						data.add(temp);
						temp = new HcmDictionariesTemp();
						temp.setTbName(tbname);
						temp.setParentCode(extable.getString("ZCS"));
						temp.setDetailCode(extable.getString("ZGZQY"));
						temp.setDetailName(extable.getString("ZGZQYT"));
						temp = new HcmDictionariesTemp();
						temp.setTbName(tbname);
						temp.setParentCode(extable.getString("ZGZQY"));
						temp.setDetailCode(extable.getString("ZGZLD"));
						temp.setDetailName(extable.getString("ZGZLDT"));
						data.add(temp);
						temp = new HcmDictionariesTemp();
						temp.setTbName(tbname);
						temp.setParentCode(extable.getString("ZGZLD"));
						temp.setDetailCode(extable.getString("ZGZLC"));
						temp.setDetailName(extable.getString("ZGZLCT"));
						break;
					case HcmDataTypeName.LWGS :
						temp.setParentCode(extable.getString("ZHRZPQD"));
						temp.setDetailCode(extable.getString("ZQDMX"));
						temp.setDetailName(extable.getString("ZQDMXT"));
						break;
					case HcmDataTypeName.XXLX :
						temp.setDetailCode(extable.getString("INSTY"));
						temp.setDetailName(extable.getString("INSTXT"));
						break;
					case HcmDataTypeName.PYMS :
						temp.setDetailCode(extable.getString("TRMODE"));
						temp.setDetailName(extable.getString("TRTXT"));
						break;
					case HcmDataTypeName.ZC :
//						temp.setParentCode(extable.getString("ZCDM"));
//						temp.setDetailCode(extable.getString("ZCCJ"));
//						temp.setDetailName(extable.getString("ZCCJ"));
//						data.add(temp);
//						temp = new HcmDictionariesTemp();
						temp.setParentCode(extable.getString("ZCLB"));
						temp.setDetailCode(extable.getString("ZCDM"));
						temp.setDetailName(extable.getString("ZCMS"));
						break;
					case HcmDataTypeName.ZCZX :
						temp.setDetailCode(extable.getString("ZCCX"));
						temp.setDetailName(extable.getString("ZCCXMS"));
						break;
					case HcmDataTypeName.ZCLB :
						temp.setParentCode(extable.getString("ZCCX"));
						temp.setDetailCode(extable.getString("ZCLB"));
						temp.setDetailName(extable.getString("ZCLBMS"));
						break;
					case HcmDataTypeName.ZD :
						temp.setParentCode(extable.getString("ZCCX"));
						temp.setDetailCode(extable.getString("ZZD"));
						temp.setDetailName(extable.getString("ZZD"));
						break;
					case HcmDataTypeName.ZPGYS :
						temp.setParentCode(extable.getString("ZPQD"));
						temp.setDetailCode(extable.getString("HZGYS"));
						temp.setDetailName(extable.getString("HZGYSMS"));
						break;
					case HcmDataTypeName.GZDD :
						temp.setDetailCode(extable.getString("ZHRFGZDD"));
						temp.setDetailName(extable.getString("ZHRFGZDDMS"));
						break;
					case HcmDataTypeName.XMWL :
						temp.setDetailCode(extable.getString("ZXMID"));
						temp.setDetailName(extable.getString("ZXMNAME"));
						data.add(temp);
						temp = new HcmDictionariesTemp();
						temp.setTbName(tbname);
						temp.setParentCode(extable.getString("ZXMID"));
						temp.setDetailCode(extable.getString("ZWLID"));
						temp.setDetailName(extable.getString("ZWLNAME"));
						data.add(temp);
						break;
					case HcmDataTypeName.ZZGD :
						temp.setParentCode(extable.getString("ZYWFW"));
						temp.setDetailCode(extable.getString("ZXM"));
						temp.setDetailName(extable.getString("ZXMNAME"));
						data.add(temp);
						temp = new HcmDictionariesTemp();
						temp.setTbName(tbname);
						temp.setParentCode(extable.getString("ZXM"));
						temp.setDetailCode(extable.getString("ZGD"));
						temp.setDetailName(extable.getString("ZGDNAME"));
						data.add(temp);
						break;
					case HcmDataTypeName.BZZW :
						temp.setDetailCode(extable.getString("GWLBM"));//业务领域
						temp.setDetailName(extable.getString("GWLMS"));
						data.add(temp);
						temp = new HcmDictionariesTemp();
						temp.setTbName(tbname);
						temp.setParentCode(extable.getString("GWLBM"));//车间
						temp.setDetailCode(extable.getString("GWXLBM"));
						temp.setDetailName(extable.getString("GWXLMS"));
						data.add(temp);
						temp = new HcmDictionariesTemp();
						temp.setTbName(tbname);
						temp.setParentCode(extable.getString("GWXLBM"));//标准岗位
						temp.setDetailCode(extable.getString("BZGWBM"));
						temp.setDetailName(extable.getString("BZGWMS"));
						break;
				}
				data.add(temp);
			}
			for (int i = 0; i < data.size(); i++) {
				gdao.getSession().save(data.get(i));
				if (i % 50 == 0) {
					gdao.getSession().flush();
					gdao.getSession().clear();
				}
			}
			gdao.getSession().flush();
			gdao.getSession().clear();
		}
		rowNum += extable.getNumRows();
		System.out.println("表" + tbname + "获取码值:" + extable.getNumRows() + "条");
	}
	
	public void storeList2(Object table, Object table2) {
		JCoTable extable = (JCoTable) table;
		JCoTable extable2 = (JCoTable) table2;
		String tbname = extable.getRecordMetaData().getName();
		List<HcmDictionariesTemp> data = new ArrayList<HcmDictionariesTemp>();
		
		if (extable2.getNumRows() > 0) {
			for (int i = 0; i < extable2.getNumRows(); i++) {
				extable2.setRow(i);
				HcmDictionariesTemp temp = new HcmDictionariesTemp();
				temp.setTbName(tbname);
				temp.setParentCode(extable2.getString("PERSG"));
				temp.setDetailCode(extable2.getString("PERSK"));
				data.add(temp);
			}
		}
		
		if (extable.getNumRows() > 0) {
			for (int i = 0; i < data.size(); i++) {
				String code = data.get(i).getDetailCode(); 
				for (int j = 0; j < extable.getNumRows(); j++) {
					extable.setRow(j);
					if (code.equals(extable.getString("PERSK"))) {
						data.get(i).setDetailName(extable.getString("PTEXT"));
						break;
					}
				}
			}
		}
		for (int i = 0; i < data.size(); i++) {
			gdao.getSession().save(data.get(i));
			if (i % 50 == 0) {
				gdao.getSession().flush();
				gdao.getSession().clear();
			}
		}
		gdao.getSession().flush();
		gdao.getSession().clear();
	}
	
	/**
	 * 调用存储过程
	 * @return 出参/失败提示
	 */
	public String exec() {
		ProcedureCall pc= gdao.getSession().createStoredProcedureCall("bpm_hcm_dic");
		pc.registerParameter("rs1", String.class, ParameterMode.OUT);
		pc.registerParameter("rs2", String.class, ParameterMode.OUT);
		String rs1 = pc.getOutputs().getOutputParameterValue("rs1").toString();
		String rs2 = pc.getOutputs().getOutputParameterValue("rs2").toString();
		if ("success".equals(rs1) && "success".equals(rs2)) {
			return "success";
		}
		return "fail";
	}
	
	private JSONObject getSAPConfig(String SearchKey){
		DatApplication dat = datApplicationService.getDatApplicationByName("SystemMG");
		Map<String, Object> config = getDocumentByField(dat.getAppId(), "SAPConfig", "SearchKey",SearchKey);
		Set keys = config.keySet();
		Iterator<String> iterator = keys.iterator();
		JSONObject SAPConfig = new JSONObject();
		while(iterator.hasNext()){
			String key = iterator.next();
			SAPConfig.put(key, config.get(key).toString());
		}
		return SAPConfig;
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