package omok_server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {

	private final int 	port = 						7775; 
	private 			ServerSocket 				serverSK; // 서버 소켓 생성
	private 			Socket 						socket;
	private static		ArrayList<ThreadHandler> 	list;
	
	public Server() throws ClassNotFoundException{
		
		try {
			serverSK = new ServerSocket();
			serverSK.bind(new InetSocketAddress("203.248.18.98", port));
			list = new ArrayList<>();
			
			System.out.println("서버 가동중...");
			System.out.println(InetAddress.getLocalHost());
		}catch(IOException e){
				e.printStackTrace();
		}	
			while(true) {
				try {
					socket = serverSK.accept();
					
					System.out.println(socket.getSoTimeout());
					
					ThreadHandler th = new ThreadHandler(socket);
					th.start();
					list.add(th); // 로그인을 하면 해당 스레드를 리스트에 추가함
					System.out.println(getConnectedUserNumber()+"명의 유저가 접속 중");
				}
				catch(IOException e) {
					try {
						socket.close();
						System.out.println("접속이 끊긴 유저가 발생");
						serverSK.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}
	}
	
	public static ArrayList<ThreadHandler> getConnectedUserList(){
		return list;
	}
	
	public static void removeSocket(ThreadHandler th) {
		int i = 0;
		for(ThreadHandler thr : list) {
			
			if(th.getSocket() == thr.getSocket()) {
				list.remove(i);
				break;
			}
			i++;
		}
	}
	
	public static int getConnectedUserNumber(){
		return list.size();
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public static void main(String[] args) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		new Server();	
	}

}
