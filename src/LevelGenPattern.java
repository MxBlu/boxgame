import java.util.Random;

/**
 * Holds a pattern which can be applied to a level.
 * @author Jumail
 *
 */
public class LevelGenPattern {
	/**
	 * Pattern
	 */
	private Tile patternMap[][];
	
	/**
	 * Creates a new LevelGenPattern.
	 * @param pattern String input of pattern
	 */
	public LevelGenPattern(String pattern) {
		this.patternMap = new Tile[5][5];
		
		byte inputArray[] = pattern.getBytes();
		int sIndex = 0;
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) 
				patternMap[i][j] = Tile.getTile(inputArray[sIndex++] - '0');
			sIndex++;
		}
	}
	
	/**
	 * Tests if the pattern can be applied to the level map at given coordinates and applies it if it can.
	 * @param levelMap Level to be applied to
	 * @param x X-coordinate to apply to
	 * @param y Y-coordinate to apply to
	 * @return Success of operation
	 */
	public boolean applyPattern(Tile levelMap[][], int x, int y) {
		// Get a random transformation of the pattern.
		Random r = new Random();
		
		Tile transformedPattern[][] = null;
		switch (r.nextInt(6)) {
		case 0:
			transformedPattern = patternMap;
			break;
		case 1:
			transformedPattern = rotate(patternMap, 1);
			break;
		case 2:
			transformedPattern = rotate(patternMap, 2);
			break;
		case 3:
			transformedPattern = rotate(patternMap, 3);
			break;
		case 4:
			transformedPattern = flip(rotate(patternMap, 1));
			break;
		case 5:
			transformedPattern = flip(rotate(patternMap, 2));
			break;
		case 6:
			transformedPattern = flip(rotate(patternMap, 3));
			break;
		}
		
		// Create a mask to test if the pattern is applicable.
		Tile borderMask[][] = new Tile[5][5];
		for (int i = 0; i < 5; i++)
			borderMask[i] = transformedPattern[i].clone();
		
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++) 
				borderMask[1 + i][1 + j] = Tile.ANY;
		
		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 5; j++) 
				if (borderMask[i][j] != Tile.ANY && levelMap[y - 1 + i][x - 1 + j] != borderMask[i][j])
					return false;
		
		// Apply the pattern to the level.
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++) 
				levelMap[y + i][x + j] = transformedPattern[1 + i][1 + j];
		
		return true;
	}
	
	/**
	 * Rotates a given pattern (90 * rotation) degrees clockwise.
	 * @param patternMap Pattern to rotate
	 * @param rotations Number of 90 degree rotation
	 * @return Transformed pattern map
	 */
	private Tile[][] rotate(Tile[][] patternMap, int rotations) {
		Tile transformed[][] = new Tile[5][5];
		
		for (int r = 0; r < rotations; r++) {
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					transformed[j][4 - i] = patternMap[i][j];
				}
			}
		}
		
		return transformed;
	}
	
	/**
	 * Flip a given pattern horizontally.
	 * @param patternMap Pattern to flip
	 * @return Transformed pattern map
	 */
	private Tile[][] flip(Tile[][] patternMap) {
		Tile transformed[][] = new Tile[5][5];
		
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				transformed[i][4 - j] = patternMap[i][j];
			}
		}
		
		return transformed;
	}
}
