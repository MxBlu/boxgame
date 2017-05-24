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

public class FurthestStateGen {
	private Tile levelMap[][];
	private ArrayList<Box> boxList;
	
	private int width; // Width of the level
	private int height; // Height of the level
	private List<List<Integer>> playerSpaces;
	private int level;
	
	/**
	 * Creates a new level.
	 * @precondition screenWidth % tileSize == 0 && screenHeight % tileSize == 0
	 * @param screenWidth Screen width in pixels.
	 * @param screenHeight Screen height in pixels.
	 * @param tileSize Width/Height of a tile in pixels.
	 */
	public FurthestStateGen(int width, int height, int tileSize, int level, Tile[][] levelMap, 
							Image tileImgs[]) {
						
		// 1 pixel padding so I don't need to add edge cases to generation.
		this.width = width;
		this.height = height;
		this.boxList = new ArrayList<Box>();
		this.levelMap = levelMap;
		this.level = level;
		
		List<List<Integer>> boxGoals = placeBoxes(tileSize, tileImgs);
		
		makeFurthestState(boxGoals);
	}
	
	private List<List<Integer>> placeBoxes(int tileSize, Image tileImgs[]) {
		// places boxes on the goals
		this.boxList = new ArrayList<Box>();
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
		
		return boxGoals;
	}
	
	private void makeFurthestState(List<List<Integer>> boxGoals) {
		List<State> stateList = makeStateList(makeStartState(), boxGoals);
		List<Integer> possibleStates = checkStateList(stateList);
		
		if (possibleStates == null) {
			this.playerSpaces = null;
		} else {
			getBestState(stateList, possibleStates);
		}
	}
	
	public List<List<Integer>> getPlayerSpaces() {
		return this.playerSpaces;
	}
	
	public ArrayList<Box> getBoxList() {
		return this.boxList;
	}
	
	private State makeStartState() {		
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
		
		State startState = new State(x, y, new ArrayList<Box>(boxList), levelMap, height, width, null, -1, -1);
		
		return startState;
	}
	
	private List<State> makeStateList(State startState, List<List<Integer>> boxGoals) {
		List<State> startList = new ArrayList<State>();
		startList.add(startState);
		
		List<State> resultList = new ArrayList<State>(startList);
		List<State> prevResultList = null;
		List<State> prevTempList = null;
		List<State> tempList = new ArrayList<State>();
		
		Integer depthLimit[] = {100, 25, 17, 12, 10, 8};
		
		for (int depth = 1; depth < depthLimit[level] && resultList.size() < 4000; depth++) {
			if (prevTempList == null) {
				prevTempList = new ArrayList<State>(startList);
				tempList = prevTempList;
			}
			
			for (int k = 0; k < prevTempList.size(); k++) {
				if (!(tempList.contains(prevTempList.get(k)))) {
					tempList.add(prevTempList.get(k).copy());
				}
			}
			
			prevResultList = new ArrayList<State>();
			for (int i = 0; i < resultList.size(); i++) {
				prevResultList.add(resultList.get(i).copy());
			}
			
			resultList = deepen(resultList, tempList, depth, boxGoals);
			
			if (resultList.size() == 0) break;
			prevTempList = expand(prevTempList, boxGoals);
			
			System.out.println("depth " + depth);
		}
		
		return prevResultList;
	}
	
	private List<Integer> checkStateList(List<State> resultList) {
		boolean flag = false;
		int randState = 0;
		int k = 0;
		Random r = new Random();
		
		List<Integer> possibleStates = new ArrayList<Integer>();
		for (int i = 0; i < 60 && k < 100; i++) {
			System.out.println("forever");
			flag = true;
			randState = r.nextInt(resultList.size());
			List<Box> tempBoxList = new ArrayList<Box>(resultList.get(randState).getBoxList());
			for (int l = 0; l < boxList.size(); l++) {
				if (levelMap[tempBoxList.get(l).getTileY()][tempBoxList.get(l).getTileX()] == Tile.GOAL) {
					flag = false;
				}
			}
			if (flag) {
				System.out.println(resultList.get(randState).getNumBoxLines());
				possibleStates.add(randState);
			} else {
				i--;
			}
			k++;
		}
		
		
		if (possibleStates.size() == 0) {
			return null;
		}
		
		return possibleStates;
	}
		
	private void getBestState(List<State> stateList, List<Integer> possibleStates) {	
		int pos = 0;
		double currScore = -1;
		double currDiffScore = -1;
		
		for (int j = 0; j < possibleStates.size(); j++) {
			if (currScore == -1) {
				this.boxList = new ArrayList<Box>(stateList.get(possibleStates.get(j)).getBoxList());
				currScore = calculateScore(stateList.get(possibleStates.get(j)));
				currDiffScore = calculateDifficulty(stateList.get(possibleStates.get(j)));
			} else {
				double newScore = calculateScore(stateList.get(possibleStates.get(j)));
				double newDiffScore = calculateDifficulty(stateList.get(possibleStates.get(j)));
				if ((currScore < newScore) || ((currScore == newScore) && (currDiffScore < newDiffScore))) {
					this.boxList = new ArrayList<Box>(stateList.get(possibleStates.get(j)).getBoxList());
					currScore = newScore;
					currDiffScore = newDiffScore;
					pos = j;
				}
			}
		}
		
		System.out.println("currMaxBoxLines " + currScore);
		this.playerSpaces = stateList.get(possibleStates.get(pos)).getPlayerSpaces();
	}
	
	private double calculateScore(State state) {
		int touchingBoxes = 0;
		if (boxList.size() != 1) {
			for (int i = 0; i < boxList.size(); i++) {
				if (checkBoxList(boxList.get(i).getTileX() + 1, boxList.get(i).getTileY(), boxList) ||
						checkBoxList(boxList.get(i).getTileX() - 1, boxList.get(i).getTileY(), boxList) ||
						checkBoxList(boxList.get(i).getTileX(), boxList.get(i).getTileY() + 1, boxList) ||
						checkBoxList(boxList.get(i).getTileX(), boxList.get(i).getTileY() - 1, boxList)) {
					touchingBoxes++;
				}
			}
		}
		
		double score = 100 * (state.getMinPathLength() + 4 * state.getNumBoxLines()) + 30 * touchingBoxes;
		
		return score;
	}
	
	private double calculateDifficulty(State state) {
		int numBoxLines = state.getNumBoxLines();
		double diffScore = numBoxLines * Math.log(numBoxLines) - (numBoxLines/state.getMinPathLength());
		
		return diffScore;
	}
	
	private List<State> deepen (List<State> prevResults, List<State> tempList, int depth,
								List<List<Integer>> boxGoals) {
		System.out.println(prevResults.size());
		List<State> resultList = expand(prevResults, boxGoals);
		
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
				if (canReachBox(curr, stateBoxList.get(boxNum))) {
					for (int i = 0; i < 4; i++) {
						Box box = stateBoxList.get(boxNum);
						
						if (!(checkBoxList(box.getTileX() + directions[i][0], box.getTileY() + directions [i][1], stateBoxList)) &&
								!((boxGoals.get(boxNum).get(0) == (box.getTileX() + directions[i][0])) && 
								(boxGoals.get(boxNum).get(1) == (box.getTileY() + directions[i][1])))) {
							box.setTilePos(box.getTileX() + directions[i][0], box.getTileY() + directions[i][1]);
							int newPlayerX = box.getTileX() + directions[i][0];
							int newPlayerY = box.getTileY() + directions[i][1];
							boolean flag = true;

							if (!canReachBox(curr, box)) 
								flag = false;

							if (levelMap[newPlayerY][newPlayerX] == Tile.WALL ||
									checkBoxList(newPlayerX, newPlayerY, stateBoxList) ||
									levelMap[box.getTileY()][box.getTileX()] == Tile.WALL) {
									flag = false;
							}
							
							if (flag) {
								List<Box> copyBoxList = new ArrayList<Box>();
								for (int j = 0; j < stateBoxList.size(); j++) {
									copyBoxList.add(stateBoxList.get(j).copy());
								}
								State newState = new State(newPlayerX, newPlayerY, copyBoxList,
															levelMap, height, width, curr, i, boxNum);
								if (!(newStates.contains(newState))) {
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

}
