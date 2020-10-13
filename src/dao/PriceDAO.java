package dao;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import javafx.scene.image.Image;
import model.History;

public class PriceDAO {
	public static int getDayPriceByIdAndStatus(long id, int status) {
		Connection conn = DBConnection.createConnection();
		PreparedStatement ptmt = null;
		String sql = "SELECT dayprice FROM price WHERE id = ? AND status = ? AND enddate is null";
		ResultSet res = null;
		try {
			ptmt = conn.prepareStatement(sql);
			ptmt.setLong(1, id);
			ptmt.setLong(2, status);
			res = ptmt.executeQuery();

			while (res.next()) {
				int dayPrice = res.getInt(1);
				res.close();
				ptmt.close();
				conn.close();
				return dayPrice;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 0;
	}
	public static int getMonthPriceByIdAndStatus(long id, int status) {
		Connection conn = DBConnection.createConnection();
		PreparedStatement ptmt = null;
		String sql = "SELECT monthprice FROM price WHERE id = ? AND status = ? AND enddate is null";
		ResultSet res = null;
		try {
			ptmt = conn.prepareStatement(sql);
			ptmt.setLong(1, id);
			ptmt.setLong(2, status);
			res = ptmt.executeQuery();
			
			while (res.next()) {
				int monthPrice = res.getInt(1);
				res.close();
				ptmt.close();
				conn.close();
				return monthPrice;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 0;
	}
}
