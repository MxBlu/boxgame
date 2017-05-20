import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.Timer;

public class Player extends Entity implements Cloneable {

	private static int movementSpeed = 8;

	private int tileX, tileY;
	
	private Image sprite;
	private int tileSize;
	private int lvlWidth, lvlHeight;
	private int movY;
	private int movX;
	private int level;
	private boolean atNewTile = true;
	
	private int renderX, renderY;
	private boolean animating = true;

	public Player(int tileX, int tileY, Image sprite, int tileSize, int lvlWidth, int lvlHeight) {
		this.tileX = tileX;
		this.tileY = tileY;
		this.tileSize = tileSize;
		this.renderX = tileX * tileSize;
		this.renderY = tileY * tileSize;
		this.lvlWidth = lvlWidth;
		this.lvlHeight = lvlHeight;
		this.level = 1;
		//System.out.println("level state manager" + this.level);
		this.sprite = sprite.getScaledInstance(tileSize, tileSize, Image.SCALE_DEFAULT);
		this.animating = false;
	}

	/*
	 * @param input
	 * 
	 * @param levelMap
	 */
	public void update(Tile[][] levelMap, ArrayList<Box> boxList) {
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
				if (getBoxAt(tileX + movX, tileY + movY, boxList) != null) {
					tileX = prevTileX;
					tileY = prevTileY;
				} else {
					if (levelMap[tileY + movY][tileX + movX] == Tile.WALKABLE) {
						box.setTilePos(tileX + movX, tileY + movY);
						if (levelMap[tileY][tileX] == Tile.GOAL)
							this.level++;
						
						atNewTile = true;
						animating = true;
					} else if (levelMap[tileY + movY][tileX + movX] == Tile.GOAL) {
						box.setTilePos(tileX + movX, tileY + movY);
						if (levelMap[tileY][tileX] != Tile.GOAL)
							this.level--;
						
						atNewTile = true;
						animating = true;
					} else {
						tileX = prevTileX;
						tileY = prevTileY;
					}
				}
				System.out.println("in condition of goal level " + this.level);
					
			} else {
				atNewTile = true;
				animating = true;
			}
		}
	}
	
	public void updateAnimation() {
		// Update animation
		if (renderX < tileX * tileSize) {
			renderX += movementSpeed;
			if (renderX >= tileX * tileSize) {
				renderX = tileX * tileSize;
			}
			return;
		} else if (renderX > tileX * tileSize) {
			renderX -= movementSpeed;
			if (renderX <= tileX * tileSize) {
				renderX = tileX * tileSize;
			}
			return;
		} else if (renderY < tileY * tileSize) {
			renderY += movementSpeed;
			if (renderY > tileY * tileSize) {
				renderY = tileY * tileSize;
			}
			return;
		} else if (renderY > tileY * tileSize) {
			renderY -= movementSpeed;
			if (renderY < tileY * tileSize) {
				renderY = tileY * tileSize;
			}
			return;
		} else {
			atNewTile = false;
			animating = false;
		}
	}
	
	public boolean isAnimating() {
		return animating;
	}
	
	public boolean atNewTile() {
		return atNewTile;
	}

	public void paintComponent(Graphics g) {
		Graphics2D bbg = (Graphics2D) g;
		int left = (int) ((double) GameMaster.WIDTH / 2 - (double) (lvlWidth * tileSize) / 2);
		int top = (int) ((double) GameMaster.HEIGHT / 2 - (double) (lvlHeight * tileSize) / 2);

		bbg.drawImage(sprite, left + renderX, top + renderY, sprite.getWidth(null), sprite.getHeight(null), null);
	}
	
	public int getTileX() {
		return tileX;
	}
	
	public int getTileY() {
		return tileY;
	}
	
	public Object clone() throws CloneNotSupportedException{  
		Player player = (Player) super.clone();
		player.atNewTile = atNewTile;
		player.tileX = tileX;
		player.tileY = tileY;
		player.renderX = renderX;
		player.renderY = renderY;
		player.sprite = sprite;
		player.tileSize = tileSize;
		player.lvlWidth = lvlWidth;
		player.lvlHeight = lvlHeight;
		player.movX = movX;
		player.movY = movY;
		player.level = level;
		return player;
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
	
	public void setMove(int x){
		switch (x) {
		case 1:
			movY = -1;
			break;
		case 2:
			movY = 1;
			break;
		case 3:
			movX = -1;
			break;
		case 4:
			movX = 1;
			break;
		}
	}	
}
