import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
	private List<List<Integer>> playerSpaces;
	
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
		prevStates = new Stack<ArrayList<Entity>>();
		setDefaultTiles();
		boxList = new ArrayList<Box>();

		levelMap = levelGen.generate(height, width, StateManager.getLevel());
		
		/*
		for (int i = 0; i < StateManager.getLevel(); i++)
			placeBox();
		*/
		makeFarthestState(StateManager.getLevel());
		
		
		makePlayer();
		
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
		
		makePlayer();
		
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
	
	/*
	private void placeBox() {
		Random r = new Random();
		
		while (true) {
			int x = r.nextInt(width);
			int y = r.nextInt(height);
			
			if (levelMap[y][x] == Tile.WALL || levelMap[y][x] == Tile.GOAL ||
					checkBoxList(x, y) == true) {
				continue;
			}
			if (	(levelMap[y + 1][x] == Tile.WALL && levelMap[y][x + 1] == Tile.WALL) ||
					(levelMap[y][x + 1] == Tile.WALL && levelMap[y - 1][x] == Tile.WALL) ||
					(levelMap[y - 1][x] == Tile.WALL && levelMap[y][x - 1] == Tile.WALL) ||
					(levelMap[y][x - 1] == Tile.WALL && levelMap[y + 1][x] == Tile.WALL))
				continue;
			
			boxList.add(new Box(x, y, tileImgs[2], tileSize, width, height));
			break;
		}
	}
	*/
	
	private void makeFarthestState(int level) {
		// places boxes on the goals
		List<List<Integer>> boxGoals = new ArrayList<List<Integer>>();
		for (int x = 0; x < width; x++) {
			for (int y = 0 ; y < height; y++) {
				if (levelMap[y][x] == Tile.GOAL)
					boxList.add(new Box(x, y, tileImgs[2], tileSize, width, height));
					List<Integer> boxGoal = new ArrayList<Integer>();
					boxGoal.add(x);
					boxGoal.add(y);
					boxGoals.add(boxGoal);
				if (boxList.size() == level) break;
			}
			if (boxList.size() == level) break;
		}
		
		// place the player on a walkable tile, where it can access
		Random r = new Random();
		int x, y;
		x = y = 0;
		
		while (true) {
			x = r.nextInt(width);
			y = r.nextInt(height);
			
			if (levelMap[y][x] == Tile.WALL || checkBoxList(x, y, this.boxList))
				continue;
			break;
		}
		
		State startState = new State(x, y, new ArrayList<Box>(boxList), levelMap, height, width);
		List<State> startList = new ArrayList<State>();
		startList.add(startState);
		List<State> resultList = new ArrayList<State>(startList);
		List<State> prevList = null;
		List<State> prevTempList = null;
		List<State> tempList = null;
		int depth = 1;
		
		while (depth < 15) {
			if (prevTempList == null) {
				prevTempList = new ArrayList<State>(startList);
				tempList = prevTempList;
			}
			for (int k = 0; k < prevTempList.size(); k++) {
				if (!(tempList.contains(prevTempList.get(k)))) {
					tempList.add(prevTempList.get(k).copy());
				}
			}
			prevList = new ArrayList<State>();
			for (int i = 0; i < resultList.size(); i++) {
				prevList.add(resultList.get(i).copy());
			}
			resultList = deepen(resultList, tempList, depth, boxGoals);
			if (resultList.size() == 0) break;
			prevTempList = expand(prevTempList, boxGoals);
			depth++;
			System.out.println("depth " + depth);
		}
		
		boolean flag = false;
		int randBox = 0;
		int i = 0;
		while (!flag && i < 30) {
			i++;
			System.out.println("forever");
			flag = true;
			randBox = r.nextInt(prevList.size());
			this.boxList = new ArrayList<Box>(prevList.get(randBox).getBoxList());
			for (int l = 0; l < boxList.size(); l++) {
				if (levelMap[boxList.get(l).getTileY()][boxList.get(l).getTileX()] == Tile.GOAL) {
					flag = false;
				}
			}
		}
		if (i == 30) {
			System.out.println("Error");
		}
		
		this.playerSpaces = prevList.get(randBox).getPlayerSpaces();
		
	}
	
	private List<State> deepen (List<State> prevResults, List<State> tempList, int depth, List<List<Integer>> boxGoals) {
		System.out.println(prevResults.size());
		List<State> resultList = expand(prevResults, boxGoals);
		//List<State> tempList = new ArrayList<State>();
		System.out.println("resultListSize " + resultList.size());
		resultList.removeAll(tempList);
			System.out.println("tempListSize " + tempList.size());
			System.out.println("resultListSize " + resultList.size());
		
		return resultList;
	}
	
	private List<State> expand(List<State> states, List<List<Integer>> boxGoals) {
		Integer[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
		
		List<State> newStates = new ArrayList<State>();
		
		for (ListIterator<State> it = states.listIterator(); it.hasNext();) {
			State curr = it.next();
			List<Box> stateBoxList = curr.getBoxList();
			
			for (int boxNum = 0; boxNum < boxList.size(); boxNum++) {
				//System.out.println(stateBoxList.get(boxNum));
				if (canReachBox(curr, stateBoxList.get(boxNum))) {
					//System.out.println("Can reach box");
					for (int i = 0; i < 4; i++) {
						Box box = stateBoxList.get(boxNum);
						if (!(checkBoxList(box.getTileX() + directions[i][0], box.getTileY() + directions [i][1], stateBoxList)) &&
								!((boxGoals.get(boxNum).get(0) == (box.getTileX() + directions[i][0])) && 
								(boxGoals.get(boxNum).get(1) == (box.getTileY() + directions[i][1])))) {
							box.setTilePos(box.getTileX() + directions[i][0], box.getTileY() + directions[i][1]);
							int newPlayerX = box.getTileX() + directions[i][0];
							int newPlayerY = box.getTileY() + directions[i][1];
							boolean flag = true;
							//System.out.println(box.getTileX()  + " " + box.getTileY());
							//System.out.println(stateBoxList.get(boxNum).getTileX() + " " + stateBoxList.get(boxNum).getTileY());
							if (!canReachBox(curr, box)) 
								flag = false;
							//if player is on a box or on a wall
							// if box is on a wall or another box
							//System.out.println("1" + flag);
							if (levelMap[newPlayerY][newPlayerX] == Tile.WALL ||
									checkBoxList(newPlayerX, newPlayerY, stateBoxList) ||
									levelMap[box.getTileY()][box.getTileX()] == Tile.WALL) {
									flag = false;
							}
							
							//System.out.println("2" + flag);
							if (flag) {
								//System.out.print("ENTERED flag");
								List<Box> copyBoxList = new ArrayList<Box>();
								for (int j = 0; j < stateBoxList.size(); j++) {
									copyBoxList.add(stateBoxList.get(j).copy());
								}
								State newState = new State(newPlayerX, newPlayerY, copyBoxList,
															levelMap, height, width);
								if (!(newStates.contains(newState))) {
									//System.out.println("Did add");
									newStates.add(newState);
								}
							}
							
							box.setTilePos(box.getTileX() - directions[i][0], box.getTileY() - directions[i][1]);
						}
					}
				}
			}
		}
		
		return newStates;
	}
	
	private boolean canReachBox (State state, Box box) {
		Integer[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
		boolean flag = false;
		
		for (int i = 0; i < 4 ; i++) {
			box.setTilePos(box.getTileX() + directions[i][0], box.getTileY() + directions[i][1]);
			if (state.accessCoords(box.getTileX(), box.getTileY())) flag = true;
			box.setTilePos(box.getTileX() - directions[i][0], box.getTileY() - directions[i][1]);
			if (flag) break;
		}
		return flag;
	}
	
	private void makePlayer() {
		/*
		int x = width/2;
		int y = height/2;
		
		if (levelMap[y][x] == Tile.WALL || checkBoxList(x, y, this.boxList) == true) {
			Random r = new Random();
				
			while (true) {
				x = r.nextInt(width);
				y = r.nextInt(height);
				
				if (levelMap[y][x] == Tile.WALL || checkBoxList(x, y, this.boxList) == true)
					continue;

				break;
			}
		}
		*/
		
		try {
			Random r = new Random();
			List<Integer> playerSpace = playerSpaces.get(r.nextInt(playerSpaces.size()));
			player = new Player(playerSpace.get(0), playerSpace.get(1), ImageIO.read(getClass().getResourceAsStream("player.png")), tileSize, width, height);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	// someone should probably change this, since it already has something similar
	// in class Player... :v
	private boolean checkBoxList(int x, int y, List<Box> boxList1) {
		for (int i = 0; i < boxList1.size(); i++) {
			Box box = boxList1.get(i);
			if (box.getTileX() == x && box.getTileY() == y) {
				return true;
			}
		}
		//System.out.println("false");
		return false;
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
