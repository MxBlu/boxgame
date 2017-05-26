import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class Level extends JPanel implements ActionListener {
	
	/**
	 * Difficulty constants
	 */
	public static final int EASY = 0;
	public static final int MEDIUM = 1;
	public static final int HARD = 2;
	
	/**
	 * Boxes for every difficulty
	 */
	private static final Integer diffLevels[][] = { {1}, {2, 3}, {4, 5}};
	
	/**
	 * Level tile-map and entities
	 */
	private Tile levelMap[][];
	private ArrayList<Box> boxList;
	private Player player;
	private Stack<ArrayList<Entity>> prevStates;
	
	/**
	 * Generation and rendering values
	 */
	private int width; // Width of the level
	private int height; // Height of the level
	private int tileSize;
	private int difficulty;

	/**
	 * Pause panel elements
	 */
	private JPanel pausePanel;
	private boolean isPaused;
	
	/**
	 * UI panel elements
	 */
	private JPanel uiPanel;
	private JLabel movesLabel;
	private JLabel timerLabel;
	private JPanel uiButtonsPanel;
	private HoverButton Pause;
	private HoverButton Undo;
	private Image pauseButton;
	private Image undoButton;
	private Image pauseHover;
	private Image undoHover;
	
	/**
	 * Score variables
	 */
	private long time;
	private int moves;

	/**
	 * Tile images and frame timer for updating and animation
	 */
	private Image tileImgs[];
	private Timer frameTimer;
	
	/**
	 * Premade level members
	 */
	private File levelFile = null;
	private String levelMapString = "";
	private int highScore = 0;
	private long bestTime = 0;
	private boolean newHighScore = false;
	
	/**
	 * Key binding constants
	 */
    private static final String MOVE_UP = "move up";
    private static final String MOVE_DOWN = "move down";
    private static final String MOVE_LEFT = "move left";
    private static final String MOVE_RIGHT = "move right";
    private static final String MENU = "return menu";
    private static final String UNDO = "undo";
    
    /**
     * Host JFrame
     */
    private JFrame frame;
	
	/**
	 * Creates a new level.
	 * @param frame Host JFrame
	 * @param screenWidth Screen width in pixels.
	 * @param screenHeight Screen height in pixels.
	 * @param tileSize Width/Height of a tile in pixels.
	 * @param difficulty Difficulty of the level to be generated.
	 */
    Level(JFrame frame, int screenWidth, int screenHeight, int tileSize, int difficulty) {
		this.frame = frame;

		// 1 pixel padding so I don't need to add edge cases to generation.
		this.width = screenWidth/tileSize + 2;
		this.height = screenHeight/tileSize + 2;
		this.tileSize = tileSize;
		this.difficulty = difficulty;
		prevStates = new Stack<ArrayList<Entity>>();

		frameTimer = new Timer(GameMaster.FRAME_DELTA, this);

		// gets a random number of goals for the assigned difficulty
		Random r = new Random();
		int numGoals = diffLevels[difficulty][r.nextInt(diffLevels[difficulty].length)];
		
		time = 0;
		moves = 0;
		
		pausePanel = new PausePanel(frame);
		isPaused = false;
		
		setDefaultTiles();
		
		LevelGen gen = new LevelGen();
		FurthestStateGen f = null;
		// goes while f is null or playerSpaces is null
		while(f == null || f.getPlayerSpaces() == null) {
			// generates the levelMap
			levelMap = gen.generate(height, width, numGoals);
			// makes the boxList according to the goal locations
			makeBoxList(numGoals);
			// makes the furthestStateGen
			f = new FurthestStateGen(width, height, numGoals, levelMap, this.boxList);
		}
		
		// gets the new boxList from f
		boxList = f.getBoxList();
		// makes the player based off playerSpaces
		makePlayer(f.getPlayerSpaces());
		
		setActions();
		setupUI();
		frameTimer.start();
		pushCurrentState();
	}

    /**
     * Creates a level from a file.
     * @param frame Host JFrame
     * @param levelFile File containing level data
     * @param tileSize Width/Height of a tile in pixels
     */
	Level(JFrame frame, File levelFile, int tileSize) {
		this.frame = frame;
		this.tileSize = tileSize;
		this.levelFile = levelFile;
		boxList = new ArrayList<Box>();
		prevStates = new Stack<ArrayList<Entity>>();
		
		frameTimer = new Timer(GameMaster.FRAME_DELTA, this);
		time = 0;
		isPaused = false;
		pausePanel = new PausePanel(frame);
		setDefaultTiles();
		
		// premadeFlag is set to true if it is passed
		// in from SelectScreen. This means a level is
		// premade. We set an internal boolean isPremade
		// to true, to alter the intermission screen
		// appropriately upon level completion. 
		loadLevelFile();
		
		// Get the width and height
		byte inputArray[] = levelMapString.getBytes();
		for (this.width = 0; inputArray[this.width] != '\n'; this.width++);
		this.height = inputArray.length/(width + 1);
		
		levelMap = new Tile[this.height][this.width];
		int sIndex = 0;
		
		// goes through initializing the levelMap using inputArray
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				// checks if it is representative of a box
				if (inputArray[sIndex] - '0' == Tile.BOX.getIntRep()) {
					// adds box to boxList and sets the coordinates as walkable on levelMap
					boxList.add(new Box(j, i, tileImgs[2], tileSize, width, height));
					levelMap[i][j] = Tile.WALKABLE;
					// checks if it is representative of a plyer
				} else if (inputArray[sIndex] - '0' == Tile.PLAYER.getIntRep()) {
					try {
						// creates a player and sets the coordinates as walkable on levelMap
						player = new Player(j, i, ImageIO.read(getClass().getResourceAsStream("player.png")), ImageIO.read(getClass().getResourceAsStream("player_up.png")), ImageIO.read(getClass().getResourceAsStream("player_right.png")), tileSize, width, height);
					} catch (IOException e) {
						e.printStackTrace();
					}
					levelMap[i][j] = Tile.WALKABLE;
				} else if (inputArray[sIndex] != '\n') {
					// sets whatever inputArray at sIndex is representative of in levelMap
					levelMap[i][j] = Tile.getTile(inputArray[sIndex] - '0');	
				}
				
				sIndex++;
			}
			sIndex++;
		}
		
		// prepares the level for input
		setActions();
		setupUI();
		frameTimer.start();
		pushCurrentState();
	}
	
	/**
	 * Loads and parses a level from a file.
	 */
	private void loadLevelFile() {
		try {
			// scans for the level string representation
			Scanner sc = new Scanner(new FileReader(levelFile));
			while (sc.hasNextLine()) {
				String lineString = sc.nextLine();
				levelMapString += lineString + "\n";
				Scanner lsScanner = new Scanner(lineString);

				if (!lsScanner.hasNext()) {
					lsScanner.close();
					break;
				}
				lsScanner.close();
			}

			String imageLocation = sc.nextLine();
			String highScoreString = sc.nextLine();
			// prints the user's highest score for the level
			if (!highScoreString.equals("None")) {
				String[] stringArray = highScoreString.split(" ");
				highScore = Integer.parseInt(stringArray[0]);
				if (stringArray.length >1){
					bestTime = Long.parseLong(stringArray[1]);
					
				}
				sc.close();
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File not found");
			e.printStackTrace();
		}	
	}
	
	/**
	 * Sets up actions and binds keys to their appropriate actions.
	 */
	private void setActions() {
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), MOVE_UP);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), MOVE_DOWN);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), MOVE_LEFT);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), MOVE_RIGHT);
		
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), MENU);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Z"), UNDO);
		
		// sets movement for the up key
		getActionMap().put(MOVE_UP, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!player.isAnimating() && !isPaused) {
					player.setMove(1);
					update();
				}
			}
		});

		// sets movement for the down key
		getActionMap().put(MOVE_DOWN, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!player.isAnimating() && !isPaused) {
					player.setMove(2);
					update();
				}
			}
		});

		// sets movement for the left key
		getActionMap().put(MOVE_LEFT, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!player.isAnimating() && !isPaused) {
					player.setMove(3);
					update();
				}
			}
		});

		// sets movement for the right key
		getActionMap().put(MOVE_RIGHT, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!player.isAnimating() && !isPaused) {
					player.setMove(4);
					update();
				}
			}
		});
		
		// sets event for the menu button
		getActionMap().put(MENU, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				togglePaused();
			}
		});
		
		// sets event for the undo button
		getActionMap().put(UNDO, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!player.isAnimating() && !isPaused)
					undo();
			}
		});
	}
	
	/**
	 * Sets the default tile images for rendering.
	 */
	private void setDefaultTiles() {
		this.tileImgs = new Image[8];
		try {
			tileImgs[0] = ImageIO.read(getClass().getResourceAsStream("ground_solid.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			tileImgs[1] = ImageIO.read(getClass().getResourceAsStream("ground_empty.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			tileImgs[2] = ImageIO.read(getClass().getResourceAsStream("box.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			tileImgs[3] = ImageIO.read(getClass().getResourceAsStream("goal.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
			tileImgs[7] = ImageIO.read(getClass().getResourceAsStream("border.png")).getScaledInstance(tileSize,
					tileSize, Image.SCALE_DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets up the UI panel elements.
	 */
	private void setupUI() {
		setOpaque(false);
		setLayout(null);

		Font gameFont = null;
		try {
			// gets the game font to be used
			gameFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("VCR_OSD_MONO.ttf")).deriveFont(Font.PLAIN, 35);
		} catch (FontFormatException | IOException e1) {e1.printStackTrace();}
		
		// sets the moves label
		movesLabel = new JLabel("Moves: " + moves);
		movesLabel.setFont(gameFont);
		movesLabel.setForeground(Color.WHITE);
		movesLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		
		// sets the time label
		timerLabel = new JLabel("Time: 0:00:000");
		timerLabel.setFont(gameFont);
		timerLabel.setForeground(Color.WHITE);
		timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		
		// sets the boundaries and background of the level
		uiPanel = new JPanel(new BorderLayout());
		uiPanel.setPreferredSize(new Dimension(GameMaster.WIDTH, 80));
		uiPanel.setBounds(new Rectangle(new Point(0, (int) (GameMaster.HEIGHT - uiPanel.getPreferredSize().getHeight())), uiPanel.getPreferredSize()));
		uiPanel.setBackground(new Color(58, 58, 58));
		
		// Load in button assets
		try {
			pauseButton = ImageIO.read(getClass().getResourceAsStream("pause.png"));
			undoButton = ImageIO.read(getClass().getResourceAsStream("undo.png"));
			pauseHover = ImageIO.read(getClass().getResourceAsStream("pause2.png"));
			undoHover = ImageIO.read(getClass().getResourceAsStream("undo2.png"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}	
		
		// sets the pause button
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
		
		// sets the listener for pause
		Pause.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				togglePaused();			
				}
		});
		
		// sets the undo button
		Undo = new HoverButton();
		Undo.setIcon(new ImageIcon(undoButton));
		Undo.setContentAreaFilled(false);
		Undo.setBorderPainted(false);
		
		// sets the listener for the user's cursor
		Undo.addMouseListener(new java.awt.event.MouseAdapter() {
		  
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		    	Undo.setIcon(new ImageIcon(undoHover));
		    }
		    public void mouseExited(java.awt.event.MouseEvent evt) {
		    	Undo.setIcon(new ImageIcon(undoButton));
		    }
		});
		
		// sets the listener for undo
		Undo.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				if (!player.isAnimating() && !isPaused)
					undo();
			}
		});
		
		// lays out the buttons on the panel
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
	
	/**
	 * Switches the paused state of the level.
	 */
	public void togglePaused() {
		if (isPaused) {
			isPaused = false;
			frameTimer.start();
			remove(pausePanel);
		} else {
			isPaused = true;
			frameTimer.stop();
			add(pausePanel);
		}
		
		revalidate();
		repaint();
	}
	
	/**
	 * Makes the player based off playerSpaces.
	 */
	private void makePlayer(List<List<Integer>> playerSpaces) {
		try {
			Random r = new Random();
			// gets an available playerSpace from playerSpaces to set the player's coordinates
			// in a walkable area
			List<Integer> playerSpace = playerSpaces.get(r.nextInt(playerSpaces.size()));
			player = new Player(playerSpace.get(0), playerSpace.get(1), ImageIO.read(getClass().getResourceAsStream("player.png")), ImageIO.read(getClass().getResourceAsStream("player_up.png")), ImageIO.read(getClass().getResourceAsStream("player_right.png")), tileSize, width, height);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Makes the boxList by placing the boxes on the goals.
	 */
	private void makeBoxList(int numGoals) {		
		this.boxList = new ArrayList<Box>();
		
		// goes through the levelMap
		for (int x = 0; x < width; x++) {
			for (int y = 0 ; y < height; y++) {
				// checks if the coordinates is a goal
				if (levelMap[y][x] == Tile.GOAL) {
					// adds box to the list
					this.boxList.add(new Box(x, y, tileImgs[2], tileSize, width, height));
				}
				if (this.boxList.size() == numGoals) break;
			}
			if (this.boxList.size() == numGoals) break;
		}
	}
	
	/**
	 * Returns whether a level has been completed.
	 * @return Level completion
	 */
	private boolean isCompleted() {
		for (Box b : boxList)
			if (levelMap[b.getTileY()][b.getTileX()] != Tile.GOAL)
				return false;
		
		return true;
	} 
	
	/**
	 * Undo the players previous action.
	 */
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
	
	/**
	 * Pushes the current state of the level
	 */
	private void pushCurrentState() {
		// saves state
		ArrayList<Entity> newState = new ArrayList<Entity>();
		try {
			// gets the state of the player
			Player newPlayer = (Player) player.clone();
			newState.add(newPlayer);
			
			// gets the state of the boxes on the level
			for (Box b : boxList) {
				newState.add((Entity) b.clone());
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		// adds the current state to prevStates
		prevStates.push(newState);
	}
	
	/**
	 * Saves the players high score for that level.
	 */
	private void saveHighScore() {
		// prints out the relevant message to high score
		if ((moves > highScore && highScore != 0)&&(time >= bestTime && bestTime != 0)) {
			return;
		}
		newHighScore = true;
		if ((moves > highScore && highScore != 0)||(time >= bestTime && bestTime != 0)) {
			
			newHighScore = false;

		}
		
		try {
			String lines = "";
		    String line = null;
	       
		    Scanner br = new Scanner(levelFile);
	        
	        int linesSinceNothingLine = -1;

	        // continues for the extent of levelFile
	        while (br.hasNextLine()) {
	        	line = br.nextLine();
	        	Scanner lsScanner = new Scanner(line);
	        	
	        	if (linesSinceNothingLine >= 0) {
	        		linesSinceNothingLine++;
	        		if (linesSinceNothingLine == 2) line = Integer.toString(moves)+ " " + Long.toString(time);
	        	}
	        	
				if (!lsScanner.hasNext()) {
					linesSinceNothingLine = 0;
				}
	            lines+=line+'\n';
	            lsScanner.close();
	        }
	        
	        br.close();
	        
	        // outputs the levelFile 
	        PrintWriter out = new PrintWriter(levelFile);
			out.write(lines);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	/* Converts the level to a string format */
	public String toString() {
		StringBuilder s = new StringBuilder(height * (width + 1));
		
		// goes through the level to get the location of the objects on the level
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				// checks if coordinate is a player
				if (player.getTileX() == i && player.getTileY() == j) {
					// appends player representation to string
					s.append(Tile.PLAYER.getIntRep());
					continue;
				}
				
				boolean box_f = false;
				// goes through the boxList
				for (Box b : boxList) {
					// checks if coordinate is a box
					if (b.getTileY() == i && b.getTileX() == j) {
						box_f = true;
						break;
					}
				}
				
				// checks if it is a confirmed box
				if (box_f) {
					// appends the box representation to string
					s.append(Tile.BOX.getIntRep());
				} else {
					// appends whatever the coordinate is (walkable)
					s.append(levelMap[i][j].getIntRep());
				}
			}
				
			s.append('\n');
		}
		
		return s.toString();
	}
	
	/**
	 * Renders the level to the screen.
	 */
	public void paintComponent(Graphics g) {
		Graphics2D bbg = (Graphics2D) g;
		int left = (int) ((double) GameMaster.WIDTH/2 - (double) (width * tileSize)/2);
		int top = (int) ((double) GameMaster.HEIGHT/2 - (double) (height * tileSize)/2);
		
		// goes through the levelMap
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				// checks if current coordinate is a wall bordering around the playable area
				// draws depending on whether it is a border or not
				if ((levelMap[i][j] == Tile.WALL) && 
						((i < (height - 1) && (levelMap[i + 1][j] == Tile.WALKABLE || levelMap[i + 1][j] == Tile.GOAL)) ||
						((i < (height - 1) && j > 0) && (levelMap[i + 1][j - 1] == Tile.WALKABLE || levelMap[i + 1][j - 1] == Tile.GOAL)) ||
						((j > 0) && (levelMap[i][j - 1] == Tile.WALKABLE || levelMap[i][j - 1] == Tile.GOAL)) ||
						((i > 0 && j > 0) && (levelMap[i - 1][j - 1] == Tile.WALKABLE || levelMap[i - 1][j - 1] == Tile.GOAL)) ||
						((i > 0) && (levelMap[i - 1][j] == Tile.WALKABLE || levelMap[i - 1][j] == Tile.GOAL)) ||
						((i > 0 && j < (width - 1)) && (levelMap[i - 1][j + 1] == Tile.WALKABLE || levelMap[i - 1][j + 1] == Tile.GOAL)) ||
						((j < (width - 1)) && (levelMap[i][j + 1] == Tile.WALKABLE || levelMap[i][j + 1] == Tile.GOAL)) ||
						((i < (height - 1) && j < (width - 1)) && (levelMap[i + 1][j + 1] == Tile.WALKABLE || levelMap[i + 1][j + 1] == Tile.GOAL))))
					bbg.drawImage(tileImgs[Tile.BORDER.getIntRep()], left + j * tileSize, top + i * tileSize, null);
				else
					bbg.drawImage(tileImgs[levelMap[i][j].getIntRep()], left + j * tileSize, top + i * tileSize, null);
			}
		}
		
		// goes through the boxList
		for (Box box : boxList) {
			if (!box.isAnimating()) {
				if (levelMap[box.getTileY()][box.getTileX()] == Tile.GOAL)
					box.setOnGoal(true);
				else
					box.setOnGoal(false);
			}
			
			// draws the box
			box.draw(bbg);
		}
		
		// paints the player
		player.paintComponent(bbg);
	}

	/* Updates the player */
	public void update() {
		player.update(levelMap, boxList);
		
		// checks if the player is at a new tile
		if (player.atNewTile()) {
			// adds to the moves
			moves++;
			movesLabel.setText("Moves: " + moves);
			// saves the current state to prevStates
			pushCurrentState();
		}
		
	}

	@Override
	/* Handles actions for a timer trigger */
	public void actionPerformed(ActionEvent e) {
		boolean stillAnimating = false;
		
		// checks if the player is still animating
		if (player.isAnimating()) {
			stillAnimating = true;
			// updates the player's animation
			player.updateAnimation();
		}
		
		// goes through the boxList
		for (Box b : boxList) {
			// checks if the box is still animating
			if (b.isAnimating()) {
				stillAnimating = true;
				// updates the box's animation
				b.updateAnimation();
			}
		}
		
		// Update time label
		time += GameMaster.FRAME_DELTA;
		Date date = new Date(time);
		DateFormat dateFormat = new SimpleDateFormat("mm:ss:SSS");
		timerLabel.setText("Time: " + dateFormat.format(date));
		
		// checks if it is still animating something else
		if (stillAnimating)
			repaint();
		else if (isCompleted()) {
			// stops the animation's timer
			frameTimer.stop();
			if (levelFile != null) {
				// saves if high score
				saveHighScore();
				// goes to the intermission screen with player data of the played level
				GameMaster.changeScreens(frame, new IntermissionScreen(frame, dateFormat.format(date), moves, difficulty, true, newHighScore, dateFormat.format(bestTime), highScore));
			} else {
				GameMaster.changeScreens(frame, new IntermissionScreen(frame, dateFormat.format(date), moves, difficulty, false,newHighScore, dateFormat.format(bestTime), highScore));
			}
		}
	}
}
