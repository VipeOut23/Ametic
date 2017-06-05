package de.jroeger.engine.timer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Timer extends Thread{
	
	/**
	 * saves running state
	 */
	private boolean running;
	
	/**
	 * saves events
	 */
	private List<Entry<Long, Entry<Timeable, Object>>> events;
	
	/**
	 * saves singleton instance
	 */
	private static Timer instance;
	
	private Timer() {
		super("timer");
		
		events = new ArrayList<>();
		
		this.start();
	}
	
	/**
	 * generic getInstance
	 * @return instance
	 */
	public synchronized static Timer getInstance() {
		if(instance == null) instance = new Timer();
		return instance;
	}
	
	public synchronized void stopThread() {
		running = false;
	}
	
	/**
	 * adds an event to the timeline
	 * @param timeable the object calling this
	 * @param param a custom parameter can be null
	 * @param in the time in ms until the event is triggered
	 */
	public void addEvent(Timeable timeable, Object param, int in) {
		long currTime = System.currentTimeMillis();
		
		synchronized (this) {
			Entry<Timeable, Object> entry = new AbstractMap.SimpleEntry<Timeable, Object>(timeable, param);
			Entry<Long, Entry<Timeable, Object>> entry2 = new AbstractMap.SimpleEntry<Long, Entry<Timeable, Object>>(currTime + in, entry);
			
			events.add(entry2);
		}
	}
	
	/**
	 * timer
	 */
	public void run() {
		this.running = true;
		
		long currTime;
		
		while(running) {
			currTime = System.currentTimeMillis();
			
			//check events
			synchronized (this) {
				for(int i = 0; i < events.size(); i++) {
					long l = events.get(i).getKey();
					if(l <= currTime) {
						//call event
						Entry<Timeable, Object> entry = events.get(i).getValue();
						entry.getKey().timedEventFinished(entry.getValue());
						events.remove(i);
					}
				}
			}
			
			//save cpu time
			try {
				sleep(1);
			} catch (InterruptedException e) {}
		}
	}
	
}
