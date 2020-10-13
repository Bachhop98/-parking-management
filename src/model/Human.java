package model;

import java.util.Date;

public abstract class Human {
	private long id;
	private String name;
	private String addr;
	private String phoneNum;
	private boolean gender;
	private User creator;
	private Date creatingDate;
	private User editor;
	private Date editingDate;

	public Human(long id, String name, String addr, String phoneNum, boolean gender) {
		super();
		this.id = id;
		this.name = name;
		this.addr = addr;
		this.phoneNum = phoneNum;
		this.gender = gender;
	}
	
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public boolean isGender() {
		return gender;
	}

	public void setGender(boolean gender) {
		this.gender = gender;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public Date getCreatingDate() {
		return creatingDate;
	}

	public void setCreatingDate(Date creatingDate) {
		this.creatingDate = creatingDate;
	}

	public User getEditor() {
		return editor;
	}

	public void setEditor(User editor) {
		this.editor = editor;
	}

	public Date getEditingDate() {
		return editingDate;
	}

	public void setEditingDate(Date editingDate) {
		this.editingDate = editingDate;
	}

}
