import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
public class GamePanel extends JPanel implements  Runnable{
	
	private boolean running;
	private StateManager stateManager; 
	private float wait;
	private int fps; 
    private int x;

	public GamePanel() {
		//setPreferredSize(WIDTH , HEIGHT);
		setFocusable(true);
		setVisible(true);
		requestFocus();
	}
	
	public void initialize() {
		fps = 30;
		running = true;
		stateManager = new StateManager();
	}
	
	public void update() {
		x++;
		stateManager.update();
	}
	
	public void render() {
		Graphics g = getGraphics(); 

		g.setColor(Color.WHITE); 
		g.fillRect(0, 0, WIDTH, HEIGHT); 

		g.setColor(Color.BLACK); 
		g.drawOval(x, 10, 20, 20); 
	}

	public void run() {
		initialize();

		while (running) {     
	        long time = System.currentTimeMillis(); 
			update();
			render(); 
	        time = (1000 / fps) - (System.currentTimeMillis() - time); 
	        if (time > 0) 
            { 
                    try 
                    { 
                            Thread.sleep(time); 
                    } 
                    catch(Exception e){} 
            } 
		}

		
	}


}
