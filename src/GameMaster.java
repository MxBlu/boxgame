import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage; 

public class GameMaster{

	// Here for debugging reasons only
	public static float adj1Chance = 0.40f;
	public static float adj2Chance = 0.20f;
	public static float adj3Chance = 0.50f;
	public static float adj4Chance = 0.60f;
	
	private boolean running;
	private int fps; 
	
	private static StateManager stateManager; 
	
	

    public static void main(String[] args) {
    	SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {                                           
            	stateManager = new StateManager();
  
            }
        });  
    }
    
	/*public GameMaster () {
		run();
	}

	public void run() {
		initialize();
		while (running) {
			long time = System.currentTimeMillis();

			update();
			draw();

			time = (1000 / fps) - (System.currentTimeMillis() - time);
			if (time > 0) {
				try {
					Thread.sleep(time);
				} catch (Exception e) {
				}
			}
		}

	}

	public void draw() {
		g = (Graphics2D) getGraphics(); 
		Graphics2D bbg = (Graphics2D) backBuffer.getGraphics(); 
		stateManager.draw(bbg);
		g.drawImage(backBuffer, 0, 0, this); 
	}*/
	
	/*public void initialize() {
		setTitle("Game");
	    setSize(WIDTH, HEIGHT);
		setFocusable(true);
		setVisible(true);
		setResizable(false); 		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		image = new BufferedImage(WIDTH, HEIGHT, 1);
		g = (Graphics2D) image.getGraphics();
		backBuffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		input = new KeyInput();
		addKeyListener(new KeyInput());
		stateManager = new StateManager();
	}
	*/
	/*public void update() {
		stateManager.update(); //input
	}
	*/
}
