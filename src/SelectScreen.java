
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import java.awt.Font;

/**
 * A screen to allow for the selection of a premade
 * level to be played
 */
public class SelectScreen extends JPanel {

	/**
	 * Small listener class containing metadata 
	 * to start up a level
	 */
	class lvlBtnListener implements ActionListener {
		private File levelFile;

		public lvlBtnListener(File levelFile) {
			this.levelFile = levelFile;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			GameMaster.changeScreens(frame, new Level(frame, levelFile, 40));
		}
	}

	/**
	 * Small listener class containing metadata 
	 * to change the pannels to a certain set
	 */
	class setBtnListener implements ActionListener {
		private char suffix;

		public setBtnListener(char suffix) {
			this.suffix = suffix;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setUpPanels(suffix);
		}
	}

	private Image background;
	private Font highScoresFont;
	private GridBagConstraints c;
	private JPanel levelsPanel;
	private JPanel setBtnPanel;
	private JPanel lvlSetPanel;
	private JPanel lvlSetLabelPanel;
	
	private JFrame frame;

	private static final String QUIT_MENU = "quit menu";
	
	private final int BUTTONS_PER_LINE = 3;
	private final int LEVEL_BUTTON_WIDTH = 100;
	private final int LEVEL_BUTTON_HEIGHT = 100;
	private final int SET_BUTTON_WIDTH = 110;
	private final int SET_BUTTON_HEIGHT = 40;
	private final int FILLER_SIZE = 20;

	/**
	 * SelectScreen constructor
	 * @param frame The frame the panel is being added to
	 */
	public SelectScreen(JFrame frame) {
		this.frame = frame;
		
		// Get the image file for the background and buttons
		try {
			background = ImageIO.read(getClass().getResourceAsStream("levelselectmenu.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		setLayout(new GridBagLayout());
		c = new GridBagConstraints();

		// Set the JPanels attributes and revalidate.
		revalidate();
		setFocusable(true);
		requestFocusInWindow();

		// Keyinput
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), QUIT_MENU);

		getActionMap().put(QUIT_MENU, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				back();
			}
		});

		//Set up the back button
		HoverButton back = new HoverButton();
		try {
			back.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("back.png"))));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		back.setToolTipText("Back button");
		back.setBorderPainted(false);
		back.setContentAreaFilled(false);
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				back();
			}
		});
		add(back, c);
		
		//Loads in the highscore font
		try {
			highScoresFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("VCR_OSD_MONO.ttf"))
					.deriveFont(Font.PLAIN, 15);
		} catch (FontFormatException | IOException e1) {
			e1.printStackTrace();
		}

		//Create the panels
		levelsPanel = new JPanel();
		setBtnPanel = new JPanel();
		lvlSetPanel = new JPanel();
		lvlSetLabelPanel = new JPanel();
		add(lvlSetLabelPanel, c);

		c.gridy = 0;
		add(levelsPanel, c);
		setUpPanels('1');

		revalidate();
		repaint();
	}

	/**
	 * Paints the panel
	 */
	public void paintComponent(Graphics g) {
		// Access the JPanel super class's
		// function for painting.
		super.paintComponent(g);

		// Draw the background for this JPanel
		g.setColor(Color.BLACK);
		g.drawImage(background, 0, 0, null);
	}

	/**
	 * Sets up all the panels on the screen
	 * @param levelSuffix The suffix of the set currently shown
	 */
	private void setUpPanels(char levelSuffix) {
		setUpSetBtnPanel(levelSuffix);
		setUpLevelsPanel(levelSuffix);
		setUpLvlSetPanel(levelSuffix);
		setUpLvlSetLabelPanel(levelSuffix);

		revalidate();
		repaint();
	}

	/**
	 * Returns back to the main menu
	 */
	private void back() {
		GameMaster.changeScreens(frame, new MenuScreen(frame));
	}

	/**
	 * Sets up the set select panel
	 * @param levelSuffix The suffix of the set currently shown
	 */
	private void setUpSetBtnPanel(char levelSuffix) {
		setBtnPanel.removeAll();
		GridBagConstraints con = new GridBagConstraints();
		setBtnPanel.setOpaque(false);
		setBtnPanel.setLayout(new GridBagLayout());

		Font buttonsFont = null;
		try {
			buttonsFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("VCR_OSD_MONO.ttf"))
					.deriveFont(Font.PLAIN, 24);
		} catch (FontFormatException | IOException e1) {
			e1.printStackTrace();
		}

		//Set up set 1 button
		HoverButton set1But = new HoverButton();
		set1But.setText("Set 1");
		set1But.setToolTipText("Set 1");
		set1But.setFont(buttonsFont);
		set1But.setForeground(new Color(255, 18, 81));
		set1But.addActionListener(new setBtnListener('1'));
		set1But.setBackground(new Color(0, 0, 0));
		set1But.setBorder(new LineBorder(Color.WHITE, 3, true));
		set1But.setPreferredSize(new Dimension(SET_BUTTON_WIDTH, SET_BUTTON_HEIGHT));
		setBtnPanel.add(set1But, con);

		//Added a filler panel to separate the buttons
		JPanel borderFiller = new JPanel();
		borderFiller.setOpaque(false);
		borderFiller.setPreferredSize(new Dimension(FILLER_SIZE, FILLER_SIZE));
		setBtnPanel.add(borderFiller, con);

		//Set up set 2 button
		HoverButton set2But = new HoverButton();
		set2But.setText("Set 2");
		set2But.setToolTipText("Set 2");
		set2But.setFont(buttonsFont);
		set2But.setForeground(new Color(255, 158, 0));
		set2But.addActionListener(new setBtnListener('2'));
		set2But.setBackground(new Color(0, 0, 0));
		set2But.setBorder(new LineBorder(Color.WHITE, 3, true));
		set2But.setPreferredSize(new Dimension(SET_BUTTON_WIDTH, SET_BUTTON_HEIGHT));
		setBtnPanel.add(set2But, con);

		//Added a filler panel to separate the buttons
		JPanel borderFiller1 = new JPanel();
		borderFiller1.setOpaque(false);
		borderFiller1.setPreferredSize(new Dimension(FILLER_SIZE, FILLER_SIZE));
		setBtnPanel.add(borderFiller1, con);

		//Set up set 3 button
		HoverButton set3But = new HoverButton();
		set3But.setText("Set 3");
		set3But.setToolTipText("Set 3");
		set3But.setFont(buttonsFont);
		set3But.setForeground(new Color(167, 255, 1));
		set3But.addActionListener(new setBtnListener('3'));
		set3But.setBackground(new Color(0, 0, 0));
		set3But.setBorder(new LineBorder(Color.WHITE, 3, true));
		set3But.setPreferredSize(new Dimension(SET_BUTTON_WIDTH, SET_BUTTON_HEIGHT));
		setBtnPanel.add(set3But, con);

		//Added a filler panel to separate the buttons
		JPanel borderFiller2 = new JPanel();
		borderFiller2.setOpaque(false);
		borderFiller2.setPreferredSize(new Dimension(FILLER_SIZE, FILLER_SIZE));
		setBtnPanel.add(borderFiller2, con);

		//Set up set 4 button
		HoverButton setCustomBut = new HoverButton();
		setCustomBut.setText("Custom");
		setCustomBut.setToolTipText("Custom levels");
		setCustomBut.setFont(buttonsFont);
		setCustomBut.setForeground(new Color(0, 255, 248));
		setCustomBut.addActionListener(new setBtnListener('c'));
		setCustomBut.setBackground(new Color(0, 0, 0));
		setCustomBut.setBorder(new LineBorder(Color.WHITE, 3, true));
		setCustomBut.setPreferredSize(new Dimension(SET_BUTTON_WIDTH, SET_BUTTON_HEIGHT));
		setBtnPanel.add(setCustomBut, con);

		//Revalidate and redraw the panel
		revalidate();
		repaint();
	}

	/**
	 * Sets up the levels panel
	 * @param levelSuffix The suffix of the set currently shown
	 */
	private void setUpLevelsPanel(char levelSuffix) {
		//Remove all the components of the panel to reset it
		levelsPanel.removeAll();
		GridBagConstraints lvlPnlCon = new GridBagConstraints();
		levelsPanel.setOpaque(false);
		levelsPanel.setLayout(new GridBagLayout());

		//Get all the files in the levels folder
		File levelsFolder = new File("levels");
		File[] filesList = levelsFolder.listFiles();

		//Loop through the files and find the ones matching the naming scheme
		//of the levels and matching the current set
		for (File file : filesList) {
			if (file.isFile()) {
				if (file.getName().startsWith("level" + levelSuffix) && file.getName().endsWith(".txt")) {
					try {
						setUpLvlButton(file, levelsPanel, lvlPnlCon);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}

		//Revalidate and redraw the panel
		revalidate();		
		repaint();
	}

	/**
	 * Sets up the panel containing the levels panel and set button panel
	 * @param levelSuffix The suffix of the set currently shown
	 */
	private void setUpLvlSetPanel(char levelSuffix) {
		//Remove all the components of the panel to reset it
		lvlSetPanel.removeAll();
		GridBagConstraints lvlPnlCon = new GridBagConstraints();
		lvlSetPanel.setOpaque(false);
		lvlSetPanel.setLayout(new GridBagLayout());

		//Add the set button panel
		lvlPnlCon.gridy = 0;
		lvlSetPanel.add(setBtnPanel, lvlPnlCon);
		lvlPnlCon.gridy++;
		
		//Add a filler panel to separate the two panels
		JPanel borderFiller = new JPanel();
		borderFiller.setOpaque(false);
		borderFiller.setPreferredSize(new Dimension(20, 20));
		lvlSetPanel.add(borderFiller, lvlPnlCon);
		
		//Add the level set panel
		lvlPnlCon.gridy++;
		lvlSetPanel.add(levelsPanel, lvlPnlCon);

		//Revalidate and redraw the panel
		revalidate();
		repaint();
	}

	/**
	 * Sets up the panel containg the level set panel and set label
	 * @param levelSuffix The suffix of the set currently shown
	 */
	private void setUpLvlSetLabelPanel(char levelSuffix) {
		//Remove all the components of the panel to reset it
		lvlSetLabelPanel.removeAll();
		lvlSetLabelPanel.setOpaque(false);
		GridBagConstraints lvlPnlCon = new GridBagConstraints();
		lvlSetLabelPanel.setLayout(new GridBagLayout());

		//Set up the set panel the the levelSuffix
		Font setFont = null;
		try {
			setFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("VCR_OSD_MONO.ttf"))
					.deriveFont(Font.PLAIN, 35);
		} catch (FontFormatException | IOException e1) {
			e1.printStackTrace();
		}
		JLabel setLabel = new JLabel("Set " + levelSuffix);
		if (levelSuffix == 'c')
			setLabel.setText("Set Custom");
		setLabel.setForeground(new Color(255, 255, 255));
		setLabel.setFont(setFont);

		//Add the set panel
		lvlPnlCon.gridy = 0;
		lvlSetLabelPanel.add(setLabel, lvlPnlCon);
		lvlPnlCon.gridy++;

		//Add a filler panel to separate the two components
		JPanel borderFiller = new JPanel();
		borderFiller.setOpaque(false);
		borderFiller.setPreferredSize(new Dimension(20, 20));
		lvlSetLabelPanel.add(borderFiller, lvlPnlCon);
		
		//Add the level set panel
		lvlPnlCon.gridy++;
		lvlSetLabelPanel.add(lvlSetPanel, lvlPnlCon);
		
		//Revalidate and redraw the panel
		revalidate();
		repaint();
	}

	/**
	 * Set up the level buttons
	 * @param file File of a level
	 * @param panel Panel to add the button to
	 * @param con Constraint
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void setUpLvlButton(File file, JPanel panel, GridBagConstraints con)
			throws FileNotFoundException, IOException {
		Scanner sc = new Scanner(new FileReader(file));

		//Read the file until we get to the empty line
		while (sc.hasNextLine()) {
			String lineString = sc.nextLine();
			Scanner lsScanner = new Scanner(lineString);

			if (!lsScanner.hasNext()) {
				break;
			}
		}

		//Load in the image location and highscore
		String imageLocation = sc.nextLine();
		String highScoreString = sc.nextLine();
		String[] stringArray = highScoreString.split(" ");
		String highScore = stringArray[0];

		if (sc != null)
			sc.close();

		//Set up the panel
		JPanel btnPanel = new JPanel();
		btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		btnPanel.setOpaque(false);
		btnPanel.setLayout(new GridBagLayout());
		GridBagConstraints levelPanelCon = new GridBagConstraints();

		//Set up the level button with the data from the file
		HoverButton newButton = new HoverButton();
		Image btnImage = ImageIO.read(new File("levels/" + imageLocation));
		newButton.setIcon(new ImageIcon(
				btnImage.getScaledInstance(LEVEL_BUTTON_WIDTH, LEVEL_BUTTON_HEIGHT, java.awt.Image.SCALE_SMOOTH)));
		newButton.setToolTipText("Click to start level!");
		newButton.setBorderPainted(false);
		newButton.setContentAreaFilled(false);
		newButton.setPreferredSize(new Dimension(LEVEL_BUTTON_WIDTH, LEVEL_BUTTON_HEIGHT));
		newButton.addActionListener(new lvlBtnListener(file));

		//Set up the highscore label
		JLabel highScoreLabel = new JLabel("Highscore: " + highScore);
		highScoreLabel.setForeground(Color.WHITE);
		highScoreLabel.setFont(highScoresFont);
		highScoreLabel.setPreferredSize(new Dimension(150, 30));

		//Add the label and the button to the panel
		btnPanel.add(newButton, levelPanelCon);
		levelPanelCon.gridy = 1;
		btnPanel.add(highScoreLabel, levelPanelCon);

		//Add the panel to the passed in panel
		con.gridy = ((panel.getComponents().length) / BUTTONS_PER_LINE);
		btnPanel.setPreferredSize(new Dimension(150, 130));
		panel.add(btnPanel, con);
	}

}
