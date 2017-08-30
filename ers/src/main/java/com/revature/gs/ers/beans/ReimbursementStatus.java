package com.revature.gs.ers.beans;

public class ReimbursementStatus {
	private int id;
	private String status;
	
	public ReimbursementStatus() {
		super();
	}
	public ReimbursementStatus(int reimbursementStatusId, String reimbursementStatus) {
		super();
		this.id = reimbursementStatusId;
		this.status = reimbursementStatus;
	}
	public int getId() {
		return id;
	}
	public void setId(int reimbursementStatusId) {
		this.id = reimbursementStatusId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String reimbursementStatus) {
		this.status = reimbursementStatus;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		ReimbursementStatus other = (ReimbursementStatus) obj;
		if (id != other.id)
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}
	
	
	
}
