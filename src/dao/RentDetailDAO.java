package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import constant.SystemConstant;
import model.Customer;
import model.RentDetail;

public class RentDetailDAO {
	public static RentDetail getOneByIdAndStatus(long cardId, int status) {
		Connection conn = DBConnection.createConnection();
		PreparedStatement ptmt = null;
		String sql = "select * from rentdetail where cardid = ? and status  = ?";
		ResultSet res = null;
		try {
			ptmt = conn.prepareStatement(sql);
			ptmt.setLong(1, cardId);
			ptmt.setInt(2, status);
			res = ptmt.executeQuery();
			while (res.next()) {
				long id = res.getLong(1);
				String plateNo = res.getString(2);
				Date endDate = res.getDate(5);
				String customerId = res.getString(6);
				long priceId = res.getLong(7);
				
				RentDetail rentDetail = new RentDetail();
				rentDetail.setCardId(id);
				rentDetail.setCustomer(CustomerDAO.getOneByIdAndStatus(customerId, SystemConstant.STATUS_ACTIVE));
				rentDetail.setEndDate(endDate);
				rentDetail.setPlateNo(plateNo);
				rentDetail.setPriceId(priceId);
				res.close();
				ptmt.close();
				conn.close();
				return rentDetail;
			}
			

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
