package application.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DBConnect {
	Connection conn;
	// heroku ClearDB
	String URL = "jdbc:mysql://bfb03fb3f4f761:48d5c287@us-cdbr-east-04.cleardb.com/heroku_c7666ab0a7cf8a4?reconnect=true";
	String USER = "bfb03fb3f4f761";
	String PASSWORD = "48d5c287";
	String DRIVER = "com.mysql.cj.jdbc.Driver";
	// NoteBook Local DB
	String URL2 = "jdbc:mysql://localhost:3306/chatdb";
	String USER2 = "root";
	String PASSWORD2 = "root";
	String DRIVER2 = "com.mysql.cj.jdbc.Driver";
	// ServerPC Local DB
	String URL3 = "jdbc:mysql://192.168.35.133:3306/chatdb";
	String USER3 = "chatuser";
	String PASSWORD3 = "chatuser";
	String DRIVER3 = "com.mysql.cj.jdbc.Driver";
	
	public void connect() {
		String url = URL2;
		String user = USER2;
		String password = PASSWORD2;
		String driver = DRIVER2;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password);
			
//			System.out.println("database connection Success.!!");
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
	}
	
	public int signup(String e, String i, String p) { // e = Email, i = id ,p = password
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql1 = "select user_pw from members where user_id = '" + i + "';";
		try {
			ps = conn.prepareStatement(sql1);
			rs = ps.executeQuery();
			if (rs.next()) {
				Alert tmp = new Alert(AlertType.CONFIRMATION);
				tmp.setContentText("해당 아이디가 이미 존재합니다.");
				tmp.setHeaderText("아이디 중복");
				tmp.setTitle("회원가입 실패");
				tmp.show();
				return 0;
			} else {
				ps.close();
				String sql2 = "insert into members value(?,?,?);";
				ps = conn.prepareStatement(sql2);
				ps.setString(1, e);
				ps.setString(2, i);
				ps.setString(3, p);
				ps.executeUpdate();
				return 1;
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
			try {
				if (ps != null) ps.close();
				if (rs != null) rs.close();
			} catch (SQLException se2) {
				se2.printStackTrace();
			}
		}
		return 0;
	}
	
	public int login(String i, String p) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select user_pw from members where user_id = '" + i + "';";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				System.out.println(rs.getString("user_pw").toString() + " :: " + p.trim());
				
				if(Objects.equals(rs.getString("user_pw"), p)) {
					System.out.println("로그인 성공");
					return 1;
				}
				else {
					Alert tmp = new Alert(AlertType.CONFIRMATION);
					tmp.setContentText("비밀번호가 일치하지 않습니다.");
					tmp.setHeaderText("비밀번호 오류");
					tmp.setTitle("로그인 실패");
					tmp.show();
					return 0;
				}
			} else {
				Alert tmp = new Alert(AlertType.CONFIRMATION);
				tmp.setContentText("아이디가 존재하지 않습니다.");
				tmp.setHeaderText("로그인 실패");
				tmp.setTitle("로그인 실패");
				tmp.show();
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) ps.close();
				if (rs != null) rs.close();
			} catch (SQLException se2) {
				se2.printStackTrace();
			}
		}
		return 0;
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
