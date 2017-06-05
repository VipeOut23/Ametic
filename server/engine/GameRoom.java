package de.jroeger.server.engine;

import de.jroeger.net.ProtocolContainer;

/**
 * this class represents a game room,
 * which manages the game of two players
 * @author Jonas
 *
 */
public class GameRoom {
	/**
	 * saves the player
	 */
	private volatile Client host, guest;
	
	/**
	 * saves running state of game
	 */
	private boolean isRunning;
	
	/**
	 * saves all field states
	 */
	private int[] iFields;
	
	/**
	 * saves the current gamepahse
	 */
	private int iGamePhase;
	
	/**
	 * saves, if it's currently the hosts turn
	 */
	private boolean bHostTurn;
	
	/**
	 * saves the current id
	 */
	private String strId;
	
	/**
	 * saves engine instance
	 */
	private Engine engine;
	
	public GameRoom (String id) {
		this.isRunning	= false;
		this.strId		= id;
		this.iFields	= new int[9];
		
		engine = Engine.getInstance();
	}
	
	/**
	 * used to start the game
	 */
	public synchronized void start() {
		bHostTurn = Math.random() < 0.5;
		
		//notify player
		host.sendResponse(ProtocolContainer.START, String.valueOf(bHostTurn));
		guest.sendResponse(ProtocolContainer.START, String.valueOf(!bHostTurn));
		
		iGamePhase = 1;
		
		isRunning = true;
	}
	
	/**
	 * used to make a game move
	 * @param cl the client who clicked
	 * @param field the clicked field
	 */
	public synchronized void clicked(Client cl, String field) {
		if(isRunning) {
			if((cl == host) != bHostTurn) {return;} //not the clients turn
			
			switch(iGamePhase) {
			case 1: doTic(cl, Integer.parseInt(field)); break;
			case 2: doTac(cl, Integer.parseInt(field)); break;
			case 3: doToe(cl, Integer.parseInt(field)); break;
			}
			
		}else {
			cl.sendResponse(ProtocolContainer.ERROR, "Game hasn't started yet!");
		}
	}
	
	/**
	 * tries to join the room
	 * @param client joiner
	 * @return could join
	 */
	public synchronized boolean join(Client client) {
		if(host == null) host = client;
		else if(guest == null) guest = client;
		else return false;
		return true;
	}
	
	/**
	 * used to get the opponent
	 * @param cl you
	 * @return opponent
	 */
	public synchronized Client getOpponent(Client cl) {
		if(host.equals(cl))
			return guest;
		return host;
	}
	
	/**
	 * used to lookup if the room is full
	 * @return joinable
	 */
	public synchronized boolean isJoinable() {
		return (host == null || guest == null);
	}
	
	/**
	 * used to handle a timeout
	 * @param cl the client timed out
	 */
	public synchronized void handleTimeout(Client cl) {
		if(isJoinable()) return; //if there is no opponent, there isn't anybody to notify
		
		//notify opponent
		getOpponent(cl).sendResponse(ProtocolContainer.DC, "timeout");
		
		cl.forceLeaveRoom();
		getOpponent(cl).forceLeaveRoom();
		
		engine.removeGameRoom(strId);
		
		isRunning = false;
	}
	
	/**
	 * used to handle a disconnect
	 * @param cl the client dc'ed
	 */
	public synchronized void handleDC(Client cl) {
		//notify opponent
		getOpponent(cl).sendResponse(ProtocolContainer.DC, "disconnected");
		
		cl.forceLeaveRoom();
		getOpponent(cl).forceLeaveRoom();
		
		engine.removeGameRoom(strId);
		
		isRunning = false;
	}
	
	/*
	 * game logic
	 */
	
	private void doTic(Client cl, int field) {
		
		//check if field is available
		if(iFields[field-1] == 0) {
			//set field
			iFields[field-1] = 1;
			
			//send response
			getOpponent(cl).sendResponse(ProtocolContainer.DO, String.valueOf(field));
			
			if(isWinSituation()) {
				//Send end messages
				cl.sendResponse(ProtocolContainer.END, "true");
				getOpponent(cl).sendResponse(ProtocolContainer.END, "false");
				isRunning = false;
				
				//force members to leave
				cl.forceLeaveRoom();
				getOpponent(cl).forceLeaveRoom();
				
				//delete room
				engine.removeGameRoom(strId);
			}
			
			bHostTurn = !bHostTurn;
			iGamePhase = 2;
		}
	}
	
	private void doTac(Client cl, int field) {
		
		//check if field is available
		if(iFields[field-1] == 0) {
			//set field
			iFields[field-1] = 2;
			
			//send response
			getOpponent(cl).sendResponse(ProtocolContainer.DO, String.valueOf(field));
			
			if(isWinSituation()) {
				//Send end messages
				cl.sendResponse(ProtocolContainer.END, "true");
				getOpponent(cl).sendResponse(ProtocolContainer.END, "false");
				isRunning = false;
				
				//force members to leave
				cl.forceLeaveRoom();
				getOpponent(cl).forceLeaveRoom();
				
				//delete room
				engine.removeGameRoom(strId);
			}
			bHostTurn = !bHostTurn;
			iGamePhase = 3;
		}
	}
	
	private void doToe(Client cl, int field) {
		
		//check if field is available
		if(iFields[field-1] != 0) {
			//set field
			iFields[field-1] = 0;
			
			//send response
			getOpponent(cl).sendResponse(ProtocolContainer.DO, String.valueOf(field));
			
			bHostTurn = !bHostTurn;
			iGamePhase = 1;
		}
	}
	
	/**
	 * used to check if there is a win situation on this field
	 * @return winsituation
	 */
	private boolean isWinSituation() {
		boolean win = false;
		//check hor rows
		if(isEqualRow(iFields[0], iFields[1], iFields[2])) win = true;
		if(isEqualRow(iFields[3], iFields[4], iFields[5])) win = true;
		if(isEqualRow(iFields[6], iFields[7], iFields[8])) win = true;
		
		//check ver rows
		if(isEqualRow(iFields[0], iFields[3], iFields[6])) win = true;
		if(isEqualRow(iFields[1], iFields[4], iFields[7])) win = true;
		if(isEqualRow(iFields[2], iFields[5], iFields[8])) win = true;
		
		//check dia rows
		if(isEqualRow(iFields[0], iFields[4], iFields[8])) win = true;
		if(isEqualRow(iFields[6], iFields[4], iFields[2])) win = true;
		
		return win;
	}
	
	private boolean isEqualRow(int f1, int f2, int f3) {
		return (f1 != 0 && f1 == f2 && f2 == f3);
	}
	
	/*
	 * Getter & Setter
	 */
	
	public String getId() {
		return strId;
	}
}
