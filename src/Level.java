import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Stack;

import javax.imageio.ImageIO;

public class Level implements GameState {
	
	private Tile levelMap[][];
	private ArrayList<Box> boxList;
	private Stack<ArrayList<Entity>> prevStates;
	
	private int width; // Width of the level
	private int height; // Height of the level
	private int tileSize;
	
	private Image tileImgs[];
	private Player player;
	private FurthestStateGen furthestState;
	
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
		prevStates = new Stack<ArrayList<Entity>>();
		setDefaultTiles();
		boxList = new ArrayList<Box>();

		levelMap = levelGen.generate(height, width, StateManager.getLevel(), tileSize, tileImgs);
		this.furthestState = new FurthestStateGen(width, height, tileSize, StateManager.getLevel(),
													levelMap, tileImgs);
		
		while(furthestState.getPlayerSpaces() == null) {
			System.out.println("RESTART");
			levelMap = levelGen.generate(height, width, StateManager.getLevel(), tileSize, tileImgs);
			furthestState = new FurthestStateGen(width, height, tileSize, StateManager.getLevel(),
													levelMap, tileImgs);
		}
		
		boxList = furthestState.getBoxList();
		makePlayer(furthestState.getPlayerSpaces());
		
	}
	
	Level(String input, int tileSize) {
		this.tileSize = tileSize;
		boxList = new ArrayList<Box>();
		byte inputArray[] = input.getBytes();
		prevStates = new Stack<ArrayList<Entity>>();
		
		// Get the width and height
		for (this.width = 0; inputArray[this.width] != '\n'; this.width++);
		this.height = inputArray.length/(width + 1);
		
		levelMap = new Tile[this.height][this.width];
		int sIndex = 0;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (inputArray[sIndex++] == Tile.BOX.getIntRep())
					boxList.add(new Box(j, i, tileImgs[2], tileSize, width, height));
				else
					levelMap[i][j] = Tile.getTile(inputArray[sIndex] - '0');
			}
			sIndex++;
		}
		
		setDefaultTiles();
		
		makePlayer(furthestState.getPlayerSpaces());
		
	}
	
	private void setDefaultTiles() {
		this.tileImgs = new Image[4];
		try {
			tileImgs[0] = ImageIO.read(getClass().getResourceAsStream("ground_solid.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			tileImgs[1] = ImageIO.read(getClass().getResourceAsStream("ground_empty.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			//todo remove [2]
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
		
		for (Box box : boxList) {
			box.draw(bbg);
		}
		
		player.draw(bbg);
	}
	
	public int getTileSize() {
		return tileSize;
	}
	
	private void makePlayer(List<List<Integer>> playerSpaces) {	
		try {
			Random r = new Random();
			List<Integer> playerSpace = playerSpaces.get(r.nextInt(playerSpaces.size()));
			player = new Player(playerSpace.get(0), playerSpace.get(1), ImageIO.read(getClass().getResourceAsStream("player.png")), tileSize, width, height);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private void undo() {
		if (!prevStates.isEmpty()) {
			ArrayList<Entity> prevState = prevStates.pop();
			boxList = new ArrayList<Box>();
			for (Entity e : prevState) {
				if (e.getClass() == Player.class) {
					player = (Player) e;
					System.out.println("o " + player.getTileX() + " " + player.getTileY());
				} else {
					boxList.add((Box)e);
				}
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder(height * (width + 1));
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (player.getTileX() == i && player.getTileY() == j) {
					s.append(Tile.PLAYER.getIntRep());
					continue;
				}
				
				boolean box_f = false;
				for (Box b : boxList) {
					if (b.getTileY() == i && b.getTileX() == j) {
						box_f = true;
						break;
					}
				}
				
				if (box_f)
					s.append(Tile.BOX.getIntRep());
				else
					s.append(levelMap[i][j].getIntRep());
			}
				
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
		player.update(levelMap, boxList);
		
		for (Box box : boxList) {
			box.update();
		}
		
		if (player.atNewTile()) {
			// save state
			ArrayList<Entity> newState = new ArrayList<Entity>();
			try {
				Player newPlayer = (Player) player.clone();
				System.out.println("n " + newPlayer.getTileX() + " " + newPlayer.getTileY());
				newState.add(newPlayer);

				for (Box b : boxList) {
					newState.add((Entity) b.clone());
				}
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			prevStates.push(newState);
		}
		
		switch (KeyInput.getPressed()) {
		case KeyEvent.VK_ESCAPE:
			//System.out.println("ESCAPE");
			StateManager.setState("MENU");	
			return;
		case KeyEvent.VK_U:
			undo();
			return;
		}
		
	}

	@Override
	public void handleInput() {
		// TODO Auto-generated method stub
	}
}
