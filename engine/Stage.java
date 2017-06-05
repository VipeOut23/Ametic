package de.jroeger.engine;

import java.util.LinkedHashMap;
import java.util.Map;

import de.jroeger.net.ProtocolContainer;

public abstract class Stage {
	/**
	 * saves all areas corresponding to this Stage
	 */
	protected Map<String, Area>mStrAAreas;
	
	/**
	 * saves the spritepanel
	 */
	protected SpritePanel spPanel;
	
	protected Engine e;
	
	public Stage(Engine e) {
		this.e 		 = e;
		this.mStrAAreas = new LinkedHashMap<>();
		this.spPanel = new SpritePanel(GUI.WINDOW_WIDTH, GUI.WINDOW_HEIGHT);
		
		this.initialize();
	}
	
	/**
	 * used to init the stage
	 */
	protected abstract void initialize();
	
	/**
	 * used to handle a click event
	 * @param x mouse x coord
	 * @param y mouse y coord
	 */
	public abstract void handleClick(int x, int y);

	public abstract void handlePackage(ProtocolContainer protCont);
	
	protected void send(int header, String content) {
		e.getConnector().send(header, content);
	}
	
	public SpritePanel getSpPanel() {
		return spPanel;
	}
	
}
