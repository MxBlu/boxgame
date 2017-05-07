import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class CreditsState implements GameState{
	
	private BufferedImage credits;
	
	private int alpha;
	private int ticks;
	
	private final int FADE_IN = 30;

	
	public CreditsState() {
		init();
	}
	
	public void init() {
		ticks = 0;
		//TODO work out who should own AudioManager
		AudioManager audMan = new AudioManager();
		audMan.addSound("intro_sound.wav", "intro_sound");
		audMan.playSound("intro_sound", 1.0f);
		try {
			credits = ImageIO.read(getClass().getResourceAsStream("credits.png"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
		handleInput();
		ticks++;
		if(ticks < FADE_IN) {
			alpha = (int) (255 - 255 * (1.0 * ticks / FADE_IN));
			if(alpha < 0) alpha = 0;
		}

	}
	
	public void draw(Graphics2D bbg) {
		bbg.setColor(Color.BLACK);
		bbg.fillRect(0, 0, GameMaster.WIDTH, GameMaster.HEIGHT);
		bbg.drawImage(credits, 0, 0, GameMaster.WIDTH, GameMaster.HEIGHT, null);
		bbg.setColor(new Color(0, 0, 0, alpha));
		bbg.fillRect(0, 0, GameMaster.WIDTH, GameMaster.HEIGHT);
		bbg.setColor(Color.BLACK); 
	}

	@Override
	public void handleInput() {
		if(KeyInput.getPressed()==5 || KeyInput.getPressed()==6){
			StateManager.setState("MENU");
			return;
		}		
	}
	
}