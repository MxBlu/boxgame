import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

public class State {
	private int playerX; // player x coordinate
	private int playerY; // player y coordinate
	
	private List<Box> boxList; // list of boxes on level
	private List<List<Integer>> playerSpaces; // walkable partition for the player
	
	private int boxNum; // the index of the box that was pulled in this state
	private int pullDir; // the index of the direction of the pull
	private int numBoxLines; // the number of boxLines that were done
	private double minPathLength; // the minimum path length
	
	public State (int x, int y, List<Box> boxList, Tile[][] levelMap, int height, int width, State prevState,
					int currPullDir, int currBoxNum) {
		this.playerX = x;
		this.playerY = y;
		this.boxList = boxList;
		this.playerSpaces = checkPlayerSpaces(levelMap, height, width);
		
		// checks if this is a startState by checking prevState
		if (prevState == null) {
			this.boxNum = -1;
			this.pullDir = -1;
			this.numBoxLines = 2;
			this.minPathLength = 0;
		} else {
			// checks if this is a state first expanded from the startState, by checking
			// the boxNum
			if (prevState.boxNum != -1) {
				// sets the boxLines to the same as previous state's
				this.numBoxLines = prevState.getNumBoxLines();
				// checks if the current pullDirr and boxNum are the same
				if (!(prevState.boxNum == currBoxNum && prevState.pullDir == currPullDir)) {
					(this.numBoxLines)++;
				}
			}
			
			// gets the minimum path length from the previous state and adds onto
			// the shortest distance from previous location to current location
			// using Pythagoras' theorem
			this.minPathLength = prevState.minPathLength + 
									Math.sqrt(Math.pow(this.playerX - prevState.playerX, 2) +
									Math.pow(this.playerY - prevState.playerY, 2));
			this.boxNum = currBoxNum;
			this.pullDir = currPullDir;
		}
	}
	
	public State (int playerX, int playerY, List<Box> boxList, List<List<Integer>> playerSpaces,
					int boxNum, int pullDir, int numBoxLines) {
		this.playerX = playerX;
		this.playerY = playerY;
		this.boxList = boxList;
		this.playerSpaces = playerSpaces;
		this.boxNum = boxNum;
		this.pullDir = pullDir;
		this.numBoxLines = numBoxLines;
	}
	
	/* Returns the list of boxes in this state */
	public List<Box> getBoxList() {
		return boxList;
	}
	
	/* Returns the number of boxLines */
	public int getNumBoxLines() {
		return numBoxLines;
	}
	
	/* Returns the minimum action path taken by the player */
	public double getMinPathLength() {
		return this.minPathLength;
	}
	
	/* Returns the player's x coordinate */
	public int getPlayerX() {
		return playerX;
	}
	
	/* Returns the player's y coordinate */
	public int getPlayerY() {
		return playerY;
	}
	
	/* Returns the list of coordinates which represent where the player
	 * can walk in the level currently */
	public List<List<Integer>> getPlayerSpaces() {
		return playerSpaces;
	}
	
	/* Checks if the given coordinates is within the playerSpaces
	 * Returns true if it's in the playerSpace
	 * Otherwise, returns false */
	public boolean accessCords(int x, int y) {
		// goes through the playerSpaces
		for (ListIterator<List<Integer>> it = playerSpaces.listIterator(); it.hasNext();) {
			List<Integer> space = it.next();
			// checks if the coordinates match with the current space
			if (space.get(0) == x && space.get(1) == y) return true;
		}
		return false;
	}
	
	/* Gets the spaces around the player which it can access currently
	 * Returns the list of these coordinates */
	private List<List<Integer>> checkPlayerSpaces(Tile[][] levelMap, int height, int width) {
		List<List<Integer>> spacePartition = new ArrayList<List<Integer>>();
		int intMap[][] = new int [height][width];
		
		// uses intMap to indicate where it is blocked off by a wall or a box
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (levelMap[i][j] == Tile.WALL || checkBoxList(j, i))
					intMap[i][j] = 1;
				else
					intMap[i][j] = 0;
			}
		}
		
		// IntPair represents the coordinates
		class IntPair { 
			int i; 
			int j; 
			IntPair(int i, int j) { 
				this.i = i; 
				this.j = j;
			} 
			
			public boolean equals(Object o) {
				IntPair e = (IntPair) o;
				return (this.i == e.i && this.j == e.j);
			}
		}
		
		Queue<IntPair> open = new LinkedList<IntPair>();
		List<IntPair> closed = new LinkedList<IntPair>();
		
		// adds onto the open queue the player's coordinates
		open.add(new IntPair(playerY, playerX));
		
		// continues while open is not empty
		while (!open.isEmpty()) {
			// gets the first element of open
			IntPair curr = open.poll();
			// adds curr to closed
			closed.add(curr);
			
			// adds curr as a coordinate on spacePartition
			List<Integer> playerSpace = new ArrayList<Integer>();
			playerSpace.add(curr.j);
			playerSpace.add(curr.i);
			spacePartition.add(playerSpace);
			// marks the coordinate as having been visited
			intMap[curr.i][curr.j] = 2;
			
			// makes intPairs of all the coordinates in all 4 directions
			IntPair newStates[] = new IntPair[4];
			newStates[0] = new IntPair(curr.i + 1, curr.j);
			newStates[1] = new IntPair(curr.i - 1, curr.j);
			newStates[2] = new IntPair(curr.i, curr.j - 1);
			newStates[3] = new IntPair(curr.i, curr.j + 1);
			
			// goes through the directions
			for (int i = 0; i < 4; i++) {
				// checks if closed or open has the intPair, or if the intPair isn't \
				// an obstacle and wasn't visited
				if (closed.contains(newStates[i]) || open.contains(newStates[i]) ||
						intMap[newStates[i].i][newStates[i].j] != 0)
					continue;
				
				// adds the intPair to open
				open.add(newStates[i]);
			}
		}
		
		return spacePartition;
	}

	/* Checks if coordinates is in boxList
	 * Returns true if it is in boxList
	 * Otherwise, return false */
	private boolean checkBoxList(int x, int y) {
		// goes through boxList
		for (int i = 0; i < boxList.size(); i++) {
			Box box = boxList.get(i);
			// checks if the box's coordnates matches the given coordinates
			if (box.getTileX() == x && box.getTileY() == y) {
				return true;
			}
		}
		return false;
	}

	/* Makes and returns a copy of the state */
	public State copy() {
		// copies the boxList
		List<Box> copyBoxList = new ArrayList<Box>();
		for (int i = 0; i < boxList.size(); i++) {
			copyBoxList.add(boxList.get(i).copy());
		}
		
		// copies the playerSpaces
		List<List<Integer>> copyPlayerSpaces = new ArrayList<List<Integer>>();
		for (int i = 0; i < playerSpaces.size(); i++) {
			List<Integer> copySpace = new ArrayList<Integer>();
			copySpace.add(playerSpaces.get(i).get(0));
			copySpace.add(playerSpaces.get(i).get(1));
			copyPlayerSpaces.add(copySpace);
		}
		
		return new State(this.playerX, this.playerY, copyBoxList, copyPlayerSpaces,
				this.boxNum, this.pullDir, this.numBoxLines);
	}
	
	@Override
	/* Checks if state is equivalent to another state
	 * Returns true if it is equivalent
	 * Otherwise, returns false */
	public boolean equals(Object o) {
		// checks if o is an instance of State
		if (o instanceof State) {
			State other = (State) o;
			
			// goes through the boxList
			for (int i = 0; i < boxList.size(); i++) {
				// checks if all the coordinates in the boxList match
				if (!(this.boxList.get(i).getTileX() == other.getBoxList().get(i).getTileX() &&
						this.boxList.get(i).getTileY() == other.getBoxList().get(i).getTileY())){
					return false;
				}
			}
			
			// checks if playerSpaces are the same size
			if (!(this.playerSpaces.size() == other.getPlayerSpaces().size()))
				return false;
			
			return true;
		}
		return false;
	}
}
