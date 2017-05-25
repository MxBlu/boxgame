import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class Level extends JPanel implements ActionListener {
	
	public static final int EASY = 0;
	public static final int MEDIUM = 1;
	public static final int HARD = 2;
	
	private static final Integer diffLevels[][] = { {1}, {2, 3}, {4, 5}};
	
	private Tile levelMap[][];
	private ArrayList<Box> boxList;
	private Stack<ArrayList<Entity>> prevStates;
	
	private int width; // Width of the level
	private int height; // Height of the level
	private int tileSize;
	private int difficulty;
	
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
	
	//Premade level members
	private File levelFile = null;
	private String levelMapString = "";
	private int highScore = 0;
	
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
	Level(int screenWidth, int screenHeight, int tileSize, int difficulty) {
		// 1 pixel padding so I don't need to add edge cases to generation.
		
		GameMaster.toggleCursorPointer();

		this.width = screenWidth/tileSize + 2;
		this.height = screenHeight/tileSize + 2;
		this.tileSize = tileSize;
		this.difficulty = difficulty;
		prevStates = new Stack<ArrayList<Entity>>();

		animationTimer = new Timer(GameMaster.FRAME_DELTA, this);

		Random r = new Random();
		// gets a random number of goals for the assigned difficulty
		int numGoals = diffLevels[difficulty][r.nextInt(diffLevels[difficulty].length)];
		
		time = 0;
		isPaused = false;
		pausePanel = new PauseScreen();
		
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
			System.out.println("RESTART");
		}
		
		// gets the new boxList from f
		boxList = f.getBoxList();
		// makes the player based off playerSpaces
		makePlayer(f.getPlayerSpaces());
		
		setActions();
		setupUI();
		animationTimer.start();
		pushCurrentState();
	}

	Level(File levelFile, int tileSize) {
		GameMaster.toggleCursorPointer();

		this.tileSize = tileSize;
		this.levelFile = levelFile;
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
		loadLevelFile();
		
		// Get the width and height
		byte inputArray[] = levelMapString.getBytes();
		for (this.width = 0; inputArray[this.width] != '\n'; this.width++);
		this.height = inputArray.length/(width + 1);
		
		levelMap = new Tile[this.height][this.width];
		int sIndex = 0;
		int playerX = 0;
		int playerY = 0;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (inputArray[sIndex] - '0' == Tile.BOX.getIntRep()) {
					boxList.add(new Box(j, i, tileImgs[2], tileSize, width, height));
					levelMap[i][j] = Tile.WALKABLE;
				} else if (inputArray[sIndex] - '0' == Tile.PLAYER.getIntRep()) {
					try {
						player = new Player(j, i, ImageIO.read(getClass().getResourceAsStream("player.png")), ImageIO.read(getClass().getResourceAsStream("player_up.png")), ImageIO.read(getClass().getResourceAsStream("player_right.png")), tileSize, width, height);
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
		

		setActions();
		setupUI();
		animationTimer.start();
		pushCurrentState();
	}
	
	private void loadLevelFile() {
		try {
			Scanner sc = new Scanner(new FileReader(levelFile));
			while (sc.hasNextLine()) {
				String lineString = sc.nextLine();
				levelMapString += lineString + "\n";
				Scanner lsScanner = new Scanner(lineString);

				if (!lsScanner.hasNext()) {
					break;
				}
			}

			String imageLocation = sc.nextLine();
			String highScoreString = sc.nextLine();
			if (!highScoreString.equals("None")) {
				highScore = Integer.parseInt(highScoreString);
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File not found");
			e.printStackTrace();
		}		
	}
	
	private void setActions() {
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), MOVE_UP);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), MOVE_DOWN);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), MOVE_LEFT);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), MOVE_RIGHT);
		
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), MENU);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Z"), UNDO);
		
		getActionMap().put(MOVE_UP, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!player.isAnimating() && !isPaused) {
					player.setMove(1);
					update();
				}
			}
		});

		getActionMap().put(MOVE_DOWN, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!player.isAnimating() && !isPaused) {
					player.setMove(2);
					update();
				}
			}
		});

		getActionMap().put(MOVE_LEFT, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!player.isAnimating() && !isPaused) {
					player.setMove(3);
					update();
				}
			}
		});

		getActionMap().put(MOVE_RIGHT, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!player.isAnimating() && !isPaused) {
					player.setMove(4);
					update();
				}
			}
		});
		
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
		this.tileImgs = new Image[8];
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
			tileImgs[7] = ImageIO.read(getClass().getResourceAsStream("border.png")).getScaledInstance(tileSize,
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
				if ((levelMap[i][j] == Tile.WALL/* && i > 0 && i < (height - 1) && j > 0  && j < (width - 1)*/) && 
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
	
	/* Makes the player based off playerSpaces */
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
	
	/* Makes the boxList by placing the boxes on the goals */
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
			if (levelFile != null) {
				saveHighScore();
				GameMaster.changeScreens(new IntermissionScreen(dateFormat.format(date), moves, difficulty, true));
			} else {
				GameMaster.changeScreens(new IntermissionScreen(dateFormat.format(date), moves, difficulty, false));
			}
		}
	}
	
	private void saveHighScore() {
		if (moves >= highScore && highScore != 0) {
			return;
		}
		
		try {
			String lines = "";
		    String line = null;
	        Scanner br = new Scanner(levelFile);
	        
	        int linesSinceNothingLine = -1;

	        while (br.hasNextLine()) {
	        	line = br.nextLine();
	        	Scanner lsScanner = new Scanner(line);
	        	
	        	if (linesSinceNothingLine >= 0) {
	        		linesSinceNothingLine++;
	        		if (linesSinceNothingLine == 2) line = Integer.toString(moves);
	        	}
	        	
				if (!lsScanner.hasNext()) {
					linesSinceNothingLine = 0;
				}
	            lines+=line+'\n';
	        }
	        
	        br.close();
	        
	        PrintWriter out = new PrintWriter(levelFile);
			out.write(lines);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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

		Font gameFont = null;
		try {
			gameFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("VCR_OSD_MONO.ttf")).deriveFont(Font.PLAIN, 35);
		} catch (FontFormatException | IOException e1) {e1.printStackTrace();}
		
		movesLabel = new JLabel("Moves: " + moves);
		movesLabel.setFont(gameFont);
		movesLabel.setForeground(Color.WHITE);
		movesLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		
		timerLabel = new JLabel("Time: 0:00:000");
		timerLabel.setFont(gameFont);
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
