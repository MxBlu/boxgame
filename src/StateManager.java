import java.awt.Graphics2D;

public class StateManager {
	
	private boolean paused;
	private static GameState currentState;
	private static int Level;
	
	public StateManager() {
		
		paused = false;
		//currentState = new BootState();
		Level = 1;
		currentState = new Level(GameMaster.WIDTH, GameMaster.HEIGHT, 50, new LevelGenBlot());
		//currentState = new Level((int)(GameMaster.WIDTH * 0.9), GameMaster.HEIGHT, 50, new LevelGenBlot());
	}
	
	public static void setState(String stateName) {
		if (stateName.equals("INTRO")) {
			currentState = new BootState();
		} else if (stateName.equals("INTRO2")) {
			currentState = new Boot2State(); 
		} else if (stateName.equals("MENU")) {
		   currentState = new MenuState();
		} else if (stateName.equals("PLAY")) {
			currentState = new PlayState();
		}  else if (stateName.equals("LEVEL")) {
			currentState = new Level(GameMaster.WIDTH, GameMaster.HEIGHT, 50, new LevelGenBlot());
		} 
	}
	
	public void draw(Graphics2D g) {
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
	}
}
