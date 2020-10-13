package model;

import java.util.Date;

public class Vehicle {
	private long id;
	private String name;
	private String status;
	private User creator;
	private Date creatingDate;
	private User editor;
	private Date editingDate;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
