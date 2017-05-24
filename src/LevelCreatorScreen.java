import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class LevelCreatorScreen extends JPanel{

    private Image background;
    private Image playbutton;
    private Image playpreset;
    private Image creditsbutton;
    private Image highscoresbutton;
    private Image backbutton;
    private Image quitbutton;

	private JButton play;
	private JButton playPreset;
	private JButton credits;
	private JButton highScore;
	private JButton back;
	private JButton quit;
	
	private Tile levelMap[][];
	private Image tileImgs[];
	private int width = 17;
	private int height = 17;
	private int tileSize = 40;
	private Tile curPlaceTile = Tile.WALKABLE;
	
	private JPanel difficultyPanel;
	private Boolean difficultyShow;
	
    private static final String MOVE_UP = "move up";
    private static final String MOVE_DOWN = "move down";
    private static final String QUIT_MENU = "quit menu";
	private static final Object MOVE_LEFT = "move left";
	private static final Object MOVE_RIGHT = "move right";
	
	public LevelCreatorScreen() {
		//setTraversalKeys();
		
		levelMap = new Tile[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				levelMap[i][j] = Tile.WALL;
			}
		}
		
		levelMap[4][5] = Tile.WALKABLE;
		levelMap[4][8] = Tile.WALKABLE;
		
		//levelMap[20][20] = Tile.WALL;
		setDefaultTiles();
		
		// Get the image file for the background and buttons
		try {
			background = ImageIO.read(getClass().getResourceAsStream("menu.png"));
			playbutton = ImageIO.read(getClass().getResourceAsStream("playbutton.png"));
			playpreset = ImageIO.read(getClass().getResourceAsStream("playpresetbutton.png"));
			backbutton = ImageIO.read(getClass().getResourceAsStream("back.png"));
			creditsbutton = ImageIO.read(getClass().getResourceAsStream("creditsbutton.png"));
			highscoresbutton = ImageIO.read(getClass().getResourceAsStream("highscoresbutton.png"));
			quitbutton = ImageIO.read(getClass().getResourceAsStream("quitbutton.png"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}		
		
		difficultyPanel = new DifficultyPanel(); 
		difficultyShow = false;
		
		// Initialize JButtons
		// Give them names, dimension, and images. Also
		// add tool tips on mouse over to assist the user.
		play = new JButton();
		play.setIcon(new ImageIcon(playbutton));
		play.setBorderPainted(false);
		play.setContentAreaFilled(false);
		
		playPreset = new JButton();
		playPreset.setIcon(new ImageIcon(playpreset));
		playPreset.setBorderPainted(false);
		playPreset.setContentAreaFilled(false);
		
		credits = new JButton();
		credits.setIcon(new ImageIcon(creditsbutton));
		credits.setBorderPainted(false);
		credits.setOpaque(false);
		credits.setContentAreaFilled(false);

		highScore = new JButton();
		highScore.setIcon(new ImageIcon(highscoresbutton));
		highScore.setBorderPainted(false);
		highScore.setContentAreaFilled(false);
		
		back = new JButton();
		back.setIcon(new ImageIcon(backbutton));
		back.setToolTipText("Back button");
		back.setBorderPainted(false);
		back.setContentAreaFilled(false);
		
		quit = new JButton();
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
				GameMaster.changeScreens(new SelectScreen());
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
		
		highScore.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				GameMaster.changeScreens(new LevelCreatorScreen());
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
		
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				//Convert click coords to grid coords
				int xCord = e.getX() - (GameMaster.WIDTH / 2);
				int yCord = e.getY() - (GameMaster.HEIGHT / 2);
				xCord += (tileSize * width) / 2;
				yCord += (tileSize * width) / 2;
				xCord /= tileSize;
				yCord /= tileSize;
				
				if (xCord < width && yCord < height && xCord >= 0 && yCord >= 0) {
					levelMap[xCord][yCord] = curPlaceTile;
				}
				repaint();
			}
		});
	
		//setButtonLayout();
		
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
				highScore.requestFocus();
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
	
	private void setDefaultTiles() {
		tileImgs = new Image[4];
		
		try {
			tileImgs[0] = ImageIO.read(getClass().getResourceAsStream("ground_solid.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			tileImgs[1] = ImageIO.read(getClass().getResourceAsStream("ground_empty.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			//todo remove [2]
			tileImgs[2] = ImageIO.read(getClass().getResourceAsStream("box.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			tileImgs[3] = ImageIO.read(getClass().getResourceAsStream("goal.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void paintComponent(Graphics g) {
		Graphics2D bbg = (Graphics2D) g;
		
		int tileSize = 40;
		
		int left = (int) ((double) GameMaster.WIDTH/2 - (double) (width * tileSize)/2);
		int top = (int) ((double) GameMaster.HEIGHT/2 - (double) (height * tileSize)/2);
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) { 
				if (tileImgs[levelMap[i][j].getIntRep()] != null) {
					bbg.drawImage(tileImgs[levelMap[j][i].getIntRep()], left + j * tileSize, top + i * tileSize, null);
				} else {
					bbg.setColor(new Color(levelMap[j][i].getIntRep() * 127));
					bbg.fillRect(left + j * tileSize, top + i * tileSize, tileSize, tileSize);
				}
			}
		}
		
		//for (Box box : boxList) {
		//	box.draw(bbg);
		//}
		
		//player.paintComponent(bbg);
		//super.paintComponent(bbg);
	}
	
	private void handleDown() {
		if (play.isFocusOwner()) {
			playPreset.requestFocus();
		} else if (playPreset.isFocusOwner()) { 
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
			playPreset.requestFocus();
		} else if (playPreset.isFocusOwner()) { 
			play.requestFocus();
		} else if (!play.isFocusOwner()) {
			highScore.requestFocus();
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
		add(credits,c);
		c.gridy = 4;
		//add(highScore,c);
		c.gridx = 3;
		c.gridy = 4;
		//add(quit,c);
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
			remove(credits);
			remove(highScore);
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
