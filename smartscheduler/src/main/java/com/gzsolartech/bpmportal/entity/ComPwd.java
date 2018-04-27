package com.gzsolartech.bpmportal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "COM_PWD")
public class ComPwd {

	private String id;
	private String sccmName;
	private String passWord;
	public ComPwd(String id, String sccmName, String passWord) {
		super();
		this.id = id;
		this.sccmName = sccmName;
		this.passWord = passWord;
	}
	public ComPwd() {
		super();
	}
	@Id
    @Column(name = "ID",length = 100)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Column(name = "SCCMNAME",length = 100)
	public String getSccmName() {
		return sccmName;
	}
	public void setSccmName(String sccmName) {
		this.sccmName = sccmName;
	}
	@Column(name = "PASSWORD",length = 100)
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	
}
