import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage; 

public class GameMaster extends JFrame{
	
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	
	private boolean running;
	private int fps; 
	
	private StateManager stateManager; 
	private BufferedImage image;
    private BufferedImage backBuffer; 
	private Graphics2D g;
	private KeyInput input;

    public static void main(String[] args) {
    	GameMaster gameMaster = new GameMaster();
    }
    
	public GameMaster () {
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
	}
	
	public void initialize() {
		setTitle("Game");
	    setSize(WIDTH, HEIGHT);
		setFocusable(true);
		setVisible(true);
		setResizable(false); 		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fps = 60;
		running = true;
		image = new BufferedImage(WIDTH, HEIGHT, 1);
		g = (Graphics2D) image.getGraphics();
		backBuffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		input = new KeyInput();
		addKeyListener(new KeyInput());
		stateManager = new StateManager();
	}
	
	public void update() {
		stateManager.update(); //input
	}
	
}
