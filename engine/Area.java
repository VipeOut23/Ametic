package de.jroeger.engine;

/**
 * defines a clickable area
 * @author Jonas
 *
 */
public class Area {
	/**
	 * saves the x - startpoint of the area
	 */
	private int x;
	
	/**
	 * saves the y - startpoint of the area
	 */
	private int y;
	
	/**
	 * saves the x - range of the area
	 */
	private int xr;
	
	/**
	 * saves the y - range of the area
	 */
	private int yr;

	/**
	 * saves the area id
	 */
	private String id;
	
	/**
	 * saves the state if this area is enabled or not
	 */
	private boolean enabled;
	
	public Area(int x, int y, int xr, int yr, String id) {
		this.x = x;
		this.y = y;
		this.xr = xr;
		this.yr = yr;
		this.id = id;
		this.enabled = false;
	}
	
	
	public boolean isInArea(int x, int y) {
		boolean is = false;
		if(x >= this.x && x <= this.x+xr) {
			if(y >= this.y && y <= this.y+yr) {
				is = true;
			}
		}
		return is;
	}


	/*
	 * Getter & Setter
	 */
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
