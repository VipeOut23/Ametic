package de.jroeger.server.engine;

import de.jroeger.net.ProtocolContainer;
import de.jroeger.server.net.Connection;

/**
 * this class represents a client
 * clients are created by connections
 * they can join game rooms
 * @author Jonas
 *
 */
public class Client {
	/**
	 * saves the current sessionID
	 */
	private String sessionID;
	
	/**
	 * saves the engine instance
	 */
	private Engine engine;
	
	/**
	 * saves the currently joined gameroom
	 */
	private GameRoom grJoined;
	
	private String name;
	
	/**
	 * saves the connection reference
	 */
	private Connection connection;
	
	/**
	 * saves the time when the client was created
	 */
	private long timeCreated;
	
	public Client(String name) {
		this.engine 		= Engine.getInstance();
		this.name			= name;
		this.timeCreated	= System.currentTimeMillis();
	}
	
	/**
	 * used to join a game room
	 * sends name of opponent if room is empty
	 * @param roomName  roomname
	 */
	public void joinRoom(String roomname) {
		GameRoom gr = engine.getRoomByName(roomname);
		if(gr != null)
		if(gr.isJoinable()) {
			//join room
			if(!gr.join(this))
				connection.addError("Couldn't join Room \"" + roomname + "\"...");
			else {
				//Joined
				
				//member gameroom
				this.grJoined = gr;
				
				//Send response
				Client opponent = gr.getOpponent(this);
				if(opponent == null) 
					connection.addResponse(ProtocolContainer.WAIT, "-");
				else {
					connection.addResponse(ProtocolContainer.JOIN, opponent.getName());
					//Tell opponent the name
					grJoined.getOpponent(this).sendResponse(ProtocolContainer.JOIN, name);
					//send start packages
					grJoined.start();
				}
			}
		}
		
	}
	
	public void joinQuick() {
		GameRoom gr = engine.joinQuick();
		if(gr != null)
		if(!gr.join(this)) {
			connection.addError("Couldn't join Room...");
		}else {
			//Joined
			
			//member gameroom
			this.grJoined = gr;
			
			//Send response
			Client opponent = gr.getOpponent(this);
			if(opponent == null) 
				connection.addResponse(ProtocolContainer.WAIT, "-");
			else {
				connection.addResponse(ProtocolContainer.JOIN, opponent.getName());
				//Tell opponent the name
				grJoined.getOpponent(this).sendResponse(ProtocolContainer.JOIN, name);
				//send start packages
				grJoined.start();
			}
		}
		
	}
	
	/**
	 * used to realize a game move
	 * @param content the content of the request
	 */
	public void doAction(String content) {
		if(grJoined != null) {
			grJoined.clicked(this, content);
		}else {
			connection.addError("No game joined!");
		}
	}
	
	/**
	 * used to send a response message
	 * @param header header
	 * @param content content
	 */
	public void sendResponse(int header, String content) {
		connection.addResponse(header, content);
	}
	
	/**
	 * forces the client to leave a room
	 */
	public void forceLeaveRoom() {
		grJoined = null;
	}
	
	/**
	 * this gets called from a connection if it timed out
	 */
	public void timedOut() { 
		if(grJoined != null)
			grJoined.handleTimeout(this);
		//remove client
		engine.removeClient(sessionID);
	}
	
	/**
	 * this gets called form a connection upon a dc request
	 */
	public void disconnected() {
		if(grJoined != null)
			grJoined.handleDC(this);
	}
	
	/*
	 * Getter & Setter
	 */
	
	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	
	public void setConnection(Connection con) {
		this.connection = con;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public long getTimeCreated() {
		return timeCreated;
	}
}
