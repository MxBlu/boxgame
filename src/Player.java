import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.Timer;

public class Player extends Entity implements Cloneable {
		
	private Image downSprite;
	private Image upSprite;
	private Image rightSprite;
	private Image leftSprite;
	private Image renderSprite;
	private AudioManager audioSource;
	private int movY;
	private int movX;
	private boolean atNewTile = true;
	
	private boolean goal = false;

	private static final String WALL_COLLISION = "wall collision";
	private static final String BOX_PUSH = "box push";
	/**
	 * Representative of the player the user plays as on the level
	 */
	public Player(int tileX, int tileY, Image downSprite, Image upSprite, Image rightSprite, int tileSize, int lvlWidth, int lvlHeight) {
		super(tileX, tileY, tileSize, lvlWidth, lvlHeight);
		
		this.downSprite = downSprite.getScaledInstance(tileSize, tileSize, Image.SCALE_DEFAULT);
		this.rightSprite = rightSprite.getScaledInstance(tileSize, tileSize, Image.SCALE_DEFAULT);
		this.audioSource = new AudioManager();
		//Mirror rightSprite for leftSprite
		AffineTransform tx = AffineTransform.getScaleInstance(-1.0,1.0);
	    tx.translate(-rightSprite.getWidth(null), 0);
        AffineTransformOp tr=new AffineTransformOp(tx, null);
		this.leftSprite = tr.filter((BufferedImage) rightSprite, null);
		this.upSprite = upSprite.getScaledInstance(tileSize, tileSize, Image.SCALE_DEFAULT);
		renderSprite = downSprite;
		
		audioSource.addSound("wall_collision.wav", WALL_COLLISION);
		audioSource.addSound("box_push_2.wav", BOX_PUSH);
	}

	/**
	 * Updates the player according to where it is on the map, and
	 * its current action
	 * @param levelMap
	 * @param boxList
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
			return;
		} else if (tileY >= lvlHeight || tileY < 0) {
			tileY = prevTileY;
			return;
		}

		// Check collision on solid tiles
		// levelMap is reversed for some reason
		Tile tile = levelMap[tileY][tileX];
		if (tile == Tile.WALL) {
			// Against wall
			audioSource.playSound(WALL_COLLISION, 1.0f);
			tileX = prevTileX;
			tileY = prevTileY;
		} else {
			// Check against boxes
			Box box = getBoxAt(tileX, tileY, boxList);
			if (box != null) {
				if (getBoxAt(tileX + movX, tileY + movY, boxList) != null) {
					audioSource.playSound(WALL_COLLISION, 1.0f);
					tileX = prevTileX;
					tileY = prevTileY;
				} else {
					if (levelMap[tileY + movY][tileX + movX] != Tile.WALL) {
						box.setTilePos(tileX + movX, tileY + movY);
						
						audioSource.playSound(BOX_PUSH, 1.0f);
						atNewTile = true;
						animating = true;
						
					} else {
						audioSource.playSound(WALL_COLLISION, 1.0f);
						tileX = prevTileX;
						tileY = prevTileY;
					}
				}	
			} else {
				atNewTile = true;
				animating = true;
			}
		}
		
		
		//Change direction for rendering
		if (prevTileX != tileX || prevTileY != tileY) {
			if (movX == 1) {
				renderSprite = rightSprite;
			} else if (movX == -1) {
				renderSprite = leftSprite;
			} else if (movY == -1) {
				renderSprite = upSprite;
			} else if (movY == 1) {
				renderSprite = downSprite;
			}
		}
	}
	
	@Override
	/**
	 * Updates the player's animation
	 */
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
	/**
	 * Checking if player is at a new tile
	 * @return true if it is at a new tile
	 * Otherwise, returns false
	 */
	public boolean atNewTile() {
		return atNewTile;
	}

	/**
	 * Paints the components for player
	 * @param g, for graphics
	 */
	public void paintComponent(Graphics g) {
		Graphics2D bbg = (Graphics2D) g;
		int left = (int) ((double) GameMaster.WIDTH / 2 - (double) (lvlWidth * tileSize) / 2);
		int top = (int) ((double) GameMaster.HEIGHT / 2 - (double) (lvlHeight * tileSize) / 2);

		// paints the player at its new location, with new movement sprite
		bbg.drawImage(renderSprite, left + renderX, top + renderY, upSprite.getWidth(null), upSprite.getHeight(null), null);		 
	}
	
	/**
	 * Clones the player
	 */
	public Object clone() throws CloneNotSupportedException{  
		Player player = (Player) super.clone();
		player.atNewTile = atNewTile;
		player.tileX = tileX;
		player.tileY = tileY;
		player.renderX = tileX * tileSize;;
		player.renderY = tileY * tileSize;
		player.downSprite = downSprite;
		player.upSprite = upSprite;
		player.rightSprite = rightSprite;
		player.leftSprite = leftSprite;
		player.tileSize = tileSize;
		player.lvlWidth = lvlWidth;
		player.lvlHeight = lvlHeight;
		player.movX = 0;
		player.movY = 0;
		player.renderSprite = renderSprite;
		return player;
	} 

	/**
	 * Gets the box at the requested coordinate
	 * @param tileX, x coordinate
	 * @param tileY, y coordinate
	 * @param boxList
	 * @return a box at the requested coordinate
	 */
	private Box getBoxAt(int tileX, int tileY, ArrayList<Box> boxList) {
		// goes through the boxList
		for (int i = 0; i < boxList.size(); i++) {
			Box box = boxList.get(i);
			// checks if the box has the same coordinates as the request
			if (box.getTileX() == tileX && box.getTileY() == tileY) {
				return box;
			}
		}

		return null;
	}
	
	/**
	 * Sets the players new movement
	 * @param x, indicates what move the player is currently doing
	 */
	public void setMove(int x){
		movY = 0;
		movX = 0;
		
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
