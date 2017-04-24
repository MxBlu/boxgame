import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class MenuState implements GameState{
	
	private BufferedImage background;
	
	public MenuState() {
		init();
	}
	
	public void init() {
		try {
			background = ImageIO.read(getClass().getResourceAsStream("background.png"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void update() {
		
	}

	public void draw(Graphics2D bbg) {
		bbg.drawImage(background, 0, 0, GameMaster.WIDTH, GameMaster.HEIGHT, null);

	}

	public void handleInput() {
		
	}

}
