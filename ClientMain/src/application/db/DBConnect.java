package application.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnect {
	Connection conn;
	public void connect() {
		String url = "jdbc:mysql://bfb03fb3f4f761:48d5c287@us-cdbr-east-04.cleardb.com/heroku_c7666ab0a7cf8a4?reconnect=true";
		String user = "bfb03fb3f4f761";
		String password = "48d5c287";
		String driver = "com.mysql.cj.jdbc.Driver";
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password);

			System.out.println("database connection Success.!!");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace(); // database driver class Not load
			try { conn.close(); } 
			catch (SQLException e) { e.printStackTrace(); }
		} catch (SQLException e2) {
			e2.printStackTrace(); // database connection failed
			try { conn.close(); } 
			catch (SQLException e) { e.printStackTrace(); }
		} catch (Exception e3) {
			e3.printStackTrace(); // unknown Exception..
			try { conn.close(); } 
			catch (SQLException e) { e.printStackTrace(); }
		}
	}
	
	public void InsertRoominfo(int c, String t, String m, int n) { // code, title, master, num
		PreparedStatement ps = null;
		String sql = "insert into roominfo values (?, ?, ?, ?);";
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, c);
			ps.setString(2, t);
			ps.setString(3, m);
			ps.setInt(4, n);
			
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (ps != null) ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void testDelete() {
		PreparedStatement ps = null;
		String sql = "delete from roominfo;";
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (ps != null) ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getCode(String t) {
		String code = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select Roomcode from roominfo where RoomTitle = ?;";
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, t);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				code = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			if (ps != null) ps.close();
			if (rs != null) rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return code;
	}
	
	public void close() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
