import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class Box extends Entity implements Cloneable {

	private static int movementSpeed = 8;
	
	private int tileX, tileY;
	private int renderX, renderY;
	private Image sprite;
	private int tileSize;
	private int lvlWidth, lvlHeight;
	
	private boolean animating;
	
	public Box(int tileX, int tileY, Image sprite, int tileSize, int lvlWidth, int lvlHeight) {
		this.tileX = tileX;
		this.tileY = tileY;
		this.sprite = sprite;
		this.tileSize = tileSize;
		this.renderX = tileX * tileSize;
		this.renderY = tileY * tileSize;
		this.lvlWidth = lvlWidth;
		this.lvlHeight = lvlHeight;
		this.animating = false;
	}
	
	public int getTileX() {
		return tileX;
	}
	
	public int getTileY() {
		return tileY;
	}
	
	public boolean isAnimating() {
		return animating;
	}
	
	public void updateAnimation() {
		//Update animation
		if (renderX < tileX * tileSize) {
			renderX+= movementSpeed;
			if (renderX > tileX * tileSize) renderX = tileX * tileSize; 
			return;
		} else if (renderX > tileX * tileSize) {
			renderX-= movementSpeed;
			if (renderX < tileX * tileSize) renderX = tileX * tileSize;
			return;
		} else if (renderY < tileY * tileSize) {
			renderY+= movementSpeed;
			if (renderY > tileY * tileSize) renderY = tileY * tileSize;
			return;
		} else if (renderY > tileY * tileSize) {
			renderY-= movementSpeed;
			if (renderY < tileY * tileSize) renderY = tileY * tileSize;
			return;
		} else {
			animating = false;
		}
				
	}
	
	//Potentially just change this to a move(int direction) method
	public void setTilePos(int tileX, int tileY){
		this.tileX = tileX;
		this.tileY = tileY;
		animating = true;
	}
	
	public void draw(Graphics2D bbg) {
		int left = (int) ((double) GameMaster.WIDTH / 2 - (double) (lvlWidth * tileSize) / 2);
		int top = (int) ((double) GameMaster.HEIGHT / 2 - (double) (lvlHeight * tileSize) / 2);

		bbg.drawImage(sprite, left + renderX, top + renderY, sprite.getWidth(null),
				sprite.getHeight(null), null);
	}
	
	public Object clone() throws CloneNotSupportedException{  
		Box box = (Box) super.clone();
		box.tileX = tileX;
		box.tileY = tileY;
		box.renderX = renderX;
		box.renderY = renderY;
		box.sprite = sprite;
		box.tileSize = tileSize;
		box.lvlWidth = lvlWidth;
		box.lvlHeight = lvlHeight;
		return box;
	}	
}
