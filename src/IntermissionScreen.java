import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * Intermission Screen (JPanel)
 * 
 * Screen displayed when level is completed
 */

public class IntermissionScreen extends JPanel {

    private Image background;
    private Image menuButton;
    private Image nextButton;
    private String time;
    private String movesString;

	private HoverButton menu;
	private HoverButton next;
	
	private String bestTime;
	private int bestMoves;
	private boolean newHighScore;
	
	private Font gameFont;
	
	private boolean isPremade;
	private JFrame frame;

	/**
	 * Representative of the screen after completing a level
	 */
	public IntermissionScreen(JFrame frame, String playerTime, int moves, int difficulty, boolean premadeFlag,
			boolean isHighScore, String highTime, int highMoves){
		this.frame = frame;

		isPremade = premadeFlag;
		init(difficulty);
		time = "Time: " + playerTime;
		movesString = "Moves: " + moves;
		bestMoves = highMoves;
		bestTime = highTime;
		newHighScore = isHighScore;
	}
	
	/*
	 * Sets up the JPanel and all is components
	 * 
	 * Adds all the buttons and draws the background and highscore
	 * 
	 * @param int difficulty sets the difficulty of next level if called
	 */
	private void init(int difficulty) {
		// Get the image file for the background
		try {
			background = ImageIO.read(getClass().getResourceAsStream("intermission.png"));
			menuButton = ImageIO.read(getClass().getResourceAsStream("menubutton.png"));
			nextButton = ImageIO.read(getClass().getResourceAsStream("nextbutton.png"));

			gameFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("VCR_OSD_MONO.ttf")).deriveFont(Font.BOLD, 50);

		}
		catch(Exception e) {
			e.printStackTrace();
		}	
		
		// Initialize JButtons
		// Give them names, dimension, and images.
		menu = new HoverButton();
		menu.setIcon(new ImageIcon(menuButton));
		menu.setToolTipText("Click this to return to the menu");
		menu.setBorderPainted(false);
		menu.setContentAreaFilled(false);
		
		next = new HoverButton();
		next.setIcon(new ImageIcon(nextButton));
		next.setToolTipText("Click this to play another level!");
		next.setBorderPainted(false);
		next.setOpaque(false);
		next.setContentAreaFilled(false);
		
		// Return to menu button action listener
		menu.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				if (isPremade == false) {
		    		GameMaster.changeScreens(frame, new MenuScreen(frame));
				} else {
		    		GameMaster.changeScreens(frame, new SelectScreen(frame));
				}
			}
		});
		
		// Next level button action listener
		next.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				GameMaster.changeScreens(frame, new LoadingScreen(frame, GameMaster.WIDTH, GameMaster.HEIGHT, 50, difficulty));
			}
		});
		
		// Set the layout of this JPanel
		// We can use one of the preset layouts from
		// JPanel's API to arrange the layout and 
		// positions of where components appear in this
		// JPanel. This is a better alternative opposed
		// to manually hard coding pixel perfect coordinates
		// where each button should appear. 
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// Add the buttons to the JPanel
		// gridx and gridy are part of JPanel's layout
		// API, and lets us organise the location of the buttons.
		c.gridx = 1;
		c.gridy = 1;
		add(menu,c);
		c.gridx = 2;
		if (isPremade == false) {
			add(next,c);
		}
		
		// Set the JPanels attributes and revalidate.
		revalidate();
		setFocusable(true);
	    requestFocusInWindow();
	}
	
	/**
	 * Paints the components of the intermission screen for the user
	 */
	public void paintComponent(Graphics g) {
		// Access the JPanel super class's
		// function for painting.
		super.paintComponent(g);
		
		g.setFont(gameFont);
		
	    // Draw the background for this JPanel
	    g.setColor(Color.BLACK);
        g.drawImage(background, 0, 0, null);
        if (time != null){
        	g.setColor(Color.CYAN);
        	g.drawString(time, GameMaster.WIDTH/3, 215);
        	g.setColor(Color.ORANGE);
        	g.drawString(movesString, GameMaster.WIDTH/3, 265);

        }
        try {
			gameFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("VCR_OSD_MONO.ttf")).deriveFont(Font.BOLD, 25);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        //If Preset level, displays a highScore
        if (isPremade){
        	g.setFont(gameFont);
        	g.setColor(Color.WHITE);
        	
        	if (newHighScore){
        		//Displays the new High Score if Player succeeds
        		g.drawString("New High Score", GameMaster.WIDTH/5, 500);
        		g.drawString(time,  GameMaster.WIDTH/5, 550 );
        		g.drawString(movesString,  GameMaster.WIDTH/5,600);
        	
        	}else {
        		//displays the current highscore
        		g.drawString("High Score",  GameMaster.WIDTH/5, 500);
        		if (bestTime != null){
        			g.drawString("Time : "+bestTime,  GameMaster.WIDTH/5, 550 );
        		}
        		g.drawString("Moves: " +
        		
        				Integer.toString(bestMoves),  GameMaster.WIDTH/5,600);
        		
        	}
        }
	}
	


}
