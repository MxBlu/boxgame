import java.awt.Graphics2D;
import java.awt.Image;

public class Player {
	
	private int tileX, tileY;
	private Image sprite;
	private int tileSize;
	private int lvlWidth, lvlHeight;
	private int framesLastUpdate = 5;
	private int movY;
	private int movX;
	
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
	public void update(byte levelMap[][]) {
		//handleInput();
		//Rubbish way to stop moving too fast
		if (framesLastUpdate < 5) {
			framesLastUpdate++;
			return;
		}

		framesLastUpdate = 0;
		
		int prevTileX = tileX;
		int prevTileY = tileY;
		movX = 0;
		movY = 0;
		handleInput();

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
		//if (input != 0) {
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
		//}
	}
	
	public void draw(Graphics2D bbg) {
		int left = (int) ((double) GameMaster.WIDTH/2 - (double) (lvlWidth * tileSize)/2);
		int top = (int) ((double) GameMaster.HEIGHT/2 - (double) (lvlHeight * tileSize)/2);
		
		bbg.drawImage(sprite, left + tileX * tileSize, top + tileY * tileSize, sprite.getWidth(null), sprite.getHeight(null), null);
	}
	public void handleInput() {
		if (KeyInput.getPressed() == 1) {
			movY --;
			System.out.println("UP");
		}
		if (KeyInput.getPressed() == 2) {
			movY ++;
			System.out.println("DOWN");
		}
		if (KeyInput.getPressed() == 3) {
			movX --;
			System.out.println("LEFT");
		}
		if (KeyInput.getPressed() == 4) {
			movX ++;
			System.out.println("RIGHT");
		}
		if (KeyInput.getPressed() == 5) {
			System.out.println("SPACE");
		}
	}
}
