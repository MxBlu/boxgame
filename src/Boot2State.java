import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.Timer;

import com.sun.glass.events.KeyEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Boot2State extends JFrame implements ActionListener{
	
	private BufferedImage logo;
	
	private int alpha;

	
	private final int FADE_IN = 0;
	private final int LENGTH = 40;
	private final int FADE_OUT = 30;
	
	Timer timer;
	private int x;
	
	public Boot2State() {
		init();
	}
	
	public void init() {
		x = 0;

		AudioManager man = new AudioManager();
		timer = new Timer(50, this);
        timer.start();
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
	
		if( x< FADE_IN) {
			alpha = (int) (255 - 255 * (1.0 * x / FADE_IN));
			if(alpha < 0) alpha = 0;
		}
		if(x > FADE_IN + LENGTH) {
			alpha = (int) (255 * (1.0 * x - FADE_IN - LENGTH) / FADE_OUT);
			if(alpha > 255) alpha = 255;
		}
		if(x > FADE_IN + LENGTH + FADE_OUT + 10) {
			//TODO Uncomment when there's a menu
			timer.stop();
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


	public void handleInput() {
		if(KeyInput.getPressed()==KeyEvent.VK_SPACE){
			timer.stop();
			StateManager.setState("MENU");
			return;
		}		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		update();
		
	}
	
	
}