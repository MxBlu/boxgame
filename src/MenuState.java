import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class MenuState implements GameState{
	
	private BufferedImage background;
	private int option;
	
	public MenuState() {
		init();

	}

	public void init() {
		try {
			background = ImageIO.read(getClass().getResourceAsStream("menu.png"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		option = 0;
	}

	public void update() {
		handleInput();
		if (option == 10) {
			StateManager.setState("LEVEL");			
		}

	}

	public void draw(Graphics2D bbg) {
		bbg.drawImage(background, 0, 0, GameMaster.WIDTH, GameMaster.HEIGHT, null);
	}

	public void handleInput() {
		/*if (input.getPressed() == 1) {
			System.out.println("UP");
			option++;
		}
		if (input.getPressed() == 2) {
			System.out.println("DOWN");
			option--;
		}
		if (input.getPressed() == 3) {
			System.out.println("LEFT");
		}
		if (input.getPressed() == 4) {
			System.out.println("RIGHT");
		}
		if (input.getPressed() == 5) {
			System.out.println("SPACE");
			option = 10;
		}*/
	}

	@Override
	public void update(KeyInput input) {
		// TODO Auto-generated method stub
		
	}


}
