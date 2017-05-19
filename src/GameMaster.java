import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage; 

public class GameMaster extends JFrame{
	
	// The window and game dimensions
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	
	// These JPanels represents some of the 
	// screens of our game that the user sees. 
	JPanel mainPanel;
	JPanel menuPanel;
	JPanel bootPanel;

	
    public static void main(String[] args) {
    	// Call the game master constructor.
    	// This creates a new JFrame, which we will
    	// populate with JPanels inside of it. The
    	// JPanels will hold the content to our
    	// boot screens, menus, and game. 
    	GameMaster gameMaster = new GameMaster();

    }
    
	public GameMaster () {
		// Initialize the settings and attributes
		// for our JFrame. This class, GameMaster
		// acts as the JFrame. We initialize its
		// size, and its visibility. 
		setTitle("Game");
	    setSize(WIDTH, HEIGHT);
		setFocusable(true);
		setVisible(true);
		setResizable(false); 
        setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Initialize the main panel. 
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(1,1));
		mainPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		mainPanel.setFocusable(true);
		setBackground(Color.BLACK);
		
		// Load the menu screen into the main
		// JPanel. Construct a new menu. 
	//	menuPanel = new Menu();	
		
		// Put the main panel into the content
		// pane of this Game Master JFrame. Add
		// the menu panel to the main panel.
		getContentPane().add(mainPanel);
	//	mainPanel.add(menuPanel, "menu");
		
		bootPanel = new FadeIn();
		mainPanel.add(bootPanel, "BootPanel");
		
		pack();
	}
}
