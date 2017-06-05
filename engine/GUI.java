package de.jroeger.engine;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.jroeger.game.Game;
import de.jroeger.net.ProtocolContainer;

public class GUI {
	/*
	 * Define Params
	 */
	public static final String 	TITLE				= "Ametic v1.0";
	public static final int 	WINDOW_WIDTH		= 501; // /3
	public static final int 	WINDOW_HEIGHT		= 501;
	public static final int		STATUSBAR_HEIGHT	= 90;
	
	/**
	 * saves game instance
	 */
	public Game game;
	
	/**
	 * saves instance of the SpritePanel
	 */
	private SpritePanel spPanel;
	
	/**
	 * saves intance of a mouselistener
	 */
	public MouseListener mlMouse;
	
	/**
	 * saves instance of the main window
	 */
	private JFrame jfFrame;

	/**
	 * holds all available panels
	 */
	private JPanel jpMain;
	
	/**
	 * saves the currently selected stage
	 */
	private Stage currentStage;

	/**
	 * saves all stages
	 */
	private Map<String, Stage> mStrStages;
	
	/**
	 * saves the currently running engine instance
	 */
	private Engine engine;
	
	public GUI(Engine e) {
		this.engine = e;
		this.game = null;
		mStrStages = new HashMap<>();
		
		//Construct Frame
		jfFrame = new JFrame();
		
		jfFrame.setTitle(TITLE);
		jfFrame.setResizable(false);
		jfFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//create panel
		jpMain = new JPanel();
		jpMain.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		jpMain.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		jpMain.setLayout(new CardLayout());
		
		initialize();
		
		jfFrame.pack();
		jfFrame.setLocationRelativeTo(null);
		jfFrame.setVisible(true);
		jfFrame.repaint();
	}
	
	/**
	 * Initializes Game instance
	 */
	public void initialize() {
		//Connect with MouseListener
		mlMouse = new MouseListener(this);
		jpMain.addMouseListener(mlMouse);
		
		//add panel to frame
		jfFrame.add(jpMain);
		
		/*
		 * setup stages
		 */
		//used to identify this object
		GUI currGUI = this;
		//Wait stage
		new Stage(this.engine) {
			@Override
			protected void initialize() {
				spPanel.add("background", new Sprite(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT,
						new Color(0x1f,0x1f,0x25,255), "back", true));
				
				String imgPath = "res/sprites/";
				
				//static sprites
				Sprite join = new Sprite(50, 250, imgPath + "opt/join.png", "join", false);
				Sprite quick = new Sprite(255, 250, imgPath + "opt/quick.png", "quick", false);
				
				//clickable areas
				Area aJoin = new Area(50, 250, 195, 195, "join");
				Area aQuick = new Area(255, 250, 195, 195, "quick");
				
				mStrAAreas.put(aJoin.getId(), aJoin);
				mStrAAreas.put(aQuick.getId(), aQuick);
				
				//animation sprites
				Sprite wfs0 = new Sprite(50, 60, imgPath + "wait/wfs0.png", "wfs0", true);
				Sprite wfs1 = new Sprite(50, 60, imgPath + "wait/wfs1.png", "wfs1", true);
				Sprite wfs2 = new Sprite(50, 60, imgPath + "wait/wfs2.png", "wfs2", true);
				Sprite wfs3 = new Sprite(50, 60, imgPath + "wait/wfs3.png", "wfs3", true);
				Sprite wfs4 = new Sprite(50, 60, imgPath + "wait/wfs4.png", "wfs4", true);
				
				Sprite ch0 = new Sprite(35, 65, imgPath + "check/ch0.png", "ch0", true);
				Sprite ch1 = new Sprite(35, 65, imgPath + "check/ch1.png", "ch1", true);
				Sprite ch2 = new Sprite(35, 65, imgPath + "check/ch2.png", "ch2", true);
				Sprite ch3 = new Sprite(35, 65, imgPath + "check/ch3.png", "ch3", true);
				Sprite ch4 = new Sprite(35, 65, imgPath + "check/ch4.png", "ch4", true);
				
				Sprite sign0 = new Sprite(50, 170, imgPath + "sign/sign0.png", "sign0", true);
				Sprite sign1 = new Sprite(50, 170, imgPath + "sign/sign1.png", "sign1", true);
				Sprite sign2 = new Sprite(50, 170, imgPath + "sign/sign2.png", "sign2", true);
				Sprite sign3 = new Sprite(50, 170, imgPath + "sign/sign3.png", "sign3", true);
				Sprite sign4 = new Sprite(50, 170, imgPath + "sign/sign4.png", "sign4", true);
				
				
				//animations
				Animation check = new Animation(currGUI);
				check.addKeyFrame(110, ch4);
				check.addKeyFrame(40, ch3);
				check.addKeyFrame(40, ch2);
				check.addKeyFrame(40, ch1);
				check.addKeyFrame(40, ch0);
				
				//copy check
				Animation check2 = new Animation(check);
				check2.moveTo(35, 175);
				
				Animation sign = new Animation(currGUI);
				sign.addKeyFrame(150, sign0);
				sign.addKeyFrame(150, sign1);
				sign.addKeyFrame(150, sign2);
				sign.addKeyFrame(150, sign3);
				sign.addKeyFrame(300, sign4);
				sign.addKeyFrame(150, sign3);
				sign.addKeyFrame(150, sign2);
				sign.addKeyFrame(150, sign1);
				sign.addKeyFrame(150, sign0);
				
				Animation wfs = new Animation(currGUI);
				wfs.addKeyFrame(150, wfs0);
				wfs.addKeyFrame(150, wfs1);
				wfs.addKeyFrame(150, wfs2);
				wfs.addKeyFrame(150, wfs3);
				wfs.addKeyFrame(300, wfs4);
				wfs.addKeyFrame(150, wfs3);
				wfs.addKeyFrame(150, wfs2);
				wfs.addKeyFrame(150, wfs1);
				wfs.addKeyFrame(150, wfs0);
				
				spPanel.add("wfs", wfs);
				spPanel.add("check", check);
				spPanel.add("check2", check2);
				spPanel.add("sign", sign);
				spPanel.add("join", join);
				spPanel.add("quick", quick);
				
				mStrStages.put("waiting", this);
			}
			//logical vars
			
			private String roomName;
			
			@Override
			public void handleClick(int x, int y) {
				for(Area area : mStrAAreas.values()) {
					if(area.isEnabled())
					if(area.isInArea(x, y)) {
						
						//if join area
						if(area.getId().equals("join")) {
							//request roomname
							roomName = (String)JOptionPane.showInputDialog(jfFrame,
									"Room name:", "Input required", JOptionPane.QUESTION_MESSAGE);
							//join room
							if(roomName != null) {
								this.send(ProtocolContainer.JOIN, roomName);
								switchStage("wait");
							}
						}
						else if(area.getId().equals("quick")) {
							this.send(ProtocolContainer.QUICK, "");
							switchStage("wait");
						}
						
					}
				}
			}

			@Override
			public void handlePackage(ProtocolContainer protCont) {
				
			}
		};
		
		//waiting stage
		new Stage(this.engine) {

			@Override
			protected void initialize() {
				spPanel.add("background", new Sprite(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT,
						new Color(0x1f,0x1f,0x25,255), "back", true));
				
				String imgPath = "res/sprites/";
				
				//waiting sprites
				Sprite wfoj = new Sprite(70, 200, imgPath+"wait/wfoj.png", "wait", true);
				
				spPanel.add("wfoj", wfoj);
				
				mStrStages.put("wait", this);
			}

			@Override
			public void handleClick(int x, int y) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void handlePackage(ProtocolContainer protCont) {
				if(protCont.getHeader() == ProtocolContainer.JOIN) {
					//go to full lobby
					System.out.println("Your opponent is: " + protCont.getContent());
					jfFrame.setTitle("Ametic - Play vs. " + protCont.getContent());
				}
				else if(protCont.getHeader() == ProtocolContainer.START) {
					switchStage("game");
					game = new Game(currGUI, protCont.getContent().equals("true"));
				}
			}
			
		};
		
		//Game stage
		new Stage(this.engine) {
			public void initialize() {
				final int AREA_HEIGHT = 133;
				final int AREA_WIDTH  = 162;
				
				//Setup areas
				//TODO-fix areas
				mStrAAreas.put("a1", new Area(0, STATUSBAR_HEIGHT, AREA_WIDTH, AREA_HEIGHT, "a1"));
				mStrAAreas.put("a2", new Area(AREA_WIDTH+5, STATUSBAR_HEIGHT, AREA_WIDTH, AREA_HEIGHT, "a2"));
				mStrAAreas.put("a3", new Area(AREA_WIDTH*2+10, STATUSBAR_HEIGHT, AREA_WIDTH, AREA_HEIGHT, "a3"));
				
				mStrAAreas.put("a4", new Area(0, STATUSBAR_HEIGHT + AREA_HEIGHT+5, AREA_WIDTH, AREA_HEIGHT, "a4"));
				mStrAAreas.put("a5", new Area(AREA_WIDTH+5, STATUSBAR_HEIGHT + AREA_HEIGHT+5, AREA_WIDTH, AREA_HEIGHT, "a5"));
				mStrAAreas.put("a6", new Area(AREA_WIDTH*2+10, STATUSBAR_HEIGHT + AREA_HEIGHT+5, AREA_WIDTH, AREA_HEIGHT, "a6"));
				
				mStrAAreas.put("a7", new Area(0, STATUSBAR_HEIGHT + AREA_HEIGHT*2+10, AREA_WIDTH, AREA_HEIGHT, "a7"));
				mStrAAreas.put("a8", new Area(AREA_WIDTH+5, STATUSBAR_HEIGHT + AREA_HEIGHT*2+10, AREA_WIDTH, AREA_HEIGHT, "a8"));
				mStrAAreas.put("a9", new Area(AREA_WIDTH*2+10, STATUSBAR_HEIGHT + AREA_HEIGHT*2+10, AREA_WIDTH, AREA_HEIGHT, "a9"));
				
				//enable areas
				for(Area a : mStrAAreas.values()) {
					a.setEnabled(true);
				}
				
				
				String imgPath = "res/sprites/";
				
				//setup sprites
				spPanel.add("background", new Sprite(0, 0, 501, 501, new Color(0x1f,0x1f,0x25,255), "back", true));
				spPanel.add("head_grid", new Sprite(0, 0, imgPath + "game/head_grid.png", "head_grid", true));
				spPanel.add("main_grid", new Sprite(0, STATUSBAR_HEIGHT, imgPath + "game/main_grid.png", "main_grid", true));
				
				//header
				Sprite htic0 = new Sprite(43,5, imgPath + "game/head/tic0.png", "htic0", true);
				Sprite htic1 = new Sprite(43,5, imgPath + "game/head/tic1.png", "htic1", true);
				Sprite htic2 = new Sprite(43,5, imgPath + "game/head/tic2.png", "htic2", true);
				Sprite htic3 = new Sprite(43,5, imgPath + "game/head/tic3.png", "htic3", true);
				Sprite htic4 = new Sprite(43,5, imgPath + "game/head/tic4.png", "htic4", true);
				
				Animation htic = new Animation(currGUI);
				htic.addKeyFrame(150, htic0);
				htic.addKeyFrame(150, htic1);
				htic.addKeyFrame(150, htic2);
				htic.addKeyFrame(150, htic3);
				htic.addKeyFrame(300, htic4);
				htic.addKeyFrame(150, htic3);
				htic.addKeyFrame(150, htic2);
				htic.addKeyFrame(150, htic1);
				htic.addKeyFrame(150, htic0);
				spPanel.add("htic",htic);
				
				Sprite htac0 = new Sprite(212, 7, imgPath + "game/head/tac0.png", "htac0", true);
				Sprite htac1 = new Sprite(212, 7, imgPath + "game/head/tac1.png", "htac1", true);
				Sprite htac2 = new Sprite(212, 7, imgPath + "game/head/tac2.png", "htac2", true);
				Sprite htac3 = new Sprite(212, 7, imgPath + "game/head/tac3.png", "htac3", true);
				Sprite htac4 = new Sprite(212, 7, imgPath + "game/head/tac4.png", "htac4", true);
				
				Animation htac = new Animation(currGUI);
				htac.addKeyFrame(150, htac0);
				htac.addKeyFrame(150, htac1);
				htac.addKeyFrame(150, htac2);
				htac.addKeyFrame(150, htac3);
				htac.addKeyFrame(300, htac4);
				htac.addKeyFrame(150, htac3);
				htac.addKeyFrame(150, htac2);
				htac.addKeyFrame(150, htac1);
				htac.addKeyFrame(150, htac0);
				spPanel.add("htac", htac);
				
				Sprite htoe0 = new Sprite(378, 5, imgPath + "game/head/toe0.png", "htoe0", true);
				Sprite htoe1 = new Sprite(378, 5, imgPath + "game/head/toe1.png", "htoe1", true);
				Sprite htoe2 = new Sprite(378, 5, imgPath + "game/head/toe2.png", "htoe2", true);
				Sprite htoe3 = new Sprite(378, 5, imgPath + "game/head/toe3.png", "htoe3", true);
				Sprite htoe4 = new Sprite(378, 5, imgPath + "game/head/toe4.png", "htoe4", true);
				
				Animation htoe = new Animation(currGUI);
				htoe.addKeyFrame(150, htoe0);
				htoe.addKeyFrame(150, htoe1);
				htoe.addKeyFrame(150, htoe2);
				htoe.addKeyFrame(150, htoe3);
				htoe.addKeyFrame(300, htoe4);
				htoe.addKeyFrame(150, htoe3);
				htoe.addKeyFrame(150, htoe2);
				htoe.addKeyFrame(150, htoe1);
				htoe.addKeyFrame(150, htoe0);
				spPanel.add("htoe", htoe);
				
				htic.initializeAnimation();
				htac.initializeAnimation();
				htoe.initializeAnimation();
				
				//game tokens
				Texture tx0 = new Texture(imgPath + "game/tic.png");
				Texture to0 = new Texture(imgPath + "game/tac.png");
				
				spPanel.add("tic1", new Sprite(30, 105, tx0, "tic1", false));
				spPanel.add("tic2", new Sprite(198, 105, tx0, "tic2", false));
				spPanel.add("tic3", new Sprite(367, 105, tx0, "tic3", false));
				
				spPanel.add("tic4", new Sprite(30, 240, tx0, "tic4", false));
				spPanel.add("tic5", new Sprite(198, 240, tx0, "tic5", false));
				spPanel.add("tic6", new Sprite(367, 240, tx0, "tic6", false));
				
				spPanel.add("tic7", new Sprite(30, 380, tx0, "tic7", false));
				spPanel.add("tic8", new Sprite(198, 380, tx0, "tic8", false));
				spPanel.add("tic9", new Sprite(367, 380, tx0, "tic9", false));
				
				
				spPanel.add("tac1", new Sprite(30, 105, to0, "tac1", false));
				spPanel.add("tac2", new Sprite(198, 105, to0, "tac2", false));
				spPanel.add("tac3", new Sprite(367, 105, to0, "tac3", false));
				
				spPanel.add("tac4", new Sprite(30, 240, to0, "tac4", false));
				spPanel.add("tac5", new Sprite(198, 240, to0, "tac5", false));
				spPanel.add("tac6", new Sprite(367, 240, to0, "tac6", false));
				
				spPanel.add("tac7", new Sprite(30, 380, to0, "tac7", false));
				spPanel.add("tac8", new Sprite(198, 380, to0, "tac8", false));
				spPanel.add("tac9", new Sprite(367, 380, to0, "tac9", false));
				
				//wait screen
				Sprite wfo0 = new Sprite(0, 0, imgPath + "game/wfo0.png", "wfo0", true);
				Sprite wfo1 = new Sprite(0, 0, imgPath + "game/wfo1.png", "wfo1", true);
				Sprite wfo2 = new Sprite(0, 0, imgPath + "game/wfo2.png", "wfo2", true);
				Sprite wfo3 = new Sprite(0, 0, imgPath + "game/wfo3.png", "wfo3", true);
				Sprite wfo4 = new Sprite(0, 0, imgPath + "game/wfo4.png", "wfo4", true);
				
				Animation wfo = new Animation(currGUI);
				wfo.addKeyFrame(150, wfo0);
				wfo.addKeyFrame(150, wfo1);
				wfo.addKeyFrame(150, wfo2);
				wfo.addKeyFrame(150, wfo3);
				wfo.addKeyFrame(300, wfo4);
				wfo.addKeyFrame(150, wfo3);
				wfo.addKeyFrame(150, wfo2);
				wfo.addKeyFrame(150, wfo1);
				wfo.addKeyFrame(150, wfo0);
				spPanel.add("wfo", wfo);
				
				//register stage
				mStrStages.put("game", this);
			}
			
			public void handleClick(int x, int y) {
				for(Area area : mStrAAreas.values()) {
					if(area.isEnabled())
					if(area.isInArea(x, y)) {
						synchronized (game) {
							if(game != null) {
								switch(area.getId()) {
								case "a1": game.click(0); break;
								case "a2": game.click(1); break;
								case "a3": game.click(2); break;
								case "a4": game.click(3); break;
								case "a5": game.click(4); break;
								case "a6": game.click(5); break;
								case "a7": game.click(6); break;
								case "a8": game.click(7); break;
								case "a9": game.click(8); break;
								}
							}
						}
					}
				}
			}

			@Override
			public void handlePackage(ProtocolContainer protCont) {
				if(protCont.getHeader() == ProtocolContainer.DO) {
					synchronized (game) {
						if(game != null) {
							game.doOpponentClick(Integer.parseInt(protCont.getContent())-1);
							spPanel.getAnimation("wfo").stopAnimation();
						}
					}
				}
				if(protCont.getHeader() == ProtocolContainer.DC) {
					JOptionPane.showMessageDialog(jfFrame, protCont.getContent(),
							"Opponent left", JOptionPane.WARNING_MESSAGE);
					jfFrame.setTitle(TITLE);
					switchStage("waiting");
				}
				if(protCont.getHeader() == ProtocolContainer.END) {
					spPanel.getAnimation("wfo").stopAnimation();
					boolean win = protCont.getContent().equals("true");
					JOptionPane.showMessageDialog(jfFrame, (win)? "You won." : "You lost.",
							"Game has ended", JOptionPane.INFORMATION_MESSAGE);
					jfFrame.setTitle(TITLE);
					switchStage("waiting");
				}
			}
		};
		
		
		//add all stages to jPanel
		for(Entry<String, Stage> entry : mStrStages.entrySet()) {
			jpMain.add(entry.getKey(), entry.getValue().spPanel);
		}
		
	}
	
	/**
	 * used to switch and render the current stage
	 * @param id the stages id 
	 */
	public synchronized void switchStage(String id) {
		//get layout
		CardLayout card = (CardLayout) jpMain.getLayout();
		
		//disable old stage
		if(currentStage != null) currentStage.spPanel.setEnabled(false);
		
		//select new stage
		currentStage = mStrStages.get(id);
		//enable the new stage
		currentStage.spPanel.setEnabled(true);
		//switch to stage
		card.show(jpMain, id);
		
		render();
	}
	
	public synchronized void render() {
		jfFrame.repaint();
	}
	
	protected void processMouseClick(int x, int y) {
		currentStage.handleClick(x, y);
	}
	
	public Stage getCurrentStage() {
		return currentStage;
	}
	
	public JFrame getJFrame() {
		return jfFrame;
	}
	
	public Engine getEngine() {
		return engine;
	}
}
