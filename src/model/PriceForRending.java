package model;

import java.util.Date;

public class PriceForRending {
private Date startDate, endDate;
private int priceDay, priceMonth;
private User creator, editor;
private Date creatingDate, editingDate;
private Vehicle vehicle;

public Date getStartDate() {
	return startDate;
}
public void setStartDate(Date startDate) {
	this.startDate = startDate;
}
public Date getEndDate() {
	return endDate;
}
public void setEndDate(Date endDate) {
	this.endDate = endDate;
}
public double getPriceDay() {
	return priceDay;
}

public double getPriceMonth() {
	return priceMonth;
}

public void setPriceDay(int priceDay) {
	this.priceDay = priceDay;
}
public void setPriceMonth(int priceMonth) {
	this.priceMonth = priceMonth;
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
public Vehicle getVehicle() {
	return vehicle;
}
public void setVehicle(Vehicle vehicle) {
	this.vehicle = vehicle;
}

}
