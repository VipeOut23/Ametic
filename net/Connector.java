package de.jroeger.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import de.jroeger.engine.Engine;
import de.jroeger.engine.timer.Timer;
import de.jroeger.engine.timer.Timeable;

public class Connector extends Thread implements Timeable{
	/**
	 * connection state flags
	 */
	private boolean fConnected, fWaiting, fFailedToConnect, fSignedIn;
	
	/**
	 * saves the current sessionID used to reconnect
	 */
	private String strSessionID;
	
	/**
	 * server ip
	 */
	private String strIP;
	
	/**
	 * server port
	 */
	private int iPort;
	
	/**
	 * saves the Objects to send
	 */
	private List<ProtocolContainer> qSend;
	
	/**
	 * saves the network socket
	 */
	private Socket sock;
	
	/**
	 * saves the engine instance owning the connector
	 */
	private Engine engine;
	
	/**
	 * saves timer instance
	 */
	private Timer timer;
	
	/**
	 * saves the thread state
	 */
	private boolean running;
	
	public Connector(String ipAddress, int port, Engine e) {
		this.iPort 			= port;
		this.strIP 			= ipAddress;
		this.engine 		= e;
		this.strSessionID 	= "";
		this.timer 			= Timer.getInstance();
		
		//Set flags
		fConnected  = false;
		fWaiting	= true;
		running		= true;
		fSignedIn	= false;
		
		qSend = new ArrayList<>();
		
		//start thread
		this.start();
	}
	
	/**
	 * used to add a new message to the send queue
	 */
	public synchronized void send(int header, String content) {
		qSend.add(new ProtocolContainer(header, content));
	}
	
	/**
	 * used to stop the thread
	 */
	public synchronized void stopConnection() {
		this.running = false;
	}
	
	/**
	 * net thread
	 */
	public void run() {
		fConnected  = false;
		fWaiting	= true;
		running		= true;
		fSignedIn	= false;
		
		//saves the input as a containter
		ProtocolContainer protCont;
		
		//Initialize Streams
		DataInputStream sockIn = null;
		DataOutputStream sockOut = null;
		
		//Connector loop
		String in = "";
		while(running) {
			//Try to connect
			try {
				sock = new Socket(strIP, iPort);
				if(sock.isConnected()) fConnected = true;
			} catch (IOException e) {
				this.fFailedToConnect = true;
			}
			
			//Setup streams
			try {
				if(fConnected) {
					sockIn = new DataInputStream(sock.getInputStream());
					sockOut = new DataOutputStream(sock.getOutputStream());
				}
			} catch (IOException e) {
				fConnected = false;
			}
			
			//initialize ping loop
			this.timer.addEvent(this, null, 5000);
			
			//While connected loop
			while(running && fConnected) {
				fWaiting = false;
				//if a session exists - try to reconnect
				if(!fSignedIn && strSessionID.length() > 0) {
					//perform a reconnect
					qSend.add(new ProtocolContainer(ProtocolContainer.SIGNIN, strSessionID));
					strSessionID = "";
				}
				
				//read input if available
				try {
					if(sockIn.available() > 0) in = sockIn.readUTF();
				} catch (IOException e) {
					fConnected = false;
				}
				
				//if there is any input
				if(!in.equals("")) {
					//process input
					protCont = new ProtocolContainer(in);
					
					if(protCont.isValid()) {
						//process input
						if(protCont.getHeader() == ProtocolContainer.SIGNIN) {
							//get session ID
							this.strSessionID = protCont.getContent();
							fSignedIn = true;
						}else {
							//let the engine handle the package
							this.engine.handlePackage(protCont);
						}
					}
					
					in = "";
				}
				
				//send messages if available
				while(!qSend.isEmpty()) {
					//get JSON String
					String out = qSend.get(0).toJSONObject().toString();
					qSend.remove(0);
					
					//Send
					try {
						sockOut.writeUTF(out);
					} catch (IOException e) {
						//connection lost
						fConnected = false;
					}
				}
				
				//Check connection
				if(!sock.isConnected()) fConnected = false;
				
				//save cpu time
				try {
					sleep(500);
				} catch (InterruptedException e) {}
			}
			//no connection
			fSignedIn = false;
			fWaiting = true;
		}
		
		//cleanup
		try {
			sock.close();
			sockIn.close();
			sockOut.close();
		} catch (IOException e) {/*Ignore*/}
		
		fWaiting 	= true;
		fConnected 	= false;
	}
	
	
	/*
	 * Getter
	 */
	public synchronized boolean isfConnected() {
		return fConnected;
	}

	public synchronized boolean isfWaiting() {
		return fWaiting;
	}

	public synchronized boolean isfFailedToConnect() {
		return fFailedToConnect;
	}
	
	public synchronized boolean isfSignedIn() {
		return fSignedIn;
	}

	@Override
	public void timedEventFinished(Object param) {
		synchronized (this) {
			this.send(ProtocolContainer.PING, "");
			this.timer.addEvent(this, null, 5000);
		}
	}
}
