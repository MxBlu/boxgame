import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import javafx.scene.transform.Scale;

enum Tile {
	WALL(0),
	WALKABLE(1),
	BOX(2),
	GOAL(3),
	TEMP_WALKABLE(5);
	
	private final int intRep;
	Tile(int intRep) {
		this.intRep = intRep;
	}
	
	public int getIntRep() {
		return this.intRep;
	}
}

public class Level implements GameState {
	
	private byte levelMap[][];
	
	private int width; // Width of the level
	private int height; // Height of the level
	private int tileSize;
	
	private Image tiles[];
	private Player player;
	private KeyInput input;
	
	/**
	 * Creates a new level.
	 * @precondition screenWidth % tileSize == 0 && screenHeight % tileSize == 0
	 * @param screenWidth Screen width in pixels.
	 * @param screenHeight Screen height in pixels.
	 * @param tileSize Width/Height of a tile in pixels.
	 */
	Level(int screenWidth, int screenHeight, int tileSize) {
		// 1 pixel padding so I don't need to add edge cases to generation.
		this.width = screenWidth/tileSize + 2;
		this.height = screenHeight/tileSize + 2;
		this.tileSize = tileSize;
		setDefaultTiles();
		
		generate();
	}
	
	Level(String input, int tileSize) {
		this.tileSize = tileSize;
		byte inputArray[] = input.getBytes();
		
		// Get the width and height
		for (this.width = 0; inputArray[this.width] != '\n'; this.width++);
		this.height = inputArray.length/(width + 1);
		//System.out.println("Length: " + inputArray.length + ", Height :" + this.height + ", Width: " + this.width);
		
		levelMap = new byte[this.height][this.width];
		int sIndex = 0;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) 
				levelMap[i][j] = (byte) (inputArray[sIndex++] - '0');
			sIndex++;
		}
		
		setDefaultTiles();
	}
	
	private void setDefaultTiles() {
		this.tiles = new Image[4];
		try {
			tiles[0] = ImageIO.read(getClass().getResourceAsStream("ground_solid.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			tiles[1] = ImageIO.read(getClass().getResourceAsStream("ground_empty.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			tiles[2] = ImageIO.read(getClass().getResourceAsStream("box.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			tiles[3] = ImageIO.read(getClass().getResourceAsStream("goal.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			player = new Player(1, 1, ImageIO.read(getClass().getResourceAsStream("player.png")), tileSize, width, height);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setTile(Tile t, Image tileImage) {
		this.tiles[t.getIntRep()] = tileImage;
	}
	
	/**
	 * Generate a random level (for now, just walls and walkable space).
	 */
	private void generate() {
		// Initialise matrix
		levelMap = new byte[height][width];
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++) 
				levelMap[i][j] = (byte) Tile.WALL.getIntRep();
		
		int roughCentreHeight = height/2;
		int roughCentreWidth = width/2;

		// Wall = 0
		// Walkable = 1
		
		// Place initial 9 walkables in the rough centre.
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				levelMap[roughCentreHeight + i][roughCentreWidth + j] = (byte) Tile.WALKABLE.getIntRep();

		Random rng = new Random();
		// numPasses = number of times to attempt to place walkables.
		// Feel free to play with this
		for (int numPasses = 10; numPasses > 0; numPasses--) {
			for (int i = 1; i < height - 1; i++) {
				for (int j = 1; j < width - 1; j++) {
					if (levelMap[i][j] != Tile.WALL.getIntRep())
						continue;
					
					// 0d0
					// d1d
					// 0d0
					// Consider '1' as being the place being checked.
					// Totals the 'd' values to find number of adjacent walkables.
					
					int numAdj = levelMap[i + 1][j] + levelMap[i - 1][j] +
							levelMap[i][j + 1] + levelMap[i][j - 1];

					// Feel free to mess with the chance values.
					float adj1Chance = 0.5f;
					float adj2Chance = 0.25f;
					float adj3Chance = 0.75f;
					
					// Set values to a temp of 5 so they don't affect the current pass.
					switch (numAdj) {
					case 0:
						break;
					case 1: // 1/4 chance
						if (rng.nextFloat() <= adj1Chance)
							levelMap[i][j] = (byte) Tile.TEMP_WALKABLE.getIntRep();
						
						break;
					case 2: // 1/2 change
						if (rng.nextFloat() <= adj2Chance)
							levelMap[i][j] = (byte) Tile.TEMP_WALKABLE.getIntRep();
						
						break;
					case 3: // 3/4 chance
						if (rng.nextFloat() <= adj3Chance)
							levelMap[i][j] = (byte) Tile.TEMP_WALKABLE.getIntRep();
						
						break;
					case 4: // definite
						levelMap[i][j] = (byte) Tile.TEMP_WALKABLE.getIntRep();
					}
				}
			}
			
			// Set all temp values to actual.
			for (int i = 1; i < height - 1; i++)
				for (int j = 1; j < width - 1; j++) 
					if (levelMap[i][j] == 5) 
						levelMap[i][j] = (byte) Tile.WALKABLE.getIntRep();
		}
		//TEMP (adds a push block)
		levelMap[height/2+1][width/2+1] = (byte) Tile.BOX.getIntRep();
		//ALSO TEMP (adds a goal block)
		levelMap[height/2-1][width/2-1] = (byte) Tile.GOAL.getIntRep();
		
	}
	
	public void draw(Graphics2D bbg) {
		int left = (int) ((double) GameMaster.WIDTH/2 - (double) (width * tileSize)/2);
		int top = (int) ((double) GameMaster.HEIGHT/2 - (double) (height * tileSize)/2);
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) { 
				if (tiles[levelMap[i][j]] != null) {
					bbg.drawImage(tiles[levelMap[i][j]], left + j * tileSize, top + i * tileSize, null);
				} else {
					bbg.setColor(new Color(levelMap[i][j] * 127));
					bbg.fillRect(left + j * tileSize, top + i * tileSize, tileSize, tileSize);
				}
			}
		}
		
		player.draw(bbg);
	}
	
	public int getTileSize() {
		return tileSize;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder(height * (width + 1));
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) 
				s.append((int) levelMap[i][j]);
			
			s.append('\n');
		}
		
		return s.toString();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(KeyInput input) {
		player.update(input.getPressed(), levelMap);
	}

	@Override
	public void handleInput() {
		// TODO Auto-generated method stub
	}
}
