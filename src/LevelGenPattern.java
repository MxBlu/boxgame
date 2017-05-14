import java.util.Random;

public class LevelGenPattern {
	private Tile patternMap[][];
	
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
	
	public boolean applyPattern(Tile levelMap[][], int x, int y) {
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
		
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++) 
				levelMap[y + i][x + j] = transformedPattern[1 + i][1 + j];
		
		return true;
	}
	
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
