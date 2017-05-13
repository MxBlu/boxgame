import java.util.Random;

public class LevelGenBlot implements LevelGen {
	
	/**
	 * Generate a random level (for now, just walls and walkable space).
	 */
	@Override
	public void generate(Tile[][] levelMap, int height, int width) {
		// Initialise matrix
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
	}

}
