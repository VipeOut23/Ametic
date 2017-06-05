package de.jroeger.engine;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sprite {
	/**
	 * saves the x - startpoint of the sprite
	 */
	private int x;
	
	/**
	 * saves the y - startpoint of the sprite
	 */
	private int y;
	
	/**
	 * saves the x - range of the sprite
	 */
	private int xr;
	
	/**
	 * saves the y - range of the sprite
	 */
	private int yr;

	/**
	 * saves the area id
	 */
	private String id;
	
	/**
	 * determines if the sprite is rendered or not
	 */
	private boolean bRender;
	
	/**
	 * saves the used texture
	 */
	private Texture texture;
	
	public Sprite(int x, int y, String imagePath, String id, boolean bRender) {
		this.id 		= id;
		this.x 			= x;
		this.y 			= y;
		this.bRender 	= bRender;
		
		this.texture = new Texture(imagePath);
	}
	
	/**
	 * load sprite from texture
	 * @param texture the texture
	 */
	public Sprite(int x, int y, Texture texture, String id, boolean bRender) {
		this.id 		= id;
		this.x 			= x;
		this.y 			= y;
		this.bRender 	= bRender;
		
		this.texture = texture;
	}
	
	/**
	 * used for single color sprites
	 * @param x start
	 * @param y start
	 * @param xr size
	 * @param yr size
	 * @param c color
	 * @param id id
	 * @param bRender should render?
	 */
	public Sprite(int x, int y, int xr, int yr, Color c, String id, boolean bRender) {
		this.id 		= id;
		this.x 			= x;
		this.y 			= y;
		this.bRender 	= bRender;
		
		this.texture = new Texture(xr, yr, c);
	}
	
	/**
	 * create a new sprite based of the old one
	 * @param sprite the to be cloned sprite
	 */
	public Sprite(Sprite sprite) {
		this.id 		= new String(sprite.id);
		this.x  		= sprite.x;
		this.y  		= sprite.y;
		this.bRender 	= sprite.bRender;
		
		this.texture = sprite.texture;
	}
	
	public void moveTo(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getPixelValueAt(int x, int y) {
		return texture.getPixelValueAt(x, y);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getXr() {
		return texture.getXr();
	}

	public int getYr() {
		return texture.getYr();
	}

	public boolean isbRender() {
		return bRender;
	}

	public void setbRender(boolean bRender) {
		this.bRender = bRender;
	}
	
}
