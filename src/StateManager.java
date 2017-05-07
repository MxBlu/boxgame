import java.awt.Graphics2D;

public class StateManager {
	
	private boolean paused;
	private static GameState currentState;
	
	public StateManager() {
		
		paused = false;
		currentState = new BootState();
		//currentState = new Level(GameMaster.WIDTH, GameMaster.HEIGHT, 50);
	}
	
	public static void setState(String stateName) {
		if (stateName.equals("INTRO")) {
			currentState = null;
			currentState = new BootState();
		} else if (stateName.equals("INTRO2")) {
			currentState = null;
			currentState = new Boot2State(); 
		} else if (stateName.equals("MENU")) {
		   currentState = new MenuState();
		} else if (stateName.equals("PLAY")) {
			currentState = new PlayState();
		}  else if (stateName.equals("LEVEL")) {
			currentState = new Level(GameMaster.WIDTH, GameMaster.HEIGHT, 50);
		} 
	}
	
	public void draw(Graphics2D g) {
		currentState.draw(g);
	}
	
	public void update() {//KeyInput input
		currentState.update(); //input
	}
}
