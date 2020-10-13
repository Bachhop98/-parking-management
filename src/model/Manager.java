package model;

import java.util.Date;

public class Manager extends User {
	public Manager(long id, String name, String addr, String phoneNum, boolean gender) {
		super(id, name, addr, phoneNum, gender);
		// TODO Auto-generated constructor stub
	}

	private long parkingLot;

	public long getParkingLot() {
		return parkingLot;
	}

	public void setParkingLot(long parkingLot) {
		this.parkingLot = parkingLot;
	}
}
