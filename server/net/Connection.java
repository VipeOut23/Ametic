package de.jroeger.server.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import de.jroeger.server.net.ProtocolContainer;
import de.jroeger.server.engine.Client;
import de.jroeger.server.engine.Engine;

/**
 * this class represents a client connection
 * saves a Connected socket and manages its connection in a thread
 * @author Jonas
 *
 */
public class Connection extends Thread{

	/**
	 * saves the Objects to send
	 */
	private List<ProtocolContainer> qSend;
	
	/**
	 * saves the client connected to the socket
	 */
	private Client clClient;
	
	/**
	 * saves the state of the existing connection
	 */
	private boolean connectionAlive;
	
	/**
	 * saves the current pingManager instance
	 */
	private PingManager pingManager;
	
	/**
	 * this saves a connected client socket
	 */
	private Socket scClient;
	
	/**
	 * saves engine instance
	 */
	private Engine engine;
	
	/**
	 * setting up the connection with an allready connected socket
	 * @param socket a connected socket
	 */
	public Connection(Socket socket, String name) {
		super(name);
		this.scClient 		= socket;
		this.qSend	  		= new ArrayList<>();
		this.engine	  		= Engine.getInstance();
		this.pingManager	= PingManager.getInstance();
		
		this.start();
	}
	
	public void stopConnection() {
		this.connectionAlive = false;
		
		//Close socket
		try {
			this.scClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * processing the communication
	 */
	public void run() {
		connectionAlive = true;
		
		//Saves the input object
		ProtocolContainer protCont;
		
		//Initialize Streams
		DataInputStream  clientIn = null;
		DataOutputStream clientOut = null;
		
		//Load Streams
		try {
			clientIn  = new DataInputStream(scClient.getInputStream());
			clientOut = new DataOutputStream(scClient.getOutputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//sign into the pingmanager
		this.pingManager.signIn(this);
		
		//Loop
		String in = "";
		while(connectionAlive) {
			
			//Read input
			try {
				if(clientIn.available() > 0) in = clientIn.readUTF();
				//test for timeouts
				if(scClient.isClosed()) connectionAlive = false;
			} catch (IOException e) {
				//Timed out
				clClient.timedOut();
				connectionAlive = false;
			}
			
			if(!in.equals("")) {
				//Process input
				protCont = new ProtocolContainer(in);
				
				if(protCont.isValid()) {
					//Perform request
					if(protCont.getHeader() == ProtocolContainer.PING) {
						this.pingManager.hasPinged(this);
					}
					else if(protCont.getHeader() == ProtocolContainer.SIGNIN) {
						//Get client
						clClient = engine.requestClient(protCont.getContent());
						//reference this
						clClient.setConnection(this);
						//Response
						addResponse(ProtocolContainer.SIGNIN, clClient.getSessionID());
					}
					else if(protCont.getHeader() == ProtocolContainer.QUICK) {
						//Process Click
						if(clClient == null) addError("Not signed in!");
						else {
							//join quickplay q
							clClient.joinQuick();
						}
					}
					else if(protCont.getHeader() == ProtocolContainer.DO) {
						//Process Click
						if(clClient == null) addError("Not signed in!");
						else {
							clClient.doAction(protCont.getContent());
						}
					}
					else if(protCont.getHeader() == ProtocolContainer.JOIN) {
						if(clClient == null) addError("Not signed in!");
						else {
							//Join a room
							clClient.joinRoom(protCont.getContent());
						}
					}
					else if(protCont.getHeader() == ProtocolContainer.DC) {
						//perform disconnect
						qSend.add(protCont);
						connectionAlive = false;
					}
				}
				in = "";
			}
			
			//Send response
			while(!qSend.isEmpty()) {
				//get JSON String
				String out = qSend.get(0).toJSONObject().toString();
				qSend.remove(0);
				
				//Send
				try {
					clientOut.writeUTF(out);
				} catch (IOException e) {
					//Timed out
					clClient.timedOut();
					connectionAlive = false;
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e){}
		}
		
		//should be the timeout case
		if(clClient != null)clClient.timedOut();
		
		//Cleanup
		try {
			clientIn.close();
			clientOut.close();
		} catch (IOException e) {/*Ignore*/}
		
		System.out.println("Connection closed!");
	}
	
	/**
	 * used to add a new Response to the send queue
	 * @param header header
	 * @param content content
	 */
	public void addResponse(int header, String content) {
		qSend.add(new ProtocolContainer(header, content));
	}
	
	/**
	 * used to add a new Error message to the send queue
	 * @param msg the error detail
	 */
	public void addError(String msg) {
		qSend.add(new ProtocolContainer(ProtocolContainer.ERROR, msg));
	}
}
