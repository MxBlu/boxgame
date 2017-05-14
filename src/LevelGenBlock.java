import java.util.Random;

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
	public void generate(Tile[][] levelMap, int height, int width) {
			if (height % 3 != 2 || width % 3 != 2)
				System.out.println("Generated map size not ideal");
			
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) { 
					if (i == 0 || j == 0 || i == height - 1 || j == width - 1)
						levelMap[i][j] = Tile.WALL;
					else
						levelMap[i][j] = Tile.WALKABLE;
				}
			}
			
			// tiles are all connected
			// cap number of pattern[0] blocks
			// needs enough empty space
			// no tile surrounded by 3 walls
			
			Random r = new Random();
			int amtPicked[] = new int[17];
			
			boolean generate_f = true;
			while (generate_f) {
				for (int i = 1; (i + 2) < height; i += 3) {
					for (int j = 1; (j + 2) < width; j += 3) {
						int pat = 0;
						while (!patterns[pat = r.nextInt(17)].applyPattern(levelMap,j, i));
						amtPicked[pat]++;
					}
				}
				
				break;
			}
	}

}
