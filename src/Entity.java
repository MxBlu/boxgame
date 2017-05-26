import java.awt.Image;

public class Entity {

	protected static int movementSpeed = 8;
	
	protected int tileX, tileY;
	protected int renderX, renderY;
	protected int tileSize;
	protected int lvlWidth, lvlHeight;
	protected boolean animating;
	
	public Entity(int tileX, int tileY, int tileSize, int lvlWidth, int lvlHeight) {
		this.tileX = tileX;
		this.tileY = tileY;
		this.tileSize = tileSize;
		this.renderX = tileX * tileSize;
		this.renderY = tileY * tileSize;
		this.lvlWidth = lvlWidth;
		this.lvlHeight = lvlHeight;
		
		this.renderX = tileX * tileSize;
		this.renderY = tileY * tileSize;
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
	
}
