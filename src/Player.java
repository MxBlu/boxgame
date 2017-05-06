import java.awt.Graphics2D;
import java.awt.Image;

public class Player {
	
	private int tileX, tileY;
	private Image sprite;
	private int tileSize;
	private int lvlWidth, lvlHeight;
	private int framesLastUpdate = 15;
	
	public Player(int tileX, int tileY, Image sprite, int tileSize, int lvlWidth, int lvlHeight) {
		this.tileX = tileX;
		this.tileY = tileY;
		this.tileSize = tileSize;
		this.lvlWidth = lvlWidth;
		this.lvlHeight = lvlHeight;
		
		this.sprite = sprite.getScaledInstance(tileSize, tileSize, Image.SCALE_DEFAULT);
	}
	
	/*
	 * @param input
	 * @param levelMap
	 */
	public void update(int input, byte levelMap[][]) {
		//Rubbish way to stop moving too fast
		if (framesLastUpdate < 15) {
			framesLastUpdate++;
			return;
		}
		if (input == 0) {
			return;
		}
		framesLastUpdate = 0;
		
		int prevTileX = tileX;
		int prevTileY = tileY;
		int movX = 0;
		int movY = 0;
		
		if (input == 1) {
			//up
			movY -= 1;
		} else if (input == 2) {
			//down
			movY += 1;
		} else if (input == 3) {
			//left
			movX -= 1;
		} else if (input == 4) {
			//right
			movX += 1;
		}
		tileX += movX;
		tileY += movY;
		
		//Check collision
		//Check level boundaries
		if (tileX >= lvlWidth || tileX < 0) {
			tileX = prevTileX;
		} else if (tileY >= lvlHeight || tileY < 0) {
			tileY = prevTileY;
		}
		
		//Check collision on solid tiles
		if (input != 0) {
			//levelMap is reversed for some reason
			int tile = levelMap[tileY][tileX];
			if (tile == Tile.WALL.getIntRep()) {
				//Against wall
				tileX = prevTileX;
				tileY = prevTileY;
			} else if (tile == Tile.BOX.getIntRep()) {
				//Against push block
				if (levelMap[tileY + movY][tileX + movX] == Tile.WALKABLE.getIntRep()) {
					levelMap[tileY + movY][tileX + movX] = (byte) Tile.BOX.getIntRep();
					levelMap[tileY][tileX] = (byte) Tile.WALKABLE.getIntRep();
				} else {
					tileX = prevTileX;
					tileY = prevTileY;
				}
			}
		}
	}
	
	public void draw(Graphics2D bbg) {
		int left = (int) ((double) GameMaster.WIDTH/2 - (double) (lvlWidth * tileSize)/2);
		int top = (int) ((double) GameMaster.HEIGHT/2 - (double) (lvlHeight * tileSize)/2);
		
		bbg.drawImage(sprite, left + tileX * tileSize, top + tileY * tileSize, sprite.getWidth(null), sprite.getHeight(null), null);
	}
	
}
