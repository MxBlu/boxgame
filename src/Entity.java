import java.awt.Image;

//The super class of player and box that features shared states and functionality
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
	
	/* Gets the x coordinate */
	public int getTileX() {
		return tileX;
	}
	
	/* Gets the y coordinate */
	public int getTileY() {
		return tileY;
	}
	
	/* Checks if the entity is still animating */
	public boolean isAnimating() {
		return animating;
	}
	
	/* Updates the animation one frame */
	public void updateAnimation() {
		//Updates animation based off its movement
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
