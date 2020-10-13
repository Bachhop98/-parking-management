package dao;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import model.Customer;
import model.History;

public class HistoryDAO {
	public static ObservableList<History> getAllHistory() {
		ObservableList<History> histories = FXCollections.observableArrayList();
		Connection connection = DBConnection.createConnection();
		Statement statement;
		try {
			statement = connection.createStatement();
			String sql = "Select id, plateno, customerid, indate, outdate, price from history order by indate DESC";
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				Long id = rs.getLong("id");
				String plateNo = rs.getString("plateno");
				long customerId = rs.getLong("customerid");
				Timestamp inDate = rs.getTimestamp("indate");
				Timestamp outDate = rs.getTimestamp("outdate");
				int price = rs.getInt("price");
				History history = new History(id, plateNo, customerId, inDate, outDate, price);
				histories.add(history);
			}
			rs.close();
			statement.close();
			connection.close();
			return histories;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static History getLastImageInByCardIdAndCustomerId(long cardId, long customerId)
			throws NullPointerException {
		Connection conn = DBConnection.createConnection();
		PreparedStatement ptmt = null;
		String sql = "SELECT h.plateoutimage, h.faceoutimage, h.outdate FROM history h INNER JOIN (select cardid,customerid, max(outdate) as MaxOutDate from history where cardid = ? and customerid = ? group by cardid, customerid) groupedh ON h.cardid = groupedh.cardid AND h.customerid = groupedh.customerid AND h.outdate = groupedh.MaxOutDate";
		ResultSet res = null;
		try {
			ptmt = conn.prepareStatement(sql);
			ptmt.setLong(1, cardId);
			ptmt.setLong(2, customerId);
			res = ptmt.executeQuery();

			while (res.next()) {
				Image imgPlateInHistory = new Image(new ByteArrayInputStream(res.getBytes(1)));
				Image imgFaceInHistory = new Image(new ByteArrayInputStream(res.getBytes(2)));
				Timestamp date = res.getTimestamp(3);
				res.close();
				ptmt.close();
				conn.close();
				return new History(imgPlateInHistory, imgFaceInHistory, new java.util.Date(date.getTime()));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public static History getHistoryOut(Long cardId) {
		Connection conn = DBConnection.createConnection();
		PreparedStatement ptmt = null;
		String sql = "SELECT * FROM history WHERE cardid = ? AND outdate is null";
		ResultSet res = null;
		try {
			ptmt = conn.prepareStatement(sql);
			ptmt.setLong(1, cardId);
			res = ptmt.executeQuery();

			while (res.next()) {
				Long card = res.getLong(2);
				Long customerId = res.getLong(3);
				Image imgPlateIn = new Image(new ByteArrayInputStream(res.getBytes(4)));
				Image imgFaceIn = new Image(new ByteArrayInputStream(res.getBytes(6)));
				String plateno = res.getString(8);
				Timestamp inDate = res.getTimestamp(9);
				res.close();
				ptmt.close();
				conn.close();
				return new History(card, customerId, plateno, inDate, imgPlateIn, imgFaceIn);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public static boolean saveMonthHistory(History history) {
		try {
			Connection conn = new DBConnection().createConnection();
			String sql = "INSERT INTO history(cardid, customerid, plateinimage, faceinimage, plateno, indate) VALUES (?,?,?,?,?,?)";
			PreparedStatement ptmt = conn.prepareStatement(sql);
			ptmt.setLong(1, history.getCardId());
			ptmt.setLong(2, history.getCustomerId());

			ptmt.setBytes(3, convertImage2bytes(history.getPlateInImage()));
			ptmt.setBytes(4, convertImage2bytes(history.getFaceInImage()));

			ptmt.setString(5, history.getPlateNo());

			ptmt.setTimestamp(6, convertDatetoTimestamp(history.getInDate()));

			ptmt.executeUpdate();

			ptmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean saveDayHistory(History history) {
		try {
			Connection conn = new DBConnection().createConnection();
			String sql = "INSERT INTO history(cardid, plateinimage, faceinimage, plateno, indate) VALUES (?,?,?,?,?)";
			PreparedStatement ptmt = conn.prepareStatement(sql);
			ptmt.setLong(1, history.getCardId());

			ptmt.setBytes(2, convertImage2bytes(history.getPlateInImage()));
			ptmt.setBytes(3, convertImage2bytes(history.getFaceInImage()));

			ptmt.setString(4, history.getPlateNo());

			ptmt.setTimestamp(5, convertDatetoTimestamp(history.getInDate()));

			ptmt.executeUpdate();

			ptmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean updateHistoryOut(History history) {
		try {
			Connection conn = new DBConnection().createConnection();
			String sql = "update history set plateoutimage = ?,faceoutimage = ?, outdate = ?, price = ? where cardid = ? and outdate is null";
			PreparedStatement ptmt = conn.prepareStatement(sql);
			ptmt.setBytes(1, convertImage2bytes(history.getPlateOutImage()));

			ptmt.setBytes(2, convertImage2bytes(history.getFaceOutImage()));
			ptmt.setTimestamp(3, convertDatetoTimestamp(history.getOutDate()));

			ptmt.setInt(4, history.getPrice());

			ptmt.setLong(5, history.getCardId());

			ptmt.executeUpdate();

			ptmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static Timestamp convertDatetoTimestamp(java.util.Date date) {
		try {
			return new Timestamp(date.getTime());
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static byte[] convertImage2bytes(Image img) {
		try {
			ByteArrayOutputStream s = new ByteArrayOutputStream();
			ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", s);
			byte[] res = s.toByteArray();
			s.close();
			return res;
		} catch (IOException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	public static History getHistoryByInDateAndPlate(History his) {
		Connection conn = DBConnection.createConnection();
		PreparedStatement ptmt = null;
		String sql = "SELECT * FROM history where indate = ? and plateno = ? ";
		ResultSet res = null;
		try {
			ptmt = conn.prepareStatement(sql);
			ptmt.setTimestamp(1, new Timestamp(his.getInDate().getTime()));
			ptmt.setString(2, his.getPlateNo());
			res = ptmt.executeQuery();

			while (res.next()) {
				History history = new History();
				history.setId(res.getLong("id"));
				history.setCardId(res.getLong("cardid"));
				history.setCustomerId(res.getLong("customerid"));
				Image imgPlateInHistory = new Image(new ByteArrayInputStream(res.getBytes("plateinimage")));
				Image imgFaceInHistory = new Image(new ByteArrayInputStream(res.getBytes("faceinimage")));
				Image imgPlateOutHistory = null;
				Image imgFaceOutHistory = null;
				if(res.getBytes("plateoutimage") != null) {
					
				imgPlateOutHistory = new Image(new ByteArrayInputStream(res.getBytes("plateoutimage")));
				imgFaceOutHistory = new Image(new ByteArrayInputStream(res.getBytes("faceoutimage")));
				}
				history.setPlateNo(res.getString("plateno"));
				history.setPlateInImage(imgPlateInHistory);
				history.setPlateOutImage(imgPlateOutHistory);
				history.setFaceInImage(imgFaceInHistory);
				history.setFaceOutImage(imgFaceOutHistory);
				history.setNote(res.getString("note"));
				history.setInDate(res.getTimestamp("indate"));
				history.setOutDate(res.getTimestamp("outdate"));
				history.setPrice(res.getInt("price"));
				history.setUserId(res.getLong("userid"));
				history.setParkingLotId(res.getLong("parkinglotid"));
				res.close();
				ptmt.close();
				conn.close();
				return history;
			}

		} catch (Exception e) {
			System.out.println("khong tim thay gi");
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public static void main(String[] args) throws SQLException, IOException {
		List<History> histories = getAllHistory();
		histories.forEach(h -> System.out.println(h.toString()));
	}

	
}
