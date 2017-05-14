import java.util.Random;

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
	public Tile[][] generate(int height, int width) {
		if (height % 3 != 2 || width % 3 != 2)
			System.out.println("Generated map size not ideal");
		
		Tile[][] levelMap = new Tile[height][width];
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				levelMap[i][j] = Tile.WALL;
		
		int workingHeight = (height - 2) - ((height - 2) % 3);
		int hStart = (height - workingHeight)/2;
		System.out.println(height + " " + workingHeight + " " + hStart);
		
		int workingWidth = (width - 2) - ((width - 2) % 3);
		int wStart = (width - workingWidth)/2;
		System.out.println(width + " " + workingWidth + " " + wStart);
		
		for (int i = hStart; i < hStart + workingHeight; i++)
			for (int j = wStart; j < wStart + workingWidth; j++)
				levelMap[i][j] = Tile.WALKABLE;
		
		System.out.println("Adjusted");
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) 
				System.out.print(levelMap[i][j].getIntRep());
			System.out.println();
		}
			
		// tiles are all connected
		// cap number of pattern[0] blocks
		// needs enough empty space
		// no tile surrounded by 3 walls
		
		Random r = new Random();
		int amtPicked[] = new int[17];
		
		boolean generate_f = true;
		while (generate_f) {
			for (int i = hStart; (i + 2) < hStart + workingHeight; i += 3) {
				for (int j = wStart; (j + 2) < wStart + workingWidth; j += 3) {
					int pat = 0;
					while (!patterns[pat = r.nextInt(17)].applyPattern(levelMap,j, i));
					amtPicked[pat]++;
				}
			}
			
			break;
		}
		
		return levelMap;
	}

}
