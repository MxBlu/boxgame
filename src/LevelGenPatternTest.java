import static org.junit.Assert.*;

import org.junit.Test;

public class LevelGenPatternTest {

	@Test
	public void testLevelGenPattern() {
		Tile levelMap[][] = new Tile[5][5];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) { 
				if (i == 0 || j == 0 || i == 4 || j == 4)
					levelMap[i][j] = Tile.WALL;
				else
					levelMap[i][j] = Tile.WALKABLE;
			}
		}
		
		LevelGenPattern p = new LevelGenPattern("66666\n"
						+ "60006\n"
						+ "60116\n"
						+ "60116\n"
						+ "66666\n");
		
		p.applyPattern(levelMap, 1, 1);
		
		System.out.println("Final");
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) 
				System.out.print(levelMap[i][j].getIntRep());
			
			System.out.println();
		}
	}

}
