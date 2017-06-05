package de.jroeger.engine;

import java.awt.event.MouseEvent;

public class MouseListener implements java.awt.event.MouseListener{

	/**
	 * saves instance of the gameengine
	 */
	private GUI engine;
	
	public MouseListener(GUI parentEngine) {
		engine = parentEngine;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		engine.processMouseClick(e.getX(), e.getY());
		
	}

}
