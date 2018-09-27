package omok_db;

import java.io.File;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Db_Proc {

	private ResultSet   rs	 = null;
	
	private final String url = "jdbc:sqlserver://220.88.9.7:1433;databaseName=Team;user=homepage;password=dipsys1!;";
	private final String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	private Connection conn = null;
	private Statement stmt = null;
	
	
	
public String phoneNumCheck(String pNum) throws Exception {
		
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn = DriverManager.getConnection(url);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
			
		String query = null;
		String strpNum = null;
		String strNick = null;
		String strWin = null;
		String strDraw = null;
		String strLose = null;
		String strLv = null;
		
		String result = null;
		query = "SELECT "+ "pNumber, Nickname, win, draw, lose, lv" + " FROM [TEAM].[dbo].[UserTable] WHERE pNumber ='"+ pNum +"'";
						
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);
		
		while(rs.next()) {
			strpNum = rs.getString("pNumber");
			strNick = rs.getString("nickname");
			strWin = rs.getString("win");
			strDraw = rs.getString("draw");
			strLose = rs.getString("lose");
			strLv = rs.getString("lv");
		}
		
					
		if(strpNum != null && strpNum.equals(pNum)) {
			result  = "true:"+strNick+":"+strWin+":"+strDraw+":"+strLose+":"+strLv;
		}else {
			result  = "false";
		}
		
		if(rs != null)
			try{rs.close();}catch(SQLException e){}
		if(stmt != null)
			try{stmt.close();}catch(SQLException e){}
		if(conn != null)
			try{conn.close();}catch(SQLException e){}		
		
		return result;
		

	}
	
	
	public String loginCheck(String id, String pw) throws Exception {
		
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn = DriverManager.getConnection(url);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
			
		String query = null;
		String strPwd = null;
		String strNick = null;
		String strWin = null;
		String strDraw = null;
		String strLose = null;
		String strLv = null;
		
		String createHash = null;
		String result = null;
		query = "SELECT "+ "pwd, nickname, win, draw, lose, lv" + " FROM [TEAM].[dbo].[UserTable] WHERE id ='"+ id +"'";
				
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);
		
		while(rs.next()) {
			strPwd = rs.getString("pwd");
			strNick = rs.getString("nickname");
			strWin = rs.getString("win");
			strDraw = rs.getString("draw");
			strLose = rs.getString("lose");
			strLv = rs.getString("lv");
		}
		
		createHash = createHash(pw);
		
		if(strPwd != null && strPwd.equals(createHash)) {
			result  = "true:"+strNick+":"+strWin+":"+strDraw+":"+strLose+":"+strLv;
		}else {
			result  = "false";
		}
		
		if(rs != null)
			try{rs.close();}catch(SQLException e){}
		if(stmt != null)
			try{stmt.close();}catch(SQLException e){}
		if(conn != null)
			try{conn.close();}catch(SQLException e){}		
		
		return result;
	}
	
	public String selectU(String nickName) {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn = DriverManager.getConnection(url);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
		String query = null;
		
		query = "SELECT "+ "win, draw, lose, lv" + " FROM [TEAM].[dbo].[UserTable] WHERE nickname ='"+ nickName +"'";

		String strWin = null;
		String strDraw = null;
		String strLose = null;
		String strLv = null;
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
		
		while(rs.next()) {
			strWin = rs.getString("win");
			strDraw = rs.getString("draw");
			strLose = rs.getString("lose");
			strLv = rs.getString("lv");
		}

		strLv = checkLevel(strWin);
			
		
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(rs != null)
			try{rs.close();}catch(SQLException e){}
		if(stmt != null)
			try{stmt.close();}catch(SQLException e){}
		if(conn != null)
			try{conn.close();}catch(SQLException e){}
		
		String result = null;
		result  = ":" + strWin+":"+strDraw+":"+strLose+":"+strLv;
				
		return result;
	
	}
	public String updateW(String winner, String loser) throws Exception {
		
		/*
		 *	UPDATE QUERY
		 */
		
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn = DriverManager.getConnection(url);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
		String sendMsg = null;
		String query = null;
		String strWin = null;
		String strDraw = null;
		String strLose = null;
		String strLv = null;
		int w;
		int l;
		/*
		 *  BLACKSTONE
		 */
		query = "SELECT "+  "win, draw, lose, lv" + " FROM [TEAM].[dbo].[UserTable] WHERE nickname ='"+ winner +"'";
		
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);
			
		while(rs.next()) {
			strWin = rs.getString("win");
			strDraw = rs.getString("draw");
			strLose = rs.getString("lose");
			strLv = rs.getString("lv");
		}
		
		w = Integer.parseInt(strWin); // 1 승
		w = w + 1;
		
		strLv = checkLevel(String.valueOf(w));
		sendMsg  = ":" + winner + "/" + w +"/"+strDraw+"/"+strLose+"/"+strLv+"@";
				
		// WINNER 전적 업데이트
		query = "UPDATE [TEAM].[dbo].[UserTable] "
				+ "SET win = " + w + ", lv = " + strLv + " FROM [TEAM].[dbo].[UserTable] WHERE nickname ='"+ winner +"'";
		
		System.out.println("유저 레벨 : " + strLv);
		
		stmt = conn.createStatement();
		stmt.executeUpdate(query);	
		
		/*
		 *  WHITE STONE
		 */
		
		query = "SELECT "+ "win, draw, lose, lv" + " FROM [TEAM].[dbo].[UserTable] WHERE nickname ='"+ loser +"'";
		
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);
		
		while(rs.next()) {
			strWin = rs.getString("win");
			strDraw = rs.getString("draw");
			strLose = rs.getString("lose");
			strLv = rs.getString("lv");
		}
		
		l = Integer.parseInt(strLose); // 1승 추가
		l = l + 1;
		
		sendMsg  = sendMsg + loser + "/" + strWin+"/"+strDraw+"/"+l+"/"+strLv;
		
		
		// LOSER 전적 업데이트
		query = "UPDATE [TEAM].[dbo].[UserTable] "
				+ "SET lose = " + l + " FROM [TEAM].[dbo].[UserTable] WHERE nickname ='"+ loser +"'";
		
		stmt = conn.createStatement();
		stmt.executeUpdate(query);
		
		
		if(rs != null)
			try{rs.close();}catch(SQLException e){}
		if(stmt != null)
			try{stmt.close();}catch(SQLException e){}
		if(conn != null)
			try{conn.close();}catch(SQLException e){}
		
		System.out.println(sendMsg);
					
		return sendMsg;
		
	}
	public String insert(String nickName, String id, String password, String pn) {
		
		String sha256 = null;		// SHA-256 암호화
		String result = null;
		
			
		try {
			sha256 = createHash(password);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}		
		
		String query = "INSERT INTO [TEAM].[dbo].[UserTable] VALUES("+ 
				"'"+nickName+"', " + "'"+id+"', " + "'" + sha256+ "'," +"'0', " + "'0', " + "'0',"+"'1'," + "'" + pn + "')";
		System.out.println(query);
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn = DriverManager.getConnection(url);
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e) {
			System.out.println(e.getErrorCode());
			
			if(e.getErrorCode() == 2627) {
				result = "dup";
				return result;
			}
			
			e.printStackTrace();
		}		
		
		if(rs != null)
			try{rs.close();}catch(SQLException e){}
		if(stmt != null)
			try{stmt.close();}catch(SQLException e){}
		if(conn != null)
			try{conn.close();}catch(SQLException e){}		
		
		result = "true";
		
		return result;
	}
	
	public String createHash(String pwd) throws Exception{
		
		if(pwd == null) {
			throw new NullPointerException();
		}
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] raw = md.digest(pwd.getBytes("EUC-KR"));
		
		StringBuffer result = new StringBuffer();
		for(int i=0;i < raw.length; i++) {
			result.append(Integer.toHexString(raw[i] & 0xff));
		}
		return result.toString();
	}
	
	public String checkLevel(String winStr) {
		
		int win = 0;
		int level = 0;
		String strLv = null;
		
		win = Integer.parseInt(winStr);
	
		if(win >= 50) {
			level = 4;
		}else if (win >= 20) {
			level = 3;			
		}else if (win >= 10) {
			level = 2;
		}else if (win >= 0) {
			level = 1;
		}
	
		strLv = String.valueOf(level);
						
		return strLv;
	}
	
}
