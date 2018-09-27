package omok_server;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomManager {
	public static ArrayList<Room> roomList;
	private static AtomicInteger atomicInteger;
	
    private static final long serialVersionUID = 2L;

	static {
		roomList = new ArrayList<>();
		atomicInteger = new AtomicInteger(-1);
	}
	
	public RoomManager() {}
	
	public static Room createRoom(GameUser owner,String roomName) {
	
		int roomId = atomicInteger.incrementAndGet();
        Room room = new Room(roomId);
        room.setRoomName(roomName);
        room.enterUser(owner);
        room.setOwner(owner);
        
        roomList.add(room);
        System.out.println("Room Created!    ID : " + roomId);
        return room;
    		
	}
	
	public static String joinRoom(GameUser joinUser, int roomID) {
		
		roomList.get(roomID).enterUser(joinUser);
		
		System.out.println("Room join!    ID : " + roomID);
		
		return roomList.get(roomID).getUserList().get(0).getUserName(); 
		
	}
	
	public static ArrayList<Room> getList() {
		return roomList;
	}
	
	public static String getStrList() {
		
		String list = "";
		
		if(roomList.size() < 1) {
			return list;			
		}else {
			
		for(Room room : roomList) {
		
			list = list + room.getId() + "/" + room.getRoomName() + "/" + room.getUserSize() + "&";

		}
		
		System.out.println(list + "        LIST 목록 STR");	
		return list;
		}
	}
	
		
	
	public static int getRoomIDX(int roomID){
		System.out.println("Room size :  " + roomCount());
		System.out.println("Room roomID :  " + roomID);

		int idx = 0;
			
		for(int i = 0; i < roomCount() ; i++) {
			
			if(roomID == roomList.get(i).getId()) {
				idx = i;
				
		        System.out.println("Room IDX :  " + idx + " 방");
				
				break;
			}
		}
		return idx;
	}

	
	public static void removeRoom(Room room) {
	        room.close();
	        roomList.remove(room); // 전달받은 룸을 제거한다.
	        System.out.println("Room Deleted!");
	}

	public static int roomCount() {
	        return roomList.size();
	}
}
