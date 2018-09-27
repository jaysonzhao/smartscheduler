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
public class Doc2Service extends BaseDataService{
	@Autowired
	private Doc2Service docService;
	@Autowired
	private DatApplicationService datApplicationService;
	
	public List getAllDocumentBykeys(){
		List data = new ArrayList();
		data = gdao.executeJDBCSqlQuery("select extractvalue(t.row_data, '//root/row/IT0001_jobNumber') AS employeeId,"
				+ "extractvalue(t.row_data, '//root/row/userName') AS employeeName,"
				+ "extractvalue(t.row_data, '//root/row/ShowSTUendTime') AS endTime, "
				+ "extractvalue(d.document_data, '//root/orderNum') AS ordernum, "
				+ "extractvalue(d.document_data, '//root/__appid') AS appid,"      
			    + "extractvalue(d.document_data, '//root/__docuid') AS docuid " 
				+ "from dat_table_row t, dat_document d "
				+ "where t.table_id = 'visitTable'"
				+ "AND t.document_id = d.document_id "
				+ "AND extractvalue(d.document_data, '//root/__stepId') = "
				+ "'200' "
				+ "AND to_date(extractvalue(t.row_data, '//root/row/ShowSTUendTime'),"
				+ "'yyyy-MM-dd') < SYSDATE - 1 ");
		return data; 
	}
	
}