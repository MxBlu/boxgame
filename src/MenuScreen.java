import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class MenuScreen extends JPanel{

    private Image background;
    private Image playbutton;
    private Image creditsbutton;
    private Image highscoresbutton;

	private JButton play;
	private JButton credits;
	private JButton highScore;
	
    private static final String MOVE_UP = "move up";
    private static final String MOVE_DOWN = "move down";
    private static final String QUIT_MENU = "quit menu";
	
	public MenuScreen() {
		//setTraversalKeys();
		
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
		// Give them names, dimension, and images. Also
		// add tool tips on mouse over to assist the user.
		play = new JButton();
		play.setIcon(new ImageIcon(playbutton));
		play.setToolTipText("Click this to begin the game!");
		play.setBorderPainted(false);
		play.setContentAreaFilled(false);
		
		credits = new JButton();
		credits.setIcon(new ImageIcon(creditsbutton));
		credits.setToolTipText("Click this to see who made the game!");
		credits.setBorderPainted(false);
		credits.setOpaque(false);
		credits.setContentAreaFilled(false);

		highScore = new JButton();
		highScore.setIcon(new ImageIcon(highscoresbutton));
		highScore.setToolTipText("Click this to everyone's highest scores!");
		highScore.setBorderPainted(false);
		highScore.setContentAreaFilled(false);

		// Play button action listener
		// When play button is clicked, call the game master
		// to change the screen. This swaps out the JPanel
		// to the JPanel that holds the game. 
		play.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				GameMaster.changeScreens(new Level(GameMaster.WIDTH, GameMaster.HEIGHT, 40, new LevelGenBlock()));
			}
		});
		
		// Credits button action listener
		// When the credits button is clicked, call the
		// game master to change to credits screen.
		credits.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				GameMaster.changeScreens(new SelectScreen());
			}
		});
		
		highScore.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				GameMaster.changeScreens(new IntermissionScreen());
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
		setFocusable(true);
	    requestFocusInWindow();
	    
	    // Keyinput
	    getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), MOVE_UP);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), MOVE_DOWN);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), QUIT_MENU);
		getActionMap().put(MOVE_UP, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				handleUp();
			}
		});
		
		getActionMap().put(MOVE_DOWN, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				handleDown();
			}
		});
		
		getActionMap().put(QUIT_MENU, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				quitMenu();
			}
		});
	}
	
	public void paintComponent(Graphics g) {
		// Access the JPanel super class's
		// function for painting.
		super.paintComponent(g);

	    // Draw the background for this JPanel
	    g.setColor(Color.BLACK);
        g.drawImage(background, 0, 0, null);
	}
	
	private void handleDown() {
		if (play.isFocusOwner()) {
			credits.requestFocus();
		} else if (credits.isFocusOwner()) {
			highScore.requestFocus();
		} else if (!highScore.isFocusOwner()) {
			play.requestFocus();
		}
	}
	
	private void handleUp() {
		if (highScore.isFocusOwner()) {
			credits.requestFocus();
		} else if (credits.isFocusOwner()) {
			play.requestFocus();
		} else if (!play.isFocusOwner()) {
			highScore.requestFocus();
		}
	}
	
	private void quitMenu() {
		((JFrame)SwingUtilities.getWindowAncestor(this)).dispose();
	}
	
	/*private void setTraversalKeys() {
		Set forwardKeys = getFocusTraversalKeys(
			    KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		Set newForwardKeys = new HashSet(forwardKeys);
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
			    newForwardKeys);
		
		Set backwardKeys = getFocusTraversalKeys(
			    KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
		Set newbackwardKeys = new HashSet(backwardKeys);
		newbackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
				newbackwardKeys);
	}*/

}
