package com.gzsolartech.bpmportal.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * 模块调度时间记录表
 * @author solar
 *
 */
@Entity
@Table(name = "APPLY_SYCN_UPDATE_RECORD")
public class ApplySycnUpdateRecord {
	
	//ID
	private String id;
	//执行模块
	private String modular;
	//执行状态
	private String  state;
	//记录的更新时间
	private Timestamp updateTime;
	//调度执行时间
	private Timestamp createTime;
	
	
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
	@Column(name = "STATE", length = 20)
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	@Column(name = "UPDATE_TIME", length = 7)
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	@Column(name = "CREATE_TIME", length = 7)
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	} 

	
	
}
