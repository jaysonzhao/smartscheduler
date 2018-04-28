package com.gzsolartech.bpmportal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "COM_DETAILS")
public class COMdetails {
	private String id;
	private String ComputerName;
	private String TimeDate;
	private String Computerhourly;
	private String SerialNumber;
	
	public COMdetails(String id, String computerName, String timeDate, String computerhourly, String serialNumber) {
		super();
		this.id = id;
		ComputerName = computerName;
		TimeDate = timeDate;
		Computerhourly = computerhourly;
		SerialNumber = serialNumber;
	}

	public COMdetails() {
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
    @Column(name = "COMPUTERNAME",length = 100)
	public void setComputerName(String computerName) {
		ComputerName = computerName;
	}

	public void setTimeDate(String timeDate) {
		TimeDate = timeDate;
	}
    @Column(name = "TIMEDATE",length = 100)
	public String getTimeDate() {
		return TimeDate;
	}
	public String getComputerName() {
		return ComputerName;
	}

    @Column(name = "COMPUTERHOURLY",length = 100)
	public String getComputerhourly() {
		return Computerhourly;
	}
	public void setComputerhourly(String computerhourly) {
		Computerhourly = computerhourly;
	}
    @Column(name = "SERIALNUMBER",length = 100)
	public String getSerialNumber() {
		return SerialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}
}
