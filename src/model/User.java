package model;

import java.util.Date;

public class User extends Human{
	public User(long id, String name, String addr, String phoneNum, boolean gender) {
		super(id, name, addr, phoneNum, gender);
		// TODO Auto-generated constructor stub
	}
	private String role;
	private long managerId;
	
	
	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public long getManagerId() {
		return managerId;
	}
	public void setManagerId(long managerId) {
		this.managerId = managerId;
	}
	
	
}
