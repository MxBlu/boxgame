import java.awt.image.BufferedImage;
import java.util.Random;

public class Level {
	private byte levelMap[][];
	
	private int width; // Width of the level
	private int height; // Height of the level
	private int tileSize;
	
	private BufferedImage tiles[];
	
	/**
	 * Creates a new level.
	 * @precondition screenWidth % tileSize == 0 && screenHeight % tileSize == 0
	 * @param screenWidth Screen width in pixels.
	 * @param screenHeight Screen height in pixels.
	 * @param tileSize Width/Height of a tile in pixels.
	 */
	Level(int screenWidth, int screenHeight, int tileSize) {
		// 1 pixel padding so I don't need to add edge cases to generation.
		this.width = screenWidth/tileSize + 2;
		this.height = screenHeight/tileSize + 2;
		this.tileSize = tileSize;
		
		generate();
	}
	
	Level(String input, int tileSize) {
		this.tileSize = tileSize;
		byte inputArray[] = input.getBytes();
		
		// Get the width and height
		for (this.width = 0; inputArray[this.width] != '\n'; this.width++);
		this.height = inputArray.length/(width + 1);
		//System.out.println("Length: " + inputArray.length + ", Height :" + this.height + ", Width: " + this.width);
		
		levelMap = new byte[this.height][this.width];
		int sIndex = 0;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) 
				levelMap[i][j] = (byte) (inputArray[sIndex++] - '0');
			sIndex++;
		}
	}
	
	/**
	 * Generate a random level (for now, just walls and walkable space).
	 */
	private void generate() {
		// Initialise matrix
		levelMap = new byte[height][width];
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++) 
				levelMap[i][j] = 0;
		
		int roughCentreHeight = height/2;
		int roughCentreWidth = width/2;

		// Wall = 0
		// Walkable = 1
		
		// Place initial 9 walkables in the rough centre.
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				levelMap[roughCentreHeight + i][roughCentreWidth + j] = 1;

		Random rng = new Random();
		// numPasses = number of times to attempt to place walkables.
		// Feel free to play with this
		for (int numPasses = 7; numPasses > 0; numPasses--) {
			for (int i = 1; i < height - 1; i++) {
				for (int j = 1; j < width - 1; j++) {
					if (levelMap[i][j] != 0)
						continue;
					
					// 0d0
					// d1d
					// 0d0
					// Consider '1' as being the place being checked.
					// Totals the 'd' values to find number of adjacent walkables.
					
					int numAdj = levelMap[i + 1][j] + levelMap[i - 1][j] +
							levelMap[i][j + 1] + levelMap[i][j - 1];

					// Feel free to mess with the chance values.
					float adj1Chance = 0.5f;
					float adj2Chance = 0.25f;
					float adj3Chance = 0.75f;
					
					// Set values to a temp of 5 so they don't affect the current pass.
					switch (numAdj) {
					case 0:
						break;
					case 1: // 1/4 chance
						if (rng.nextFloat() <= adj1Chance)
							levelMap[i][j] = 5;
						
						break;
					case 2: // 1/2 change
						if (rng.nextFloat() <= adj2Chance)
							levelMap[i][j] = 5;
						
						break;
					case 3: // 3/4 chance
						if (rng.nextFloat() <= adj3Chance)
							levelMap[i][j] = 5;
						
						break;
					case 4: // definite
						levelMap[i][j] = 5;
					}
				}
			}
			
			// Set all temp values to actual.
			for (int i = 1; i < height - 1; i++)
				for (int j = 1; j < width - 1; j++) 
					if (levelMap[i][j] == 5) 
						levelMap[i][j] = 1;
		}
	}
	
	public void print() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) 
				System.out.print((int) levelMap[i][j]);
			
			System.out.println();
		}			
	}
}
