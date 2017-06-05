package de.jroeger.server.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import de.jroeger.server.engine.timer.Timeable;
import de.jroeger.server.engine.timer.Timer;
import de.jroeger.server.net.Server;

/**
 * the main construct of the application
 * manages all processes
 * @author Jonas
 *
 */
public class Engine implements Timeable{
	public static final int TIMEOUT = 600000;
	
	
	/**
	 * saves all GameRooms corresponding to a name
	 */
	private Map<String, GameRoom> mSGRRooms;
	
	/**
	 * saves all rooms available for quickplay
	 */
	private List<GameRoom> quickPlayAvailableRooms;
	
	/**
	 * saves all Clients corresponding to a SessionID
	 */
	private Map<String, Client> mSClClientsByID;
	
	/**
	 * saves an instance of the running servergate
	 */
	private Server servGate;
	
	/**
	 * saves the timer instance
	 */
	private Timer timer;
	
	/**
	 * singleton instance
	 */
	private static Engine instance;
	
	private Engine() {
		this.servGate					= Server.getInstance();
		this.mSClClientsByID			= new HashMap<>();
		this.mSGRRooms					= new HashMap<>();
		this.quickPlayAvailableRooms	= new ArrayList<>();
	}
	
	public static Engine getInstance() {
		if(instance == null) {
			instance = new Engine();
		}
		return instance;
	}
	
	/**
	 * used to gain a new Session ID
	 * Connects the client to the generated id
	 * @param client the requesting client
	 * @return the Generated ID for the client
	 */
	private String requestSessionID(Client client) {
		String sessionID;
		
		//Generate ID
		sessionID = UUID.randomUUID().toString();
		
		//add id to client
		client.setSessionID(sessionID);
		
		//Connect ID with client
		this.mSClClientsByID.put(sessionID, client);
		
		return sessionID;
	}
	
	/**
	 * get the gameroom if it doesn't exist create a new one
	 * @param roomname desired room
	 * @return the room
	 */
	public synchronized GameRoom getRoomByName(String roomname) {
		if(!mSGRRooms.containsKey(roomname)) {
			//Create new Room
			mSGRRooms.put(roomname, new GameRoom(roomname));
		}
		//return room
		return mSGRRooms.get(roomname);
	}
	
	/**
	 * returns the client corresponding to the id 
	 * if id not found -> the protocol ensures the id is used as a name now -> set name
	 * @param sessionID current id
	 * @return the client
	 */
	public synchronized Client requestClient(String sessionID) {
		//if session is still in use
		if(mSClClientsByID.containsKey(sessionID)) {
			return mSClClientsByID.get(sessionID);
		}else {
			//Generate new Client and id
			Client cl = new Client(sessionID);
			System.out.println("Generated ID: " + requestSessionID(cl));
			return cl;
		}
	}
	
	/**
	 * this is used to join a game queue
	 * @param client you
	 * @return the game room
	 */
	public synchronized GameRoom joinQuick() {
		GameRoom room;
		
		if(!quickPlayAvailableRooms.isEmpty()) {
			room = quickPlayAvailableRooms.get(0);
			mSGRRooms.put(room.getId(), room);
			quickPlayAvailableRooms.remove(0);
		}else {
			//create room
			String roomName = "room_" + UUID.randomUUID();
			room = new GameRoom(roomName);
			
			//register room
			quickPlayAvailableRooms.add(room);
		}
		
		return room;
	}
	
	public void removeGameRoom(String str) {
		if(quickPlayAvailableRooms.contains(str))
			quickPlayAvailableRooms.remove(str);
		if(mSGRRooms.containsKey(str))
			mSGRRooms.remove(str);
	}
	
	public void removeClient(String id) {
		mSClClientsByID.remove(id);
	}

	@Override
	public void timedEventFinished(Object param) {
		//check all clients
		long currTime = System.currentTimeMillis();
		
		List<String> invalidClients = new ArrayList<>();
		
		for(Entry<String, Client> entry : mSClClientsByID.entrySet()) {
			if(entry.getValue().getTimeCreated() + TIMEOUT >= currTime)
				invalidClients.add(entry.getKey());
		}
		
		//delete all clients
		for(String id : invalidClients) {
			mSClClientsByID.remove(id);
		}
		
		//register new task
		timer.addEvent(this, null, 30000);
	}
}
