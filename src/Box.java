import java.awt.Graphics2D;
import java.awt.Image;

public class Box {

	private int tileX, tileY;
	private Image sprite;
	private int tileSize;
	private int lvlWidth, lvlHeight;
	
	public Box(int tileX, int tileY, Image sprite, int tileSize, int lvlWidth, int lvlHeight) {
		this.tileX = tileX;
		this.tileY = tileY;
		this.sprite = sprite;
		this.tileSize = tileSize;
		this.lvlWidth = lvlWidth;
		this.lvlHeight = lvlHeight;
	}
	
	public int getTileX() {
		return tileX;
	}
	
	public int getTileY() {
		return tileY;
	}
	
	//Potentially just change this to a move(int direction) method
	public void setTilePos(int tileX, int tileY){
		this.tileX = tileX;
		this.tileY = tileY;
	}
	
	public void draw(Graphics2D bbg) {
		int left = (int) ((double) GameMaster.WIDTH / 2 - (double) (lvlWidth * tileSize) / 2);
		int top = (int) ((double) GameMaster.HEIGHT / 2 - (double) (lvlHeight * tileSize) / 2);

		bbg.drawImage(sprite, left + tileX * tileSize, top + tileY * tileSize, sprite.getWidth(null),
				sprite.getHeight(null), null);
	}
	
}
