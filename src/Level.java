import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister.Pack;

public class Level extends JPanel implements ActionListener {
	
	private Tile levelMap[][];
	private ArrayList<Box> boxList;
	private Stack<ArrayList<Entity>> prevStates;
	
	private int width; // Width of the level
	private int height; // Height of the level
	private int tileSize;
	
	private Image tileImgs[];
	private Player player;
	private int moves = 0;
	private Timer animationTimer;
	private JPanel uiPanel;
	private JLabel movesLabel;
	private JLabel timerLabel;
	private long time;
	
	private boolean isPaused;
	
	//for key input
    private static final String MOVE_UP = "move up";
    private static final String MOVE_DOWN = "move down";
    private static final String MOVE_LEFT = "move left";
    private static final String MOVE_RIGHT = "move right";
    
    private static final String MENU = "return menu";
    private static final String UNDO = "undo";
	
	/**
	 * Creates a new level.
	 * @precondition screenWidth % tileSize == 0 && screenHeight % tileSize == 0
	 * @param screenWidth Screen width in pixels.
	 * @param screenHeight Screen height in pixels.
	 * @param tileSize Width/Height of a tile in pixels.
	 */
	Level(int screenWidth, int screenHeight, int tileSize, LevelGen levelGen) {
		// 1 pixel padding so I don't need to add edge cases to generation.
		this.width = screenWidth/tileSize + 2;
		this.height = screenHeight/tileSize + 2;
		this.tileSize = tileSize;
		prevStates = new Stack<ArrayList<Entity>>();
		boxList = new ArrayList<Box>();

		animationTimer = new Timer(GameMaster.FRAME_DELTA, this);
		levelMap = levelGen.generate(height, width, 1);
		time = 0;
		isPaused = false;
		
		setDefaultTiles();
		makePlayer();
		placeBox();
		setActions();
		setupUI();
		animationTimer.start();
	}
	
	Level(String input, int tileSize) {
		this.tileSize = tileSize;
		boxList = new ArrayList<Box>();
		prevStates = new Stack<ArrayList<Entity>>();
		
		// Get the width and height
		byte inputArray[] = input.getBytes();
		for (this.width = 0; inputArray[this.width] != '\n'; this.width++);
		this.height = inputArray.length/(width + 1);
		
		levelMap = new Tile[this.height][this.width];
		int sIndex = 0;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (inputArray[sIndex++] == Tile.BOX.getIntRep())
					boxList.add(new Box(j, i, tileImgs[2], tileSize, width, height));
				else
					levelMap[i][j] = Tile.getTile(inputArray[sIndex] - '0');
			}
			sIndex++;
		}
		
		setDefaultTiles();
		makePlayer();
		setActions();
		setupUI();
		animationTimer.start();
	}
	
	private void setActions() {
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), MOVE_UP);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), MOVE_DOWN);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), MOVE_LEFT);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), MOVE_RIGHT);
		
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), MENU);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("U"), UNDO);
		
		getActionMap().put(MENU, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (isPaused) {
					isPaused = false;
					animationTimer.start();
				} else {
					isPaused = true;
					animationTimer.stop();
				}
			}
		});
		
		getActionMap().put(UNDO, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!player.isAnimating() && !isPaused)
					undo();
			}
		});
	}
	
	private void setDefaultTiles() {
		this.tileImgs = new Image[4];
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
	
	public void setTile(Tile t, Image tileImage) {
		this.tileImgs[t.getIntRep()] = tileImage;
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D bbg = (Graphics2D) g;
		int left = (int) ((double) GameMaster.WIDTH/2 - (double) (width * tileSize)/2);
		int top = (int) ((double) GameMaster.HEIGHT/2 - (double) (height * tileSize)/2);
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) { 
				if (tileImgs[levelMap[i][j].getIntRep()] != null) {
					bbg.drawImage(tileImgs[levelMap[i][j].getIntRep()], left + j * tileSize, top + i * tileSize, null);
				} else {
					bbg.setColor(new Color(levelMap[i][j].getIntRep() * 127));
					bbg.fillRect(left + j * tileSize, top + i * tileSize, tileSize, tileSize);
				}
			}
		}
		
		for (Box box : boxList) {
			box.draw(bbg);
		}
		
		player.paintComponent(bbg);
		super.paintComponent(bbg);
		//movesLabel.paint(bbg);
	}
	
	public int getTileSize() {
		return tileSize;
	}
	
	private void placeBox() {
		Random r = new Random();
		
		while (true) {
			int x = r.nextInt(width);
			int y = r.nextInt(height);
			
			if (levelMap[y][x] == Tile.WALL || levelMap[y][x] == Tile.GOAL ||
					checkBoxList(x, y) == true) {
				continue;
			}
			if (	(levelMap[y + 1][x] == Tile.WALL && levelMap[y][x + 1] == Tile.WALL) ||
					(levelMap[y][x + 1] == Tile.WALL && levelMap[y - 1][x] == Tile.WALL) ||
					(levelMap[y - 1][x] == Tile.WALL && levelMap[y][x - 1] == Tile.WALL) ||
					(levelMap[y][x - 1] == Tile.WALL && levelMap[y + 1][x] == Tile.WALL))
				continue;
			
			boxList.add(new Box(x, y, tileImgs[2], tileSize, width, height));
			break;
		}
	}
	
	private void makePlayer() {
		int x = width/2;
		int y = height/2;
		
		if (levelMap[y][x] == Tile.WALL || checkBoxList(x, y) == true) {
			Random r = new Random();
				
			while (true) {
				x = r.nextInt(width);
				y = r.nextInt(height);
				
				if (levelMap[y][x] == Tile.WALL || checkBoxList(x, y) == true)
					continue;

				break;
			}
		}
		
		try {
			player = new Player(x, y, ImageIO.read(getClass().getResourceAsStream("player.png")), ImageIO.read(getClass().getResourceAsStream("player_up.png")), ImageIO.read(getClass().getResourceAsStream("player_right.png")), tileSize, width, height);
			
			this.getActionMap().put(MOVE_UP, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					if (!player.isAnimating() && !isPaused) {
						player.setMove(1);
						update();
					}
				}
			});

			this.getActionMap().put(MOVE_DOWN, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					if (!player.isAnimating() && !isPaused) {
						System.out.print("none " + Thread.currentThread().getId());
						player.setMove(2);
						update();
					}
				}
			});

			this.getActionMap().put(MOVE_LEFT, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					if (!player.isAnimating() && !isPaused) {
						System.out.print("none " + Thread.currentThread().getId());
						player.setMove(3);
						update();
					}
				}
			});

			this.getActionMap().put(MOVE_RIGHT, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					if (!player.isAnimating() && !isPaused) {
						player.setMove(4);
						update();
					}
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// someone should probably change this, since it already has something similar
	// in class Player... :v
	private boolean checkBoxList(int x, int y) {
		for (int i = 0; i < boxList.size(); i++) {
			Box box = boxList.get(i);
			if (box.getTileX() == x && box.getTileY() == y) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isCompleted() {
		for (Box b : boxList)
			if (levelMap[b.getTileY()][b.getTileX()] != Tile.GOAL)
				return false;
		
		return true;
	}
	
	private void undo() {
		if (!prevStates.isEmpty()) {
			ArrayList<Entity> prevState = prevStates.pop();
			boxList = new ArrayList<Box>();
			for (Entity e : prevState) {
				if (e.getClass() == Player.class) {
					player = (Player) e;
					System.out.println("o " + player.getTileX() + " " + player.getTileY());
				} else {
					boxList.add((Box)e);
				}
			}
		}
		System.out.print(getHeight());
		
		repaint();
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder(height * (width + 1));
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (player.getTileX() == i && player.getTileY() == j) {
					s.append(Tile.PLAYER.getIntRep());
					continue;
				}
				
				boolean box_f = false;
				for (Box b : boxList) {
					if (b.getTileY() == i && b.getTileX() == j) {
						box_f = true;
						break;
					}
				}
				
				if (box_f)
					s.append(Tile.BOX.getIntRep());
				else
					s.append(levelMap[i][j].getIntRep());
			}
				
			s.append('\n');
		}
		
		return s.toString();
	}

	public void update() {
		//System.out.println("player.update");
		player.update(levelMap, boxList);
		
//		if (player.isAnimating())
//			animationTimer.start();
		
		if (player.atNewTile()) {
			moves++;
			movesLabel.setText("Moves: " + moves);
			
			// save state
			ArrayList<Entity> newState = new ArrayList<Entity>();
			try {
				Player newPlayer = (Player) player.clone();
				System.out.println("n " + newPlayer.getTileX() + " " + newPlayer.getTileY());
				newState.add(newPlayer);

				for (Box b : boxList) {
					newState.add((Entity) b.clone());
				}
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			prevStates.push(newState);
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean stillAnimating = false;
		
		if (player.isAnimating()) {
			stillAnimating = true;
			player.updateAnimation();
		}
		
		for (Box b : boxList) {
			if (b.isAnimating()) {
				stillAnimating = true;
				b.updateAnimation();
			}
		}
		
		// Update time label
		time += GameMaster.FRAME_DELTA;
		Date date = new Date(time);
		DateFormat dateFormat = new SimpleDateFormat("mm:ss:SSS");
		timerLabel.setText("Time: " + dateFormat.format(date));
		
		if (stillAnimating)
			repaint();
		else if (isCompleted()) {
			animationTimer.stop();
			GameMaster.changeScreens(new IntermissionScreen(dateFormat.format(date), moves));
		}
	}

	private void setupUI() {
		setOpaque(false);
		setLayout(null);

		movesLabel = new JLabel("Moves: " + moves);
		Font font = new Font("Arial", Font.PLAIN, 35);
		movesLabel.setFont(font);
		movesLabel.setForeground(Color.WHITE);
		movesLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		
		timerLabel = new JLabel("Time: 0:00:000");
		timerLabel.setFont(font);
		timerLabel.setForeground(Color.WHITE);
		timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		
		uiPanel = new JPanel(new BorderLayout());
		uiPanel.setPreferredSize(new Dimension(GameMaster.WIDTH, 80));
		uiPanel.setBounds(new Rectangle(new Point(0, (int) (GameMaster.HEIGHT - uiPanel.getPreferredSize().getHeight())), uiPanel.getPreferredSize()));
		uiPanel.setBackground(new Color(58, 58, 58));
		
		uiPanel.add(movesLabel, BorderLayout.WEST);
		uiPanel.add(timerLabel);
		
		add(uiPanel);
	}
	
}
