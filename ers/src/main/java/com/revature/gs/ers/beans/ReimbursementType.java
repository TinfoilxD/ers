package com.revature.gs.ers.beans;

public class ReimbursementType {
	private int id;
	private String type;
	
	public ReimbursementType() {
		super();
	}
	public ReimbursementType(int reimbursementTypeId, String reimbursementType) {
		super();
		this.id = reimbursementTypeId;
		this.type = reimbursementType;
	}
	public int getId() {
		return id;
	}
	public void setId(int reimbursementTypeId) {
		this.id = reimbursementTypeId;
	}
	public String getType() {
		return type;
	}
	public void setType(String reimbursementType) {
		this.type = reimbursementType;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReimbursementType other = (ReimbursementType) obj;
		if (id != other.id)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	
}
