package de.jroeger.server.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * this class manages the lifetime of every connection signd in here
 * if the connection entry is false after the given time -> the connection gets destroyed
 * @author Jonas
 *
 */
public class PingManager extends Thread{
	/**
	 * saves the time until a connection times out
	 */
	public static final int TIMEOUT = 6000;
	
	
	/**
	 * saves all connections,
	 * the boolean is to identify if the connection has pinged in this iteration
	 */
	private volatile Map<Connection, Boolean> mcbActiveConnections;
	
	/**
	 * saves running state of the thread
	 */
	private boolean running;
	
	private static PingManager instance;
	
	private PingManager() {
		super("pingmanager");
		mcbActiveConnections = new HashMap<>();
		
		this.start();
	}
	
	public static synchronized PingManager getInstance() {
		if(instance == null) instance = new PingManager();
		return instance;
	}
	
	/**
	 * this must get called by a new connection
	 * @param con the connection
	 */
	public synchronized void signIn(Connection con) {
		mcbActiveConnections.put(con, true);
	}
	
	/**
	 * this is called by a connection if it was pinged
	 * @param con the connection
	 */
	public synchronized void hasPinged(Connection con) {
		mcbActiveConnections.replace(con, true);
	}
	
	/**
	 * run method of the thread
	 */
	public void run() {
		running = true;
		List<Connection> invalidCons;
		while(running) {
			invalidCons = new ArrayList<>();
			
			//check all connections
			for(Entry<Connection, Boolean> entry : mcbActiveConnections.entrySet()) {
				if(!entry.getValue())
					invalidCons.add(entry.getKey());
				entry.setValue(false);
			}
			
			//process invalid connections
			for(int i = 0; i < invalidCons.size(); i++) {
				Connection con = invalidCons.get(i);
				mcbActiveConnections.remove(con);
				con.stopConnection();
			}
			
			//wait
			try {
				Thread.sleep(TIMEOUT);
			} catch (InterruptedException e) {/*ignore*/}
		}
	}
}
