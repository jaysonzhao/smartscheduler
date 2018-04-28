package com.gzsolartech.bpmportal.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
@Entity
@Table(name = "COM_DETAILS_SUM")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "COMdetailsSum")
public class COMdetailsSum{
	@Id
	@Column(name = "COMPUTERNAME", length = 100)
	private String computerName;//计算机名
	
	@Column(name = "COMPUTERHOURLY", length = 100)
	private String computerHourly;//活动总时间
	
	@Column(name = "SERIALNUMBER", length = 100)
	private String serialnumber;//出厂编号
	
	@Column(name = "ACTIVITYHR", length = 100)
	private String activityhr;//平均活动时长
	
	@Column(name = "ACTIVITYDAY", length = 100)
	private String activityday;//活动天数
	
	@Column(name = "DAYS", length = 100)
	private String days;//年月

	public String getComputerName() {
		return computerName;
	}

	public void setComputerName(String computerName) {
		this.computerName = computerName;
	}

	public String getComputerHourly() {
		return computerHourly;
	}

	public void setComputerHourly(String computerHourly) {
		this.computerHourly = computerHourly;
	}

	public String getSerialnumber() {
		return serialnumber;
	}

	public void setSerialnumber(String serialnumber) {
		this.serialnumber = serialnumber;
	}

	public String getActivityhr() {
		return activityhr;
	}

	public void setActivityhr(String activityhr) {
		this.activityhr = activityhr;
	}

	public String getActivityday() {
		return activityday;
	}

	public void setActivityday(String activityday) {
		this.activityday = activityday;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	@Override
	public String toString() {
		return "COMdetailsSum [computerName=" + computerName + ", computerHourly=" + computerHourly + ", serialnumber="
				+ serialnumber + ", activityhr=" + activityhr + ", activityday=" + activityday + ", days=" + days + "]";
	}
}
