package com.revature.gs.ers.beans;

public class UserRole {
	private int id;
	private String userRole;
	
	
	public UserRole() {
		super();
	}
	public UserRole(int userRoleId, String userRole) {
		super();
		this.id = userRoleId;
		this.userRole = userRole;
	}
	public int getId() {
		return id;
	}
	public void setId(int userRoleId) {
		this.id = userRoleId;
	}
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((userRole == null) ? 0 : userRole.hashCode());
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
		UserRole other = (UserRole) obj;
		if (id != other.id)
			return false;
		if (userRole == null) {
			if (other.userRole != null)
				return false;
		} else if (!userRole.equals(other.userRole))
			return false;
		return true;
	}
	
	
}
