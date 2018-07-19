package com.gzsolartech.bpmportal.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 环节审批用时（考勤）记录表
 * @author 69990012
 *
 */
@Entity
@Table(name = "BPM_ACTIVITY_COST_TIME")
public class BpmActivityCostTime {

	private String costId;
	private String instanceId;
	private String taskId;
	private String documentId;
	private String nodeId;
	private String bpdId;
	private String taskName;
	private String taskOwner; //第一个收到待办的人
	private Timestamp createTime;
	private Timestamp submitTime;
	private String sapCostTime;
	private String isCheckSuccess;
	private String msg;
	
	
	
	public BpmActivityCostTime(String costId, String instanceId, String taskId,
			String documentId, String nodeId, String bpdId, String taskName,
			String taskOwner, Timestamp createTime, Timestamp submitTime,
			String sapCostTime, String isCheckSuccess, String msg) {
		super();
		this.costId = costId;
		this.instanceId = instanceId;
		this.taskId = taskId;
		this.documentId = documentId;
		this.nodeId = nodeId;
		this.bpdId = bpdId;
		this.taskName = taskName;
		this.taskOwner = taskOwner;
		this.createTime = createTime;
		this.submitTime = submitTime;
		this.sapCostTime = sapCostTime;
		this.isCheckSuccess = isCheckSuccess;
		this.msg = msg;
	}
	
	public BpmActivityCostTime() {
		// TODO Auto-generated constructor stub
	}
	
	@Id
	@Column(name = "ID",unique = true, nullable = false, length = 100)
	public String getCostId() {
		return costId;
	}
	public void setCostId(String costId) {
		this.costId = costId;
	}
	
	
	@Column(name = "INSTANCE_ID", length = 100)
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	
	@Column(name = "TASK_ID", length = 100)
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	@Column(name = "DOCUMENT_ID", length = 100)
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	@Column(name = "NODE_ID", length = 100)
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	
	@Column(name = "BPD_ID", length = 100)
	public String getBpdId() {
		return bpdId;
	}
	public void setBpdId(String bpdId) {
		this.bpdId = bpdId;
	}
	
	
	@Column(name = "TASK_NAME", length = 100)
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	@Column(name = "TASK_OWNER", length = 100)
	public String getTaskOwner() {
		return taskOwner;
	}
	public void setTaskOwner(String taskOwner) {
		this.taskOwner = taskOwner;
	}
	
	
	@Column(name = "CREATE_TIME", length = 7)
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	
	@Column(name = "SUBMIT_TIME", length = 7)
	public Timestamp getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(Timestamp submitTime) {
		this.submitTime = submitTime;
	}
	
	
	@Column(name = "SAP_COST_TIME", length = 100)
	public String getSapCostTime() {
		return sapCostTime;
	}
	public void setSapCostTime(String sapCostTime) {
		this.sapCostTime = sapCostTime;
	}
	
	@Column(name = "IS_CHECK_SUCCESS", length = 100)
	public String getIsCheckSuccess() {
		return isCheckSuccess;
	}
	public void setIsCheckSuccess(String isCheckSuccess) {
		this.isCheckSuccess = isCheckSuccess;
	}
	
	@Column(name = "MSG", length = 100)
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
