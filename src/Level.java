import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class Level implements GameState {
	
	private Tile levelMap[][];
	
	private int width; // Width of the level
	private int height; // Height of the level
	private int tileSize;
	
	private Image tileImgs[];
	private Player player;
	
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
		
		try {
			player = new Player(width / 2, height / 2, ImageIO.read(getClass().getResourceAsStream("player.png")), tileSize, width, height);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	Level(String input, int tileSize) {
		this.tileSize = tileSize;
		byte inputArray[] = input.getBytes();
		
		// Get the width and height
		for (this.width = 0; inputArray[this.width] != '\n'; this.width++);
		this.height = inputArray.length/(width + 1);
		//System.out.println("Length: " + inputArray.length + ", Height :" + this.height + ", Width: " + this.width);
		
		levelMap = new Tile[this.height][this.width];
		int sIndex = 0;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) 
				levelMap[i][j] = Tile.getTile(inputArray[sIndex++] - '0');
			sIndex++;
		}
		
		setDefaultTiles();
		
		try {
			player = new Player(1, 1, ImageIO.read(getClass().getResourceAsStream("player.png")), tileSize, width/2, height/2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setDefaultTiles() {
		this.tileImgs = new Image[4];
		try {
			tileImgs[0] = ImageIO.read(getClass().getResourceAsStream("ground_solid.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			tileImgs[1] = ImageIO.read(getClass().getResourceAsStream("ground_empty.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			tileImgs[2] = ImageIO.read(getClass().getResourceAsStream("box.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			tileImgs[3] = ImageIO.read(getClass().getResourceAsStream("goal.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setTile(Tile t, Image tileImage) {
		this.tileImgs[t.getIntRep()] = tileImage;
	}
	
	/**
	 * Generate a random level (for now, just walls and walkable space).
	 */
	private void generate() {
		// Initialise matrix
		levelMap = new Tile[height][width];
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++) 
				levelMap[i][j] = Tile.WALL;
		
		int roughCentreHeight = height/2;
		int roughCentreWidth = width/2;

		// Wall = 0
		// Walkable = 1
		
		// Place initial 9 walkables in the rough centre.
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				levelMap[roughCentreHeight + i][roughCentreWidth + j] = Tile.WALKABLE;

		Random rng = new Random();
		// numPasses = number of times to attempt to place walkables.
		// Feel free to play with this
		for (int numPasses = 10; numPasses > 0; numPasses--) {
			for (int i = 1; i < height - 1; i++) {
				for (int j = 1; j < width - 1; j++) {
					if (levelMap[i][j] != Tile.WALL)
						continue;
					
					// 0d0
					// d1d
					// 0d0
					// Consider '1' as being the place being checked.
					// Totals the 'd' values to find number of adjacent walkables.
					
					int numAdj = ((levelMap[i + 1][j] == Tile.WALKABLE) ? 1 : 0) +
							((levelMap[i - 1][j] == Tile.WALKABLE) ? 1 : 0) +
							((levelMap[i][j + 1] == Tile.WALKABLE) ? 1 : 0) +
							((levelMap[i][j - 1] == Tile.WALKABLE) ? 1 : 0);

					// Feel free to mess with the chance values.
					
					// Set values to a temp of 5 so they don't affect the current pass.
					switch (numAdj) {
					case 0:
						break;
					case 1: // 1/4 chance
						if (rng.nextFloat() <= GameMaster.adj1Chance)
							levelMap[i][j] = Tile.TEMP_WALKABLE;
						
						break;
					case 2: // 1/2 change
						if (rng.nextFloat() <= GameMaster.adj2Chance)
							levelMap[i][j] = Tile.TEMP_WALKABLE;
						
						break;
					case 3: // 3/4 chance
						if (rng.nextFloat() <= GameMaster.adj3Chance)
							levelMap[i][j] = Tile.TEMP_WALKABLE;
						
						break;
					case 4: // definite
						if (rng.nextFloat() <= GameMaster.adj4Chance)
							levelMap[i][j] = Tile.TEMP_WALKABLE;
					}
				}
			}
			
			// Set all temp values to actual.
			for (int i = 1; i < height - 1; i++)
				for (int j = 1; j < width - 1; j++)  
					if (levelMap[i][j] == Tile.TEMP_WALKABLE) 
						levelMap[i][j] = Tile.WALKABLE;
		}
	
		
		setLevel(StateManager.getLevel());
		
	}
	
	public void draw(Graphics2D bbg) {
		int left = (int) ((double) GameMaster.WIDTH/2 - (double) (width * tileSize)/2);
		int top = (int) ((double) GameMaster.HEIGHT/2 - (double) (height * tileSize)/2);
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) { 
				if (tileImgs[levelMap[i][j].getIntRep()] != null) {
					bbg.drawImage(tileImgs[levelMap[i][j].getIntRep()], left + j * tileSize, top + i * tileSize, null);
				} else {
					bbg.setColor(new Color(levelMap[i][j].getIntRep() * 127));
					bbg.fillRect(left + j * tileSize, top + i * tileSize, tileSize, tileSize);
				}
			}
		}
		
		player.draw(bbg);
	}
	
	public int getTileSize() {
		return tileSize;
	}
	
	private void setLevel(int x){
		for (int i =0; i< x; i++){
			setLoc("Box");
			setLoc("Goal");
		}
	}
	
	/*
	 * @param type  Tile that needs to be set
	 * makes sure that all the surrounding blocks are walkable 
	 * 
	 */
	private void setLoc(String type) {
		Random xRand = new Random();
		Random yRand = new Random();
		int x = Math.abs(xRand.nextInt())%(width-1)+1;
		int y = Math.abs(yRand.nextInt())%(height-1)+1;
		/*System.out.println("system height and width "+ height + " "+ width);
		System.out.println(y + " " + x);*/
		
		/*System.out.println(x + " " + y);*/
		if (type.equals("Box")) {
			while(levelMap[y][x]!= Tile.WALKABLE || levelMap[y+1][x+1]!= Tile.WALKABLE
					|| levelMap[y+1][x]!= Tile.WALKABLE || levelMap[y+1][x-1]!= Tile.WALKABLE
					|| levelMap[y][x+1]!= Tile.WALKABLE || levelMap[y][x-1]!= Tile.WALKABLE
					|| levelMap[y-1][x+1]!= Tile.WALKABLE || levelMap[y-1][x]!= Tile.WALKABLE
					|| levelMap[y-1][x-1]!= Tile.WALKABLE || (x!= width/2 && y != height/2)){
				xRand = new Random();
				yRand = new Random();
				x = Math.abs(xRand.nextInt())%(width-1) +1;
				y = Math.abs(yRand.nextInt())%(height-1) +1;
				//System.out.println("fails on "+ x + " " + y);
				
			}
			levelMap[y][x] = Tile.BOX;
		} else if (type.equals("Goal")){
			while(levelMap[y][x]!= Tile.WALKABLE || (x!= width/2 && y != height/2)){
				xRand = new Random();
				yRand = new Random();
				x = Math.abs(xRand.nextInt())%(width-1) +1;
				y = Math.abs(yRand.nextInt())%(height-1) +1;
				//System.out.println("fails on "+ x + " " + y);
				
			}
			levelMap[y][x] = Tile.GOAL;
		}

	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder(height * (width + 1));
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) 
				s.append(levelMap[i][j].getIntRep());
			
			s.append('\n');
		}
		
		return s.toString();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() { //KeyInput input
		player.update(levelMap);
		
		switch (KeyInput.getPressed()) {
		case KeyEvent.VK_ESCAPE:
			System.out.println("ESCAPE");
			StateManager.setState("MENU");	
			return;
		}
		
		if (KeyInput.getPressed() == KeyEvent.VK_ESCAPE) {
		}
	}

	@Override
	public void handleInput() {
		// TODO Auto-generated method stub
	}
}
