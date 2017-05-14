import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Player {

	private static int movementSpeed = 8;

	private int tileX, tileY;
	private int renderX, renderY;
	private Image sprite;
	private int tileSize;
	private int lvlWidth, lvlHeight;
	private int movY;
	private int movX;
	private int level;

	public Player(int tileX, int tileY, Image sprite, int tileSize, int lvlWidth, int lvlHeight) {
		this.tileX = tileX;
		this.tileY = tileY;
		this.tileSize = tileSize;
		this.renderX = tileX * tileSize;
		this.renderY = tileY * tileSize;
		this.lvlWidth = lvlWidth;
		this.lvlHeight = lvlHeight;
		this.level = StateManager.getLevel();
		System.out.println("level state manager" + this.level);
		this.sprite = sprite.getScaledInstance(tileSize, tileSize, Image.SCALE_DEFAULT);
	}

	/*
	 * @param input
	 * 
	 * @param levelMap
	 */
	public void update(Tile[][] levelMap, ArrayList<Box> boxList) {
		// Update animation
		if (renderX < tileX * tileSize) {
			renderX += movementSpeed;
			if (renderX > tileX * tileSize)
				renderX = tileX * tileSize;
			return;
		} else if (renderX > tileX * tileSize) {
			renderX -= movementSpeed;
			if (renderX < tileX * tileSize)
				renderX = tileX * tileSize;
			return;
		} else if (renderY < tileY * tileSize) {
			renderY += movementSpeed;
			if (renderY > tileY * tileSize)
				renderY = tileY * tileSize;
			return;
		} else if (renderY > tileY * tileSize) {
			renderY -= movementSpeed;
			if (renderY < tileY * tileSize)
				renderY = tileY * tileSize;
			return;
		}

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
		Tile tile = levelMap[tileY][tileX];
		if (tile == Tile.WALL) {
			// Against wall
			tileX = prevTileX;
			tileY = prevTileY;
		} else {
			// Check against boxes
			Box box = getBoxAt(tileX, tileY, boxList);
			if (box != null) {
				if (levelMap[tileY + movY][tileX + movX] == Tile.WALKABLE
						|| levelMap[tileY + movY][tileX + movX] == Tile.BOX) {
					box.setTilePos(tileX + movX, tileY + movY);
				} else if (levelMap[tileY + movY][tileX + movX] == Tile.GOAL) {
					box.setTilePos(tileX + movX, tileY + movY);

					System.out.println("in condition of goal level " + this.level);
					this.level--;
					if (this.level == 0) {
						System.out.println("in condition of goal levle " + this.level);
						StateManager.setLevel();
						StateManager.setState("LEVEL");
					}
				} else {
					tileX = prevTileX;
					tileY = prevTileY;
				}
			}
		}
	}

	public void draw(Graphics2D bbg) {
		int left = (int) ((double) GameMaster.WIDTH / 2 - (double) (lvlWidth * tileSize) / 2);
		int top = (int) ((double) GameMaster.HEIGHT / 2 - (double) (lvlHeight * tileSize) / 2);

		bbg.drawImage(sprite, left + renderX, top + renderY, sprite.getWidth(null), sprite.getHeight(null), null);
	}

	public void handleInput() {
		movX = 0;
		movY = 0;
		int curMovement = KeyInput.getPressed();

		switch (curMovement) {
		case KeyEvent.VK_UP:
			movY--;
			break;
		case KeyEvent.VK_DOWN:
			movY++;
			break;
		case KeyEvent.VK_LEFT:
			movX--;
			break;
		case KeyEvent.VK_RIGHT:
			movX++;
			break;
		case KeyEvent.VK_SPACE:
			// Space (TODO)
			break;
		}

	}

	private Box getBoxAt(int tileX, int tileY, ArrayList<Box> boxList) {
		for (int i = 0; i < boxList.size(); i++) {
			Box box = boxList.get(i);
			if (box.getTileX() == tileX && box.getTileY() == tileY) {
				return box;
			}
		}

		return null;
	}
}
