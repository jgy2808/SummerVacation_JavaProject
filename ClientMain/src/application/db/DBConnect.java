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
		String sql = "insert into roominfo values (?, ?, ?, 1, ?);";
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, c);
			ps.setString(2, t);
			ps.setString(3, m);
			ps.setInt(4, n);
			
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String testSelect() {
		String roominfo = "";
		PreparedStatement ps = null;
		String sql = "select * from roominfo;";
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()) {
				roominfo += (rs.getString(1) + ", " + rs.getString(2) + ", "  + rs.getString(3) + ", "  + rs.getString(4) + ", "  + rs.getString(5));
				roominfo += "\n";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) ps.close();
				if (rs != null) rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return roominfo;
	}
	
	public void EnterRoom(int roomcode) {
		PreparedStatement ps = null;
		String sql = "update roominfo set CurrentNum = (CurrentNum+1) where Roomcode = ?;";
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, roomcode);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void ExitRoom(int roomcode) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		int currentNum = 0;
		String sql = "select CurrentNum from roominfo where Roomcode = ?;";
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, roomcode);
			rs = ps.executeQuery();
			currentNum = rs.getInt(1);
			
			if (currentNum > 1) {
				sql = "update roominfo set CurrentNum = CurrentNum - 1 where Roomcode = ?;";
				ps.setInt(1, roomcode);
				ps.execute();
			} else {
				sql = "delete from roominfo where Roomcode = ?;";
				ps.setInt(1, roomcode);
				ps.execute();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) ps.close();
				if (rs != null) rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getLastCode() {
		int code = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select Roomcode from roominfo;";
		
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				code = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) ps.close();
				if (rs != null) rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println(code);
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
