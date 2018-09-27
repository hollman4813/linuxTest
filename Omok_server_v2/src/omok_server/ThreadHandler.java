package omok_server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import omok_db.Db_Proc;

public class ThreadHandler extends Thread{

	private Socket socket;
	
	private InputStream 		in;
	private DataInputStream 	din;
	private OutputStream		out;
	private DataOutputStream 	dout;
	
	private GameUser 			gameUser;
	
	private Db_Proc				db_proc;
	
	public ThreadHandler(Socket socket) {
	
		this.socket = socket;
		gameUser = new GameUser();
		gameUser.setId(-2); // 로그인 전 id 상태 초기화
	}
		
	@Override
	public void run() {
	
		try {
					
			in = socket.getInputStream();
			din = new DataInputStream(in);
			out = socket.getOutputStream();
			dout = new DataOutputStream(out);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		String receiMsg;
		db_proc = new Db_Proc();		
		
			while(true) {
				try {
					receiMsg = din.readUTF();
					String[] receiveMsg = receiMsg.split(":");
					
					if("CONN".equals(receiveMsg[0])){
						/*
						 * CONN에서 보낼 정보 
						 * DB접속 성공여부, ID,PW 일치 확인 
						 */
						
						String result = null;
						String[] data = null;
						String sendMsg = null;
						
						data = receiveMsg[1].split("/");
						
						db_proc = new Db_Proc();						
						result = db_proc.loginCheck(data[0], data[1]);
																
						String[] strRs = result.split(":");
												 
						if(strRs[0].equals("true")) {	
							sendMsg = "CONN:"+ "true:" + strRs[1] + ":" + strRs[2] + ":" + strRs[3] + ":" + strRs[4] + ":" + strRs[5];
							gameUser.setSocket(socket);
							gameUser.setRoom(null);
							gameUser.setUserName(strRs[1]);
							gameUser.setId(-1);
							gameUser.setWin(Integer.parseInt(strRs[2]));
							gameUser.setDraw(Integer.parseInt(strRs[3]));
							gameUser.setLose(Integer.parseInt(strRs[4]));
							gameUser.setLv(Integer.parseInt(strRs[5]));
						}else {	sendMsg = "CONN:"+ "false";}
						
						dout.writeUTF(sendMsg);
						dout.flush();
					}if("A_CONN".equals(receiveMsg[0])){
						/*
						 * A_CONN에서 보낼 정보 
						 * DB접속 성공여부, phone번호가 등록되어있으면 true 리턴함 
						 */
												
						String result = null;
						String[] data = null;
						String sendMsg = null;
						
						data = receiveMsg[1].split("/");
						System.out.println(data[0]);
						
						db_proc = new Db_Proc();						
						result = db_proc.phoneNumCheck(data[0]); // pnumcheck
						
						
						String[] strRs = result.split(":");
												 
						if(strRs[0].equals("true")) {	
							sendMsg = "A_CONN:"+ "true:" + strRs[1] + ":" + strRs[2] + ":" + strRs[3] + ":" + strRs[4] + ":" + strRs[5];
							gameUser.setSocket(socket);
							gameUser.setRoom(null);
							gameUser.setUserName(strRs[1]);
							gameUser.setId(-1);
							gameUser.setWin(Integer.parseInt(strRs[2]));
							gameUser.setDraw(Integer.parseInt(strRs[3]));
							gameUser.setLose(Integer.parseInt(strRs[4]));
							gameUser.setLv(Integer.parseInt(strRs[5]));
						}else {	sendMsg = "A_CONN:"+ "false";}

						
						
						dout.writeUTF(sendMsg);
						dout.flush();
					}else if("LOGIN".equals(receiveMsg[0])){
						/*
						 * 
						 * LOGIN에서 보낼정보 
						 * 로그인 : , 접속자 수 , 방목록
						 * 
						 * 받은정보
						 * 닉네임
						 * 
						 */
											
						String sendMsg = "LOGIN:"+ Server.getConnectedUserNumber() + ":" + RoomManager.getStrList() + "@";
											
						dout.writeUTF(sendMsg);
						dout.flush();

					}else if("JOIN".equals(receiveMsg[0])){ // 회원가입
						/*
						 * 
						 * JOIN에서 보낼정보 
						 * 완료 메세지
						 * 
						 * 받은정보
						 * ID, PW, 닉네임
						 * insert 명령보냄
						 * 
						 */
						String result = null;
						String[] data = null;
						
						data = receiveMsg[1].split("/");
						
						db_proc = new Db_Proc();						
						result = db_proc.insert(data[0], data[1], data[2], data[3]);
						
						String sendMsg = "JOIN:"+ result;
						
						if(result.equals("true")) {
							gameUser.setSocket(socket);
							gameUser.setRoom(null);
							gameUser.setUserName(data[0]);
							gameUser.setId(-1);
							gameUser.setWin(0);
							gameUser.setDraw(0);
							gameUser.setLose(0);
							gameUser.setLv(1);
						}
						
						dout.writeUTF(sendMsg);
						dout.flush();
						
					}else if("GAMEJOIN".equals(receiveMsg[0])){
						/*
						 * GAMEJOIN에서 보낼정보 
						 * 방에 있는 사람 이름
						 * 
						 * 받은정보
						 * 리스트 번호 -> 방 ID로 변환
						 */
									
						int roomId = Integer.parseInt(receiveMsg[1]);
						int roomListIdx = RoomManager.getRoomIDX(roomId);
						String result  	= null;
						String oUser   	= null; // 상대방 닉네임
						String sendMsg 	= null;
						
						if(RoomManager.getList().get(roomListIdx).getUserSize() < 2 ) {
							RoomManager.joinRoom(gameUser, roomListIdx);
							
							gameUser.setId(roomId);
							oUser = gameUser.getRoom().getUserList().get(0).getUserName();
														
							db_proc = new Db_Proc();						
							result = db_proc.selectU(oUser);
													
							sendMsg = "GAMEJOIN:"+ oUser + result;
							
							System.out.println(sendMsg);
							
							dout.writeUTF(sendMsg);
							dout.flush();
						}else {
							sendMsg = "GAMEJOIN:" + "full"; // 방에 2명이 있으면 full이라고 보냄
							
							dout.writeUTF(sendMsg);
							dout.flush();
						}
	
					}else if("GAMEIN".equals(receiveMsg[0])){
						String result = null;
						String oUser = receiveMsg[1];
						String sendMsg = null;
						
						db_proc = new Db_Proc();						
						result = db_proc.selectU(oUser);
												
						sendMsg = "GAMEIN:"+ oUser + result;
						
						System.out.println(sendMsg);
						rBroadCast(sendMsg, gameUser.getId());
					
					}else if("READY".equals(receiveMsg[0])){
						/*
						 * READY에서 보낼정보
						 * 상대방에게 READY 여부 보냄
						 * 
						 * 받은정보
						 * READY 상태 				 
						 * 
						 */
						
						String sendMsg = "READY:";
						rBroadCast(sendMsg, gameUser.getId());
										
						
					}else if("START".equals(receiveMsg[0])){
						/*
						 * START에서 보낼 정보
						 * 상대방에게 START 메세지 보냄
						 * 
						 * 받은정보
						 * START 신호
						 * 
						 */
									
						String sendMsg = "START:";
						rBroadCast(sendMsg, gameUser.getId());
	
					}else if("QUITROOM".equals(receiveMsg[0])){
						gameUser.getRoom().exitUser(gameUser);
						
						int tempId = gameUser.getId();
						String sendMsg = "QUITROOM:"+receiveMsg[1];
						
						rBroadCast(sendMsg, tempId);
						System.out.println("QUITROOM 작동");
						
						
					}else if("CREATEROOM".equals(receiveMsg[0])){
							
						/*
						 * CREATEROOM에서 보낼 정보
						 * CREATEROOM 메세지
						 * 
						 * 받은정보 
						 * CREATEROOM 명령
						 * CREATEROOM 방제목
						 * 
						 */
						RoomManager.createRoom(gameUser, receiveMsg[1]);
						gameUser.setId(gameUser.getRoom().getId());
						System.out.println("게임생성 id " + gameUser.getRoom().getId());
	
						System.out.println("CreateRoom setID :   " +    gameUser.getId());
						
						String sendMsg = "CREATEROOM:";
						rBroadCast(sendMsg, gameUser.getId());
						
						//broadCast(sendMsg);
					}else if("BLACKSTONE".equals(receiveMsg[0])) {
						
						/*
						 * BLACKSTONE에서 보낼 정보
						 * 좌표     1,2,black
						 * 
						 * 받은정보 
						 * 좌표
						 * 
						 */
										
						String sendMsg = "BLACKSTONE:"+receiveMsg[1];
						rBroadCast(sendMsg, gameUser.getId());
							
						
					}else if("WHITESTONE".equals(receiveMsg[0])) {
						
						/*
						 * WHITESTONE에서 보낼 정보
						 * 좌표     1,2,white
						 * 
						 * 받은정보 
						 * 좌표
						 * 
						 */
										
						String sendMsg = "WHITESTONE:"+receiveMsg[1];
						rBroadCast(sendMsg, gameUser.getId());
							
						
					}else if("OMOK".equals(receiveMsg[0])) {
						/*
						 * OMOK에서 보낼 정보
						 * 오복을 한 돌 색깔
						 * 
						 * 받은정보 
						 * 좌표
						 * 
						 */	
										
						String sendMsg = "OMOK:"+receiveMsg[1];
						String result = null;
						
						db_proc = new Db_Proc();
						
						if(receiveMsg[1].equals("white")) {
							result = db_proc.updateW(gameUser.getRoom().getUserList().get(1).getUserName(), 
									gameUser.getRoom().getUserList().get(0).getUserName());
							System.out.println("white윈");
						}else {
							result = db_proc.updateW(gameUser.getRoom().getUserList().get(0).getUserName(), 
									gameUser.getRoom().getUserList().get(1).getUserName());
							System.out.println("Black윈");
						}
						
						rBroadCast(sendMsg+result, gameUser.getId());
			
					}else if("LIST".equals(receiveMsg[0])) {
						
						String sendMsg = "LIST:"+ Server.getConnectedUserNumber() + ":" + RoomManager.getStrList() + "@";
						broadCast(sendMsg);
						
					}else if("GIVEUP".equals(receiveMsg[0])) {
						
						String sendMsg;
						String result = null;
						
						if(gameUser.getRoom().getUserList().get(0).getUserName().equals(receiveMsg[1])) { //black stone
							result = db_proc.updateW(gameUser.getRoom().getUserList().get(1).getUserName(), 
									gameUser.getRoom().getUserList().get(0).getUserName());
						}else { // white stone
							result = db_proc.updateW(gameUser.getRoom().getUserList().get(0).getUserName(), 
									gameUser.getRoom().getUserList().get(1).getUserName());
						}
							
						sendMsg = receiveMsg[0] + ":" + receiveMsg[1];		
						rBroadCast(sendMsg + result, gameUser.getId());
											
					}else if("UNCONNECTED".equals(receiveMsg[0])){
						
						String sendMsg = "UNCONNECTED:"+receiveMsg[1];
						rBroadCast(sendMsg, gameUser.getId());
						
						gameUser.getRoom().exitUser(gameUser);
						
						String bSendMsg = "LIST:"+ Server.getConnectedUserNumber() + ":" + RoomManager.getStrList() + "@";
						broadCast(bSendMsg);
						
						socket.close();
						din.close();
						dout.close();
						gameUser = null;
						
						Server.getConnectedUserList().remove(this);		
					}else if("CLOSEAPP".equals(receiveMsg[0])){
						
						/*
						 * CLOSEAPP에서 보낼 정보
						 * CLOSEAPP 신호
						 * 
						 * 받은정보
						 * CLOSEAPP 신호
						 *  
						 */
						String sendMsg = "CLOSEAPP:";					
	
						dout.writeUTF(sendMsg);
						dout.flush();
						
						socket.close();
						din.close();
						dout.close();
						gameUser = null;
						
						Server.getConnectedUserList().remove(this);					
						
						break;
					}
				
			
				}catch(Exception e) { // 접속이 끊겼거나 클라이언트에 문제가 있을 경우
			
					if(gameUser.getRoom() != null){
						if(gameUser.getRoom().getUserSize() > 1) {
							System.out.println("방에 있는데 나감2");
							String sendMsg = "QUITROOM:"+gameUser.getUserName(); // 방에 있는 유저를 QUITROOM 명령어로 빼냄
							
							int tempID = gameUser.getId(); // 임시id보관
							gameUser.getRoom().exitUser(gameUser); // 유저 퇴장 조치

							rBroadCast(sendMsg, tempID); // 방에 퇴장 알림
						}else {
							System.out.println("방에 있는데 나감1");
							gameUser.getRoom().exitUser(gameUser);
									
						}
					}else {
						System.out.println("접속끊김");
					}
					
					String bSendMsg = "LIST:"+ Server.getConnectedUserNumber() + ":" + RoomManager.getStrList() + "@";
					broadCast(bSendMsg);
					
					Server.removeSocket(this); // 소켓리스트에서도 지움
					break;			
			}
		}
	}
	public void broadCast(String str) { // 전체 통신
		int i = 0;
		for(ThreadHandler th : Server.getConnectedUserList()) {
			if(th.socket != null) {
				try {
					if(th.gameUser.getId() > -2) {
						th.dout.writeUTF(str);
						th.dout.flush();
					}
				} catch (IOException e) {
					System.out.println("전송오류");
					Server.getConnectedUserList().remove(i);
					e.printStackTrace();
				}
			}
			else {
				System.out.println("소켓이 비엇음!");
				Server.getConnectedUserList().remove(i);
			}
			
			i++;
		}
	}
	
	public void rBroadCast(String str, int roomId) { // 방안에 사람들만 통신
		
		for(ThreadHandler th : Server.getConnectedUserList()){
			if(th.socket != null) {
				try {
					if(th.getGameUser().getId() == roomId) { // roomId가 같은 유저면
						th.dout.writeUTF(str);
						th.dout.flush();
					}
				} catch (IOException e) {
					System.out.println("전송오류2");
					e.printStackTrace();
				}
			}
			else {
				Server.removeSocket(th);
			}
		}
	}
	public GameUser getGameUser() {
		
		return gameUser; 
	}
	
	public Socket getSocket() {
		return socket;
	}
}
