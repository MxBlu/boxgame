import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class BootState implements GameState{
	
	private BufferedImage logo;
	
	private int alpha;
	private int ticks;
	
	private final int FADE_IN = 30;
	private final int LENGTH = 40;
	private final int FADE_OUT = 0;
	private int x;
	
	public BootState() {
		init();
	}
	
	public void init() {
		x = 0;
		ticks = 0;
		//TODO work out who should own AudioManager
		AudioManager audMan = new AudioManager();
		audMan.addSound("intro_sound.wav", "intro_sound");
		audMan.playSound("intro_sound", 1.0f);
		try {
			logo = ImageIO.read(getClass().getResourceAsStream("logo.png"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
		handleInput();
		x ++;
		ticks++;
		if(ticks < FADE_IN) {
			alpha = (int) (255 - 255 * (1.0 * ticks / FADE_IN));
			if(alpha < 0) alpha = 0;
		}
		if(ticks > FADE_IN + LENGTH) {
			alpha = (int) (255 * (1.0 * ticks - FADE_IN - LENGTH) / FADE_OUT);
			if(alpha > 255) alpha = 255;
		}
		if(ticks > FADE_IN + LENGTH + FADE_OUT) {
			StateManager.setState("INTRO2");
		}
	}
	
	public void draw(Graphics2D bbg) {
		bbg.setColor(Color.WHITE);
		bbg.fillRect(0, 0, GameMaster.WIDTH, GameMaster.HEIGHT);
		bbg.drawImage(logo, 0, 0, GameMaster.WIDTH, GameMaster.HEIGHT, null);
		bbg.setColor(new Color(0, 0, 0, alpha));
		bbg.fillRect(0, 0, GameMaster.WIDTH, GameMaster.HEIGHT);
		bbg.setColor(Color.BLACK); 
		//bbg.drawRect(GameMaster.WIDTH/2, GameMaster.HEIGHT/2, x, x); 	 
	}

	@Override
	public void handleInput() {
		if(KeyInput.getPressed()==5){
			StateManager.setState("INTRO2");
			return;
			
		}		
	}
	
	
}