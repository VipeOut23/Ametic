package de.jroeger.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * this represents a ServerThread managing incoming connections as a new thread
 * @author Jonas
 *
 */
public class Server extends Thread{
	public static final int SERVER_PORT = 43344;
	
	private boolean bConnectorRunning;
	
	/**
	 * saves all the existing connections
	 */
	private List<Connection> lConConnectionThreads;
	
	/**
	 * saves the used socket
	 */
	private ServerSocket svsServer;
	
	/**
	 * saves the singleton instance
	 */
	private static Server instance;
	
	private Server(String name) {
		super(name);
		this.lConConnectionThreads = new ArrayList<>();
		
		//start socket
		openServerSocket();
		
		//run connector thread
		System.out.println("Starting up Server!");
		this.start();
	}
	
	/**
	 * Generic getInstance()
	 * @return current instance
	 */
	public static Server getInstance() {
		if(instance == null)
			instance = new Server("server");
		return instance;
	}
	
	/**
	 * used to open and bind a new ServerSocket to the SERVER_PORT
	 */
	private void openServerSocket() {
		//Open socket
		try {
			this.svsServer = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			throw new RuntimeException("Couldn'd bind ServerSocket to Port " + SERVER_PORT, e);
		}
	}
	
	public void stopThread() {
		System.out.println("Stopping Server!");
		for(Connection con : lConConnectionThreads) {
			con.stopConnection();
		}
		lConConnectionThreads.clear();
		
		this.bConnectorRunning = false;
		
		//close socket
		try {
			this.svsServer.close();
		} catch (IOException e) {
			throw new RuntimeException("Couldn't close Socket!", e);
		}
	}
	
	/**
	 * the connector thread of the server
	 */
	public void run() {
		bConnectorRunning = true;
		while(bConnectorRunning) {
			//setup a new Client Socket
			Socket clientSocket = null;
			
			//awaiting new Connections
			try {
				clientSocket = svsServer.accept();
			} catch (IOException e) {
				//occurs when the server stops
			}
			
			//cleanup dead connections
			for(int i = 0; i < lConConnectionThreads.size(); i++) {
				Connection con = lConConnectionThreads.get(i);
				if(!con.isAlive()) lConConnectionThreads.remove(i);
			}
			
			//Register the new ConnectionThread
			if(clientSocket != null) {
				if(clientSocket.isConnected()) {
					lConConnectionThreads.add(new Connection(clientSocket, "connection" + lConConnectionThreads.size()));
				}
			}
		}

		System.out.println("Stopped Server");
	}
}
