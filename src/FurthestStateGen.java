import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class FurthestStateGen {
	private static final Integer directions[][] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
	//private static final Integer depthLimit[] = {-1, 40, 35, 20, 12, 11}; // Literal Depth limit
	private static final Integer depthLimit[] = {-1, 2, 3, 3, 4, 4}; // Time Depth limit
	
	private Tile levelMap[][]; // layout of the level
	private ArrayList<Box> boxList; // layout of the boxes on the level
	
	private int width; // width of the level
	private int height; // height of the level
	private List<List<Integer>> playerSpaces; // walkable partition for the player
	private int level; // gives number of goals on the maze
	
	public FurthestStateGen(int width, int height, int level, Tile[][] levelMap, ArrayList<Box> boxList) {
		this.width = width;
		this.height = height;
		this.boxList = new ArrayList<Box>(boxList);
		this.levelMap = levelMap;
		this.level = level;
		
		// gets the coordinates of the boxes placed on the level
		List<List<Integer>> boxGoals = placeBoxes();
		
		// starts searching for the furthest state of the level
		makeFurthestState(boxGoals);
	}
	
	/* Places the boxes on the level
	 * Returns the coordinates of the boxes */
	private List<List<Integer>> placeBoxes() {
		// places boxes on the goals
		List<List<Integer>> boxGoals = new ArrayList<List<Integer>>();
		
		// goes through the different coordinates of the level
		for (int x = 0; x < width; x++) {
			for (int y = 0 ; y < height; y++) {
				if (levelMap[y][x] == Tile.GOAL) {
					// gets the goal's coordinates as the boxes starting coordinates
					List<Integer> boxGoal = new ArrayList<Integer>();
					boxGoal.add(x);
					boxGoal.add(y);
					boxGoals.add(boxGoal);
				}
				// checks if all the goals have been covered by boxes
				if (boxGoals.size() == level) break;
			}
			if (boxGoals.size() == level) break;
		}
		
		return boxGoals;
	}
	
	/* Finds the furthest viable state for the position of the boxes on the level */
	private void makeFurthestState(List<List<Integer>> boxGoals) {
		// gets the list of states of a certain depth in the furthestState traversal
		List<State> stateList = makeStateList(makeStartState(), boxGoals);
		// gets the list of indexes of stateList that meets the conditions of being
		// applicable as a playable state
		List<Integer> possibleStates = checkStateList(stateList);
		
		// checks if there are no possibleStates
		if (possibleStates == null) {
			this.playerSpaces = null;
		} else {
			// gets the best state from stateList
			getBestState(stateList, possibleStates);
		}
	}
	
	/* Gets the walkable partition for the player */
	public List<List<Integer>> getPlayerSpaces() {
		return this.playerSpaces;
	}
	
	/* Returns the array of boxes on the level */
	public ArrayList<Box> getBoxList() {
		return this.boxList;
	}
	
	/* Gets the starting state before the furthestState traversal */
	private State makeStartState() {		
		Random r = new Random();
		int x, y;
		x = y = 0;
		
		while (true) {
			// gets a random x, y coordinate on the level
			x = r.nextInt(width);
			y = r.nextInt(height);
			
			// checks if the coordinates are walkable (not a wall nor a box)
			if (levelMap[y][x] == Tile.WALL || checkBoxList(x, y, this.boxList))
				continue;
			break;
		}
		
		// gets the start state
		State startState = new State(x, y, new ArrayList<Box>(boxList), levelMap, height, width, null, -1, -1);
		
		return startState;
	}
	
	/* Gets the  list of states that represent the furthest viable placement of the boxes
	 * from the goals
	 * This is the furthestState traversal
	 * Returns stateList */
	private List<State> makeStateList(State startState, List<List<Integer>> boxGoals) {
		List<State> startList = new ArrayList<State>();
		startList.add(startState);
		
		List<State> prevResultList = null;
		List<State> resultList = new ArrayList<State>(startList);
		List<State> prevTempList = null;
		List<State> tempList = new ArrayList<State>();
		
		// goes until it reaches the allocated depthLimit or it goes above a certain list size
		// for that type of level
		LocalDateTime start = LocalDateTime.now();
		for (int depth = 1; ChronoUnit.SECONDS.between(start, LocalDateTime.now()) < depthLimit[level] &&
				(((level > 3) && (resultList.size() < 1500)) ||
					((level < 4) && (resultList.size() < 3000))); depth++) {
			// checks if the prevTempList is empty
			// happens when it just enters the loop
			if (prevTempList == null) {
				prevTempList = new ArrayList<State>(startList);
				tempList = prevTempList;
			}
			
			// goes through prevTempList
			for (int k = 0; k < prevTempList.size(); k++) {
				// checks if tempList doesn't contain the states in prevTempList
				if (!(tempList.contains(prevTempList.get(k)))) {
					// adds a copy of the state to tempList
					tempList.add(prevTempList.get(k).copy());
				}
			}
			
			prevResultList = new ArrayList<State>();
			// goes through resultList and adds the states to prevResultList
			for (int i = 0; i < resultList.size(); i++) {
				prevResultList.add(resultList.get(i).copy());
			}
			
			// expands resultList and removes states from tempList from resultList
			resultList = deepen(resultList, tempList, depth, boxGoals);
			
			// exit when resultList is empty
			if (resultList.size() == 0) break;
			// expands prevTempList
			prevTempList = expand(prevTempList, boxGoals);
			
			System.out.println("depth " + depth);
			System.out.println("Time taken: " + ChronoUnit.SECONDS.between(start, LocalDateTime.now()));
		}
		
		return prevResultList;
	}

	/* Checks if any states in the resultList is playable and is of a certain score level,
	 * if it is a level of the hard difficulty
	 * Returns a list of indexes of the states that fulfills this condition
	 * Returns a null list if no state fulfills this condition */
	private List<Integer> checkStateList(List<State> resultList) {
		boolean goalCheck = false;
		boolean currScore = false;
		
		int randState = 0;
		int i = 0;
		Random r = new Random();
		
		List<Integer> possibleStates = new ArrayList<Integer>();
		
		// goes through until possibleStates' size reaches 60 or it iterates 100 times
		for (int possibleSize = 0; possibleSize < 60 && i < 100; possibleSize++) {
			//System.out.println("forever");
			goalCheck = false;
			// gets a random index from resultList
			randState = r.nextInt(resultList.size());
			// gets the boxList from the state with that index from resultList
			List<Box> tempBoxList = new ArrayList<Box>(resultList.get(randState).getBoxList());
			
			// goes through boxList
			for (int l = 0; l < boxList.size(); l++) {
				// checks if the box's coordinates is on the goal
				if (levelMap[tempBoxList.get(l).getTileY()][tempBoxList.get(l).getTileX()] == Tile.GOAL) {
					goalCheck = true;
				}
			}
			
			// checks if any of the boxes on tempBoxList are on the goal
			if (!goalCheck) {
				// check if the calculated level score is above 5000 and a difficulty
				// score of 6.5 (value determined through experimentation)
				if (5500 < calculateScore(resultList.get(randState)) && 6.5 < calculateDifficulty(resultList.get(randState))) {
					currScore = true;
				}
				// adds the state's index onto possibleStates
				possibleStates.add(randState);
			} else {
				possibleSize--;
			}
			
			i++;
		}
		
		// checks if possibleStates is empty or currScore is true when boxList size is 4 or 5
		if (possibleStates.size() == 0 || (!(currScore) && ((boxList.size() == 4) || (boxList.size() == 5)))) {
			return null;
		}
		
		return possibleStates;
	}
	
	/* Gets the best state from the possibleStates indexes for stateList
	 * Uses scoring systems to determine this */
	private void getBestState(List<State> stateList, List<Integer> possibleStates) {	
		int pos = 0;
		double currScore = -1;
		double currDiffScore = -1;
		
		// goes through all the indexes in possibleStates
		for (int j = 0; j < possibleStates.size(); j++) {
			// checks if currScore hasn't been set
			if (currScore == -1) {
				// sets state's boxList as the current ideal boxList
				this.boxList = new ArrayList<Box>(stateList.get(possibleStates.get(j)).getBoxList());
				currScore = calculateScore(stateList.get(possibleStates.get(j)));
				currDiffScore = calculateDifficulty(stateList.get(possibleStates.get(j)));
			} else {
				// gets the scores for the current state
				double newScore = calculateScore(stateList.get(possibleStates.get(j)));
				double newDiffScore = calculateDifficulty(stateList.get(possibleStates.get(j)));
				
				// compares the scores with the current set scores
				if (currDiffScore < newDiffScore) {
					// sets state's boxList as the current ideal boxList
					this.boxList = new ArrayList<Box>(stateList.get(possibleStates.get(j)).getBoxList());
					currScore = newScore;
					currDiffScore = newDiffScore;
					pos = j;
				}
			}
		}
		
		System.out.println("currMaxBoxLines " + currScore);
		System.out.println("currMaxBoxLines " + currDiffScore);
		System.out.println("currMaxBoxLines " + stateList.get(possibleStates.get(pos)).getMinPathLength());
		// sets the current ideal state's playerSpaces
		this.playerSpaces = stateList.get(possibleStates.get(pos)).getPlayerSpaces();
	}
	
	/* Calculates the state's level score, based on certain conditions
	 * Returns this score */
	private double calculateScore(State state) {
		int touchingBoxes = 0;
		
		// gets how many boxes are touching other boxes
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
		
		// calculates the state's level score based on the length of their minimum action path,
		// the number of boxLines, and the number of boxes touching other boxes
		double score = 100 * (state.getMinPathLength() + 4 * state.getNumBoxLines()) + 30 * touchingBoxes;
		
		return score;
	}
	
	/* Calculates the state's difficulty score, based on certain conditions
	 * Returns this score */
	private double calculateDifficulty(State state) {
		int numBoxLines = state.getNumBoxLines();
		
		// calculates based of boxLines and the length of the state's minimum action path
		double diffScore = numBoxLines * Math.log10(numBoxLines) + Math.log10(20) - (numBoxLines/state.getMinPathLength());
		
		return diffScore;
	}

	/* Initiates the resultList expansion and element removal
	 * Returns a list of states */
	private List<State> deepen (List<State> prevResults, List<State> tempList, int depth,
								List<List<Integer>> boxGoals) {
		// expands the resultList
		List<State> resultList = expand(prevResults, boxGoals);
		// removes the tempList from resultList
		resultList.removeAll(tempList);
		
		return resultList;
	}
	
	/* Expands the list of states using the state's playerSpaces
	 * Returns a list of states that is expansion of the original list */
	private List<State> expand(List<State> states, List<List<Integer>> boxGoals) {
		List<State> newStates = new ArrayList<State>();
		
		// goes through all of states
		for (ListIterator<State> it = states.listIterator(); it.hasNext();) {
			State curr = it.next();
			List<Box> stateBoxList = curr.getBoxList();
			
			// goes through all of boxList
			for (int boxNum = 0; boxNum < boxList.size(); boxNum++) {
				// checks if the player can reach the box from current position
				if (canReachBox(curr, stateBoxList.get(boxNum))) {
					// goes through all the directions that a player can go through
					for (int i = 0; i < 4; i++) {
						Box box = stateBoxList.get(boxNum);
						
						// checks if there is a box located in the intended location to move into, and if the intended
						// move makes it move go to it's original goal position
						if (!(checkBoxList(box.getTileX() + directions[i][0], box.getTileY() + directions [i][1], stateBoxList)) &&
								!((boxGoals.get(boxNum).get(0) == (box.getTileX() + directions[i][0])) && 
								(boxGoals.get(boxNum).get(1) == (box.getTileY() + directions[i][1])))) {
							// sets the new tile position based off pull direction
							box.setTilePos(box.getTileX() + directions[i][0], box.getTileY() + directions[i][1]);
							// resets the player's position based off new box position
							int newPlayerX = box.getTileX() + directions[i][0];
							int newPlayerY = box.getTileY() + directions[i][1];
							boolean flag = true;

							// checks if the player can still reach the box from the new position
							if (!canReachBox(curr, box)) 
								flag = false;

							// checks if the player is on a waklable area
							if (levelMap[newPlayerY][newPlayerX] == Tile.WALL ||
									checkBoxList(newPlayerX, newPlayerY, stateBoxList) ||
									levelMap[box.getTileY()][box.getTileX()] == Tile.WALL) {
									flag = false;
							}
							
							if (flag) {
								List<Box> copyBoxList = new ArrayList<Box>();
								
								// copies over all of stateBoxList
								for (int j = 0; j < stateBoxList.size(); j++) {
									copyBoxList.add(stateBoxList.get(j).copy());
								}
								
								// makes a new state containing the data of the new coordinates of the box
								// and the player
								State newState = new State(newPlayerX, newPlayerY, copyBoxList,
															levelMap, height, width, curr, i, boxNum);
								// checks if newStates doesn't have the newState
								if (!(newStates.contains(newState))) {
									// adds the newState onto newStates
									newStates.add(newState);
								}
							}
							
							// resets the box position
							box.setTilePos(box.getTileX() - directions[i][0], box.getTileY() - directions[i][1]);
						}
					}
				}
			}
		}
		
		return newStates;
	}

	/* Checks if the player can reach the box
	 * Returns true if it can reach the box
	 * Otherwise, returns false */
	private boolean canReachBox (State state, Box box) {
		boolean flag = false;
		
		// goes through all of the directions from the box
		for (int i = 0; i < 4 ; i++) {
			box.setTilePos(box.getTileX() + directions[i][0], box.getTileY() + directions[i][1]);
			// checks if the box is within the playerSpaces of the player
			if (state.accessCords(box.getTileX(), box.getTileY()))
				flag = true;
			// resets the box's original position
			box.setTilePos(box.getTileX() - directions[i][0], box.getTileY() - directions[i][1]);
			if (flag) break;
		}
		return flag;
	}

	/* Checks if the given coordinates has the box 
	 * Returns true if a box is on the given coordinate
	 * Otherwise, returns false */
	private boolean checkBoxList(int x, int y, List<Box> boxList1) {
		// goes through boxList1
		for (int i = 0; i < boxList1.size(); i++) {
			Box box = boxList1.get(i);
			// checks if the box's coordinates matches the given coordinates
			if (box.getTileX() == x && box.getTileY() == y) {
				return true;
			}
		}
		return false;
	}

}
