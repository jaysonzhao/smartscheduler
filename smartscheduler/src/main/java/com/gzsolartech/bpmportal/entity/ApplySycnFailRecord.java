package com.gzsolartech.bpmportal.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 模块调度失败记录表
 * @author solar
 *
 */
@Entity
@Table(name = "APPLY_SYCN_FAIL_RECORD")
public class ApplySycnFailRecord {
	
	
		//ID
		private String id;
		//执行模块
		private String modular;
		//调度执行时间
		private Timestamp createTime;
		//文档ID 
		private String documentId;
		
		@Id
		@Column(name = "ID", unique = true, nullable = false, length = 100)
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		@Column(name = "MODULAR", length = 100)
		public String getModular() {
			return modular;
		}
		public void setModular(String modular) {
			this.modular = modular;
		}
		@Column(name = "CREATE_TIME", length = 7)
		public Timestamp getCreateTime() {
			return createTime;
		}
		public void setCreateTime(Timestamp createTime) {
			this.createTime = createTime;
		}
		@Column(name = "DOCUMENT_ID", length = 100)
		public String getDocumentId() {
			return documentId;
		}
		public void setDocumentId(String documentId) {
			this.documentId = documentId;
		}
		
		
		
		
		
		
}
