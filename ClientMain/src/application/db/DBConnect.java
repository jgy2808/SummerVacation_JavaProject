package application.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnect {
	Connection conn;
	String URL = "jdbc:mysql://bfb03fb3f4f761:48d5c287@us-cdbr-east-04.cleardb.com/heroku_c7666ab0a7cf8a4?reconnect=true";
	String USER = "bfb03fb3f4f761";
	String PASSWORD = "48d5c287";
	String DRIVER = "com.mysql.cj.jdbc.Driver";
	
	public void connect() {
		String url = URL;
		String user = USER;
		String password = PASSWORD;
		String driver = DRIVER;
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
	
	public int InsertRoominfo(String t, String m, int mn) { // code, title, master, max_num
		int roomcode = -1;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select Roomcode from roominfo order by Roomcode DESC limit 1;";
		String sql1 = "insert into roominfo values (?, ?, ?, 1, ?);";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				roomcode = rs.getInt(1) + 1;
			} else {
				roomcode = 0;
			}
			ps.close();
			rs.close();
			
			ps = conn.prepareStatement(sql1);
			ps.setInt(1, roomcode);
			ps.setString(2, t);
			ps.setString(3, m);
			ps.setInt(4, mn);
			
			ps.executeUpdate();
			ps.close();
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
		return roomcode;
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
	
	public String SelectRoomInfo() {
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
	// 방에 입장하기 -> 방의 인원이 꽉 차지 않았는가를 체크한 후 만족하면 입장 성공 return 1 -> 방의 인원을 찾을 수 없다면 존재하지 않는 방 -> return 2;
	// 둘 중 하나라도 만족하지 않으면 return 0;
	// Controller2 에서 입장 버튼 눌렀을 때 이 함수의 return 값을 이용해서 입장할 지 안할지 판별
	public int EnterRoom(int roomcode) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql;
		try {
			sql = "select CurrentNum, MaxNum from roominfo where Roomcode = ?;";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, roomcode);
			rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) < rs.getInt(2)) {
					ps.close();
					sql = "update roominfo set CurrentNum = (CurrentNum+1) where Roomcode = ?;";
					ps = conn.prepareStatement(sql);
					ps.setInt(1, roomcode);
					ps.execute();
					ps.close();
					rs.close();
					return 1;
				} else { 
					// 방이 꽉 찼을 때
					ps.close();
					rs.close();
					return 2;
				}
			} else {
				// 방이 존재하지 않을 때
				ps.close();
				rs.close();
				return 0;
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
		System.out.println("EnterRoom 불완전 종료");
		return 0;
	}
	
	public void DeleteRoom(int roomcode) {
		PreparedStatement ps = null;
		String sql = "delete from roominfo where Roomcode = ?;";
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, roomcode);
			ps.execute();
			ps.close();
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
		System.out.println("exitroom method on");
		PreparedStatement ps = null;
		String sql = "update roominfo set CurrentNum = CurrentNum - 1 where Roomcode = ?;";
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, roomcode);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("exitroom method off");
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
