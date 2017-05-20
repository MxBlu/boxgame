import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MenuStateTrial extends JPanel{

    private Image background;
    private Image playbutton;
    private Image creditsbutton;
    private Image highscoresbutton;

	private JButton play;
	private JButton credits;
	private JButton highScore;
	//private JPanel gamePanel;
	
	public MenuStateTrial() {
		System.out.println("in menu screen");
		
		// Get the image file for the background and buttons
		try {
			background = ImageIO.read(getClass().getResourceAsStream("menu.png"));
			playbutton = ImageIO.read(getClass().getResourceAsStream("playbutton.png"));
			creditsbutton = ImageIO.read(getClass().getResourceAsStream("creditsbutton.png"));
			highscoresbutton = ImageIO.read(getClass().getResourceAsStream("highscoresbutton.png"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}		
		
		// Initialize JButtons
		// Give them names, and dimensions. Also
		// add tool tips on mouse over to assist the user.
		play = new JButton();
		play.setIcon(new ImageIcon(playbutton));
		play.setToolTipText("Click this to begin the game!");
		play.setBorderPainted(false);
		
		credits = new JButton();
		credits.setIcon(new ImageIcon(creditsbutton));
		credits.setToolTipText("Click this to see who made the game!");
		credits.setBorderPainted(false);

		highScore = new JButton();
		highScore.setIcon(new ImageIcon(highscoresbutton));
		highScore.setToolTipText("Click this to everyone's highest scores!");
		highScore.setBorderPainted(false);

		// Play button action listener
		// When play button is clicked, call the game master
		// to change the screen. This swaps out the JPanel
		// to the JPanel that holds the game. 
		play.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				System.out.println("change thing");
				GameMaster.changeScreens(new Level(GameMaster.WIDTH, GameMaster.HEIGHT, 50, new LevelGenBlock()));
			}
		});
		
		// Credits button action listener
		// When the credits button is clicked, call the
		// game master to change to credits screen.
		credits.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				System.out.println("change screen");
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
		c.gridx = 2;
		c.gridy = 1;
		add(play,c);
		c.gridy = 2;
		add(credits,c);
		c.gridy = 3;
		add(highScore,c);
		
		// Set the JPanels attributes and revalidate.
		revalidate();
		this.setFocusable(true);
	    this.requestFocusInWindow();
		
	}
	
	public void paintComponent(Graphics g) {
		// Access the JPanel super class's
		// function for painting.
		super.paintComponent(g);

	    // Draw the background for this JPanel
	    g.setColor(Color.BLACK);
        g.drawImage(background, 0, 0, null);
        	     
	}
	
	
/*
	@Override
	public void init() {
		play = new JButton("Play");
		credits = new JButton("Credits");
		highScore = new JButton("High Score");
		gamePanel = new JPanel(new FlowLayout());
		gamePanel.add(play);
		gamePanel.add(credits);
		gamePanel.add(highScore);
		gamePanel.revalidate();
		gamePanel.setBackground(Color.BLUE);
		
		System.out.println("finished in menu");
		
	}*/

	/*@Override
	public void update() {
		handleInput();
		
		
	}

	@Override
	public void draw(Graphics2D g) {
		Font font = new Font("arial", Font.BOLD, 50);
		g.setFont(font);
		g.setColor(Color.WHITE);
		g.drawString("BLOCK STUFF",GameMaster.WIDTH/2 , 100);
		System.out.println("inside the block for draw mentustate ");
	}

	@Override
	public void handleInput() {
		play.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				StateManager.getFrame().removeAll();
				StateManager.setState("LEVEL");		
			}
		});
		
		credits.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				StateManager.getFrame().removeAll();
				StateManager.setState("CREDITS");	
			}
		});
		
	}*/

}
