package com.gzsolartech.bpmportal.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
@Entity
@Table(name = "AAC_CHANGES_BPM")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "AacChangerBpm")
public class AacChangerBpm{
	@Id
	@Column(name = "PERNR", length = 200)
	private String pernr;//员工编号
	
	@Column(name = "ENAME", length = 200)
	private String ename;//员工姓名
	
	@Column(name = "STATE", length = 200)
	private String state;//异动状态
	
	@Column(name = "MASSN", length = 200)
	private String massn;//操作类型代码
	
	@Column(name = "MASSN_T", length = 200)
	private String massnT;//操作类型文本
	
	@Column(name = "MASSN_BPM", length = 200)
	private String massnBpm;//传输至BPM的操作类型
	
	@Column(name = "MASSG", length = 200)
	private String massg;//操作原因代码
	
	@Column(name = "MASSG_T", length = 200)
	private String massgT;//操作原因文本
	
	@Column(name = "WERKS_FR", length = 200)
	private String werksFr;//异动前人事范围代码
	
	@Column(name = "WERKS_FR_T", length = 200)
	private String werksFrT;//异动前人事范围文本
	
	@Column(name = "BTRTL_FR", length = 200)
	private String btrtlFr;//异动前人事子范围代码
	
	@Column(name = "BTRTL_FR_T", length = 200)
	private String btrtlFrT;//异动前人事子范围文本
	
	@Column(name = "ORGEH_LV1_FR", length = 200)
	private String orgehLv1Fr;//异动前一级部门编号
	
	@Column(name = "ORGEH_LV1_FR_T", length = 200)
	private String orgehLv1FrT;//异动前一级部门名称
	
	@Column(name = "ORGEH_LV2_FR", length = 200)
	private String orgehLv2Fr;//异动前二级部门编号
	
	@Column(name = "ORGEH_LV2_FR_T", length = 200)
	private String orgehLv2FrT;//异动前二级部门名称
	
	@Column(name = "ORGEH_LV3_FR", length = 200)
	private String orgehLv3Fr;//异动前三级部门编号
	
	@Column(name = "ORGEH_LV3_FR_T", length = 200)
	private String orgehLv3FrT;//异动前三级部门名称
	
	@Column(name = "ORGEH_LV4_FR", length = 200)
	private String orgehLv4Fr;//异动前四级部门编号
	
	@Column(name = "ORGEH_LV4_FR_T", length = 200)
	private String orgehLv4FrT;//异动前四级部门名称
	
	@Column(name = "ORGEH_LV5_FR", length = 200)
	private String orgehLv5Fr;//异动前五级部门编号
	
	@Column(name = "ORGEH_LV5_FR_T", length = 200)
	private String orgehLv5FrT;//异动前五级部门名称
	
	@Column(name = "ORGEH_LV6_FR", length = 200)
	private String orgehLv6Fr;//异动前六级部门编号
	
	@Column(name = "ORGEH_LV6_FR_T", length = 200)
	private String orgehLv6FrT;//异动前六级部门名称
	
	@Column(name = "PLANS_FR", length = 200)
	private String plansFr;//异动前职位代码
	
	@Column(name = "PLANS_FR_T", length = 200)
	private String plansFrT;//异动前职位文本
	
	@Column(name = "ZHRFZCMC_FR", length = 200)
	private String zhrfzcmcFr;//异动前职称代码
	
	@Column(name = "ZHRFZCMC_FR_T", length = 200)
	private String zhrfzcmcFrT;//异动前职称
	
	@Column(name = "LEADERENAME_FR", length = 200)
	private String leaderenameFr;//异动前领导
	
	@Column(name = "BEGDA_TO", length = 200)
	private String begdaTo;//异动日期
	
	@Column(name = "WERKS_TO", length = 200)
	private String werksTo;//异动后人事范围代码
	
	@Column(name = "WERKS_TO_T", length = 200)
	private String werksToT;//异动后人事范围文本
	
	@Column(name = "BTRTL_TO", length = 200)
	private String btrtlTo;//异动后人事子范围代码
	
	@Column(name = "BTRTL_TO_T", length = 200)
	private String btrtlToT;//异动后人事子范围文本
	
	@Column(name = "ORGEH_LV1_TO", length = 200)
	private String orgehLv1To;//异动后一级部门编号
	
	@Column(name = "ORGEH_LV1_TO_T", length = 200)
	private String orgehLv1ToT;//异动后一级部门名称
	
	@Column(name = "ORGEH_LV2_TO", length = 200)
	private String orgehLv2To;//异动后二级部门编号
	
	@Column(name = "ORGEH_LV2_TO_T", length = 200)
	private String orgehLv2ToT;//异动后二级部门名称
	
	@Column(name = "ORGEH_LV3_TO", length = 200)
	private String orgehLv3To;//异动后三级部门编号
	
	@Column(name = "ORGEH_LV3_TO_T", length = 200)
	private String orgehLv3ToT;//异动后三级部门名称
	
	@Column(name = "ORGEH_LV4_TO", length = 200)
	private String orgehLv4To;//异动后四级部门编号
	
	@Column(name = "ORGEH_LV4_TO_T", length = 200)
	private String orgehLv4ToT;//异动后四级部门名称
	
	@Column(name = "ORGEH_LV5_TO", length = 200)
	private String orgehLv5To;//异动后五级部门编号
	
	@Column(name = "ORGEH_LV5_TO_T", length = 200)
	private String orgehLv5ToT;//异动后五级部门名称
	
	@Column(name = "ORGEH_LV6_TO", length = 200)
	private String orgehLv6To;//异动后六级部门编号
	
	@Column(name = "ORGEH_LV6_TO_T", length = 200)
	private String orgehLv6ToT;//异动后六级部门名称
	
	@Column(name = "PLANS_TO", length = 200)
	private String plansTo;//异动后职位代码
	
	@Column(name = "PLANS_TO_T", length = 200)
	private String plansToT;//异动后职位文本
	
	@Column(name = "ZHRFZCMC_TO", length = 200)
	private String zhrfzcmcTo;//异动后职称代码
	
	@Column(name = "ZHRFZCMC_TO_T", length = 200)
	private String zhrfzcmcToT;//异动后职称文本
	
	@Column(name = "LEADERENAME_TO", length = 200)
	private String leaderenameTo;//异动后直属领导

	public String getPernr() {
		return pernr;
	}

	public void setPernr(String pernr) {
		this.pernr = pernr;
	}

	public String getEname() {
		return ename;
	}

	public void setEname(String ename) {
		this.ename = ename;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMassn() {
		return massn;
	}

	public void setMassn(String massn) {
		this.massn = massn;
	}

	public String getMassnT() {
		return massnT;
	}

	public void setMassnT(String massn_t) {
		this.massnT = massnT;
	}

	public String getMassnBpm() {
		return massnBpm;
	}

	public void setMassnBpm(String massnBpm) {
		this.massnBpm = massnBpm;
	}

	public String getMassg() {
		return massg;
	}

	public void setMassg(String massg) {
		this.massg = massg;
	}

	public String getMassgT() {
		return massgT;
	}

	public void setMassgT(String massgT) {
		this.massgT = massgT;
	}

	public String getWerksFr() {
		return werksFr;
	}

	public void setWerksFr(String werksFr) {
		this.werksFr = werksFr;
	}

	public String getWerksFrT() {
		return werksFrT;
	}

	public void setWerksFrT(String werksFrT) {
		this.werksFrT = werksFrT;
	}

	public String getBtrtlFr() {
		return btrtlFr;
	}

	public void setBtrtlFr(String btrtlFr) {
		this.btrtlFr = btrtlFr;
	}

	public String getBtrtlFrT() {
		return btrtlFrT;
	}

	public void setBtrtlFrT(String btrtlFrT) {
		this.btrtlFrT = btrtlFrT;
	}

	public String getOrgehLv1Fr() {
		return orgehLv1Fr;
	}

	public void setOrgehLv1Fr(String orgehLv1Fr) {
		this.orgehLv1Fr = orgehLv1Fr;
	}

	public String getOrgehLv1FrT() {
		return orgehLv1FrT;
	}

	public void setOrgehLv1FrT(String orgehLv1FrT) {
		this.orgehLv1FrT = orgehLv1FrT;
	}

	public String getOrgehLv2Fr() {
		return orgehLv2Fr;
	}

	public void setOrgehLv2Fr(String orgehLv2Fr) {
		this.orgehLv2Fr = orgehLv2Fr;
	}

	public String getOrgehLv2FrT() {
		return orgehLv2FrT;
	}

	public void setOrgehLv2FrT(String orgehLv2FrT) {
		this.orgehLv2FrT = orgehLv2FrT;
	}

	public String getOrgehLv3Fr() {
		return orgehLv3Fr;
	}

	public void setOrgehLv3Fr(String orgehLv3Fr) {
		this.orgehLv3Fr = orgehLv3Fr;
	}

	public String getOrgehLv3FrT() {
		return orgehLv3FrT;
	}

	public void setOrgehLv3FrT(String orgehLv3FrT) {
		this.orgehLv3FrT = orgehLv3FrT;
	}

	public String getOrgehLv4Fr() {
		return orgehLv4Fr;
	}

	public void setOrgehLv4Fr(String orgehLv4Fr) {
		this.orgehLv4Fr = orgehLv4Fr;
	}

	public String getOrgehLv4FrT() {
		return orgehLv4FrT;
	}

	public void setOrgehLv4FrT(String orgehLv4FrT) {
		this.orgehLv4FrT = orgehLv4FrT;
	}

	public String getOrgehLv5Fr() {
		return orgehLv5Fr;
	}

	public void setOrgehLv5Fr(String orgehLv5Fr) {
		this.orgehLv5Fr = orgehLv5Fr;
	}

	public String getOrgehLv5FrT() {
		return orgehLv5FrT;
	}

	public void setOrgehLv5FrT(String orgehLv5FrT) {
		this.orgehLv5FrT = orgehLv5FrT;
	}

	public String getOrgehLv6Fr() {
		return orgehLv6Fr;
	}

	public void setOrgehLv6Fr(String orgehLv6Fr) {
		this.orgehLv6Fr = orgehLv6Fr;
	}

	public String getOrgehLv6FrT() {
		return orgehLv6FrT;
	}

	public void setOrgehLv6FrT(String orgehLv6FrT) {
		this.orgehLv6FrT = orgehLv6FrT;
	}

	public String getPlansFr() {
		return plansFr;
	}

	public void setPlansFr(String plansFr) {
		this.plansFr = plansFr;
	}

	public String getPlansFrT() {
		return plansFrT;
	}

	public void setPlansFrT(String plansFrT) {
		this.plansFrT = plansFrT;
	}

	public String getZhrfzcmcFr() {
		return zhrfzcmcFr;
	}

	public void setZhrfzcmcFr(String zhrfzcmcFr) {
		this.zhrfzcmcFr = zhrfzcmcFr;
	}

	public String getZhrfzcmcFrT() {
		return zhrfzcmcFrT;
	}

	public void setZhrfzcmcFrT(String zhrfzcmcFrT) {
		this.zhrfzcmcFrT = zhrfzcmcFrT;
	}

	public String getLeaderenameFr() {
		return leaderenameFr;
	}

	public void setLeaderenameFr(String leaderenameFr) {
		this.leaderenameFr = leaderenameFr;
	}

	public String getBegdaTo() {
		return begdaTo;
	}

	public void setBegdaTo(String begdaTo) {
		this.begdaTo = begdaTo;
	}

	public String getWerksTo() {
		return werksTo;
	}

	public void setWerksTo(String werksTo) {
		this.werksTo = werksTo;
	}

	public String getWerksToT() {
		return werksToT;
	}

	public void setWerksToT(String werksToT) {
		this.werksToT = werksToT;
	}

	public String getBtrtlTo() {
		return btrtlTo;
	}

	public void setBtrtlTo(String btrtlTo) {
		this.btrtlTo = btrtlTo;
	}

	public String getBtrtlToT() {
		return btrtlToT;
	}

	public void setBtrtlToT(String btrtlToT) {
		this.btrtlToT = btrtlToT;
	}

	public String getOrgehLv1To() {
		return orgehLv1To;
	}

	public void setOrgehLv1To(String orgehLv1To) {
		this.orgehLv1To = orgehLv1To;
	}

	public String getOrgehLv1ToT() {
		return orgehLv1ToT;
	}

	public void setOrgehLv1ToT(String orgehLv1ToT) {
		this.orgehLv1ToT = orgehLv1ToT;
	}

	public String getOrgehLv2To() {
		return orgehLv2To;
	}

	public void setOrgehLv2To(String orgehLv2To) {
		this.orgehLv2To = orgehLv2To;
	}

	public String getOrgehLv2ToT() {
		return orgehLv2ToT;
	}

	public void setOrgehLv2ToT(String orgehLv2ToT) {
		this.orgehLv2ToT = orgehLv2ToT;
	}

	public String getOrgehLv3To() {
		return orgehLv3To;
	}

	public void setOrgehLv3To(String orgehLv3To) {
		this.orgehLv3To = orgehLv3To;
	}

	public String getOrgehLv3ToT() {
		return orgehLv3ToT;
	}

	public void setOrgehLv3ToT(String orgehLv3ToT) {
		this.orgehLv3ToT = orgehLv3ToT;
	}

	public String getOrgehLv4To() {
		return orgehLv4To;
	}

	public void setOrgehLv4To(String orgehLv4To) {
		this.orgehLv4To = orgehLv4To;
	}

	public String getOrgehLv4ToT() {
		return orgehLv4ToT;
	}

	public void setOrgehLv4ToT(String orgehLv4ToT) {
		this.orgehLv4ToT = orgehLv4ToT;
	}

	public String getOrgehLv5To() {
		return orgehLv5To;
	}

	public void setOrgehLv5To(String orgehLv5To) {
		this.orgehLv5To = orgehLv5To;
	}

	public String getOrgehLv5ToT() {
		return orgehLv5ToT;
	}

	public void setOrgehLv5ToT(String orgehLv5ToT) {
		this.orgehLv5ToT = orgehLv5ToT;
	}

	public String getOrgehLv6To() {
		return orgehLv6To;
	}

	public void setOrgehLv6To(String orgehLv6To) {
		this.orgehLv6To = orgehLv6To;
	}

	public String getOrgehLv6ToT() {
		return orgehLv6ToT;
	}

	public void setOrgehLv6ToT(String orgehLv6ToT) {
		this.orgehLv6ToT = orgehLv6ToT;
	}

	public String getPlansTo() {
		return plansTo;
	}

	public void setPlansTo(String plansTo) {
		this.plansTo = plansTo;
	}

	public String getPlansToT() {
		return plansToT;
	}

	public void setPlansToT(String plansToT) {
		this.plansToT = plansToT;
	}

	public String getZhrfzcmcTo() {
		return zhrfzcmcTo;
	}

	public void setZhrfzcmcTo(String zhrfzcmcTo) {
		this.zhrfzcmcTo = zhrfzcmcTo;
	}

	public String getZhrfzcmcToT() {
		return zhrfzcmcToT;
	}

	public void setZhrfzcmcToT(String zhrfzcmcToT) {
		this.zhrfzcmcToT = zhrfzcmcToT;
	}

	public String getLeaderenameTo() {
		return leaderenameTo;
	}

	public void setLeaderenameTo(String leaderenameTo) {
		this.leaderenameTo = leaderenameTo;
	}

	@Override
	public String toString() {
		return "AacChangerBpm [pernr=" + pernr + ", ename=" + ename + ", state=" + state + ", massn=" + massn
				+ ", massnT=" + massnT + ", massnBpm=" + massnBpm + ", massg=" + massg + ", massgT=" + massgT
				+ ", werksFr=" + werksFr + ", werksFrT=" + werksFrT + ", btrtlFr=" + btrtlFr + ", btrtlFrT=" + btrtlFrT
				+ ", orgehLv1Fr=" + orgehLv1Fr + ", orgehLv1FrT=" + orgehLv1FrT + ", orgehLv2Fr=" + orgehLv2Fr
				+ ", orgehLv2FrT=" + orgehLv2FrT + ", orgehLv3Fr=" + orgehLv3Fr + ", orgehLv3FrT=" + orgehLv3FrT
				+ ", orgehLv4Fr=" + orgehLv4Fr + ", orgehLv4FrT=" + orgehLv4FrT + ", orgehLv5Fr=" + orgehLv5Fr
				+ ", orgehLv5FrT=" + orgehLv5FrT + ", orgehLv6Fr=" + orgehLv6Fr + ", orgehLv6FrT=" + orgehLv6FrT
				+ ", plansFr=" + plansFr + ", plansFrT=" + plansFrT + ", zhrfzcmcFr=" + zhrfzcmcFr + ", zhrfzcmcFrT="
				+ zhrfzcmcFrT + ", leaderenameFr=" + leaderenameFr + ", begdaTo=" + begdaTo + ", werksTo=" + werksTo
				+ ", werksToT=" + werksToT + ", btrtlTo=" + btrtlTo + ", btrtlToT=" + btrtlToT + ", orgehLv1To="
				+ orgehLv1To + ", orgehLv1ToT=" + orgehLv1ToT + ", orgehLv2To=" + orgehLv2To + ", orgehLv2ToT="
				+ orgehLv2ToT + ", orgehLv3To=" + orgehLv3To + ", orgehLv3ToT=" + orgehLv3ToT + ", orgehLv4To="
				+ orgehLv4To + ", orgehLv4ToT=" + orgehLv4ToT + ", orgehLv5To=" + orgehLv5To + ", orgehLv5ToT="
				+ orgehLv5ToT + ", orgehLv6To=" + orgehLv6To + ", orgehLv6ToT=" + orgehLv6ToT + ", plansTo=" + plansTo
				+ ", plansToT=" + plansToT + ", zhrfzcmcTo=" + zhrfzcmcTo + ", zhrfzcmcToT=" + zhrfzcmcToT
				+ ", leaderenameTo=" + leaderenameTo + "]";
	}

	
}
