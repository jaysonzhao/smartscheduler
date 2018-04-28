package com.gzsolartech.bpmportal.entity;

public class ComModel {
	
	private String ComputerName;      //电脑名
	private String TimeDate;          //日期
	private String Computerhourly;    //计算机每天活动小时
	private String SerialNumber;      //出厂编号
	public String getComputerName() {
		return ComputerName;
	}
	public void setComputerName(String computerName) {
		ComputerName = computerName;
	}

	public String getTimeDate() {
		return TimeDate;
	}
	public void setTimeDate(String timeDate) {
		TimeDate = timeDate;
	}
	public String getComputerhourly() {
		return Computerhourly;
	}
	public void setComputerhourly(String computerhourly) {
		Computerhourly = computerhourly;
	}
	public String getSerialNumber() {
		return SerialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}
	
	
	public ComModel(String computerName, String timeDate, String computerhourly, String serialNumber) {
		super();
		ComputerName = computerName;
		TimeDate = timeDate;
		Computerhourly = computerhourly;
		SerialNumber = serialNumber;
	}
	public ComModel() {
		super();
	}
	

}
