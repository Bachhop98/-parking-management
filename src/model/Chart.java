package model;

import java.util.Date;

public class Chart {
	Date date;
	int money;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getMoney() {
		return money;
	}
	public void setMoney(int money) {
		this.money = money;
	}
	public Chart(Date date, int money) {
		this.date = date;
		this.money = money;
	}
	
}
