import java.awt.Graphics2D;

public class StateManager {
	
	private boolean paused;
	private static GameState currentState;
	
	public StateManager() {
		
		paused = false;		
		currentState = new BootState();
		
	}
	
	public static void setState(String stateName) {
		if (stateName.equals("INTRO")) {
			currentState = null;
			currentState = new BootState();
		} else if (stateName.equals("INTRO2")) {
			currentState = null;
			currentState = new Boot2State();
		} else if (stateName.equals("INTRO3")) {
			currentState = null;
			currentState = new Boot3State();
		} else if (stateName.equals("MENU")) {
		   currentState = new MenuState();
		} else if (stateName.equals("PLAY")) {
			currentState = new PlayState();
		} 
	}
	
	public void draw(Graphics2D g) {
		currentState.draw(g);
	}
	
	public void update() {
		currentState.update();
	}
}
