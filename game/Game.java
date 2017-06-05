package de.jroeger.game;

import de.jroeger.engine.GUI;
import de.jroeger.engine.SpritePanel;
import de.jroeger.net.ProtocolContainer;

/**
 * this class represents the game logic
 * @author Jonas
 *
 */
public class Game {
	
	/**
	 * saves the current gamePhase
	 * 1 - Tic
	 * 2 - Tac
	 * 3 - Toe
	 */
	private int gamePhase;
	
	/**
	 * saves which player is playing right now
	 */
	private boolean playerTurn;
	
	/**
	 * saves all fields 1-9
	 */
	private Field[] fields;
	
	/**
	 * saves gui instance
	 * used to mpodify gui elements
	 */
	public GUI gui;
	
	public Game(GUI gui, boolean start) {
		this.gui = gui;
		
		playerTurn = start;
		
		//set gamephase
		gamePhase = 1;
		gui.getCurrentStage().getSpPanel().getAnimation("htic").startAnimation(true);
		if(!start) gui.getCurrentStage().getSpPanel().getAnimation("wfo").startAnimation(true);
		
		//setup fields
		fields = new Field[9];
		for(int i = 0; i < fields.length; i++) {
			fields[i] = new Field(i+1);
		}
		
		//cleanup field
		SpritePanel sp = gui.getCurrentStage().getSpPanel();
		sp.getAnimation("htic").startAnimation(true);
		sp.getAnimation("htac").initializeAnimation();
		sp.getAnimation("htoe").initializeAnimation();
		
		for(int i = 1; i < 10; i++) {
			sp.getSprite("tac" + i).setbRender(false);
			sp.getSprite("tic" + i).setbRender(false);
		}
		
	}
	
	/*
	 * game logic
	 */
	
	private void doTic(Field f) {
		if(f.isEmpty()) {
			f.setValue(Field.VALUE_X);
			
			//send action
			if(playerTurn) {
				gui.getEngine().getConnector().send(ProtocolContainer.DO, 
						String.valueOf(f.getIndex()));
				gui.getCurrentStage().getSpPanel().getAnimation("wfo").startAnimation(true);
			}
			
			//update gui
			gui.getCurrentStage().getSpPanel().getAnimation("htic").initializeAnimation();
			gui.getCurrentStage().getSpPanel().getAnimation("htac").startAnimation(true);
			gui.getCurrentStage().getSpPanel().getSprite("tic" + f.getIndex()).setbRender(true);
			
			gamePhase = 2;
			playerTurn = !playerTurn;
		}
	}
	
	private void doTac(Field f) {
		if(f.isEmpty()) {
			f.setValue(Field.VALUE_O);
			
			//send action
			if(playerTurn) {
				gui.getEngine().getConnector().send(ProtocolContainer.DO, 
						String.valueOf(f.getIndex()));
				gui.getCurrentStage().getSpPanel().getAnimation("wfo").startAnimation(true);
			}
			
			//update gui
			gui.getCurrentStage().getSpPanel().getAnimation("htac").initializeAnimation();
			gui.getCurrentStage().getSpPanel().getAnimation("htoe").startAnimation(true);
			gui.getCurrentStage().getSpPanel().getSprite("tac" + f.getIndex()).setbRender(true);
			
			gamePhase = 3;
			playerTurn = !playerTurn;
		}
	}
	
	private void doToe(Field f) {
		if(!f.isEmpty()) {
			f.setValue(Field.VALUE_VOID);
			
			//send action
			if(playerTurn) {
				gui.getEngine().getConnector().send(ProtocolContainer.DO, 
						String.valueOf(f.getIndex()));
				gui.getCurrentStage().getSpPanel().getAnimation("wfo").startAnimation(true);
			}
			
			//update gui
			gui.getCurrentStage().getSpPanel().getAnimation("htoe").initializeAnimation();
			gui.getCurrentStage().getSpPanel().getAnimation("htic").startAnimation(true);
			gui.getCurrentStage().getSpPanel().getSprite("tac" + f.getIndex()).setbRender(false);
			gui.getCurrentStage().getSpPanel().getSprite("tic" + f.getIndex()).setbRender(false);
			
			gamePhase = 1;
			playerTurn = !playerTurn;
		}
	}
	
	/**
	 * used to process a click
	 * @param field field no. 1-9
	 */
	public void click(int field) {
		if(field > 8 || field < 0) return; //Invalid field no
		if(!playerTurn) {
			System.out.println("nenene"); return; //not the players turn
		}
		switch(gamePhase) {
		case 1: doTic(fields[field]); break;
		case 2: doTac(fields[field]); break;
		case 3: doToe(fields[field]); break;
		}
	}
	
	/**
	 * used to process the opponents click
	 * @param field
	 */
	public void doOpponentClick(int field) {
		if(field > 8 || field < 0) return; //Invalid field no
		if(playerTurn) return; //not the opponent players turn
		switch(gamePhase) {
		case 1: doTic(fields[field]); break;
		case 2: doTac(fields[field]); break;
		case 3: doToe(fields[field]); break;
		}
	}
}
