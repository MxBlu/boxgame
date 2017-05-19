import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class StateManager {
	
	
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	
	private BufferedImage image;
    private BufferedImage backBuffer; 
    private Graphics2D g;
	private KeyInput input;

	private boolean paused;
	private static JFrame currentState;
	private static int Level;
	
	public StateManager() {
		
		paused = false;
		currentState = new MenuStateTrial();
		Level = 1;
		//currentState = new JFrame();
		init();
		/*currentState.revalidate();
		currentState.repaint();*/
		//currentState = new Level(GameMaster.WIDTH, GameMaster.HEIGHT, 50, new LevelGenBlock());
	}
	
	private void init() {
		currentState.setTitle("Game");
		currentState.setSize(WIDTH, HEIGHT);
		currentState.setFocusable(true);
		currentState.setVisible(true);
		currentState.setResizable(false); 		
		currentState.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		image = new BufferedImage(WIDTH, HEIGHT, 1);
		g = (Graphics2D) image.getGraphics();
		backBuffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		input = new KeyInput();
		currentState.addKeyListener(new KeyInput());
	}
	
/*	public static void setState(String stateName) {
		if (stateName.equals("INTRO")) {
			currentState = new BootState();
		} else if (stateName.equals("INTRO2")) {
			currentState = new Boot2State(); 
		} else if (stateName.equals("MENU")) {
		   currentState = new MenuState();
		} else if (stateName.equals("PLAY")) {
			currentState = new PlayState();
		}  else if (stateName.equals("LEVEL")) {
			currentState = new Level(WIDTH, HEIGHT, 50, new LevelGenBlock());
		} 
	}*/
	
	/*public void draw(Graphics2D g) {
		currentState.draw(g);
	}
	
	public void update() {//KeyInput input
		currentState.update(); //input
	}
	
	public static int getLevel(){
		return Level;
	}
	
	public static void setLevel(){
		Level++;
	}*/
}
