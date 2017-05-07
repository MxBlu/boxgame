import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Boot3State implements GameState{
		
	private int alpha;
	private int ticks;
	
	private final int FADE_IN = 0;
	private final int LENGTH = 45;
	private final int FADE_OUT = 100;
	
	private int x;
	
	public Boot3State() {
		init();
	}
	
	public void init() {
		x = 0;
		ticks = 0;
	}
	
	public void update(KeyInput input) {
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
		if(ticks > FADE_IN + LENGTH + FADE_OUT + 20) {
			StateManager.setState("MENU");
		}
	}
	
	public void draw(Graphics2D bbg) {
		bbg.setColor(new Color(0, 0, 0, alpha));
		bbg.fillOval(GameMaster.WIDTH/2, GameMaster.HEIGHT/2, GameMaster.WIDTH * 2, GameMaster.HEIGHT * 2);
		bbg.setColor(Color.WHITE); 
		bbg.drawOval(GameMaster.WIDTH/2, GameMaster.HEIGHT/2, x*2, x*2); 
	}

	@Override
	public void handleInput() {
		// TODO Auto-generated method stub
		
	}
	
	
}