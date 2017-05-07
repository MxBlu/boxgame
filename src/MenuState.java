import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class MenuState implements GameState{
	
	private BufferedImage background;
	private int option;
	private int enter;
	private int framesLastUpdate = 3;

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
		if (framesLastUpdate < 3) {
			framesLastUpdate++;
			return;
		}

		framesLastUpdate = 0;
		//System.out.println(option);
		if (option < 0) {
			option = 0;
		} else if (option > 2) {
			option = 2;
		} 
		handleInput();
		if (option == 0 && enter == 1) {
			StateManager.setState("LEVEL");			
		}
		enter = 0;
	}

	public void draw(Graphics2D bbg) {
		bbg.drawImage(background, 0, 0, GameMaster.WIDTH, GameMaster.HEIGHT, null);
		if (option == 0) {
			bbg.setColor(Color.BLUE);
			bbg.fillRect(GameMaster.WIDTH/2 + 100,280,20,30);
		} else if (option == 1) {
			bbg.setColor(Color.BLUE);
			bbg.fillRect(GameMaster.WIDTH/2 + 100,400,20,30);
		} else if (option == 2) {
			bbg.setColor(Color.BLUE);
			bbg.fillRect(GameMaster.WIDTH/2 + 100,520,20,30);
		}
	}

	public void handleInput() {
		if (KeyInput.getPressed() == 1) {
			System.out.println("UP");
			option--;
		}
		if (KeyInput.getPressed() == 2) {
			System.out.println("DOWN");
			option++;
		}
		if (KeyInput.getPressed() == 3) {
			System.out.println("LEFT");
		}
		if (KeyInput.getPressed() == 4) {
			System.out.println("RIGHT");
		}
		if (KeyInput.getPressed() == 5) {
			System.out.println("SPACE");
			enter = 1;
		}
	}



}
