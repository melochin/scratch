package scratch.model.entity;

import java.io.Serializable;

public class Dict implements Serializable {
	
	private static final long serialVersionUID = -2929207270259270671L;

	private String code;
	
	private String parentCode;
	
	private String value;
	
	private int sequence;
	
	private String used;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getUsed() {
		return used;
	}

	public void setUsed(String used) {
		this.used = used;
	}

	@Override
	public String toString() {
		return "Dict [code=" + code + ", parentCode=" + parentCode + ", value=" + value + ", sequence=" + sequence
				+ ", used=" + used + "]";
	}

}