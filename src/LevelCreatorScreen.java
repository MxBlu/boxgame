import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;

/**
 * A screen that provides a UI and method to both
 * load, save and create custom maps for the game
 */
public class LevelCreatorScreen extends JPanel {
	
	/**
	 * Small listener class containing metadata 
	 * to change the active tile
	 */
	class tileBtnListener implements ActionListener {
		private Tile tile;
		
		public tileBtnListener(Tile tile) {
			this.tile = tile;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			curPlaceTile = tile;
		}
	}
	
	/**
	 * Small listener class containing metadata 
	 * to change the load a level
	 */
	class loadSlotListener implements ActionListener {
		private int slot;
		
		public loadSlotListener(int slot) {
			this.slot = slot;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			loadLevel(slot);
		}
	}
	
	/**
	 * Small listener class containing metadata 
	 * to change the save a level
	 */
	class saveSlotListener implements ActionListener {
		private int slot;
		
		public saveSlotListener(int slot) {
			this.slot = slot;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			saveLevel(slot);
		}
	}

	private JFrame frame;
	
	private Image tileImgs[];
	private Image savebutton;
	private Image loadbutton;
	private Image backbutton;
	
	private int width;
	private int height;
	private int tileSize;
	private Tile levelMap[][];
	private Tile curPlaceTile = Tile.PLAYER;
	
	private JPanel uiPanel;
	private Font buttonsFont;
	
	private final int slotButtonWidth = 120;
	private final int slotButtonHeight = 48;
	private final int fillerSize = 20;
    private static final String QUIT_MENU = "quit menu";
	
    /**
     * LevelCreatorScreen constructor
     * @param frame The frame which the panel is being added to
     * @param screenWidth Width of the window
     * @param screenHeight Height of the window
     * @param tileSize Size of tiles in pixels
     */
	public LevelCreatorScreen(JFrame frame, int screenWidth, int screenHeight, int tileSize) {
		this.frame = frame;

		this.width = screenWidth / tileSize;
		this.height = screenHeight / tileSize;
		this.tileSize = tileSize;

		levelMap = new Tile[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				levelMap[i][j] = Tile.WALL;
			}
		}

		try {
			buttonsFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("VCR_OSD_MONO.ttf"))
					.deriveFont(Font.PLAIN, 24);
		} catch (FontFormatException | IOException e1) {
			e1.printStackTrace();
		}

		setDefaultTiles();

		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				// Convert click coords to grid coords
				int xCord = e.getX();
				int yCord = e.getY();
				xCord /= tileSize;
				yCord /= tileSize;

				setTile(xCord, yCord, curPlaceTile);
			}
		});

		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				//Convert click coords to grid coords
				int xCord = e.getX();
				int yCord = e.getY();
				xCord /= tileSize;
				yCord /= tileSize;
				
				setTile(xCord, yCord, curPlaceTile);
			}
		});
		
		// Set the JPanels attributes and revalidate.
		revalidate();
		setFocusable(true);
	    requestFocusInWindow();
	    setupUI(); 
	    
	    // Keyinput
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), QUIT_MENU);
		
		getActionMap().put(QUIT_MENU, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				exitScreen();
			}
		});
	}
	
	/**
	 * Loads in the default tile image set
	 */
	private void setDefaultTiles() {
		tileImgs = new Image[8];
		
		try {
			tileImgs[0] = ImageIO.read(getClass().getResourceAsStream("ground_solid.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			tileImgs[1] = ImageIO.read(getClass().getResourceAsStream("ground_empty.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			tileImgs[2] = ImageIO.read(getClass().getResourceAsStream("box.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			tileImgs[3] = ImageIO.read(getClass().getResourceAsStream("goal.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			tileImgs[4] = ImageIO.read(getClass().getResourceAsStream("player.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			tileImgs[7] = ImageIO.read(getClass().getResourceAsStream("border.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			
			savebutton = ImageIO.read(getClass().getResourceAsStream("savebutton.png"));
			loadbutton = ImageIO.read(getClass().getResourceAsStream("loadbutton.png"));
			backbutton = ImageIO.read(getClass().getResourceAsStream("backbutton.png"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Draws the panel
	 */
	public void paintComponent(Graphics g) {
		int left = (int) ((double) GameMaster.WIDTH/2 - (double) (width * tileSize)/2);
		int top = (int) ((double) GameMaster.HEIGHT/2 - (double) (height * tileSize)/2);
		paintMap(g, left, top);
		
		super.paintComponent(g);
	}
	
	/**
	 * Draws the level map
	 */
	private void paintMap(Graphics g, int left, int top) {
		Graphics2D bbg = (Graphics2D) g;
		
		//Paint all as walkables first to "clear the screen"
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) { 
				bbg.drawImage(tileImgs[Tile.WALKABLE.getIntRep()], left + j * tileSize, top + i * tileSize, null);
			}
		}		
		
		//Paints the level tiles
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) { 
				//Border logic
				if ((levelMap[j][i] == Tile.WALL) && 
						((j < (width - 1) && (levelMap[j + 1][i] == Tile.WALKABLE || levelMap[j + 1][i] == Tile.GOAL || levelMap[j + 1][i] == Tile.PLAYER || levelMap[j + 1][i] == Tile.BOX)) ||
						((j < (width - 1) && i > 0) && (levelMap[j + 1][i - 1] == Tile.WALKABLE || levelMap[j + 1][i - 1] == Tile.GOAL || levelMap[j + 1][i - 1] == Tile.PLAYER || levelMap[j + 1][i - 1] == Tile.BOX)) ||
						((i > 0) && (levelMap[j][i - 1] == Tile.WALKABLE || levelMap[j][i - 1] == Tile.GOAL || levelMap[j][i - 1] == Tile.PLAYER || levelMap[j][i - 1] == Tile.BOX)) ||
						((j > 0 && i > 0) && (levelMap[j - 1][i - 1] == Tile.WALKABLE || levelMap[j - 1][i - 1] == Tile.GOAL || levelMap[j - 1][i - 1] == Tile.PLAYER || levelMap[j - 1][i - 1] == Tile.BOX)) ||
						((j > 0) && (levelMap[j - 1][i] == Tile.WALKABLE || levelMap[j - 1][i] == Tile.GOAL || levelMap[j - 1][i] == Tile.PLAYER || levelMap[j - 1][i] == Tile.BOX)) ||
						((j > 0 && i < (height - 1)) && (levelMap[j - 1][i + 1] == Tile.WALKABLE || levelMap[j - 1][i + 1] == Tile.GOAL || levelMap[j - 1][i + 1] == Tile.PLAYER || levelMap[j - 1][i + 1] == Tile.BOX)) ||
						((i < (height - 1)) && (levelMap[j][i + 1] == Tile.WALKABLE || levelMap[j][i + 1] == Tile.GOAL || levelMap[j][i + 1] == Tile.PLAYER || levelMap[j][i + 1] == Tile.BOX)) ||
						((j < (width - 1) && i < (height - 1)) && (levelMap[j + 1][i + 1] == Tile.WALKABLE || levelMap[j + 1][i + 1] == Tile.GOAL || levelMap[j + 1][i + 1] == Tile.PLAYER || levelMap[j + 1][i + 1] == Tile.BOX))))
					bbg.drawImage(tileImgs[Tile.BORDER.getIntRep()], left + j * tileSize, top + i * tileSize, null);
				else
					bbg.drawImage(tileImgs[levelMap[j][i].getIntRep()], left + j * tileSize, top + i * tileSize, null);
			}
		}
	}
	
	/**
	 * Sets the the tile at the grid cordinates
	 * @param gridX The x coord in the grid
	 * @param gridY The y coord in the grid
	 * @param tile The tile type to set it as
	 */
	private void setTile(int gridX, int gridY, Tile tile) {
		//Check if out of bounds
		if (!(gridX > 0 && gridY > 0 && gridX < (width - 1) && gridY < (height - 1)))
			return;
		
		//Change the position of the player tile if needed
		if (tile == Tile.PLAYER) {
			//Remove the previous player
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					if (levelMap[i][j] == Tile.PLAYER) {
						levelMap[i][j] = Tile.WALKABLE;
					}
				}
			}
		}
		
		//Set the tile
		levelMap[gridX][gridY] = curPlaceTile;
		
		//Redraw the level
		repaint();
	}
	
	/**
	 * Sets up the UI for the panel
	 */
	private void setupUI() {
		setOpaque(false);
		setLayout(null);

		//Set up the panel on the bottom of the screen
		uiPanel = new JPanel(new GridBagLayout());
		uiPanel.setPreferredSize(new Dimension(GameMaster.WIDTH, 80));
		uiPanel.setBounds(new Rectangle(new Point(0, (int) (GameMaster.HEIGHT - uiPanel.getPreferredSize().getHeight())), uiPanel.getPreferredSize()));
		uiPanel.setBackground(new Color(58, 58, 58));
		
		setUpTilesPanel();
		
		add(uiPanel);
	}
	
	/**
	 * Sets up the panel containing the tiles
	 */
	private void setUpTilesPanel() {
		uiPanel.removeAll();
		uiPanel.revalidate();
		
		//Set up the buttons for the tiles
		for (int i = 0; i < Tile.values().length-3; i++) {
			JButton tileButton = new HoverButton();
			tileButton.setIcon(new ImageIcon(tileImgs[i]));
			tileButton.setPreferredSize(new Dimension(tileImgs[i].getWidth(null), tileImgs[i].getHeight(null)));
			tileButton.addActionListener(new tileBtnListener(Tile.getTile(i)));
			uiPanel.add(tileButton);
		}
		
		//Set up the load button
		JButton loadButton = new HoverButton();
		loadButton.setIcon(new ImageIcon(loadbutton));
		loadButton.setBorderPainted(false);
		loadButton.setContentAreaFilled(false);
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setUpLoadPanel();
			}
		});
		uiPanel.add(loadButton);
		
		//Set up the save button
		JButton saveButton = new HoverButton();
		saveButton.setIcon(new ImageIcon(savebutton));
		saveButton.setBorderPainted(false);
		saveButton.setContentAreaFilled(false);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setUpSavePanel();
			}
		});
		uiPanel.add(saveButton);
		
		//Set up the back button
		JButton backButton = new HoverButton();
		backButton.setIcon(new ImageIcon(backbutton));
		backButton.setBorderPainted(false);
		backButton.setContentAreaFilled(false);
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exitScreen();
			}
		});
		backButton.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		uiPanel.add(backButton);
		
		//Redraw after all set up
		repaint();
	}
	
	/**
	 * Sets up the load panel
	 */
	private void setUpLoadPanel() {
		uiPanel.removeAll();
		uiPanel.revalidate();
		
		//Sets up a button for each of the 6 slots
		for (int i = 0; i < 6; i++) {
			JButton tileButton = new HoverButton();
			tileButton.setText("Slot " + (i+1));
			tileButton.addActionListener(new loadSlotListener(i+1));
			tileButton.setFont(buttonsFont);
			tileButton.setForeground(new Color(167, 255, 1));
			tileButton.setBorder(new LineBorder(Color.WHITE, 2, true));
			tileButton.setBackground(new Color(0, 0, 0));
			tileButton.setPreferredSize(new Dimension(slotButtonWidth, slotButtonHeight));
			uiPanel.add(tileButton);
			
			//Skip last filler
			if (i == 6) break;
			
			//Add a filler panel for blank space between buttons
			JPanel borderFiller2 = new JPanel();
			borderFiller2.setOpaque(false);
			borderFiller2.setPreferredSize(new Dimension(fillerSize, fillerSize));
			uiPanel.add(borderFiller2);
		}
		
		//Set up the back button
		JButton backButton = new JButton();
		backButton.setIcon(new ImageIcon(backbutton));
		backButton.setBorderPainted(false);
		backButton.setContentAreaFilled(false);		
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setUpTilesPanel();
			}
		});
		backButton.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		uiPanel.add(backButton);
		
		//Redraw the panel after setup
		repaint();
	}
	
	/**
	 * Sets up the save panel
	 */
	private void setUpSavePanel() {
		uiPanel.removeAll();
		uiPanel.revalidate();
		
		//Sets up a button for each of the 6 slots
		for (int i = 0; i < 6; i++) {
			JButton tileButton = new HoverButton();
			tileButton.setText("Slot " + (i+1));
			tileButton.addActionListener(new saveSlotListener(i+1));
			tileButton.setFont(buttonsFont);
			tileButton.setForeground(new Color(0, 255, 248));
			tileButton.setBorder(new LineBorder(Color.WHITE, 2, true));
			tileButton.setBackground(new Color(0, 0, 0));
			tileButton.setPreferredSize(new Dimension(slotButtonWidth, slotButtonHeight));
			uiPanel.add(tileButton);
			
			//Skip last filler
			if (i == 6) break;
			
			//Add a filler panel for blank space between buttons
			JPanel borderFiller2 = new JPanel();
			borderFiller2.setOpaque(false);
			borderFiller2.setPreferredSize(new Dimension(fillerSize, fillerSize));
			uiPanel.add(borderFiller2);
		}
		
		//Set up the back button
		JButton backButton = new JButton();
		backButton.setIcon(new ImageIcon(backbutton));
		backButton.setBorderPainted(false);
		backButton.setContentAreaFilled(false);
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setUpTilesPanel();
			}
		});
		backButton.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		uiPanel.add(backButton);
		
		//Redraw the panel after setup
		repaint();
	}
	
	/**
	 * Checks if the current level is valid
	 * by doing a simple check of if there's a player
	 * and if the box count is = to the goal count
	 */
	private boolean isValidLevel() {
		int boxCount = 0;
		int goalCount = 0;
		int playerCount = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (levelMap[i][j] == Tile.BOX) {
					boxCount++;
				} else if (levelMap[i][j] == Tile.GOAL) {
					goalCount++;
				} else if (levelMap[i][j] == Tile.PLAYER) {
					playerCount++;
				}
			}
		}
		
		if (!(boxCount == goalCount && boxCount > 0)) {
			return false;
		}
		
		if (playerCount != 1) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Load the level from one of the 6 slots
	 * @param slot The slot to load from (slot >=1 && slot <=6)
	 */
	private void loadLevel(int slot) {
		setUpTilesPanel();
		
		try {
			Scanner sc = new Scanner(new FileReader(new File("levels/levelc-" + slot + ".txt")));
			String levelString = "";
			
			width = 0;
			height = 0;
			
			while (sc.hasNextLine()) {
				String lineString = sc.nextLine();
				Scanner lsScanner = new Scanner(lineString);
				if (!lsScanner.hasNext()) {
					break;
				}

				width = lineString.length();
				levelString += lineString + "\n";
				height++;

			}
			
			levelMap = new Tile[width][height];
			
			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width; i++) {
					levelMap[i][j] =  Tile.getTile(levelString.charAt(i+ (j*width) + j) - '0');
				}
			}
			
			repaint();
			
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File not found");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Save the level to one of the 6 slots
	 * @param slot The slot to load from (slot >=1 && slot <=6)
	 */
	private void saveLevel(int slot) {
		//Change back to the tiles panel
		setUpTilesPanel();
		
		//Check if the level is valid and if not exit
		if (!isValidLevel()) {
			JOptionPane.showMessageDialog(null, "Invalid Level");
			return;
		}
		
		String levelString = "";
		//Store level map in the level string
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				levelString += (char)(((char) levelMap[i][j].getIntRep()) + '0');
			}
			levelString += "\n";
		}
		levelString += "\n";
		//Save level picture
		levelString += "levelc-" + slot + ".png\n";
		//Save default highscore
		levelString += "None";
		
		//Write out to the file
		try {
			PrintWriter out = new PrintWriter("levels/levelc-" + slot + ".txt");
			out.write(levelString);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//Generate and save out a render of the level
		// Find out how wide the level actually is to calculate the image size
		int startingX = -1;
		int endingX = -1;
		int startingY = -1;
		int endingY = -1;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (levelMap[i][j] == Tile.WALKABLE || levelMap[i][j] == Tile.BOX || levelMap[i][j] == Tile.BOX
						|| levelMap[i][j] == Tile.PLAYER) {
					if (i < startingX || startingX == -1) {
						startingX = i;
					}
					if (i > endingX || endingX == -1) {
						endingX = i;
					}
					if (j < startingY || startingY == -1) {
						startingY = j;
					}
					if (j > endingY || endingY == -1) {
						endingY = j;
					}
				}
			}
		}
		
		//Calculate the offset for rendering and the size of the render image
		int borderSize = 4;
		int renderTileWidth = endingX - startingX;
		int renderTileHeight = endingY - startingY;
		int renderTileSize = 1;
		if (renderTileHeight > renderTileWidth) {
			renderTileSize = renderTileHeight + borderSize;
			startingY -= borderSize / 2;
			startingX -= (renderTileSize - renderTileWidth) / 2;
		} else {
			renderTileSize = renderTileWidth + borderSize;
			startingX -= borderSize / 2;
			startingY -= (renderTileSize - renderTileHeight) / 2;
		}
		
		//Render an image of the map and write it out
		BufferedImage image = new BufferedImage(renderTileSize * tileSize, renderTileSize * tileSize, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) image.getGraphics();
		
		//Paint all as walkables first to "clear the screen"
		for (int i = 0; i < renderTileSize; i++) {
			for (int j = 0; j < renderTileSize; j++) { 
				g.drawImage(tileImgs[Tile.WALL.getIntRep()], 0 + j * tileSize, 0+ i * tileSize, null);
			}
		}	
		
		//Render the map
		paintMap(g, -startingX * tileSize, -startingY * tileSize);
		try {
			//Write out the image
			ImageIO.write(image, "png", new File("levels/levelc-" + slot + ".png"));
		} catch (IOException ex) {
			//Logger.getLogger(CustomApp.class.getName()).log(Level.SEVERE, null, ex);
		}

		JOptionPane.showMessageDialog(null, "Saved successfully to slot " + slot);
		
	}

	/**
	 * Exits back to the menu screen
	 */
	private void exitScreen() {
		GameMaster.changeScreens(frame, new MenuScreen(frame));
	}
	
}
