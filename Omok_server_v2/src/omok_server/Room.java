package omok_server;

import java.util.ArrayList;

public class Room {
	 private static final long serialVersionUID = 2L;

	    private int id; // room ID
	    private ArrayList<GameUser> userList;
	    private String roomName;
	    public GameUser roomOwner;
	    private boolean isFull;

	    
	    public Room(int roomId) { 
	        this.id = roomId;
	        userList = new ArrayList<>();
	    }
	    
	    public Room(GameUser user){
	        userList = new ArrayList<>();
	        user.enterRoom(this);
	        userList.add(user);
	        this.roomOwner = user;
	    }

	    public void enterUser(GameUser user){
	        user.enterRoom(this);
	        userList.add(user);
	    }

	    public void exitUser(GameUser user){

	        user.exitRoom(this);
	        userList.remove(user); 
  
	        if(userList.size() < 1) { 
	        	RoomManager.removeRoom(this); 
	        	
	        	System.out.println(RoomManager.roomList.size());
	        	
	            return;
	        }

	        if (userList.size() < 2) {
	            this.roomOwner = userList.get(0); // 남은 유저가 방장이 됨
	            return;
	        }
	    }

	    public void close() {
	        for (GameUser user : userList) {
	            user.exitRoom(this);
	        }
	        this.userList.clear();
	        this.userList = null;
	    }
	    
	    
	    public void setOwner(GameUser gameUser) {
	        this.roomOwner = gameUser; 
	    }

	    public void setRoomName(String name) { 
	        this.roomName = name;
	    }

	    public String getRoomName() { 
	        return roomName;
	    }

	    public int getUserSize() { 
	        return userList.size();
	    }

	    public GameUser getOwner() {
	        return roomOwner;
	    }

	    public int getId() {
	        return id;
	    }

	    public void setId(int id) {
	        this.id = id;
	    }

	    public ArrayList<GameUser> getUserList() {
	        return userList;
	    }

	    public void setUserList(ArrayList<GameUser> userList) {
	        this.userList = userList;
	    }

	    public GameUser getRoomOwner() {
	        return roomOwner;
	    }

	    public void setRoomOwner(GameUser roomOwner) {
	        this.roomOwner = roomOwner;
	    }
	}

