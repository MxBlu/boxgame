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
	Level(int screenWidth, int screenHeight, int tileSize, LevelGen levelGen) {
		// 1 pixel padding so I don't need to add edge cases to generation.
		this.width = screenWidth/tileSize + 2;
		this.height = screenHeight/tileSize + 2;
		this.tileSize = tileSize;
		setDefaultTiles();

		levelMap = levelGen.generate(height, width);
		
		try {
			player = new Player(width / 2, height / 2, ImageIO.read(getClass().getResourceAsStream("player.png")), tileSize, width, height);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setLevel(StateManager.getLevel());
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

		setLevel(StateManager.getLevel());
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
