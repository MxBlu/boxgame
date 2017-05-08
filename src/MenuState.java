import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class MenuState implements GameState{

	private static int movementSpeed = 6;
	
	private BufferedImage background;
	private int option;
	private int enter;
	private ArrayList<Rectangle> boxes;
	private int eventTick;
	private boolean eventStart;
	private int lastInput = -1;
	private int currentInput = 0;
	private int framesLastUpdate = 0;

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
		boxes = new ArrayList<Rectangle>();
		eventStart = true;
		eventStart();
	}

	public void update() {
		if(eventStart) eventStart();
		handleInput();
		if (option == 0 && enter == 1) {
			StateManager.setState("LEVEL");			
		}
		if (option == 1 && enter == 1) {
			StateManager.setState("CREDITS");			
		}
		enter = 0;
	}

	public void draw(Graphics2D bbg) {
		bbg.drawImage(background, 0, 0, GameMaster.WIDTH, GameMaster.HEIGHT, null);
		if (option == 0) {
			bbg.setColor(Color.RED);
			bbg.fillRect(GameMaster.WIDTH/2 - 161,367,28,40);
		} else if (option == 1) {
			bbg.setColor(Color.RED);
			bbg.fillRect(GameMaster.WIDTH/2 - 161,469,28,40);
		} 
		
		// draw transition boxes
		bbg.setColor(Color.BLACK);
		for(int i = 0; i < boxes.size(); i++) {
			bbg.fill(boxes.get(i));
		}
		
	}

	public void handleInput() {
		lastInput = currentInput;
		currentInput = KeyInput.getPressed();
		
		if (lastInput != currentInput || framesLastUpdate > movementSpeed) {
			framesLastUpdate = 0;
			
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
		} else {
			framesLastUpdate++;
			System.out.println(framesLastUpdate + " " + movementSpeed);
		}
	}

	private void eventStart() {
		eventTick++;
		if(eventTick == 1) {
			boxes.clear();
			for(int i = 0; i < 9; i++) {
				boxes.add(new Rectangle(0, i * 80, GameMaster.WIDTH, 80));
			}
		}
		if(eventTick > 1 && eventTick < 35) {
			for(int i = 0; i < boxes.size(); i++) {
				Rectangle r = boxes.get(i);
				if(i % 2 == 0) {
					r.x -= 40;
				}
				else {
					r.x += 40;
				}
			}
		}
		if(eventTick == 36) {
			boxes.clear();
			eventStart = false;
			eventTick = 0;
		}
	}


}
