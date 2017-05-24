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
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
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
	
	private Tile levelMap[][];
	private Image tileImgs[];
	private int width = 17;
	private int height = 17;
	private int tileSize = 40;
	private Tile curPlaceTile = Tile.PLAYER;
	private JPanel uiPanel;
	
    private static final String QUIT_MENU = "quit menu";
	
	public LevelCreatorScreen(int width, int height, int tileSize) {
		this.width = width;
		this.height = height;
		this.tileSize = tileSize;
		
		levelMap = new Tile[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				levelMap[i][j] = Tile.WALL;
			}
		}
		
		levelMap[4][5] = Tile.WALKABLE;
		levelMap[4][8] = Tile.WALKABLE;
		
		setDefaultTiles();
		
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
				GameMaster.changeScreens(new MenuScreen());
			}
		});
	}
	
	private void setDefaultTiles() {
		tileImgs = new Image[5];
		
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void paintComponent(Graphics g) {
		Graphics2D bbg = (Graphics2D) g;
		
		
		int left = (int) ((double) GameMaster.WIDTH/2 - (double) (width * tileSize)/2);
		int top = (int) ((double) GameMaster.HEIGHT/2 - (double) (height * tileSize)/2);
		
		//Paint all as walkables first to "clear the screen"
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) { 
				bbg.drawImage(tileImgs[Tile.WALKABLE.getIntRep()], left + j * tileSize, top + i * tileSize, null);
			}
		}		
		
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
		
		super.paintComponent(bbg);
	}
	
	private void setTile(int gridX, int gridY, Tile tile) {
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

		/*movesLabel = new JLabel("Moves: " + moves);
		Font font = new Font("Arial", Font.PLAIN, 35);
		movesLabel.setFont(font);
		movesLabel.setForeground(Color.WHITE);
		movesLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		
		timerLabel = new JLabel("Time: 0:00:000");
		timerLabel.setFont(font);
		timerLabel.setForeground(Color.WHITE);
		timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));*/
		
		uiPanel = new JPanel(new GridBagLayout());
		uiPanel.setPreferredSize(new Dimension(GameMaster.WIDTH, 80));
		uiPanel.setBounds(new Rectangle(new Point(0, (int) (GameMaster.HEIGHT - uiPanel.getPreferredSize().getHeight())), uiPanel.getPreferredSize()));
		uiPanel.setBackground(new Color(58, 58, 58));
		
		/*Pause = new JButton("Pause");
		Pause.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				togglePaused();			
				}
		});
		//Pause.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		Undo = new JButton("Undo");
		Undo.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				if (!player.isAnimating() && !isPaused)
					undo();
			}
		});
		//Undo.setBorder(new EmptyBorder(10, 10, 10, 10));
		uiButtonsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10,10,10,10);
		c.gridy = 1;
		uiButtonsPanel.add(Pause, c);
		c.gridx = 2 ;
		uiButtonsPanel.add(Undo, c);
		uiButtonsPanel.setOpaque(false);
		uiButtonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
	
		uiPanel.add(movesLabel, BorderLayout.WEST);
		uiPanel.add(timerLabel);
		uiPanel.add(uiButtonsPanel, BorderLayout.EAST);*/
		
		for (int i = 0; i < Tile.values().length; i++) {
			JButton tileButton = new JButton();
			tileButton.setIcon(new ImageIcon(tileImgs[i]));
			tileButton.setPreferredSize(new Dimension(tileImgs[i].getWidth(null), tileImgs[i].getHeight(null)));
			tileButton.setBorderPainted(false);
			tileButton.addActionListener(new tileBtnListener(Tile.getTile(i)));
			uiPanel.add(tileButton);
		}
		
		add(uiPanel);
	}

}
