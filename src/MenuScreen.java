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
    private Image playpreset;
    private Image levelcreatorbutton;
    private Image backbutton;
    private Image quitbutton;

	private HoverButton play;
	private HoverButton playPreset;
	private HoverButton levelcreator;
	private HoverButton back;
	private HoverButton quit;

	private JPanel difficultyPanel;
	private Boolean difficultyShow;
	
	private JFrame frame;
	
    private static final String MOVE_UP = "move up";
    private static final String MOVE_DOWN = "move down";
    private static final String QUIT_MENU = "quit menu";
	private static final Object MOVE_LEFT = "move left";
	private static final Object MOVE_RIGHT = "move right";
	
	public MenuScreen(JFrame frame) {
		this.frame = frame;
		
		// Get the image file for the background and buttons
		try {
			background = ImageIO.read(getClass().getResourceAsStream("menu.png"));
			playbutton = ImageIO.read(getClass().getResourceAsStream("playbutton.png"));
			playpreset = ImageIO.read(getClass().getResourceAsStream("playpresetbutton.png"));
			backbutton = ImageIO.read(getClass().getResourceAsStream("back.png"));
			levelcreatorbutton = ImageIO.read(getClass().getResourceAsStream("levelcreatorbutton.png"));
			quitbutton = ImageIO.read(getClass().getResourceAsStream("quitbutton.png"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}		
		
		difficultyPanel = new DifficultyPanel(frame); 
		difficultyShow = false;
		
		// Initialize JButtons
		// Give them names, dimension, and images. Also
		// add tool tips on mouse over to assist the user.
		play = new HoverButton();
		play.setIcon(new ImageIcon(playbutton));
		play.setBorderPainted(false);
		play.setContentAreaFilled(false);
		
		playPreset = new HoverButton();
		playPreset.setIcon(new ImageIcon(playpreset));
		playPreset.setBorderPainted(false);
		playPreset.setContentAreaFilled(false);

		levelcreator = new HoverButton();
		levelcreator.setIcon(new ImageIcon(levelcreatorbutton));
		levelcreator.setBorderPainted(false);
		levelcreator.setContentAreaFilled(false);
		
		back = new HoverButton();
		back.setIcon(new ImageIcon(backbutton));
		back.setToolTipText("Back button");
		back.setBorderPainted(false);
		back.setContentAreaFilled(false);
		
		quit = new HoverButton();
		quit.setIcon(new ImageIcon(quitbutton));
		quit.setBorderPainted(false);
		quit.setContentAreaFilled(false);

		// Play button action listener
		// When play button is clicked, call the game master
		// to change the screen. This swaps out the JPanel
		// to the JPanel that holds the game. 
		play.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				toggleDifficulty();
			}
		});
		
		playPreset.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				GameMaster.changeScreens(frame, new SelectScreen(frame));
			}
		});
		
		levelcreator.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				GameMaster.changeScreens(frame, new LevelCreatorScreen(frame, GameMaster.WIDTH, GameMaster.HEIGHT, 40));
			}
		});
	
		back.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				toggleDifficulty();
			}
		});
		
		quit.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				quitMenu();
			}
		});
	
		setButtonLayout();
		
		// Set the JPanels attributes and revalidate.
		revalidate();
		setFocusable(true);
	    requestFocusInWindow();
	    
	    // Keyinput
	    getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), MOVE_UP);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), MOVE_DOWN);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), MOVE_LEFT);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), MOVE_RIGHT);
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
		
		getActionMap().put(MOVE_UP, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				handleUp();
			}
		});
		
		getActionMap().put(MOVE_LEFT, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				levelcreator.requestFocus();
			}
		});
		
		getActionMap().put(MOVE_RIGHT, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				quit.requestFocus();
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
			playPreset.requestFocus();
		} else if (playPreset.isFocusOwner()) {
			levelcreator.requestFocus();
		} else if (!levelcreator.isFocusOwner()) {
			play.requestFocus();
		}
	}
	
	private void handleUp() {
		if (levelcreator.isFocusOwner()) {
			playPreset.requestFocus();
		} else if (playPreset.isFocusOwner()) { 
			play.requestFocus();
		} else if (!play.isFocusOwner()) {
			levelcreator.requestFocus();
		}
	}
	
	private void quitMenu() {
		System.exit(0);
	}
	
	private void setButtonLayout() {
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
		add(playPreset,c);
		c.gridy = 3;
		add(levelcreator,c);
		c.gridx = 3;
		c.gridy = 3;
		add(quit,c);
	}
	
	public void toggleDifficulty() {
		if (difficultyShow) {
			difficultyShow = false;
			remove(difficultyPanel);
			remove(back);
			setButtonLayout();
		} else {
			difficultyShow = true;
			add(back);
			add(difficultyPanel);
			remove(play);
			remove(playPreset);
			remove(levelcreator);
			remove(quit);

		}
		
		revalidate();
		repaint();
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
