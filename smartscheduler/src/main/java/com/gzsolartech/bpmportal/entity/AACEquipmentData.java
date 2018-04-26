package com.gzsolartech.bpmportal.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
/**
 * 台账信息
 * @author Tsy
   2018年3月14日
      下午7:57:04
 */
@Entity
@Table(name = "AAC_EQUIPMENT_DATA")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "AACEquipmentData")
public class AACEquipmentData {
	@Id
	@Column(name = "FACTORY_NUM", unique = true, nullable = false,length = 100)
	private String factoryNum;//出厂编号
	
	@Column(name = "ASSET_CATEGORY", length = 100)
	private String assetCategory;//资产总类别
	
	@Column(name = "ASSET_TYPE", length = 100)
	private String assetType;//资产类型
	
	@Column(name = "ASSET_BY_CATEGORY", length = 100)
	private String assetByCategory;//资产分类别
	
	@Column(name = "EQUIPMENT_NUM", length = 100)
	private String equipmentNum;//设备编号
	
	@Column(name = "BROADHEADING", length = 100)
	private String broadHeading;//专业大类
	
	@Column(name = "SUBCLASS", length = 100)
	private String subClass;//专业小类
	
	@Column(name = "EQUIPMENT_CH_NAME", length = 100)
	private String equipmentChName;//设备名称(中文)
	
	@Column(name = "EQUIPMENT_EN_NAME", length = 100)
	private String equipmentEnName;//设备名称(英文)
	
	@Column(name = "MANUFACTURER", length = 100)
	private String manufacturer;//生产厂家
	
	@Column(name = "ASSET_TYPES", length = 100)
	private String assetTypes;//资产型号
	
	@Column(name = "ASSET_PARAMETER", length = 100)
	private String assetParameter;//资产规格参数
	
	@Column(name = "MAC", length = 100)
	private String mac;//MAC
	
	@Column(name = "SUPPLIER", length = 100)
	private String supplier;//供应商信息
	
	@Column(name = "PRODUCTION_DATE", length = 100)
	private String productionDate;//出厂日期
	
	@Column(name = "ARRIVAL_DATE", length = 100)
	private String arrivalDate;//到货日期
	
	@Column(name = "OFFICIAL_DATE", length = 100)
	private String officialDate;//正式使用日期
	
	@Column(name = "REVERT_DATE", length = 100)
	private String revertDate;//归还日期
	
	@Column(name = "USERNAME", length = 100)
	private String userName;//使用人姓名
	
	@Column(name = "JOBNUMBER", length = 100)
	private String jobNumber;//使用人工号
	
	@Column(name = "DEPARTMENT", length = 100)
	private String department;//使用人部门
	
	@Column(name = "MANAGER", length = 100)
	private String manager;//使用人经理
	
	@Column(name = "MAJORDOMO", length = 100)
	private String majordomo;//使用人总监
	
	@Column(name = "COMPANY", length = 100)
	private String company;//使用人公司
	
	@Column(name = "CITY", length = 100)
	private String city;//使用城市
	
	@Column(name = "FACTORY", length = 100)
	private String factory;//厂区
	
	@Column(name = "TOWER_NAME", length = 100)
	private String towerName;//楼名
	
	@Column(name = "FLOOR", length = 100)
	private String floor;//楼层
	
	@Column(name = "ADMINISTRATOR", length = 100)
	private String administrator;//资产管理员
	
	@Column(name = "STATE", length = 100)
	private String state;//资产状态
	
	@Column(name = "USERS", length = 100)
	private String users;//关键用户
	
	@Column(name = "CONTRACT_NUM", length = 100)
	private String contractNum;//合同编号
	
	@Column(name = "ACCOUNTING_COMPANY", length = 100)
	private String accountingCompany;//入账公司
	
	@Column(name = "INDATE", length = 100)
	private String indate;//质保有效期
	
	@Column(name = "EXPIRATION_DATE", length = 100)
	private String expirationDate;//质保截止日期
	
	@Column(name = "YEAR", length = 100)
	private String year;//使用年限
	
	@Column(name = "WAY", length = 100)
	private String way;//引进方式
	
	@Column(name = "DEDICATED", length = 100)
	private String dedicated;//通用/专用
	
	@Column(name = "STAGE_EQUIPMENT", length = 100)
	private String stageEquipment;//海关监管期设备
	
	@Column(name = "SUPERVISING", length = 100)
	private String supervising;//监管年限
	
	@Column(name = "SUBSETNUM", length = 100)
	private String subsetNum;//子设备编号
	
	@Column(name = "SUBSETNAME", length = 100)
	private String subsetName;//子设备名称
	
	@Column(name = "CUSTOMS_NAME", length = 100)
	private String customsName;//报关名称
	
	@Column(name = "CUSTOMS_NUM", length = 100)
	private String customsNum;//报关单号
	
	@Column(name = "CUSTOMS_NUMLINK", length = 100)
	private String customsNumlink;//报关单号链接
	
	@Column(name = "OA_NUM", length = 100)
	private String oaNum;//OA申请单号
	
	@Column(name = "OA_NUMLINK", length = 100)
	private String oaNumlink;//OA单号链接
	
	@Column(name = "ADVICE_NUM", length = 100)
	private String adviceNum;//到货验收单号
	
	@Column(name = "ADVICE_NUMLINK", length = 100)
	private String adviceNumlink;//到货单号链接
	
	@Column(name = "SCRAP_NUM", length = 100)
	private String scrapNum;//报废单号
	
	@Column(name = "SCRAP_NUMLINK", length = 100)
	private String scrapNumlink;//报废单号链接
	
	@Column(name = "RANK", length = 100)
	private String rank;//资产安全定级
	
	@Column(name = "WHETHER_SCCM", length = 100)
	private String whetherSCCM;//是否安装SCCM
	
	@Column(name = "WHETHER_DLP", length = 100)
	private String whetherDLP;//是否安装DLP
	
	@Column(name = "WHETHER_SEP", length = 100)
	private String whetherSEP;//是否安装SEP
	
	@Column(name = "FREQUENCY", length = 100)
	private String frequency;//使用频率
	
	@Column(name = "PURPOSE", length = 100)
	private String purpose;//用途
	
	@Column(name = "ACTIVITYHR", length = 100)
	private String activityhr;//平均活动时长
	
	@Column(name = "ACTIVITYDAY", length = 100)
	private String activityday;//活动天数
	
	public String getFactoryNum() {
		return factoryNum;
	}

	public void setFactoryNum(String factoryNum) {
		this.factoryNum = factoryNum;
	}

	public String getAssetCategory() {
		return assetCategory;
	}

	public void setAssetCategory(String assetCategory) {
		this.assetCategory = assetCategory;
	}

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getAssetByCategory() {
		return assetByCategory;
	}

	public void setAssetByCategory(String assetByCategory) {
		this.assetByCategory = assetByCategory;
	}

	public String getEquipmentNum() {
		return equipmentNum;
	}

	public void setEquipmentNum(String equipmentNum) {
		this.equipmentNum = equipmentNum;
	}

	public String getBroadHeading() {
		return broadHeading;
	}

	public void setBroadHeading(String broadHeading) {
		this.broadHeading = broadHeading;
	}

	public String getSubClass() {
		return subClass;
	}

	public void setSubClass(String subClass) {
		this.subClass = subClass;
	}

	public String getEquipmentChName() {
		return equipmentChName;
	}

	public void setEquipmentChName(String equipmentChName) {
		this.equipmentChName = equipmentChName;
	}

	public String getEquipmentEnName() {
		return equipmentEnName;
	}

	public void setEquipmentEnName(String equipmentEnName) {
		this.equipmentEnName = equipmentEnName;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getAssetTypes() {
		return assetTypes;
	}

	public void setAssetTypes(String assetTypes) {
		this.assetTypes = assetTypes;
	}

	public String getAssetParameter() {
		return assetParameter;
	}

	public void setAssetParameter(String assetParameter) {
		this.assetParameter = assetParameter;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getProductionDate() {
		return productionDate;
	}

	public void setProductionDate(String productionDate) {
		this.productionDate = productionDate;
	}

	public String getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(String arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public String getOfficialDate() {
		return officialDate;
	}

	public void setOfficialDate(String officialDate) {
		this.officialDate = officialDate;
	}

	public String getRevertDate() {
		return revertDate;
	}

	public void setRevertDate(String revertDate) {
		this.revertDate = revertDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getJobNumber() {
		return jobNumber;
	}

	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getMajordomo() {
		return majordomo;
	}

	public void setMajordomo(String majordomo) {
		this.majordomo = majordomo;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getFactory() {
		return factory;
	}

	public void setFactory(String factory) {
		this.factory = factory;
	}

	public String getTowerName() {
		return towerName;
	}

	public void setTowerName(String towerName) {
		this.towerName = towerName;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getAdministrator() {
		return administrator;
	}

	public void setAdministrator(String administrator) {
		this.administrator = administrator;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

	public String getContractNum() {
		return contractNum;
	}

	public void setContractNum(String contractNum) {
		this.contractNum = contractNum;
	}

	public String getAccountingCompany() {
		return accountingCompany;
	}

	public void setAccountingCompany(String accountingCompany) {
		this.accountingCompany = accountingCompany;
	}

	public String getIndate() {
		return indate;
	}

	public void setIndate(String indate) {
		this.indate = indate;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getWay() {
		return way;
	}

	public void setWay(String way) {
		this.way = way;
	}

	public String getDedicated() {
		return dedicated;
	}

	public void setDedicated(String dedicated) {
		this.dedicated = dedicated;
	}

	public String getStageEquipment() {
		return stageEquipment;
	}

	public void setStageEquipment(String stageEquipment) {
		this.stageEquipment = stageEquipment;
	}

	public String getSupervising() {
		return supervising;
	}

	public void setSupervising(String supervising) {
		this.supervising = supervising;
	}

	public String getSubsetNum() {
		return subsetNum;
	}

	public void setSubsetNum(String subsetNum) {
		this.subsetNum = subsetNum;
	}

	public String getSubsetName() {
		return subsetName;
	}

	public void setSubsetName(String subsetName) {
		this.subsetName = subsetName;
	}

	public String getCustomsName() {
		return customsName;
	}

	public void setCustomsName(String customsName) {
		this.customsName = customsName;
	}

	public String getCustomsNum() {
		return customsNum;
	}

	public void setCustomsNum(String customsNum) {
		this.customsNum = customsNum;
	}

	public String getCustomsNumlink() {
		return customsNumlink;
	}

	public void setCustomsNumlink(String customsNumlink) {
		this.customsNumlink = customsNumlink;
	}

	public String getOaNum() {
		return oaNum;
	}

	public void setOaNum(String oaNum) {
		this.oaNum = oaNum;
	}

	public String getOaNumlink() {
		return oaNumlink;
	}

	public void setOaNumlink(String oaNumlink) {
		this.oaNumlink = oaNumlink;
	}

	public String getAdviceNum() {
		return adviceNum;
	}

	public void setAdviceNum(String adviceNum) {
		this.adviceNum = adviceNum;
	}

	public String getAdviceNumlink() {
		return adviceNumlink;
	}

	public void setAdviceNumlink(String adviceNumlink) {
		this.adviceNumlink = adviceNumlink;
	}

	public String getScrapNum() {
		return scrapNum;
	}

	public void setScrapNum(String scrapNum) {
		this.scrapNum = scrapNum;
	}

	public String getScrapNumlink() {
		return scrapNumlink;
	}

	public void setScrapNumlink(String scrapNumlink) {
		this.scrapNumlink = scrapNumlink;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getWhetherSCCM() {
		return whetherSCCM;
	}

	public void setWhetherSCCM(String whetherSCCM) {
		this.whetherSCCM = whetherSCCM;
	}

	public String getWhetherDLP() {
		return whetherDLP;
	}

	public void setWhetherDLP(String whetherDLP) {
		this.whetherDLP = whetherDLP;
	}

	public String getWhetherSEP() {
		return whetherSEP;
	}

	public void setWhetherSEP(String whetherSEP) {
		this.whetherSEP = whetherSEP;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
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

	@Override
	public String toString() {
		return "AACEquipmentData [factoryNum=" + factoryNum + ", assetCategory=" + assetCategory + ", assetType="
				+ assetType + ", assetByCategory=" + assetByCategory + ", equipmentNum=" + equipmentNum
				+ ", broadHeading=" + broadHeading + ", subClass=" + subClass + ", equipmentChName=" + equipmentChName
				+ ", equipmentEnName=" + equipmentEnName + ", manufacturer=" + manufacturer + ", assetTypes="
				+ assetTypes + ", assetParameter=" + assetParameter + ", mac=" + mac + ", supplier=" + supplier
				+ ", productionDate=" + productionDate + ", arrivalDate=" + arrivalDate + ", officialDate="
				+ officialDate + ", revertDate=" + revertDate + ", userName=" + userName + ", jobNumber=" + jobNumber
				+ ", department=" + department + ", manager=" + manager + ", majordomo=" + majordomo + ", company="
				+ company + ", city=" + city + ", factory=" + factory + ", towerName=" + towerName + ", floor=" + floor
				+ ", administrator=" + administrator + ", state=" + state + ", users=" + users + ", contractNum="
				+ contractNum + ", accountingCompany=" + accountingCompany + ", indate=" + indate + ", expirationDate="
				+ expirationDate + ", year=" + year + ", way=" + way + ", dedicated=" + dedicated + ", stageEquipment="
				+ stageEquipment + ", supervising=" + supervising + ", subsetNum=" + subsetNum + ", subsetName="
				+ subsetName + ", customsName=" + customsName + ", customsNum=" + customsNum + ", customsNumlink="
				+ customsNumlink + ", oaNum=" + oaNum + ", oaNumlink=" + oaNumlink + ", adviceNum=" + adviceNum
				+ ", adviceNumlink=" + adviceNumlink + ", scrapNum=" + scrapNum + ", scrapNumlink=" + scrapNumlink
				+ ", rank=" + rank + ", whetherSCCM=" + whetherSCCM + ", whetherDLP=" + whetherDLP + ", whetherSEP="
				+ whetherSEP + ", frequency=" + frequency + ", purpose=" + purpose + ", activityhr=" + activityhr
				+ ", activityday=" + activityday + "]";
	}

	
	

	

	
	
	
	
}
