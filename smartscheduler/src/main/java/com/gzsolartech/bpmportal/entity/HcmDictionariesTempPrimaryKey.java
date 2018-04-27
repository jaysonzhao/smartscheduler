package com.gzsolartech.bpmportal.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

public class HcmDictionariesTempPrimaryKey implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6326723011252788635L;
	private String tbName;
	private String dictCode;
	
	@Column(name = "TbName")
	public String getTbName() {
    	return tbName;
	}

	public void setTbName(String tbName) {
		this.tbName = tbName;
	}
	@Id
    @Column(name = "DictCode")
	public String getDictCode() {
    	return dictCode;
	}

	public void setDictCode(String dictCode) {
		this.dictCode = dictCode;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj instanceof HcmDictionariesTempPrimaryKey) {
        	HcmDictionariesTempPrimaryKey key = (HcmDictionariesTempPrimaryKey)obj;
            if (this.tbName.equals(key.getTbName()) && this.dictCode.equals(key.getDictCode())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;  
          result = 7 * result + (getDictCode() == null ? 0 : this.getDictCode().hashCode());  
          result = 7 * result + (getTbName() == null ? 0 : this.getTbName().hashCode());  
        return result;
    }
}
