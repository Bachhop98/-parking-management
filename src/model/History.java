package model;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

import constant.SystemConstant;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class History {

	private long id, cardId, customerId, userId, parkingLotId;

	private int price;

	private String plateNo, note;

	private Date inDate, outDate;

	private Image plateInImage, plateOutImage, faceInImage, faceOutImage;

	public History(Image plateOutImage, Image faceOutImage, Date outDate) {
		super();
		this.outDate = outDate;
		this.plateOutImage = plateOutImage;
		this.faceOutImage = faceOutImage;
	}

	public History(String plateNo, long customerId, Date inDate, Date outDate) {
		super();
		this.customerId = customerId;
		this.plateNo = plateNo;
		this.inDate = inDate;
		this.outDate = outDate;
	}

	public History(int cardId, int price, Date outDate, Image plateOutImage, Image faceOutImage) {
		super();
		this.price = price;
		this.outDate = outDate;
		this.plateOutImage = plateOutImage;
		this.faceOutImage = faceOutImage;
		this.cardId = cardId;
	}

	public History(long cardId, long customerId, String plateNo, Date inDate, Image plateInImage, Image faceInImage) {
		super();
		this.cardId = cardId;
		this.customerId = customerId;
		this.plateNo = plateNo;
		this.inDate = inDate;
		this.plateInImage = plateInImage;
		this.faceInImage = faceInImage;
	}

	public History(long id, long cardId, long customerId, long userId, long parkingLotId, int price, String plateNo,
			String note, Date inDate, Date outDate, Image plateInImage, Image plateOutImage, Image faceInImage,
			Image faceOutImage) {
		this.id = id;
		this.cardId = cardId;
		this.customerId = customerId;
		this.userId = userId;
		this.parkingLotId = parkingLotId;
		this.price = price;
		this.plateNo = plateNo;
		this.note = note;
		this.inDate = inDate;
		this.outDate = outDate;
		this.plateInImage = plateInImage;
		this.plateOutImage = plateOutImage;
		this.faceInImage = faceInImage;
		this.faceOutImage = faceOutImage;
	}

	public History(long cardId, long customerId, Image plateInImage, Image faceInImage, String plateNo, Date inDate,
			int price) {
		this.cardId = cardId;
		this.customerId = customerId;
		this.plateNo = plateNo;
		this.price = price;
		this.inDate = inDate;
		this.plateInImage = plateInImage;
		this.faceInImage = faceInImage;
	}

	public History(long cardId, Image plateInImage, Image faceInImage, String plateNo, Date inDate, int price) {
		this.cardId = cardId;
		this.plateNo = plateNo;
		this.price = price;
		this.inDate = inDate;
		this.plateInImage = plateInImage;
		this.faceInImage = faceInImage;
	}

	// Tuyen
	public History(Long id2, String plateNo2, long customerId2, Timestamp inDate2, Timestamp outDate2, int price) {
		this.id = id2;
		this.plateNo = plateNo2;
		this.customerId = customerId2;
		this.inDate = inDate2;
		this.outDate = outDate2;
		this.price = price;
	}
	// tuyen

	public History() {
		// TODO Auto-generated constructor stub
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCardId() {
		return cardId;
	}

	public void setCardId(long cardId) {
		this.cardId = cardId;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getParkingLotId() {
		return parkingLotId;
	}

	public void setParkingLotId(long parkingLotId) {
		this.parkingLotId = parkingLotId;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getPlateNo() {
		return plateNo;
	}

	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getInDate() {
		return inDate;
	}

	public void setInDate(Date inDate) {
		this.inDate = inDate;
	}

	public Date getOutDate() {
		return outDate;
	}

	public void setOutDate(Date outDate) {
		this.outDate = outDate;
	}

	public Image getPlateInImage() {
		return plateInImage;
	}

	public void setPlateInImage(Image plateInImage) {
		this.plateInImage = plateInImage;
	}

	public Image getPlateOutImage() {
		return plateOutImage;
	}

	public void setPlateOutImage(Image plateOutImage) {
		this.plateOutImage = plateOutImage;
	}

	public Image getFaceInImage() {
		return faceInImage;
	}

	public void setFaceInImage(Image faceInImage) {
		this.faceInImage = faceInImage;
	}

	public Image getFaceOutImage() {
		return faceOutImage;
	}

	public void setFaceOutImage(Image faceOutImage) {
		this.faceOutImage = faceOutImage;
	}

	@Override
	public String toString() {
		return "History [id=" + id + ", cardId=" + cardId + ", customerId=" + customerId + ", userId=" + userId
				+ ", parkingLotId=" + parkingLotId + ", price=" + price + ", plateNo=" + plateNo + ", note=" + note
				+ ", inDate=" + inDate + ", outDate=" + outDate + ", plateInImage=" + plateInImage + ", plateOutImage="
				+ plateOutImage + ", faceInImage=" + faceInImage + ", faceOutImage=" + faceOutImage + "]";
	}

	public StringProperty getPlateNoProperty() {
		return new SimpleStringProperty("" + plateNo);
	}

	public StringProperty getCustomerIdProperty() {
		if (customerId != 0)
			return new SimpleStringProperty("" + customerId);
		else
			return new SimpleStringProperty("");

	}

	public StringProperty getInDateProperty() {
		return new SimpleStringProperty(SystemConstant.dateFormat.format(inDate));
	}

	public StringProperty getOutDateProperty() {
		if (outDate == null) {
			return new SimpleStringProperty("");
		} else {
			return new SimpleStringProperty(SystemConstant.dateFormat.format(outDate));
		}

	}

	public String getCustomerId_String() {
		return "" + customerId;
	}

	public StringProperty getIdProperty() {
		return new SimpleStringProperty(this.getId() + "");
	}
	
	public StringProperty getPriceProperty() {
		return new SimpleStringProperty(this.price + " VNĐ");
	}

}