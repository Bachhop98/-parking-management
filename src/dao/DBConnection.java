package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
	public static Connection createConnection() {
		Connection conn = null;
		String url = "jdbc:mysql://127.0.0.1:3306/parking";
		String username = "root";
		String pass = "123456";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(url, username, pass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static void main(String[] args) {
		Connection conn = createConnection();
		if (conn == null) {
			System.out.println("connection fail");
		} else {
			System.out.println("connection success");
		}
	}
}
