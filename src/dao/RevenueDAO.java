package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import model.Chart;
public class RevenueDAO {

	public List<Chart> calMoney(String date_from, String date_to) {
		Connection conn = DBConnection.createConnection();
		PreparedStatement ptmt = null;
		String sql = "select date(h.outdate) as days ,  sum(h.price) as tongtien " + "from history h "
				+ "where h.outdate between '" + date_from + "' and '" + date_to + " 23:59:59' group by date(h.outdate) order by h.outdate ASC";
		ResultSet res = null;
		List<Chart> list = new ArrayList<Chart>();
		try {
			ptmt = conn.prepareStatement(sql);
			res = ptmt.executeQuery();

			while (res.next()) {
				Date date = res.getDate(1);
				int sum = res.getInt(2);
				list.add(new Chart(date, sum));
			}
			res.close();
			ptmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	public List<Chart> calMoneyMonth() {
		Connection conn = DBConnection.createConnection();
		PreparedStatement ptmt = null;
		String sql = "select date(h.outdate) as days ,  sum(h.price) as tongtien "
				+ "from history h "
				+ "where month(h.outdate) = month(sysdate()) group by date(h.outdate) order by h.outdate ASC";
		ResultSet res = null;
		List<Chart> list = new ArrayList<Chart>();
		try {
			ptmt = conn.prepareStatement(sql);
			res = ptmt.executeQuery();

			while (res.next()) {
				Date date = res.getDate(1);
				int sum = res.getInt(2);
				list.add(new Chart(date, sum));
			}
			
			res.close();
			ptmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	

	
	

	    public LineChart createChart() {
	    	RevenueDAO dao = new RevenueDAO();
			Date date_from = new Date("2020/01/01");
			SimpleDateFormat fm= new SimpleDateFormat("yyyy-MM-dd");
			String datef = fm.format(date_from);
			
			Date date_to = new Date("2020/01/30");
			String datet = fm.format(date_to);
			dao.calMoney(datef, datet);
	    	//
	        CategoryAxis xAxis = new CategoryAxis();
	        xAxis.setLabel("Programming Languages");

	        NumberAxis yAxis = new NumberAxis();
	        yAxis.setLabel("Ratings (%)");

	        
	        XYChart.Series dataSeries = new XYChart.Series();
	        dataSeries.setName("Java");
	        for (Chart chart : calMoney(datef, datet)) {
	        	dataSeries.getData().add(new XYChart.Data(chart.getDate().toString(), chart.getMoney()));
			}
	        
	        LineChart chart = new LineChart(xAxis, yAxis);
	        chart.getData().addAll(dataSeries);
	        chart.setTitle("Top Programming Languages");
	        return chart;
	    }

}
