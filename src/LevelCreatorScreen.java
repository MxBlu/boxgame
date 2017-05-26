import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import javafx.scene.control.ButtonBar.ButtonData;

public class LevelCreatorScreen extends JPanel{
	
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
	
	private Tile levelMap[][];
	private Image tileImgs[];
	private Image savebutton;
	private Image loadbutton;
	private Image backbutton;
	private int width = 17;
	private int height = 17;
	private int tileSize = 40;
	private Tile curPlaceTile = Tile.PLAYER;
	private JPanel uiPanel;
	private JFrame frame;
	
    private static final String QUIT_MENU = "quit menu";
	
	public LevelCreatorScreen(JFrame frame, int screenWidth, int screenHeight, int tileSize) {
		//GameMaster.toggleCursorPointer();
		this.frame = frame;
		
		this.width = screenWidth/tileSize;
		this.height = screenHeight/tileSize;
		this.tileSize = tileSize;
		
		levelMap = new Tile[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				levelMap[i][j] = Tile.WALL;
			}
		}
		
		setDefaultTiles();
		
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				//Convert click coords to grid coords
				int xCord = e.getX();// - (GameMaster.WIDTH / 2);
				int yCord = e.getY();// - (GameMaster.HEIGHT / 2);
				//xCord += (tileSize * width) / 2;
				//yCord += (tileSize * width) / 2;
				xCord /= tileSize;
				yCord /= tileSize;
				
				if (xCord < width && yCord < height && xCord >= 0 && yCord >= 0) {
					setTile(xCord, yCord, curPlaceTile);
				}
				repaint();
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
				int xCord = e.getX();// - (GameMaster.WIDTH / 2);
				int yCord = e.getY();// - (GameMaster.HEIGHT / 2);
				//xCord += (tileSize * width) / 2;
				//yCord += (tileSize * width) / 2;
				xCord /= tileSize;
				yCord /= tileSize;
				
				if (xCord < width && yCord < height && xCord >= 0 && yCord >= 0) {
					setTile(xCord, yCord, curPlaceTile);
				}
				repaint();
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
	
	private void setDefaultTiles() {
		tileImgs = new Image[8];
		
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
	
	
	public void paintComponent(Graphics g) {
		int left = (int) ((double) GameMaster.WIDTH/2 - (double) (width * tileSize)/2);
		int top = (int) ((double) GameMaster.HEIGHT/2 - (double) (height * tileSize)/2);
		paintMap(g, left, top);
		
		super.paintComponent(g);
	}
	
	private void paintMap(Graphics g, int left, int top) {
		Graphics2D bbg = (Graphics2D) g;
		
		//Paint all as walkables first to "clear the screen"
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) { 
				bbg.drawImage(tileImgs[Tile.WALKABLE.getIntRep()], left + j * tileSize, top + i * tileSize, null);
			}
		}		
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) { 
				if ((levelMap[j][i] == Tile.WALL) && 
						((j < (width - 1) && (levelMap[j + 1][i] == Tile.WALKABLE || levelMap[j + 1][i] == Tile.GOAL)) ||
						((j < (width - 1) && i > 0) && (levelMap[j + 1][i - 1] == Tile.WALKABLE || levelMap[j + 1][i - 1] == Tile.GOAL)) ||
						((i > 0) && (levelMap[j][i - 1] == Tile.WALKABLE || levelMap[j][i - 1] == Tile.GOAL)) ||
						((j > 0 && i > 0) && (levelMap[j - 1][i - 1] == Tile.WALKABLE || levelMap[j - 1][i - 1] == Tile.GOAL)) ||
						((j > 0) && (levelMap[j - 1][i] == Tile.WALKABLE || levelMap[j - 1][i] == Tile.GOAL)) ||
						((j > 0 && i < (height - 1)) && (levelMap[j - 1][i + 1] == Tile.WALKABLE || levelMap[j - 1][i + 1] == Tile.GOAL)) ||
						((i < (height - 1)) && (levelMap[j][i + 1] == Tile.WALKABLE || levelMap[j][i + 1] == Tile.GOAL)) ||
						((j < (width - 1) && i < (height - 1)) && (levelMap[j + 1][i + 1] == Tile.WALKABLE || levelMap[j + 1][i + 1] == Tile.GOAL))))
					bbg.drawImage(tileImgs[Tile.BORDER.getIntRep()], left + j * tileSize, top + i * tileSize, null);
				else
					bbg.drawImage(tileImgs[levelMap[j][i].getIntRep()], left + j * tileSize, top + i * tileSize, null);
			}
		}
	}
	
	private void setTile(int gridX, int gridY, Tile tile) {
		if (!(gridX > 0 && gridY > 0 && gridX < (width - 1) && gridY < (height - 1)))
			return;
		
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
		
		levelMap[gridX][gridY] = curPlaceTile;
	}
	
	private void setupUI() {
		setOpaque(false);
		setLayout(null);

		uiPanel = new JPanel(new GridBagLayout());
		uiPanel.setPreferredSize(new Dimension(GameMaster.WIDTH, 80));
		uiPanel.setBounds(new Rectangle(new Point(0, (int) (GameMaster.HEIGHT - uiPanel.getPreferredSize().getHeight())), uiPanel.getPreferredSize()));
		uiPanel.setBackground(new Color(58, 58, 58));
		
		setUpTilesPanel();
		
		add(uiPanel);
	}
	
	private void setUpTilesPanel() {
		uiPanel.removeAll();
		uiPanel.revalidate();
		
		for (int i = 0; i < Tile.values().length-3; i++) {
			JButton tileButton = new HoverButton();
			tileButton.setIcon(new ImageIcon(tileImgs[i]));
			tileButton.setPreferredSize(new Dimension(tileImgs[i].getWidth(null), tileImgs[i].getHeight(null)));
			//tileButton.setBorderPainted(false);
			tileButton.addActionListener(new tileBtnListener(Tile.getTile(i)));
			uiPanel.add(tileButton);
		}
		
		JButton loadButton = new JButton();
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
		
		JButton saveButton = new JButton();
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
		
		JButton backButton = new JButton();
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
		
		repaint();
	}
	
	private void setUpLoadPanel() {
		uiPanel.removeAll();
		uiPanel.revalidate();
		
		for (int i = 0; i < 6; i++) {
			JButton tileButton = new JButton("Slot " + (i+1));
			tileButton.addActionListener(new loadSlotListener(i+1));
			uiPanel.add(tileButton);
		}
		
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
		
		repaint();
	}
	
	private void setUpSavePanel() {
		uiPanel.removeAll();
		uiPanel.revalidate();
		
		for (int i = 0; i < 6; i++) {
			JButton tileButton = new JButton("Slot " + (i+1));
			tileButton.addActionListener(new saveSlotListener(i+1));
			uiPanel.add(tileButton);
		}
		
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
		
		repaint();
	}
	
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
	
	private void saveLevel(int slot) {
		setUpTilesPanel();
		
		if (!isValidLevel()) {
			JOptionPane.showMessageDialog(null, "Invalid Level");
			return;
		}
		
		String levelString = "";
		//Save level map
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
		
		//Render an image of the map and write it out
		BufferedImage image = new BufferedImage(width * tileSize, height * tileSize, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		paintMap(g, 0, 0);
		try {
			ImageIO.write(image, "png", new File("levels/levelc-" + slot + ".png"));
		} catch (IOException ex) {
//			Logger.getLogger(CustomApp.class.getName()).log(Level.SEVERE, null, ex);
		}

		JOptionPane.showMessageDialog(null, "Saved successfully to slot " + slot);
		
	}

	private void exitScreen() {
		GameMaster.changeScreens(frame, new MenuScreen(frame));
	}
	
}
