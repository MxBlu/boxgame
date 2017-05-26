import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Timer;

public class Box extends Entity implements Cloneable {
	
	private Image sprite;
	
	private Image offGoalSprite;
	private Image onGoalSprite;
	
	public Box(int tileX, int tileY, Image sprite, int tileSize, int lvlWidth, int lvlHeight) {
		super(tileX, tileY, tileSize, lvlWidth, lvlHeight);
		this.offGoalSprite = sprite;
		this.sprite = this.offGoalSprite;
		
		try {
			this.onGoalSprite = ImageIO.read(getClass().getResourceAsStream("box_goal.png")).getScaledInstance(tileSize, tileSize, Image.SCALE_DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* Sets the tile's coordinates */
	public void setTilePos(int tileX, int tileY){
		this.tileX = tileX;
		this.tileY = tileY;
		animating = true;
	}
	
	/* Switches the sprite for the box appropriately*/
	public void setOnGoal(boolean val) {
		if (val)
			sprite = onGoalSprite;
		else
			sprite = offGoalSprite;
	}
	
	/* Draws the box */
	public void draw(Graphics2D bbg) {
		int left = (int) ((double) GameMaster.WIDTH / 2 - (double) (lvlWidth * tileSize) / 2);
		int top = (int) ((double) GameMaster.HEIGHT / 2 - (double) (lvlHeight * tileSize) / 2);

		bbg.drawImage(sprite, left + renderX, top + renderY, sprite.getWidth(null),
			sprite.getHeight(null), null);
	}
	
	/* Returns a copy of Box */
	public Box copy() {
		Box box = new Box(this.tileX, this.tileY, this.sprite, this.tileSize, this.lvlWidth, this.lvlHeight);
		return box;
	}
	
	/* Clones the box
	 * Returns the object instance of the box */
	public Object clone() throws CloneNotSupportedException{  
		Box box = (Box) super.clone();
		box.tileX = tileX;
		box.tileY = tileY;
		box.renderX = tileX * tileSize;
		box.renderY = tileY * tileSize;
		box.sprite = sprite;
		box.tileSize = tileSize;
		box.lvlWidth = lvlWidth;
		box.lvlHeight = lvlHeight;
		return box;
	}	
}
