import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Boot2State implements GameState{
	
	private BufferedImage logo;
	
	private int alpha;
	private int ticks;
	
	private final int FADE_IN = 0;
	private final int LENGTH = 40;
	private final int FADE_OUT = 35;
	
	private int x;
	
	public Boot2State() {
		init();
	}
	
	public void init() {
		x = 0;
		ticks = 0;
		AudioManager man = new AudioManager();
		man.playSound("intro_sound.wav", 0.0f);
		try {
			logo = ImageIO.read(getClass().getResourceAsStream("logo2.png"));
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
		if(ticks > FADE_IN + LENGTH + FADE_OUT + 10) {
			//TODO Uncomment when there's a menu
			StateManager.setState("MENU");
			//StateManager.setState("LEVEL");
		}
	}
	
	public void draw(Graphics2D bbg) {
		bbg.setColor(Color.WHITE);
		bbg.drawImage(logo, 0, 0, GameMaster.WIDTH, GameMaster.HEIGHT, null);
		bbg.setColor(new Color(0, 0, 0, alpha));
		bbg.fillRect(0, 0, GameMaster.WIDTH, GameMaster.HEIGHT);
		
	}

	@Override
	public void handleInput() {
		if(KeyInput.getPressed()==5){
			StateManager.setState("MENU");
			return;
		}		
	}
	
	
}