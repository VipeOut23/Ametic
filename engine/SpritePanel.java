package de.jroeger.engine;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;

public class SpritePanel extends JPanel{
	
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = -3205544591545209007L;

	/**
	 * saves the x and y size coordinates of the panel
	 */
	private int x, y;
	
	/**
	 * determines if the panel is currently in use
	 */
	private boolean enabled;
	
	/**
	 * saves all sprites on this panel
	 */
	private Map<String, Sprite> mstrsSprites;
	
	/**
	 * saves all animations on this panel
	 */
	private Map<String, Animation>mstraAnimations;
	
	/**
	 * saves all Sprites pixels
	 */
	private int[] pixels;
	
	public SpritePanel(int x, int y) {
		this.x 			= x;
		this.y 			= y;
		this.enabled 	= false;
		this.pixels 	= new int[x*y];//RGBA
		
		this.mstraAnimations 	= new LinkedHashMap<>();
		this.mstrsSprites		= new LinkedHashMap<>();
	}
	
	public void add(String name, Sprite sprite) {
		this.mstrsSprites.put(name, sprite);
	}
	
	public void add(String name, Animation anim) {
		this.mstraAnimations.put(name, anim);
	}
	
	/**
	 * used to update the pixel array based of the sprite's pixel arrays
	 * @return success
	 */
	private boolean updatePixels() {
		boolean success = true;
		
		this.pixels = new int[x*y];
		
		//render all sprites
		for(Sprite spr : mstrsSprites.values()) {
			if(spr!= null)
			if(spr.isbRender()) {
				int sprY = spr.getY();
				int sprX = spr.getX();
				int sprYr= spr.getYr();
				int sprXr= spr.getXr();
				
				//Go through the coordinates of the sprite
				for(int y = sprY; y < sprYr + sprY; y++) {
					for(int x = sprX; x < sprXr + sprX; x++) {
						//Prepend out of bounds exception
						if(y*this.x + x < this.pixels.length) {
							int index = y*this.x + x;
							
							int i1 = (this.pixels[y*this.x + x]);
							int i2 = (spr.getPixelValueAt(x - sprX, y - sprY));
							
							//background color
						    float r1 = ((i1 & 0xff0000) >> 16) / 255.f;
						    float g1 = ((i1 & 0xff00) >> 8) / 255.f;
						    float b1 = (i1 & 0xff) / 255.f;
						    
						    //new color
						    float a2 = (i2 >> 24 & 0xff) / 255.f;
						    float r2 = ((i2 & 0xff0000) >> 16) / 255.f;
						    float g2 = ((i2 & 0xff00) >> 8) / 255.f;
						    float b2 = (i2 & 0xff) / 255.f;
							
							int an = 255;
							int rn = (int) ((r1 * (1.0 - a2) + r2 * a2) * 255.f);
							int gn = (int) ((g1 * (1.0 - a2) + g2 * a2) * 255.f);
							int bn = (int) ((b1 * (1.0 - a2) + b2 * a2) * 255.f);
							
							this.pixels[index] =  an << 24 | rn << 16 | gn << 8 | bn;
						}
					}
				}
			}
		}
		
		//render all animations
		for(Animation anim : mstraAnimations.values()) {
			Sprite spr = anim.getCurrentSprite();
			
			
			if(spr != null)
			if(spr.isbRender()) {
				int sprY = spr.getY();
				int sprX = spr.getX();
				int sprYr= spr.getYr();
				int sprXr= spr.getXr();
				
				//Go through the coordinates of the sprite
				for(int y = sprY; y < sprYr + sprY; y++) {
					for(int x = sprX; x < sprXr + sprX; x++) {
						//Prepend out of bounds exception
						if(y*this.x + x < this.pixels.length) {
							int index = y*this.x + x;
							
							int i1 = (this.pixels[y*this.x + x]);
							int i2 = (spr.getPixelValueAt(x - sprX, y - sprY));
							
							//background color
						    float r1 = ((i1 & 0xff0000) >> 16) / 255.f;
						    float g1 = ((i1 & 0xff00) >> 8) / 255.f;
						    float b1 = (i1 & 0xff) / 255.f;
						    
						    //new color
						    float a2 = (i2 >> 24 & 0xff) / 255.f;
						    float r2 = ((i2 & 0xff0000) >> 16) / 255.f;
						    float g2 = ((i2 & 0xff00) >> 8) / 255.f;
						    float b2 = (i2 & 0xff) / 255.f;
							
							int an = 255;
							int rn = (int) ((r1 * (1.0 - a2) + r2 * a2) * 255.f);
							int gn = (int) ((g1 * (1.0 - a2) + g2 * a2) * 255.f);
							int bn = (int) ((b1 * (1.0 - a2) + b2 * a2) * 255.f);
							
							this.pixels[index] =  an << 24 | rn << 16 | gn << 8 | bn;
						}
					}
				}
			}
		}
		
		return success;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(enabled) g.drawImage(this.toImage(), 0, 0, this);
	}
	
	/**
	 * used to obtain a BufferedImage form all sprites
	 * @return the Image
	 */
	public BufferedImage toImage() {
		BufferedImage img = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);
		
		//load pixels
		if(!updatePixels()) {
			//handle
		}
		
		//Write data
		for(int y = 0; y < this.y; y++) {
			for(int x = 0; x < this.x; x++) {
				img.setRGB(x, y, this.pixels[y*this.x + x]);
			}
		}
		
		return img;
	}

	/*
	 * Getter & Setter
	 */
	public Animation getAnimation(String name) {
		return mstraAnimations.get(name);
	}
	
	public Sprite getSprite(String name) {
		return mstrsSprites.get(name);
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
