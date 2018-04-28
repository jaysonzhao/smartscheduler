package com.gzsolartech.bpmportal.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * HCM码值
 */
@Entity
@Table(name = "HCM_DICTIONARIES_TEMP")
//@IdClass(HcmDictionariesTempPrimaryKey.class)
@SequenceGenerator(name = "HCM_DICTIONARIES_TEMP_SEQ", sequenceName = "HCM_DICTIONARIES_TEMP_SEQ", allocationSize = 1)
@Embeddable
public class HcmDictionariesTemp {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HCM_DICTIONARIES_TEMP_SEQ")
	@Column(name = "TbId")
	private Long tbId;
	private String tbName;
	private String detailCode;
    private String detailName;
    private String parentCode;
	@Column(name = "DictOrder", length = 200)
    private String dictOrder;
	@Column(name = "Remark", length = 200)
    private String remark;
    
    @Column(name = "TbName", length = 200)
	public String getTbName() {
    	return tbName;
	}

	public void setTbName(String tbName) {
		this.tbName = tbName;
	}
	
    @Column(name = "DetailCode", length = 200)
	public String getDetailCode() {
    	return detailCode;
	}

	public void setDetailCode(String detailCode) {
		this.detailCode = detailCode;
	}
    
    @Column(name = "DetailName", length = 200)
	public String getDetailName() {
    	return detailName;
	}

	public void setDetailName(String detailName) {
		this.detailName = detailName;
	}
	
	@Column(name = "ParentCode", length = 200)
	public String getParentCode() {
    	return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
}