import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import javax.swing.JFrame;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;
import com.sun.glass.events.KeyEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;

public class BootState extends JFrame implements  ActionListener{
	
	private BufferedImage logo;
	
	private int alpha;
	private Timer timer;
	
	private final int FADE_IN = 30;
	private final int LENGTH = 40;
	private final int FADE_OUT = 0;
	private int x;
	
	public BootState() {
		init();
	}
	
	public void init() {
		x = 0;
		timer = new Timer(100, this);
        timer.start();
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

		x++;
		if(x < FADE_IN) {
			alpha = (int) (255 - 255 * (1.0 * x / FADE_IN));
			if(alpha < 0) alpha = 0;
		}
		if(x > FADE_IN + LENGTH) {
			alpha = (int) (255 * (1.0 * x - FADE_IN - LENGTH) / FADE_OUT);
			if(alpha > 255) alpha = 255;
		}
		if(x > FADE_IN + LENGTH + FADE_OUT) {
			timer.stop();
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


	public void handleInput() {
		if(KeyInput.getPressed()==KeyEvent.VK_SPACE){
			timer.stop();
			StateManager.setState("INTRO2");
			return;
			
		}		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		update();
		
	}
	
	
	
}