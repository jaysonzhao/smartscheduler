package com.gzsolartech.schedule.service;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.gzsolartech.bpmportal.entity.AacChangerBpm;
import com.gzsolartech.bpmportal.entity.AacChangerSap;
import com.gzsolartech.bpmportal.util.RfcManager;
import com.gzsolartech.smartforms.entity.DatApplication;
import com.gzsolartech.smartforms.entity.DatDocument;
import com.gzsolartech.smartforms.service.BaseDataService;
import com.gzsolartech.smartforms.utils.DatDocumentUtil;
import com.gzsolartech.smartforms.utils.XmlDataUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
/**
 * 批量同步异动信息
 * @author Tsy
   2018年9月5日
   下午6:51:25
 */
@Service
public class SynChangerService extends  BaseDataService{
	private static final Logger LOG = LoggerFactory.getLogger(SynChangerService.class);
	/**
	 * 保存SAP异动信息到BPM
	   Tsy
	 * 2018年9月5日下午3:57:19
	 */
	public void saveSapChanger(){
		List<AacChangerSap> aacChangerSaps=new ArrayList<>();
		try {
			JCoFunction function = null;
			JCoDestination destination = null;
			RfcManager rfcManager = RfcManager.getInstance(getSAPUserInfo());
			destination = rfcManager.getDestination();
			function = rfcManager.getFunction(destination, "ZHRPAFM049");// 调用接口方法
			DateFormat df = new SimpleDateFormat("yyyyMMdd");
	    	Calendar calendar = Calendar.getInstance();
	    	String dateName = df.format(calendar.getTime());
			JCoParameterList input = function.getImportParameterList();
			input.setValue("I_DATE1", getChangerDate().replace("-",""));
			input.setValue("I_DATE2", dateName);
			function.execute(destination);// 执行这个方法
			JCoParameterList parameterList = function.getTableParameterList();
			JCoTable table = parameterList.getTable("IT_RETURN");
			if(!table.isEmpty()){
				for (int i = 0; i < table.getNumRows(); i++) {
					AacChangerSap aacChangerSap=new AacChangerSap();
					table.setRow(i);
					aacChangerSap.setPernr(String.valueOf(table.getString("PERNR")).equals("null") ? "" : table.getString("PERNR"));//编号
					aacChangerSap.setEname(String.valueOf(table.getString("ENAME")).equals("null") ? "" : table.getString("ENAME"));//员工姓名
					aacChangerSap.setState("完成");//异动状态
					aacChangerSap.setMassn(String.valueOf(table.getString("MASSN")).equals("null") ? "" : table.getString("MASSN"));//操作类型代码
					aacChangerSap.setMassnT(String.valueOf(table.getString("MASSN_T")).equals("null") ? "" : table.getString("MASSN_T"));//操作类型文本
					aacChangerSap.setMassnBpm(String.valueOf(table.getString("MASSN_BPM")).equals("null") ? "" : table.getString("MASSN_BPM"));//传输至BPM的操作类型
					aacChangerSap.setMassg(String.valueOf(table.getString("MASSG")).equals("null") ? "" : table.getString("MASSG"));//操作原因代码
					aacChangerSap.setMassgT(String.valueOf(table.getString("MASSG_T")).equals("null") ? "" : table.getString("MASSG_T"));//操作原因文本
					aacChangerSap.setWerksFr(String.valueOf(table.getString("WERKS_FR")).equals("null") ? "" : table.getString("WERKS_FR"));//异动前人事范围代码
					aacChangerSap.setWerksFrT(String.valueOf(table.getString("WERKS_FR_T")).equals("null") ? "" : table.getString("WERKS_FR_T"));//异动前人事范围文本
					aacChangerSap.setBtrtlFr(String.valueOf(table.getString("BTRTL_FR")).equals("null") ? "" : table.getString("BTRTL_FR"));//异动前人事子范围代码
					aacChangerSap.setBtrtlFrT(String.valueOf(table.getString("BTRTL_FR_T")).equals("null") ? "" : table.getString("BTRTL_FR_T"));//异动前人事子范围文本
					aacChangerSap.setOrgehLv1Fr(String.valueOf(table.getString("ORGEH_LV1_FR")).equals("null") ? "" : table.getString("ORGEH_LV1_FR"));//异动前一级部门编号
					aacChangerSap.setOrgehLv1FrT(String.valueOf(table.getString("ORGEH_LV1_FR_T")).equals("null") ? "" : table.getString("ORGEH_LV1_FR_T"));//异动前一级部门名称
					aacChangerSap.setOrgehLv2Fr(String.valueOf(table.getString("ORGEH_LV2_FR")).equals("null") ? "" : table.getString("ORGEH_LV2_FR"));//异动前二级部门编号
					aacChangerSap.setOrgehLv2FrT(String.valueOf(table.getString("ORGEH_LV2_FR_T")).equals("null") ? "" : table.getString("ORGEH_LV2_FR_T"));//异动前二级部门名称
					aacChangerSap.setOrgehLv3Fr(String.valueOf(table.getString("ORGEH_LV3_FR")).equals("null") ? "" : table.getString("ORGEH_LV3_FR"));//异动前三级部门编号
					aacChangerSap.setOrgehLv3FrT(String.valueOf(table.getString("ORGEH_LV3_FR_T")).equals("null") ? "" : table.getString("ORGEH_LV3_FR_T"));//异动前三级部门名称
					aacChangerSap.setOrgehLv4Fr(String.valueOf(table.getString("ORGEH_LV4_FR")).equals("null") ? "" : table.getString("ORGEH_LV4_FR"));//异动前四级部门编号
					aacChangerSap.setOrgehLv4FrT(String.valueOf(table.getString("ORGEH_LV4_FR_T")).equals("null") ? "" : table.getString("ORGEH_LV4_FR_T"));//异动前四级部门名称
					aacChangerSap.setOrgehLv5Fr(String.valueOf(table.getString("ORGEH_LV5_FR")).equals("null") ? "" : table.getString("ORGEH_LV5_FR"));//异动前五级部门编号
					aacChangerSap.setOrgehLv5FrT(String.valueOf(table.getString("ORGEH_LV5_FR_T")).equals("null") ? "" : table.getString("ORGEH_LV5_FR_T"));//异动前五级部门名称
					aacChangerSap.setOrgehLv6Fr(String.valueOf(table.getString("ORGEH_LV6_FR")).equals("null") ? "" : table.getString("ORGEH_LV6_FR"));//异动前六级部门编号
					aacChangerSap.setOrgehLv6FrT(String.valueOf(table.getString("ORGEH_LV6_FR_T")).equals("null") ? "" : table.getString("ORGEH_LV6_FR_T"));//异动前六级部门名称
					aacChangerSap.setPlansFr(String.valueOf(table.getString("PLANS_FR")).equals("null") ? "" : table.getString("PLANS_FR"));//异动前职位代码
					aacChangerSap.setPlansFrT(String.valueOf(table.getString("PLANS_FR_T")).equals("null") ? "" : table.getString("PLANS_FR_T"));//异动前职位文本
					aacChangerSap.setZhrfzcmcFr(String.valueOf(table.getString("ZHRFZCMC_FR")).equals("null") ? "" : table.getString("ZHRFZCMC_FR"));//异动前职称代码
					aacChangerSap.setZhrfzcmcFrT(String.valueOf(table.getString("ZHRFZCMC_FR_T")).equals("null") ? "" : table.getString("ZHRFZCMC_FR_T"));//异动前职称
					aacChangerSap.setLeaderenameFr(String.valueOf(table.getString("LEADERENAME_FR")).equals("null") ? "" : table.getString("LEADERENAME_FR"));//异动前领导
					aacChangerSap.setBegdaTo(String.valueOf(table.getString("BEGDA_TO")).equals("null") ? "" : table.getString("BEGDA_TO"));//异动日期
					aacChangerSap.setWerksTo(String.valueOf(table.getString("WERKS_TO")).equals("null") ? "" : table.getString("WERKS_TO"));//异动后人事范围代码
					aacChangerSap.setWerksToT(String.valueOf(table.getString("WERKS_TO_T")).equals("null") ? "" : table.getString("WERKS_TO_T"));//异动后人事范围文本
					aacChangerSap.setBtrtlTo(String.valueOf(table.getString("BTRTL_TO")).equals("null") ? "" : table.getString("BTRTL_TO"));//异动后人事子范围代码
					aacChangerSap.setBtrtlToT(String.valueOf(table.getString("BTRTL_TO_T")).equals("null") ? "" : table.getString("BTRTL_TO_T"));//异动后人事子范围文本
					aacChangerSap.setOrgehLv1To(String.valueOf(table.getString("ORGEH_LV1_TO")).equals("null") ? "" : table.getString("ORGEH_LV1_TO"));//异动后一级部门编号
					aacChangerSap.setOrgehLv1ToT(String.valueOf(table.getString("ORGEH_LV1_TO_T")).equals("null") ? "" : table.getString("ORGEH_LV1_TO_T"));//异动后一级部门名称
					aacChangerSap.setOrgehLv2To(String.valueOf(table.getString("ORGEH_LV2_TO")).equals("null") ? "" : table.getString("ORGEH_LV2_TO"));//异动后二级部门编号
					aacChangerSap.setOrgehLv2ToT(String.valueOf(table.getString("ORGEH_LV2_TO_T")).equals("null") ? "" : table.getString("ORGEH_LV2_TO_T"));//异动后二级部门名称
					aacChangerSap.setOrgehLv3To(String.valueOf(table.getString("ORGEH_LV3_TO")).equals("null") ? "" : table.getString("ORGEH_LV3_TO"));//异动后三级部门编号
					aacChangerSap.setOrgehLv3ToT(String.valueOf(table.getString("ORGEH_LV3_TO_T")).equals("null") ? "" : table.getString("ORGEH_LV3_TO_T"));//异动后三级部门名称
					aacChangerSap.setOrgehLv4To(String.valueOf(table.getString("ORGEH_LV4_TO")).equals("null") ? "" : table.getString("ORGEH_LV4_TO"));//异动后四级部门编号
					aacChangerSap.setOrgehLv4ToT(String.valueOf(table.getString("ORGEH_LV4_TO_T")).equals("null") ? "" : table.getString("ORGEH_LV4_TO_T"));//异动后四级部门名称
					aacChangerSap.setOrgehLv5To(String.valueOf(table.getString("ORGEH_LV5_TO")).equals("null") ? "" : table.getString("ORGEH_LV5_TO"));//异动后五级部门编号
					aacChangerSap.setOrgehLv5ToT(String.valueOf(table.getString("ORGEH_LV5_TO_T")).equals("null") ? "" : table.getString("ORGEH_LV5_TO_T"));//异动后五级部门名称
					aacChangerSap.setOrgehLv6To(String.valueOf(table.getString("ORGEH_LV6_TO")).equals("null") ? "" : table.getString("ORGEH_LV6_TO"));//异动后六级部门编号
					aacChangerSap.setOrgehLv6ToT(String.valueOf(table.getString("ORGEH_LV6_TO_T")).equals("null") ? "" : table.getString("ORGEH_LV6_TO_T"));//异动后六级部门名称
					aacChangerSap.setPlansTo(String.valueOf(table.getString("PLANS_TO")).equals("null") ? "" : table.getString("PLANS_TO"));//异动后职位代码
					aacChangerSap.setPlansToT(String.valueOf(table.getString("PLANS_TO_T")).equals("null") ? "" : table.getString("PLANS_TO_T"));//异动后职位文本
					aacChangerSap.setZhrfzcmcTo(String.valueOf(table.getString("ZHRFZCMC_TO")).equals("null") ? "" : table.getString("ZHRFZCMC_TO"));//异动后职称代码
					aacChangerSap.setZhrfzcmcToT(String.valueOf(table.getString("ZHRFZCMC_TO_T")).equals("null") ? "" : table.getString("ZHRFZCMC_TO_T"));//异动后职称文本
					aacChangerSap.setLeaderenameTo(String.valueOf(table.getString("LEADERENAME_TO")).equals("null") ? "" : table.getString("LEADERENAME_TO"));//异动后直属领导
					aacChangerSaps.add(aacChangerSap);
				}
				gdao.executeJDBCSql("delete AAC_CHANGES_SAP");
				gdao.getSession().flush();
				gdao.getSession().clear();
				for (int i = 0; i < aacChangerSaps.size(); i++) {
					gdao.save(aacChangerSaps.get(i));
					gdao.getSession().flush();
					gdao.getSession().clear();
				}
			}
		} catch (Exception e) {
			LOG.error("定时获取SAP异动数据异常",e.toString());
			System.out.println(e.toString());
		}
	}
	/**
	 * 保存BPM异动信息
	   Tsy
	 * 2018年9月6日上午10:57:28
	 */
	public void saveBpmChanger(){
		List<AacChangerBpm> aacChangerBpms=getBpmChangers();
		try {
			if(!aacChangerBpms.isEmpty()){
				gdao.executeJDBCSql("delete AAC_CHANGES_BPM");
				gdao.getSession().flush();
				gdao.getSession().clear();
				for (int i = 0; i < aacChangerBpms.size(); i++) {
					gdao.save(aacChangerBpms.get(i));
					gdao.getSession().flush();
					gdao.getSession().clear();
				}
			}
		} catch (Exception e) {
			LOG.error("定时获取BPM异动数据异常",e.toString());
			System.out.println(e.toString());
		}
	}
	/**
	 * 获取BPM在途转正和离职单异动数据
	   Tsy
	 * 2018年9月11日下午5:08:41
	 */
	public List<AacChangerBpm> getBpmChangers(){
		List<AacChangerBpm> aacChangerBpms=new ArrayList<>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    	Calendar calendar = Calendar.getInstance();
    	String dateName = df.format(calendar.getTime());
		String changerDate=getChangerDate();
        String sql="select extractvalue(doc.DOCUMENT_DATA,'/root/jobNumber') PERNR,"
        		+ "extractvalue(doc.DOCUMENT_DATA,'/root/name') ENAME,"
		        + "'在途' STATE,"
		        + "'试用期转正' MASSN_BPM,"
		        + "'转正申请' MASSG_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/positiveDate') BEGDA_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/WERKS_FR') WERKS_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/WERKS_FR_T') WERKS_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/diqusave') BTRTL_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/BTRTL_FR_T') BTRTL_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV1_FR') ORGEH_LV1_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV1_FR_T') ORGEH_LV1_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV2_FR') ORGEH_LV2_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV2_FR_T') ORGEH_LV2_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV3_FR') ORGEH_LV3_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV3_FR_T') ORGEH_LV3_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV4_FR') ORGEH_LV4_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV4_FR_T') ORGEH_LV4_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV5_FR') ORGEH_LV5_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV5_FR_T') ORGEH_LV5_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV6_FR') ORGEH_LV6_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV6_FR_T') ORGEH_LV6_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/PLANS_FR') PLANS_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/PLANS_FR_T') PLANS_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/positiveLaterProfessional') ZHRFZCMC_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/positiveLaterProfessional1') ZHRFZCMC_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/leadership') LEADERENAME_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/WERKS_FR') WERKS_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/WERKS_FR_T') WERKS_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/diqusave') BTRTL_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/BTRTL_FR_T') BTRTL_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV1_FR') ORGEH_LV1_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV1_FR_T') ORGEH_LV1_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV2_FR') ORGEH_LV2_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV2_FR_T') ORGEH_LV2_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV3_FR') ORGEH_LV3_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV3_FR_T') ORGEH_LV3_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV4_FR') ORGEH_LV4_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV4_FR_T') ORGEH_LV4_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV5_FR') ORGEH_LV5_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV5_FR_T') ORGEH_LV5_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV6_FR') ORGEH_LV6_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV6_FR_T') ORGEH_LV6_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/PLANS_FR') PLANS_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/PLANS_FR_T') PLANS_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ZHRFZCMC_FR') ZHRFZCMC_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ZHRFZCMC_FR_T') ZHRFZCMC_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/leadership') LEADERENAME_FR"
		        + " from DAT_DOCUMENT doc where doc.FORM_NAME='Form_HR20' and "
		        + "(doc.DOCUMENT_STATUS!='deleted' or doc.DOCUMENT_STATUS is null) and "
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/createDate')>='"+changerDate+"' and "
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/createDate')<='"+dateName+"' and "
		        + "doc.DOCUMENT_ID in(select document_id from BPM_TASK_INFO where NODE_ID "
		        + "<> 'bpdid:eda64c74b0f340b3:-2cc2e685:1593a6aec60:-792a' and task_status='Received') "
		        + "union all "
		        + "select extractvalue(doc.DOCUMENT_DATA,'/root/empNum') PERNR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/empName') ENAME,"
		        + "'在途' STATE,"
		        + "'离职' MASSN_BPM,"
		        + "'离职申请' MASSG_T,"
		        + "decode(extractvalue(doc.DOCUMENT_DATA,'/root/termDate'),'',"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/vacateinfodate'),extractvalue(doc.DOCUMENT_DATA,'/root/termDate')) BEGDA_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/WERKS_FR') WERKS_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/WERKS_FR_T') WERKS_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/regionValue') BTRTL_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/BTRTL_FR_T') BTRTL_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV1_FR') ORGEH_LV1_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV1_FR_T') ORGEH_LV1_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV2_FR') ORGEH_LV2_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV2_FR_T') ORGEH_LV2_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV3_FR') ORGEH_LV3_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV3_FR_T') ORGEH_LV3_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV4_FR') ORGEH_LV4_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV4_FR_T') ORGEH_LV4_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV5_FR') ORGEH_LV5_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV5_FR_T') ORGEH_LV5_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV6_FR') ORGEH_LV6_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV6_FR_T') ORGEH_LV6_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/PLANS_FR') PLANS_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/PLANS_FR_T') PLANS_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ZHRFZCMC_FR') ZHRFZCMC_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ZHRFZCMC_FR_T') ZHRFZCMC_TO_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/leaderShip') LEADERENAME_TO,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/WERKS_FR') WERKS_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/WERKS_FR_T') WERKS_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/regionValue') BTRTL_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/BTRTL_FR_T') BTRTL_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV1_FR') ORGEH_LV1_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV1_FR_T') ORGEH_LV1_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV2_FR') ORGEH_LV2_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV2_FR_T') ORGEH_LV2_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV3_FR') ORGEH_LV3_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV3_FR_T') ORGEH_LV3_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV4_FR') ORGEH_LV4_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV4_FR_T') ORGEH_LV4_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV5_FR') ORGEH_LV5_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV5_FR_T') ORGEH_LV5_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV6_FR') ORGEH_LV6_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ORGEH_LV6_FR_T') ORGEH_LV6_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/PLANS_FR') PLANS_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/PLANS_FR_T') PLANS_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ZHRFZCMC_FR') ZHRFZCMC_FR,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/ZHRFZCMC_FR_T') ZHRFZCMC_FR_T,"
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/leaderShip') LEADERENAME_FR"
		        + " from DAT_DOCUMENT doc where doc.FORM_NAME='Form_HR19' and "
		        + "(doc.DOCUMENT_STATUS!='deleted' or doc.DOCUMENT_STATUS is null) and "
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/applDate')>='"+changerDate+"' and "
		        + "extractvalue(doc.DOCUMENT_DATA,'/root/applDate')<='"+dateName+"' and "
		        + "doc.DOCUMENT_ID in(select document_id from BPM_TASK_INFO where NODE_ID "
		        + "<> 'bpdid:7a8f0308bd0e0db7:-7fe8d7e1:15938d1ac62:-7f4f' and task_status='Received')";
		List<Map<String, String>> datas=gdao.executeJDBCSqlQuery(sql);
		for (int i = 0; i < datas.size(); i++) {
			Map<String, String> table=datas.get(i);
			AacChangerBpm aacChangerBpm=new AacChangerBpm();
			aacChangerBpm.setPernr(String.valueOf(table.get("PERNR")).equals("null") ? "" : table.get("PERNR"));//编号
			aacChangerBpm.setEname(String.valueOf(table.get("ENAME")).equals("null") ? "" : table.get("ENAME"));//员工姓名
			aacChangerBpm.setState(String.valueOf(table.get("STATE")).equals("null") ? "" : table.get("STATE"));//异动状态
			aacChangerBpm.setMassnBpm(String.valueOf(table.get("MASSN_BPM")).equals("null") ? "" : table.get("MASSN_BPM"));//传输至BPM的操作类型
			aacChangerBpm.setMassgT(String.valueOf(table.get("MASSG_T")).equals("null") ? "" : table.get("MASSG_T"));//操作原因文本
			aacChangerBpm.setBegdaTo(String.valueOf(table.get("BEGDA_TO")).equals("null") ? "" : table.get("BEGDA_TO"));//异动日期
			aacChangerBpm.setWerksFr(String.valueOf(table.get("WERKS_FR")).equals("null") ? "" : table.get("WERKS_FR"));//异动前人事范围代码
			aacChangerBpm.setWerksFrT(String.valueOf(table.get("WERKS_FR_T")).equals("null") ? "" : table.get("WERKS_FR_T"));//异动前人事范围文本
			aacChangerBpm.setBtrtlFr(String.valueOf(table.get("BTRTL_FR")).equals("null") ? "" : table.get("BTRTL_FR"));//异动前人事子范围代码
			aacChangerBpm.setBtrtlFrT(String.valueOf(table.get("BTRTL_FR_T")).equals("null") ? "" : table.get("BTRTL_FR_T"));//异动前人事子范围文本
			aacChangerBpm.setOrgehLv1Fr(String.valueOf(table.get("ORGEH_LV1_FR")).equals("null") ? "" : table.get("ORGEH_LV1_FR"));//异动前一级部门编号
			aacChangerBpm.setOrgehLv1FrT(String.valueOf(table.get("ORGEH_LV1_FR_T")).equals("null") ? "" : table.get("ORGEH_LV1_FR_T"));//异动前一级部门名称
			aacChangerBpm.setOrgehLv2Fr(String.valueOf(table.get("ORGEH_LV2_FR")).equals("null") ? "" : table.get("ORGEH_LV2_FR"));//异动前二级部门编号
			aacChangerBpm.setOrgehLv2FrT(String.valueOf(table.get("ORGEH_LV2_FR_T")).equals("null") ? "" : table.get("ORGEH_LV2_FR_T"));//异动前二级部门名称
			aacChangerBpm.setOrgehLv3Fr(String.valueOf(table.get("ORGEH_LV3_FR")).equals("null") ? "" : table.get("ORGEH_LV3_FR"));//异动前三级部门编号
			aacChangerBpm.setOrgehLv3FrT(String.valueOf(table.get("ORGEH_LV3_FR_T")).equals("null") ? "" : table.get("ORGEH_LV3_FR_T"));//异动前三级部门名称
			aacChangerBpm.setOrgehLv4Fr(String.valueOf(table.get("ORGEH_LV4_FR")).equals("null") ? "" : table.get("ORGEH_LV4_FR"));//异动前四级部门编号
			aacChangerBpm.setOrgehLv4FrT(String.valueOf(table.get("ORGEH_LV4_FR_T")).equals("null") ? "" : table.get("ORGEH_LV4_FR_T"));//异动前四级部门名称
			aacChangerBpm.setOrgehLv5Fr(String.valueOf(table.get("ORGEH_LV5_FR")).equals("null") ? "" : table.get("ORGEH_LV5_FR"));//异动前五级部门编号
			aacChangerBpm.setOrgehLv5FrT(String.valueOf(table.get("ORGEH_LV5_FR_T")).equals("null") ? "" : table.get("ORGEH_LV5_FR_T"));//异动前五级部门名称
			aacChangerBpm.setOrgehLv6Fr(String.valueOf(table.get("ORGEH_LV6_FR")).equals("null") ? "" : table.get("ORGEH_LV6_FR"));//异动前六级部门编号
			aacChangerBpm.setOrgehLv6FrT(String.valueOf(table.get("ORGEH_LV6_FR_T")).equals("null") ? "" : table.get("ORGEH_LV6_FR_T"));//异动前六级部门名称
			aacChangerBpm.setPlansFr(String.valueOf(table.get("PLANS_FR")).equals("null") ? "" : table.get("PLANS_FR"));//异动前职位代码
			aacChangerBpm.setPlansFrT(String.valueOf(table.get("PLANS_FR_T")).equals("null") ? "" : table.get("PLANS_FR_T"));//异动前职位文本
			aacChangerBpm.setZhrfzcmcFr(String.valueOf(table.get("ZHRFZCMC_FR")).equals("null") ? "" : table.get("ZHRFZCMC_FR"));//异动前职称代码
			aacChangerBpm.setZhrfzcmcFrT(String.valueOf(table.get("ZHRFZCMC_FR_T")).equals("null") ? "" : table.get("ZHRFZCMC_FR_T"));//异动前职称
			aacChangerBpm.setLeaderenameFr(String.valueOf(table.get("LEADERENAME_FR")).equals("null") ? "" : table.get("LEADERENAME_FR"));//异动前领导
			aacChangerBpm.setWerksTo(String.valueOf(table.get("WERKS_TO")).equals("null") ? "" : table.get("WERKS_TO"));//异动后人事范围代码
			aacChangerBpm.setWerksToT(String.valueOf(table.get("WERKS_TO_T")).equals("null") ? "" : table.get("WERKS_TO_T"));//异动后人事范围文本
			aacChangerBpm.setBtrtlTo(String.valueOf(table.get("BTRTL_TO")).equals("null") ? "" : table.get("BTRTL_TO"));//异动后人事子范围代码
			aacChangerBpm.setBtrtlToT(String.valueOf(table.get("BTRTL_TO_T")).equals("null") ? "" : table.get("BTRTL_TO_T"));//异动后人事子范围文本
			aacChangerBpm.setOrgehLv1To(String.valueOf(table.get("ORGEH_LV1_TO")).equals("null") ? "" : table.get("ORGEH_LV1_TO"));//异动后一级部门编号
			aacChangerBpm.setOrgehLv1ToT(String.valueOf(table.get("ORGEH_LV1_TO_T")).equals("null") ? "" : table.get("ORGEH_LV1_TO_T"));//异动后一级部门名称
			aacChangerBpm.setOrgehLv2To(String.valueOf(table.get("ORGEH_LV2_TO")).equals("null") ? "" : table.get("ORGEH_LV2_TO"));//异动后二级部门编号
			aacChangerBpm.setOrgehLv2ToT(String.valueOf(table.get("ORGEH_LV2_TO_T")).equals("null") ? "" : table.get("ORGEH_LV2_TO_T"));//异动后二级部门名称
			aacChangerBpm.setOrgehLv3To(String.valueOf(table.get("ORGEH_LV3_TO")).equals("null") ? "" : table.get("ORGEH_LV3_TO"));//异动后三级部门编号
			aacChangerBpm.setOrgehLv3ToT(String.valueOf(table.get("ORGEH_LV3_TO_T")).equals("null") ? "" : table.get("ORGEH_LV3_TO_T"));//异动后三级部门名称
			aacChangerBpm.setOrgehLv4To(String.valueOf(table.get("ORGEH_LV4_TO")).equals("null") ? "" : table.get("ORGEH_LV4_TO"));//异动后四级部门编号
			aacChangerBpm.setOrgehLv4ToT(String.valueOf(table.get("ORGEH_LV4_TO_T")).equals("null") ? "" : table.get("ORGEH_LV4_TO_T"));//异动后四级部门名称
			aacChangerBpm.setOrgehLv5To(String.valueOf(table.get("ORGEH_LV5_TO")).equals("null") ? "" : table.get("ORGEH_LV5_TO"));//异动后五级部门编号
			aacChangerBpm.setOrgehLv5ToT(String.valueOf(table.get("ORGEH_LV5_TO_T")).equals("null") ? "" : table.get("ORGEH_LV5_TO_T"));//异动后五级部门名称
			aacChangerBpm.setOrgehLv6To(String.valueOf(table.get("ORGEH_LV6_TO")).equals("null") ? "" : table.get("ORGEH_LV6_TO"));//异动后六级部门编号
			aacChangerBpm.setOrgehLv6ToT(String.valueOf(table.get("ORGEH_LV6_TO_T")).equals("null") ? "" : table.get("ORGEH_LV6_TO_T"));//异动后六级部门名称
			aacChangerBpm.setPlansTo(String.valueOf(table.get("PLANS_TO")).equals("null") ? "" : table.get("PLANS_TO"));//异动后职位代码
			aacChangerBpm.setPlansToT(String.valueOf(table.get("PLANS_TO_T")).equals("null") ? "" : table.get("PLANS_TO_T"));//异动后职位文本
			aacChangerBpm.setZhrfzcmcTo(String.valueOf(table.get("ZHRFZCMC_TO")).equals("null") ? "" : table.get("ZHRFZCMC_TO"));//异动后职称代码
			aacChangerBpm.setZhrfzcmcToT(String.valueOf(table.get("ZHRFZCMC_TO_T")).equals("null") ? "" : table.get("ZHRFZCMC_TO_T"));//异动后职称文本
			aacChangerBpm.setLeaderenameTo(String.valueOf(table.get("LEADERENAME_TO")).equals("null") ? "" : table.get("LEADERENAME_TO"));//异动后直属领导
			aacChangerBpms.add(aacChangerBpm);
		}
		return aacChangerBpms;
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
	private String getChangerDate() {
		String changerDate="";
		DatApplication dat = getDatApplicationByName("SystemMG");
		Map<String, Object> changerDateMap = getDocumentByField(dat.getAppId(), "MultiValue",
				"ThisLevelMenu", "changerDate");
		changerDate=String.valueOf(changerDateMap.get("MutilData"));
		return changerDate;
	}
}
