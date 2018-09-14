package com.gzsolartech.schedule.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gzsolartech.smartforms.constant.HttpSessionKey;
import com.gzsolartech.smartforms.constant.SotTableSaveFormat;
import com.gzsolartech.smartforms.service.BaseDataService;
import com.gzsolartech.smartforms.service.DatApplicationService;

@Service("HR0003Service")
public class HR0003Service extends BaseDataService{
	
	public List<Map<String, String>> getFailDataList(String formName) {
		String sql = "SELECT trow.row_id "
				+",extractvalue(trow.row_data, '//root/row/signDate') AS signDate "
				+",extractvalue(trow.row_data, '//root/row/signTime') AS signTime "
				+",extractvalue(trow.row_data, '//root/row/H_signType_text') AS signType "
				+",extractvalue(trow.row_data, '//root/row/H_OverrideTypeName_value') AS overrideTypeName "
				+",extractvalue(tdoc.document_data, '//root/LNumber') AS empNum "
			+"FROM dat_table_row trow "
				+",dat_document tdoc "
				+",bpm_instance_info tins "
			+"WHERE trow.document_id = tdoc.document_id "
				+"AND tins.document_id = tdoc.document_id "
				+"AND tdoc.form_name = '"+formName+"' "
				+"AND ( "
					+ "extractvalue(trow.row_data, '//root/row/writeStatus') != 'Success' "
					+ "OR extractvalue(trow.row_data, '//root/row/writeStatus') IS NULL "
					+ ") "
				+"and tins.instance_state = 'STATE_FINISHED' ";
		SQLQuery rs = gdao.getSession().createSQLQuery(sql.toString());
		rs.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return rs.list();
		/*Map<String, Object> result = new HashMap<String, Object>();
		String tableId = "dynamicRowsIdTwo";
		// 1. 读取表格数据
		List<Map<String,Object>> tableDatas = datTableRowService.getTableDatas(documentId, tableId);
		// 2. 分析结果
		List<String> kRes = new ArrayList<String>();
		List<Map<String, Object>> bRes = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> line: tableDatas) {
			Map<String, Object> bpmResLine = new HashMap<String, Object>();
			if("Success".equals(line.get("writeStatus")))
				continue;// 跳过成功行
			
	        try {
				JSONObject qianka = kronosService.qianka(getValue(line, "signDate")
						, getValue(line, "signTime")
						, getValue(line, "H_signType_text")
						, getValue(line, "H_OverrideTypeName_value")
						, empNum);
				if("Success".equals(qianka.get("Status"))) 
					line.put("writeStatus", "Success");
				else 
					line.put("writeStatus", "Failure");
				line.put("message", getMessage(qianka));
				kRes.add(qianka.toString());
				// 封装返回结果
				bpmResLine.put("writeStatus", line.get("writeStatus"));
				bpmResLine.put("message", line.get("message"));
				bpmResLine.put("rowgroup", line.get("__rowgrpId"));
				bRes.add(bpmResLine);
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("签卡写入kronos发生异常，" + e.getMessage());
			}
	        
		}*/
	}
	
	/**
	 * 批量更新状态
	 * @param leaveIds
	 * @param status
	 */
	public int updateRowStatus(List<String> ids, String status){
		String sql = "update dat_table_row t "
				+ "set t.row_data = updatexml(t.row_data, '//root/row/writeStatus','<writeStatus>" + status + "</writeStatus>', '//root/row/message', '<message></message>') "
				+ "where t.row_id in ( :ids )";
		SQLQuery rs = gdao.getSession().createSQLQuery(sql);
		rs.setParameterList("ids", ids);
		return rs.executeUpdate();
	}
}
