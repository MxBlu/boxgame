import java.awt.Graphics2D;
import java.awt.Image;

public class Player {

	private static int movementSpeed = 15;

	private int tileX, tileY;
	private Image sprite;
	private int tileSize;
	private int lvlWidth, lvlHeight;
	private int lastMovement = 0;
	private int curMovement = 0;
	private int framesLastUpdate = movementSpeed;
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
	 * 
	 * @param levelMap
	 */
	public void update(byte levelMap[][]) {
		handleInput();

		int prevTileX = tileX;
		int prevTileY = tileY;

		tileX += movX;
		tileY += movY;

		// Check collision
		// Check level boundaries
		if (tileX >= lvlWidth || tileX < 0) {
			tileX = prevTileX;
		} else if (tileY >= lvlHeight || tileY < 0) {
			tileY = prevTileY;
		}

		// Check collision on solid tiles
		// levelMap is reversed for some reason
		int tile = levelMap[tileY][tileX];
		if (tile == Tile.WALL.getIntRep()) {
			// Against wall
			tileX = prevTileX;
			tileY = prevTileY;
		} else if (tile == Tile.BOX.getIntRep()) {
			// Against push block
			if (levelMap[tileY + movY][tileX + movX] == Tile.WALKABLE.getIntRep()) {
				levelMap[tileY + movY][tileX + movX] = (byte) Tile.BOX.getIntRep();
				levelMap[tileY][tileX] = (byte) Tile.WALKABLE.getIntRep();
			} else {
				tileX = prevTileX;
				tileY = prevTileY;
			}
		}
	}

	public void draw(Graphics2D bbg) {
		int left = (int) ((double) GameMaster.WIDTH / 2 - (double) (lvlWidth * tileSize) / 2);
		int top = (int) ((double) GameMaster.HEIGHT / 2 - (double) (lvlHeight * tileSize) / 2);

		bbg.drawImage(sprite, left + tileX * tileSize, top + tileY * tileSize, sprite.getWidth(null),
				sprite.getHeight(null), null);
	}

	public void handleInput() {
		movX = 0;
		movY = 0;
		lastMovement = curMovement;
		curMovement = KeyInput.getPressed();
		
		if (curMovement == lastMovement && framesLastUpdate < movementSpeed) {
			framesLastUpdate++;
			return;
		}
		framesLastUpdate = 0;
		
		switch (curMovement) {
		case 1:
			movY--;
			break;
		case 2:
			movY++;
			break;
		case 3:
			movX--;
			break;
		case 4:
			movX++;
			break;
		case 5:
			//Space (TODO)
			break;
		}
		
	}
}
