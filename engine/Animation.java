package de.jroeger.engine;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import de.jroeger.engine.timer.Timeable;
import de.jroeger.engine.timer.Timer;

public class Animation implements Timeable, Cloneable{
	
	/**
	 * indicators for the animation process
	 */
	private boolean loop, active;
	
	/**
	 * saves the currently displayed sprite
	 */
	private Sprite sprCurrentSprite;
	
	/**
	 * saves the timer instance
	 */
	private Timer timer;
	
	/**
	 * the frame this animation is on
	 * used to update the frame
	 */
	private GUI gui;
	
	/**
	 * saves the animation timeline 
	 */
	private List<Entry<Integer, Sprite>> timeline;
	
	/**
	 * saves the current timeline index
	 */
	private int timelineIndex;
	
	public Animation(GUI gui) {
		this(new ArrayList<>(), gui);
	}
	
	public Animation(List<Entry<Integer, Sprite>> timeline, GUI gui) {
		this.timeline = timeline;
		this.gui = gui;
		this.timer = Timer.getInstance();
	}
	
	/**
	 * used to generate a new animation based of the parameter
	 * @param anim the to be cloned animation
	 */
	public Animation(Animation anim) {
		this.gui  = anim.gui;
		this.timeline = new ArrayList<>();
		this.timer = Timer.getInstance();
		
		//copy timeline
		for(int i = 0; i < anim.timeline.size(); i++) {
			Entry<Integer, Sprite> entry = anim.timeline.get(i);
			
			Integer in = new Integer(entry.getKey());
			Sprite spr = new Sprite(entry.getValue());
			
			this.timeline.add(i, new SimpleEntry<Integer, Sprite>(in, spr));
		}
		
	}
	
	/**
	 * adds a new KeyFrame to the animation timeline 
	 * @param dur the duration the sprite lasts in ms
	 * @param sprite the sprite
	 */
	public void addKeyFrame(int dur, Sprite sprite) {
		this.timeline.add(new SimpleEntry<Integer, Sprite>(dur, sprite));
	}
	
	/**
	 * used to start the animation
	 * @param loop should it loop?
	 */
	public synchronized void startAnimation(boolean loop) {
		this.loop = loop;
		this.active = true;
		this.timelineIndex = 0;
		
		//display first sprite
		Entry<Integer, Sprite> entry = timeline.get(0);
		sprCurrentSprite = entry.getValue();
		
		advanceTimelineIndex();
		
		//register new sprite
		if(active) {
			timer.addEvent(this, timeline.get(timelineIndex).getValue(), entry.getKey());
		}
		
	}
	
	public synchronized void initializeAnimation() {
		active = false;
		this.timelineIndex = 0;
		
		//display first sprite
		Entry<Integer, Sprite> entry = timeline.get(0);
		sprCurrentSprite = entry.getValue();
		
	}
	
	public synchronized void stopAnimation() {
		active = false;
		sprCurrentSprite = null;
	}
	
	public void moveTo(int x, int y) {
		for(Entry<Integer, Sprite> entry : timeline) {
			entry.getValue().moveTo(x, y);
		}
	}
	
	/**
	 * used to count
	 */
	private void advanceTimelineIndex() {
		//if a loop is required
		if(timelineIndex >= timeline.size()-1) {
			if(loop)
				timelineIndex = 0;
			else 
				active = false;
		}else
			timelineIndex++;
	}
	
	@Override
	public void timedEventFinished(Object param) {
	if(active) {
			//set sprite
			Sprite spr = (Sprite) param;
			if(spr != null)
				sprCurrentSprite = spr;
			
			gui.render();
			
			//set new timer event
			Entry<Integer, Sprite> entry = timeline.get(timelineIndex);
			advanceTimelineIndex();
			timer.addEvent(this, timeline.get(timelineIndex).getValue(), entry.getKey());
		}
	}
	
	/**
	 * used to get the current sprite
	 * @return sprite
	 */
	public Sprite getCurrentSprite() {
		return sprCurrentSprite;
	}
	
	public Object clone()throws CloneNotSupportedException{  
		return super.clone();
	}  
}
