import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class Level extends JPanel implements ActionListener {
	
	private Tile levelMap[][];
	private ArrayList<Box> boxList;
	private Stack<ArrayList<Entity>> prevStates;
	
	private int width; // Width of the level
	private int height; // Height of the level
	private int tileSize;
	private int difficulty;
	private FurthestStateGen furthestState;
	
	private Image tileImgs[];
	private Player player;
	private int moves = 0;
	private Timer animationTimer;
	private JPanel uiPanel;
	private JLabel movesLabel;
	private JLabel timerLabel;
	private HoverButton Pause;
	private HoverButton Undo;
	private Image pauseButton;
	private Image undoButton;
	private Image pauseHover;
	private Image undoHover;
	
	private JPanel pausePanel;
	private JPanel uiButtonsPanel;
	private long startTime;

	private long time;

	private boolean isPaused;
	private boolean isPremade;
	
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
	Level(int screenWidth, int screenHeight, int tileSize, int difficulty, LevelGen levelGen) {
		// 1 pixel padding so I don't need to add edge cases to generation.
		
		GameMaster.toggleCursorPointer();

		this.width = screenWidth/tileSize + 2;
		this.height = screenHeight/tileSize + 2;
		this.tileSize = tileSize;
		this.difficulty = difficulty;
		prevStates = new Stack<ArrayList<Entity>>();
		int numGoals = 0;

		animationTimer = new Timer(GameMaster.FRAME_DELTA, this);
		Random r = new Random();
		Integer diffLevels[][] = {{2, 3}, {4, 5}};
		
		switch(difficulty) {
			case 1:
				numGoals = 1;
				break;
			case 2:
				numGoals = diffLevels[0][r.nextInt(2)];
				break;
			case 3:
				numGoals = diffLevels[1][r.nextInt(2)];
				break;
			default:
				System.out.println("Difficulty error");
		}
		
		levelMap = levelGen.generate(height, width, numGoals);
		time = 0;
		isPaused = false;
		pausePanel = new PauseScreen();
		
		setDefaultTiles();
		
		makeBoxList(numGoals);
		this.furthestState = new FurthestStateGen(width, height, numGoals, levelMap, this.boxList);
		
		while(furthestState.getPlayerSpaces() == null) {
			System.out.println("RESTART");
			levelMap = levelGen.generate(height, width, numGoals);
			makeBoxList(numGoals);
			this.furthestState = new FurthestStateGen(width, height, numGoals, levelMap, this.boxList);
		}
		
		boxList = furthestState.getBoxList();
		makePlayer(furthestState.getPlayerSpaces());
		
		setActions();
		setupUI();
		animationTimer.start();
		pushCurrentState();
	}
	
	Level(String input, int tileSize, boolean premadeFlag) {
		GameMaster.toggleCursorPointer();

		this.tileSize = tileSize;
		boxList = new ArrayList<Box>();
		prevStates = new Stack<ArrayList<Entity>>();
		
		animationTimer = new Timer(GameMaster.FRAME_DELTA, this);
		time = 0;
		isPaused = false;
		pausePanel = new PauseScreen();
		setDefaultTiles();
		
		// premadeFlag is set to true if it is passed
		// in from SelectScreen. This means a level is
		// premade. We set an internal boolean isPremade
		// to true, to alter the intermission screen
		// appropriately upon level completion. 
		isPremade = false;
		if (premadeFlag == true) {
			isPremade = true;
		}
		
		// Get the width and height
		byte inputArray[] = input.getBytes();
		for (this.width = 0; inputArray[this.width] != '\n'; this.width++);
		this.height = inputArray.length/(width + 1);
		
		levelMap = new Tile[this.height][this.width];
		int sIndex = 0;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (inputArray[sIndex] - '0' == Tile.BOX.getIntRep()) {
					boxList.add(new Box(j, i, tileImgs[2], tileSize, width, height));
					levelMap[i][j] = Tile.WALKABLE;
				} else if (inputArray[sIndex] - '0' == Tile.PLAYER.getIntRep()) {
					try {
						player = new Player(i, j, ImageIO.read(getClass().getResourceAsStream("player.png")), ImageIO.read(getClass().getResourceAsStream("player_up.png")), ImageIO.read(getClass().getResourceAsStream("player_right.png")), tileSize, width, height);
					} catch (IOException e) {
						e.printStackTrace();
					}
					levelMap[i][j] = Tile.WALKABLE;
				} else if (inputArray[sIndex] != '\n') {
					levelMap[i][j] = Tile.getTile(inputArray[sIndex] - '0');	
				}
				sIndex++;
			}
			sIndex++;
		}
		
		makePlayer(furthestState.getPlayerSpaces());
		setActions();
		setupUI();
		animationTimer.start();
		pushCurrentState();
	}
	
	private void setActions() {
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), MOVE_UP);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), MOVE_DOWN);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), MOVE_LEFT);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), MOVE_RIGHT);
		
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), MENU);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Z"), UNDO);
		
		getActionMap().put(MENU, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				togglePaused();
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
	private void getImages(){
		try {
			
			pauseButton = ImageIO.read(getClass().getResourceAsStream("pause.png"));
			undoButton = ImageIO.read(getClass().getResourceAsStream("undo.png"));
			pauseHover = ImageIO.read(getClass().getResourceAsStream("pause2.png"));
			undoHover = ImageIO.read(getClass().getResourceAsStream("undo2.png"));
		}
		catch(Exception e) {
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
	
	public void togglePaused() {
		if (isPaused) {
			isPaused = false;
			animationTimer.start();
			remove(pausePanel);
		} else {
			isPaused = true;
			animationTimer.stop();
			add(pausePanel);
		}
		
		revalidate();
		repaint();
	}
	
	public int getTileSize() {
		return tileSize;
	}
	
	private void makePlayer(List<List<Integer>> playerSpaces) {
		try {
			Random r = new Random();
			List<Integer> playerSpace = playerSpaces.get(r.nextInt(playerSpaces.size()));
			player = new Player(playerSpace.get(0), playerSpace.get(1), ImageIO.read(getClass().getResourceAsStream("player.png")), ImageIO.read(getClass().getResourceAsStream("player_up.png")), ImageIO.read(getClass().getResourceAsStream("player_right.png")), tileSize, width, height);
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
						player.setMove(2);
						update();
					}
				}
			});

			this.getActionMap().put(MOVE_LEFT, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					if (!player.isAnimating() && !isPaused) {
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
	
	private void makeBoxList(int numGoals) {
		// places boxes on the goals		
		this.boxList = new ArrayList<Box>();
		
		for (int x = 0; x < width; x++) {
			for (int y = 0 ; y < height; y++) {
				if (levelMap[y][x] == Tile.GOAL)
					this.boxList.add(new Box(x, y, tileImgs[2], tileSize, width, height));
				if (this.boxList.size() == numGoals) break;
			}
			if (this.boxList.size() == numGoals) break;
		}
	} 
	
	private boolean isCompleted() {
		for (Box b : boxList)
			if (levelMap[b.getTileY()][b.getTileX()] != Tile.GOAL)
				return false;
		
		return true;
	} 
	
	private void undo() {
		//Since we can't store the state when the player starts moving
		//as we have no way of knowing when that is at the moment, we 
		//need to pop twice and push at the end
		if (prevStates.size() >= 2 || (!prevStates.isEmpty() && player.isAnimating())) {
			if (!player.isAnimating())
				prevStates.pop();
			ArrayList<Entity> prevState = prevStates.pop();
			boxList = new ArrayList<Box>();
			for (Entity e : prevState) {
				if (e.getClass() == Player.class) {
					player = (Player) e;
				} else {
					boxList.add((Box) e);
				}
			}
			pushCurrentState();
		}
		
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
			pushCurrentState();
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
			GameMaster.changeScreens(new IntermissionScreen(dateFormat.format(date), moves, difficulty, isPremade));
		}
	}
	
	private void pushCurrentState() {
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
		
		
		getImages();
		
		Pause = new HoverButton();
		Pause.setIcon(new ImageIcon(pauseButton));
		Pause.setContentAreaFilled(false);
		Pause.setBorderPainted(false);
		Pause.addMouseListener(new java.awt.event.MouseAdapter() {
			  
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		    	Pause.setIcon(new ImageIcon(pauseHover));
		    }
		    public void mouseExited(java.awt.event.MouseEvent evt) {
		    	Pause.setIcon(new ImageIcon(pauseButton));
		    }
		});
		
		Pause.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				togglePaused();			
				}
		});
		//Pause.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		Undo = new HoverButton();
		Undo.setIcon(new ImageIcon(undoButton));
		Undo.setContentAreaFilled(false);
		Undo.setBorderPainted(false);
		
		Undo.addMouseListener(new java.awt.event.MouseAdapter() {
		  
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		    	Undo.setIcon(new ImageIcon(undoHover));
		    }
		    public void mouseExited(java.awt.event.MouseEvent evt) {
		    	Undo.setIcon(new ImageIcon(undoButton));
		    }
		});
		
		Undo.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				if (!player.isAnimating() && !isPaused)
					undo();
			}
		});
		
		//Undo.setBorder(new EmptyBorder(10, 10, 10, 10));
		uiButtonsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5,5,5,5);
		c.gridy = 1;
		uiButtonsPanel.add(Pause, c);
		c.gridx = 2 ;
		uiButtonsPanel.add(Undo, c);
		uiButtonsPanel.setOpaque(false);
		uiButtonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
	
		uiPanel.add(movesLabel, BorderLayout.WEST);
		uiPanel.add(timerLabel);
		uiPanel.add(uiButtonsPanel, BorderLayout.EAST);
		
		
		
		add(uiPanel);
	}
	
	public void unPause(){
		isPaused = false;
	}
	
}
