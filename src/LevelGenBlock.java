import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import javax.swing.plaf.basic.BasicScrollPaneUI.HSBChangeListener;

import com.sun.jndi.url.corbaname.corbanameURLContextFactory;

import sun.awt.windows.WPrinterJob;

public class LevelGenBlock implements LevelGen {

	LevelGenPattern patterns[];
	
	public LevelGenBlock() {
		patterns = new LevelGenPattern[17];
		patterns[0] = new LevelGenPattern("66666\n"
						+ "61116\n"
						+ "61116\n"
						+ "61116\n"
						+ "66666\n");
		
		patterns[1] = new LevelGenPattern("66666\n"
						+ "60116\n"
						+ "61116\n"
						+ "61116\n"
						+ "66666\n");
		
		patterns[2] = new LevelGenPattern("66611\n"
						+ "60011\n"
						+ "61116\n"
						+ "61116\n"
						+ "66666\n");
		
		patterns[3] = new LevelGenPattern("66666\n"
						+ "60006\n"
						+ "61116\n"
						+ "61116\n"
						+ "66666\n");
		
		patterns[4] = new LevelGenPattern("66666\n"
						+ "60006\n"
						+ "60116\n"
						+ "60116\n"
						+ "66666\n");
		
		patterns[5] = new LevelGenPattern("66166\n"
						+ "60116\n"
						+ "11116\n"
						+ "61106\n"
						+ "66666\n");
		
		patterns[6] = new LevelGenPattern("66666\n"
						+ "60116\n"
						+ "11116\n"
						+ "60116\n"
						+ "66666\n");
		
		patterns[7] = new LevelGenPattern("66166\n"
						+ "60116\n"
						+ "11116\n"
						+ "60106\n"
						+ "66166\n");
		
		patterns[8] = new LevelGenPattern("66166\n"
						+ "60106\n"
						+ "11111\n"
						+ "60106\n"
						+ "66166\n");
		
		patterns[9] = new LevelGenPattern("66166\n"
						+ "60106\n"
						+ "60111\n"
						+ "60006\n"
						+ "66666\n");
		
		patterns[10] = new LevelGenPattern("66666\n"
						+ "60006\n"
						+ "11111\n"
						+ "60006\n"
						+ "66666\n");
		
		patterns[11] = new LevelGenPattern("66666\n"
						+ "61111\n"
						+ "61011\n"
						+ "61116\n"
						+ "66666\n");
		
		patterns[12] = new LevelGenPattern("66666\n"
						+ "60006\n"
						+ "60006\n"
						+ "60006\n"
						+ "66666\n");
		
		patterns[13] = new LevelGenPattern("66666\n"
						+ "60006\n"
						+ "60116\n"
						+ "11116\n"
						+ "11666\n");
		
		patterns[14] = new LevelGenPattern("61616\n"
						+ "61116\n"
						+ "60106\n"
						+ "61116\n"
						+ "61616\n");
		
		patterns[15] = new LevelGenPattern("66666\n"
						+ "60006\n"
						+ "60006\n"
						+ "61116\n"
						+ "61116\n");
		
		patterns[16] = new LevelGenPattern("66666\n"
						+ "60006\n"
						+ "11011\n"
						+ "61116\n"
						+ "61166\n");
	}
	
	@Override
	public Tile[][] generate(int height, int width, int level) {
		if (height % 3 != 2 || width % 3 != 2)
			System.out.println("Generated map size not ideal");
		
		Tile[][] levelMap = new Tile[height][width];

		// needs enough empty space? might not be necessary
		
		Random r = new Random();
		
		int workingHeight = 9;//(height - 2) - ((height - 2) % 3);
		int hStart = (height - workingHeight)/2;
		
		int workingWidth = 9;//(width - 2) - ((width - 2) % 3);
		int wStart = (width - workingWidth)/2;
		
		int numRegions = (workingHeight/3) * (workingWidth/3);
		
		while (true) {
			int amtPicked[] = new int[17];
			for (int i = 0; i < height; i++)
				for (int j = 0; j < width; j++)
					levelMap[i][j] = Tile.WALL;
			
			for (int i = hStart; i < hStart + workingHeight; i++)
				for (int j = wStart; j < wStart + workingWidth; j++)
					levelMap[i][j] = Tile.WALKABLE;
			
			for (int i = hStart; (i + 2) < hStart + workingHeight; i += 3) {
				for (int j = wStart; (j + 2) < wStart + workingWidth; j += 3) {
					int pat = 0;
					while (!patterns[pat = r.nextInt(17)].applyPattern(levelMap,j, i));
					amtPicked[pat]++;
				}
			}

			if (amtPicked[0] > ((numRegions % 2 == 0) ? numRegions/2 : (numRegions + 1)/2))
				continue;
			
			if (!checkConnectedness(levelMap, height, width))
				continue;
			
			if ((float) getNumWalkable(levelMap, height, width)/(float) 81 <= 0.6f)
				break;
		}
		
		boolean noChange_f = false;
		while (!noChange_f) {
			noChange_f = true;
			for (int i = hStart; i < hStart + workingHeight; i++) {
				for (int j = wStart; j < wStart + workingWidth; j++) {
					if (levelMap[i][j] == Tile.WALL)
						continue;
					
					int numAdj = ((levelMap[i + 1][j] == Tile.WALL) ? 1 : 0) +
							((levelMap[i - 1][j] == Tile.WALL) ? 1 : 0) +
							((levelMap[i][j + 1] == Tile.WALL) ? 1 : 0) +
							((levelMap[i][j - 1] == Tile.WALL) ? 1 : 0);
					
					if (numAdj != 3)
						continue;
					
					levelMap[i][j] = Tile.WALL;
					noChange_f = false;
				}
			}
		}
		
		for (int l = 0; l < level; l++) {
			while (true) {
				int i = 0;
				int j = 0;
				int k = r.nextInt(workingWidth * workingHeight); 
				
				for (i = hStart; k >= 0 && i < hStart + workingHeight; i++)
					for (j = wStart; k >= 0 && j < wStart + workingWidth; j++, k--);
				
				if (levelMap[i][j] == Tile.WALL || levelMap[i][j] == Tile.GOAL)
					continue;
				
				if (	(levelMap[i + 1][j] == Tile.WALL && levelMap[i][j + 1] == Tile.WALL) ||
						(levelMap[i][j + 1] == Tile.WALL && levelMap[i - 1][j] == Tile.WALL) ||
						(levelMap[i - 1][j] == Tile.WALL && levelMap[i][j - 1] == Tile.WALL) ||
						(levelMap[i][j - 1] == Tile.WALL && levelMap[i + 1][j] == Tile.WALL))
					continue;
				
				Integer[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
				boolean flag = false;
				
				for (int count = 0; count < 4; count++) {
					if (levelMap[i + directions[count][1]][j + directions[count][0]] == Tile.WALKABLE &&
							levelMap[i + (2 * directions[count][1])][j + (2 * directions[count][0])] == Tile.WALKABLE) {
						flag = true;
					}
				}
				if (!flag)
					continue;
				
				flag = false;
				for (int count = 0; count < 4; count++) {
					if ((level == 4 || level == 5) && l == 2) {
						if (levelMap[i + directions[count][1]][j + directions[count][0]] == Tile.GOAL) {
							flag = true;
						}
					} else {
						if (levelMap[i + directions[count][1]][j + directions[count][0]] == Tile.WALL ||
								levelMap[i + directions[count][1]][j + directions[count][0]] == Tile.GOAL) {
							flag = true;
						}
					}
				}
				if (!flag)
					continue;
				
				levelMap[i][j] = Tile.GOAL;
				break;
			}
		}
		
		System.out.println("Final");
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) 
				System.out.print(levelMap[i][j].getIntRep());
			System.out.println();
		}
		
		return levelMap;
	}

	private int getNumWalkable(Tile[][] levelMap, int height, int width) {
		int numWalkable = 0;
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				if (levelMap[i][j] == Tile.WALKABLE)
					numWalkable++;
		return numWalkable;
	}

	private boolean checkConnectedness(Tile[][] levelMap, int height, int width) {
		int intMap[][] = new int [height][width];
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (levelMap[i][j] == Tile.WALL)
					intMap[i][j] = 1;
				else
					intMap[i][j] = 0;
			}
		}
		
		int i = 0;
		int j = 0;
		boolean startFound_f = false;
		for (i = 0; i < height; i++) {
			for (j = 0; j < width; j++) {
				if (intMap[i][j] == 0) {
					startFound_f = true;
					System.out.println(i + " " + j);
					break;
				}
			}
			if (startFound_f)
				break;
		}
		
		if (!startFound_f)
			return false;
		
		
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
		
		open.add(new IntPair(i, j));
		
		while (!open.isEmpty()) {
			IntPair curr = open.poll();
			closed.add(curr);
			
			intMap[curr.i][curr.j] = 2;
			
			IntPair newStates[] = new IntPair[4];
			newStates[0] = new IntPair(curr.i + 1, curr.j);
			newStates[1] = new IntPair(curr.i - 1, curr.j);
			newStates[2] = new IntPair(curr.i, curr.j - 1);
			newStates[3] = new IntPair(curr.i, curr.j + 1);
			
			for (i = 0; i < 4; i++) {
				if (closed.contains(newStates[i]) || open.contains(newStates[i]) || intMap[newStates[i].i][newStates[i].j] != 0)
					continue;
				
				open.add(newStates[i]);
			}
		}
		
		for (i = 0; i < height; i++)
			for (j = 0; j < width; j++) 
				if (intMap[i][j] == 0)
					return false;
		
		return true;
	}
}
