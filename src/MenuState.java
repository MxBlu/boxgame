import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.sun.glass.events.KeyEvent;

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

	public static float adj1Chance = 0.40f;
	public static float adj2Chance = 0.20f;
	public static float adj3Chance = 0.50f;
	public static float adj4Chance = 0.60f;

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
			
			switch(KeyInput.getPressed()) {
			case KeyEvent.VK_UP:
				option--;
				break;
			case KeyEvent.VK_DOWN:
				option++;
				break;
			case KeyEvent.VK_LEFT:
				break;
			case KeyEvent.VK_RIGHT:
				break;
			case KeyEvent.VK_SPACE:
				enter = 1;
				break;
			case KeyEvent.VK_1:
				MenuState.adj1Chance += 0.05f;
				System.out.println("1: " + adj1Chance);
				break;
			case KeyEvent.VK_2:
				MenuState.adj1Chance -= 0.05f;
				System.out.println("1: " + adj1Chance);
				break;
			case KeyEvent.VK_3:
				MenuState.adj2Chance += 0.05f;
				System.out.println("2: " + adj2Chance);
				break;
			case KeyEvent.VK_4:
				MenuState.adj2Chance -= 0.05f;
				System.out.println("2: " + adj2Chance);
				break;
			case KeyEvent.VK_5:
				MenuState.adj3Chance += 0.05f;
				System.out.println("3: " + adj3Chance);
				break;
			case KeyEvent.VK_6:
				MenuState.adj3Chance -= 0.05f;
				System.out.println("3: " + adj3Chance);
				break;
			case KeyEvent.VK_7:
				MenuState.adj4Chance += 0.05f;
				System.out.println("4: " + adj4Chance);
				break;
			case KeyEvent.VK_8:
				MenuState.adj4Chance -= 0.05f;
				System.out.println("4: " + adj4Chance);
				break;
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
