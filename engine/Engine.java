package de.jroeger.engine;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.jroeger.net.Connector;
import de.jroeger.net.ProtocolContainer;

public class Engine {

	/**
	 * saves the connector instance
	 */
	private Connector connector;
	
	/**
	 * saves the gui instance
	 */
	private GUI gui;
	
	
	public Engine() {
		//set system theme
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//start gui
		this.gui = new GUI(this);
		//start connector
		this.connector = new Connector("internetz.space", 43344, this);
		//do game
		this.run();
	}
	
	/**
	 * main thread
	 */
	public void run() {
		//waiting for connection
		gui.switchStage("waiting");
		gui.getCurrentStage().spPanel.getAnimation("wfs").startAnimation(true);
		while(connector.isfWaiting()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		
		//mod gui to sign in
		gui.getCurrentStage().spPanel.getAnimation("check").startAnimation(false);
		gui.getCurrentStage().spPanel.getAnimation("wfs").initializeAnimation();
		gui.getCurrentStage().spPanel.getAnimation("sign").startAnimation(true);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
		
		//get playername
		String name = (String)JOptionPane.showInputDialog(gui.getJFrame(), "Your name:", "Input required", JOptionPane.QUESTION_MESSAGE);
		
		//sign in
		connector.send(ProtocolContainer.SIGNIN, name);
		
		//waiting for sign in
		while(!connector.isfSignedIn()) {
			System.out.println("Signing in...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		
		//mod gui to join
		gui.getCurrentStage().spPanel.getAnimation("check2").startAnimation(false);
		gui.getCurrentStage().spPanel.getAnimation("sign").initializeAnimation();
		gui.getCurrentStage().spPanel.getSprite("join").setbRender(true);
		gui.getCurrentStage().mStrAAreas.get("join").setEnabled(true);
		gui.getCurrentStage().spPanel.getSprite("quick").setbRender(true);
		gui.getCurrentStage().mStrAAreas.get("quick").setEnabled(true);
	}
	
	/**
	 * called by the connector thread to handle incoming packages
	 * @param protCont the package
	 */
	public synchronized void handlePackage(ProtocolContainer protCont) {
		System.out.println(protCont.toJSONObject().toString());
		gui.getCurrentStage().handlePackage(protCont);
	}
	
	/**
	 * main
	 * @param args args
	 */
	public static void main(String[] args) {
		new Engine();
	}

	/**
	 * used by the stages to send messages
	 * @return connector
	 */
	public Connector getConnector() {
		return this.connector;
	}
	
}
