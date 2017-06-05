package de.jroeger.engine;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Texture {
	
	/**
	 * saves the x size of the texture
	 */
	private int xr;
	
	/**
	 * saves the y size of the texture
	 */
	private int yr;
	
	private int[] pixels;
	
	public Texture(String imagePath) {
		BufferedImage img;
		try {
			img = ImageIO.read(new File(imagePath));
			loadPixels(img);
		} catch (IOException e) {
			System.out.println("Error loading image@"+imagePath);
		}
	}
	
	public Texture(int xr, int yr, Color c) {
		this.xr = xr;
		this.yr = yr;
		
		this.pixels = new int[xr*yr];
		
		int rgb = c.getRGB();
		
		for(int i = 0; i < yr; i++) {
			for(int j = 0; j < xr; j++) {
				this.pixels[i*xr + j] = rgb;
			}
		}
	}
	
	
	private void loadPixels(BufferedImage image) {
		int x = image.getWidth();
		int y = image.getHeight();
		
		//set the range
		this.xr = x;
		this.yr = y;
		
		//create array
		this.pixels = new int[x*y];
		
		//obtain every pixel value
		for(int i = 0; i < y; i++) {
			for(int j = 0; j < x; j++) {
				this.pixels[i*x + j] = image.getRGB(j, i);
			}
		}
	}
	
	public int getPixelValueAt(int x, int y) {
		int out;
		
		out = this.pixels[y *this.xr + x];
		
		return out;
	}

	/*
	 * Getter & Setter
	 */
	
	public int getXr() {
		return xr;
	}

	public int getYr() {
		return yr;
	}
	
	
}

