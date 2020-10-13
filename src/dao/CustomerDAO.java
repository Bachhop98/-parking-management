package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Customer;

public class CustomerDAO {
	public static Customer getOneByIdAndStatus(String idCustomer, int status) {
		Connection conn = DBConnection.createConnection();
		PreparedStatement ptmt = null;
		String sql = "select * from customer where id = ? and status  = ?";
		ResultSet res = null;
		try {
			ptmt = conn.prepareStatement(sql);
			ptmt.setString(1, idCustomer);
			ptmt.setInt(2, status);
			res = ptmt.executeQuery();
			
			while (res.next()) {
				long id = res.getLong(1);
				String name = res.getString(2);
				String addr = res.getString(3);
				int gender = res.getInt(4);
				String phoneNum = res.getString(5);
				res.close();
				ptmt.close();
				conn.close();
				return new Customer(id, name, addr, phoneNum, gender == 1 ? true : false);
			}
			

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
