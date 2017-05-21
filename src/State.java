import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

public class State {
	private int playerX;
	private int playerY;
	private List<Box> boxList;
	private List<List<Integer>> playerSpaces;
	
	public State (int x, int y, List<Box> boxList, Tile[][] levelMap, int height, int width) {
		this.playerX = x;
		this.playerY = y;
		this.boxList = boxList;
		this.playerSpaces = checkPlayerSpaces(levelMap, height, width);
	}
	
	public State (int playerX, int playerY, List<Box> boxList, List<List<Integer>> playerSpaces) {
		this.playerX = playerX;
		this.playerY = playerY;
		this.boxList = boxList;
		this.playerSpaces = playerSpaces;
	}
	
	public List<Box> getBoxList() {
		return boxList;
	}
	
	public int getPlayerX() {
		return playerX;
	}
	
	public int getPlayerY() {
		return playerY;
	}
	
	public List<List<Integer>> getPlayerSpaces() {
		return playerSpaces;
	}
	
	public boolean checkPlayerSpaces(int x, int y) {
		for (ListIterator<List<Integer>> it = playerSpaces.listIterator(); it.hasNext();) {
			List<Integer> space = it.next();
			if (space.get(0) == x && space.get(1) == y) return true;
		}
		return false;
	}
	
	public boolean accessCoords(int x, int y) {
		boolean flag = false;
		
		//System.out.println(playerSpaces);
		for (ListIterator<List<Integer>> it = playerSpaces.listIterator(); it.hasNext();) {
			List<Integer> playerSpace = it.next();
			if (playerSpace.get(0) == x && playerSpace.get(1) == y) {
				flag = true;
				break;
			}
		}
		
		return flag;
	}
	
	private List<List<Integer>> checkPlayerSpaces(Tile[][] levelMap, int height, int width) {
		List<List<Integer>> spacePartition = new ArrayList<List<Integer>>();
		int intMap[][] = new int [height][width];
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (levelMap[i][j] == Tile.WALL || checkBoxList(j, i))
					intMap[i][j] = 1;
				else
					intMap[i][j] = 0;
			}
		}
		
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
		
		open.add(new IntPair(playerY, playerX));
		
		while (!open.isEmpty()) {
			IntPair curr = open.poll();
			closed.add(curr);
			
			List<Integer> playerSpace = new ArrayList<Integer>();
			playerSpace.add(curr.j);
			playerSpace.add(curr.i);
			spacePartition.add(playerSpace);
			intMap[curr.i][curr.j] = 2;
			
			IntPair newStates[] = new IntPair[4];
			newStates[0] = new IntPair(curr.i + 1, curr.j);
			newStates[1] = new IntPair(curr.i - 1, curr.j);
			newStates[2] = new IntPair(curr.i, curr.j - 1);
			newStates[3] = new IntPair(curr.i, curr.j + 1);
			
			for (int i = 0; i < 4; i++) {
				if (closed.contains(newStates[i]) || open.contains(newStates[i]) || intMap[newStates[i].i][newStates[i].j] != 0)
					continue;
				
				open.add(newStates[i]);
			}
		}
		
		return spacePartition;
	}
	
	private boolean checkBoxList(int x, int y) {
		for (int i = 0; i < boxList.size(); i++) {
			Box box = boxList.get(i);
			if (box.getTileX() == x && box.getTileY() == y) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof State) {
			State other = (State) o;
			/*if (this.playerSpaces.size() == other.getPlayerSpaces().size()) {
				if (!(checkPlayerSpaces(other.getPlayerX(), other.getPlayerY()))) {
					return false;
				}
			} else
				return false;*/
			for (int i = 0; i < boxList.size(); i++) {
				if (!(this.boxList.get(i).getTileX() == other.getBoxList().get(i).getTileX() &&
						this.boxList.get(i).getTileY() == other.getBoxList().get(i).getTileY())){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public State copy() {
		List<Box> copyBoxList = new ArrayList<Box>();
		for (int i = 0; i < boxList.size(); i++) {
			copyBoxList.add(boxList.get(i).copy());
		}
		
		List<List<Integer>> copyPlayerSpaces = new ArrayList<List<Integer>>();
		for (int i = 0; i < playerSpaces.size(); i++) {
			List<Integer> copySpace = new ArrayList<Integer>();
			copySpace.add(playerSpaces.get(i).get(0));
			copySpace.add(playerSpaces.get(i).get(1));
			copyPlayerSpaces.add(copySpace);
		}
		
		return new State(this.playerX, this.playerY, copyBoxList, copyPlayerSpaces);
	}
}
