import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class IntermissionScreen extends JPanel {

    private Image background;
    private Image menuButton;
    private Image nextButton;
    private String time;
    private String movesString;

	private HoverButton menu;
	private HoverButton next;
	
	private boolean isPremade;
	
	public IntermissionScreen(String d, int moves, int difficulty, boolean premadeFlag){
		isPremade = premadeFlag;
		init(difficulty);
		time = "Time: " + d;
		movesString = "Moves: " + moves;
	}
	
	public IntermissionScreen(){
		init(1);
	}
	
	private void init(int difficulty) {
		GameMaster.toggleCursorPointer();

		// Get the image file for the background
		try {
			background = ImageIO.read(getClass().getResourceAsStream("intermission.png"));
			menuButton = ImageIO.read(getClass().getResourceAsStream("menubutton.png"));
			nextButton = ImageIO.read(getClass().getResourceAsStream("nextbutton.png"));

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
				System.out.println("return to menu");
				if (isPremade == false) {
		    		GameMaster.changeScreens(new MenuScreen());
				} else {
		    		GameMaster.changeScreens(new SelectScreen());
				}
			}
		});
		
		// Next level button action listener
		next.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				System.out.println("next level load");
				GameMaster.changeScreens(new Level(GameMaster.WIDTH, GameMaster.HEIGHT, 40, difficulty));
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
		
	public void paintComponent(Graphics g) {
		// Access the JPanel super class's
		// function for painting.
		super.paintComponent(g);
		Font f = new Font("arial", Font.BOLD, 50);
		g.setFont(f);		
		
	    // Draw the background for this JPanel
	    g.setColor(Color.BLACK);
        g.drawImage(background, 0, 0, null);
        if (time != null){
        	g.setColor(Color.CYAN);
        	g.drawString(time, GameMaster.WIDTH/3, 215);
        	g.setColor(Color.ORANGE);
        	g.drawString(movesString, GameMaster.WIDTH/3, 265);

        }
	}
	


}
