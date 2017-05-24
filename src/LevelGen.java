import java.awt.Image;

public interface LevelGen {

	public Tile[][] generate(int height, int width, int level, int tileSize, Image tileImgs[]);
	
}
