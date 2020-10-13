package model;

import java.util.ArrayList;
import java.util.Date;

public class Customer extends Human {

	public Customer(long id, String name, String addr, String phoneNum, boolean gender) {
		super(id, name, addr, phoneNum, gender);
	}

	private ArrayList<RentDetail> rentDetails;

	public ArrayList<RentDetail> getRentDetails() {
		return rentDetails;
	}

	public void setRentDetails(ArrayList<RentDetail> rentDetails) {
		this.rentDetails = rentDetails;
	}
	
	@Override
	public String toString() {
		return this.getId() + "\t" + this.getName() + "\t" + this.getAddr() + "\t" + this.getPhoneNum() + "\t" + this.isGender();
	}
}
