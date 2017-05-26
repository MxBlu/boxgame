
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

public class SelectScreen extends JPanel {

	class lvlBtnListener implements ActionListener {
		private File levelFile;

		public lvlBtnListener(File levelFile) {
			this.levelFile = levelFile;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			GameMaster.changeScreens(new Level(levelFile, 40));
		}
	}

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

	private static final String MOVE_UP = "move up";
	private static final String MOVE_DOWN = "move down";
	private static final String QUIT_MENU = "quit menu";

	private final int BUTTONS_PER_LINE = 3;
	private final int LEVEL_BUTTON_WIDTH = 100;
	private final int LEVEL_BUTTON_HEIGHT = 100;

	private Image background;
	private GridBagConstraints c;
	private JPanel levelsPanel;
	private JPanel setBtnPanel;
	private JPanel lvlSetPanel;
	private JPanel lvlSetLabelPanel;
	private Font highScoresFont;

	public SelectScreen() {
		GameMaster.toggleCursorPointer();

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
		
		try {
			highScoresFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("VCR_OSD_MONO.ttf"))
					.deriveFont(Font.PLAIN, 15);
		} catch (FontFormatException | IOException e1) {
			e1.printStackTrace();
		}

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

	public void paintComponent(Graphics g) {
		// Access the JPanel super class's
		// function for painting.
		super.paintComponent(g);

		// Draw the background for this JPanel
		g.setColor(Color.BLACK);
		g.drawImage(background, 0, 0, null);
	}

	private void setUpPanels(char levelSuffix) {
		setUpSetBtnPanel(levelSuffix);
		setUpLevelsPanel(levelSuffix);
		setUpLvlSetPanel(levelSuffix);
		setUpLvlSetLabelPanel(levelSuffix);

		revalidate();
		repaint();
	}

	private void quitMenu() {
		((JFrame) SwingUtilities.getWindowAncestor(this)).dispose();
	}

	private void back() {
		GameMaster.changeScreens(new MenuScreen());
	}

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
		
		int buttonWidth = 110;
		int buttonHeight = 40;
		int fillerSize = 20;

		HoverButton world1But = new HoverButton();
		world1But.setText("Set 1");
		world1But.setToolTipText("Set 1");
		world1But.setFont(buttonsFont);
		world1But.setForeground(new Color(255, 18, 81));
		world1But.addActionListener(new setBtnListener('1'));
		world1But.setBackground(new Color(0, 0, 0));
		world1But.setBorder(new LineBorder(Color.WHITE, 3, true));
		world1But.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		setBtnPanel.add(world1But, con);

		JPanel borderFiller = new JPanel();
		borderFiller.setOpaque(false);
		borderFiller.setPreferredSize(new Dimension(fillerSize, fillerSize));
		setBtnPanel.add(borderFiller, con);

		HoverButton world2But = new HoverButton();
		world2But.setText("Set 2");
		world2But.setToolTipText("Set 2");
		world2But.setFont(buttonsFont);
		world2But.setForeground(new Color(255, 158, 0));
		world2But.addActionListener(new setBtnListener('2'));
		world2But.setBackground(new Color(0, 0, 0));
		world2But.setBorder(new LineBorder(Color.WHITE, 3, true));
		world2But.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		setBtnPanel.add(world2But, con);

		JPanel borderFiller1 = new JPanel();
		borderFiller1.setOpaque(false);
		borderFiller1.setPreferredSize(new Dimension(fillerSize, fillerSize));
		setBtnPanel.add(borderFiller1, con);

		HoverButton world3But = new HoverButton();
		world3But.setText("Set 3");
		world3But.setToolTipText("Set 3");
		world3But.setFont(buttonsFont);
		world3But.setForeground(new Color(167, 255, 1));
		world3But.addActionListener(new setBtnListener('3'));
		world3But.setBackground(new Color(0, 0, 0));
		world3But.setBorder(new LineBorder(Color.WHITE, 3, true));
		world3But.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		setBtnPanel.add(world3But, con);

		JPanel borderFiller2 = new JPanel();
		borderFiller2.setOpaque(false);
		borderFiller2.setPreferredSize(new Dimension(fillerSize, fillerSize));
		setBtnPanel.add(borderFiller2, con);

		HoverButton setCustomBut = new HoverButton();
		setCustomBut.setText("Custom");
		setCustomBut.setToolTipText("Custom levels");
		setCustomBut.setFont(buttonsFont);
		setCustomBut.setForeground(new Color(0, 255, 248));
		setCustomBut.addActionListener(new setBtnListener('c'));
		setCustomBut.setBackground(new Color(0, 0, 0));
		setCustomBut.setBorder(new LineBorder(Color.WHITE, 3, true));
		setCustomBut.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		setBtnPanel.add(setCustomBut, con);

		revalidate();
		repaint();
	}

	private void setUpLevelsPanel(char levelSuffix) {
		levelsPanel.removeAll();
		GridBagConstraints lvlPnlCon = new GridBagConstraints();
		levelsPanel.setOpaque(false);
		levelsPanel.setLayout(new GridBagLayout());

		File levelsFolder = new File("levels");
		File[] filesList = levelsFolder.listFiles();

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

		revalidate();		
		repaint();
	}

	private void setUpLvlSetPanel(char levelSuffix) {
		lvlSetPanel.removeAll();
		GridBagConstraints lvlPnlCon = new GridBagConstraints();
		lvlSetPanel.setOpaque(false);
		lvlSetPanel.setLayout(new GridBagLayout());

		lvlPnlCon.gridy = 0;
		lvlSetPanel.add(setBtnPanel, lvlPnlCon);
		lvlPnlCon.gridy++;
		JPanel borderFiller = new JPanel();
		borderFiller.setOpaque(false);
		borderFiller.setPreferredSize(new Dimension(20, 20));
		lvlSetPanel.add(borderFiller, lvlPnlCon);
		lvlPnlCon.gridy++;
		lvlSetPanel.add(levelsPanel, lvlPnlCon);

		revalidate();
		repaint();
	}

	private void setUpLvlSetLabelPanel(char levelSuffix) {
		lvlSetLabelPanel.removeAll();
		lvlSetLabelPanel.setOpaque(false);
		GridBagConstraints lvlPnlCon = new GridBagConstraints();
		lvlSetLabelPanel.setLayout(new GridBagLayout());

		JLabel setLabel = new JLabel("Set " + levelSuffix);

		if (levelSuffix == 'c')
			setLabel.setText("Set Custom");

		Font setFont = null;
		try {
			setFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("VCR_OSD_MONO.ttf"))
					.deriveFont(Font.PLAIN, 35);
		} catch (FontFormatException | IOException e1) {
			e1.printStackTrace();
		}
		setLabel.setForeground(new Color(255, 255, 255));
		setLabel.setFont(setFont);

		lvlPnlCon.gridy = 0;
		lvlSetLabelPanel.add(setLabel, lvlPnlCon);
		lvlPnlCon.gridy++;
		JPanel borderFiller = new JPanel();
		borderFiller.setOpaque(false);
		borderFiller.setPreferredSize(new Dimension(20, 20));
		lvlSetLabelPanel.add(borderFiller, lvlPnlCon);
		lvlPnlCon.gridy++;
		lvlSetLabelPanel.add(lvlSetPanel, lvlPnlCon);

		revalidate();
		repaint();
	}

	private void setUpLvlButton(File file, JPanel panel, GridBagConstraints con)
			throws FileNotFoundException, IOException {
		Scanner sc = new Scanner(new FileReader(file));
		String levelString = "";

		while (sc.hasNextLine()) {
			String lineString = sc.nextLine();
			levelString += lineString + "\n";
			Scanner lsScanner = new Scanner(lineString);

			if (!lsScanner.hasNext()) {
				break;
			}
		}

		String imageLocation = sc.nextLine();
		String highScoreString = sc.nextLine();
		String[] stringArray = highScoreString.split(" ");
		String highScore = stringArray[0];
			

		if (sc != null)
			sc.close();

		JPanel btnPanel = new JPanel();
		btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		btnPanel.setOpaque(false);
		btnPanel.setLayout(new GridBagLayout());
		GridBagConstraints levelPanelCon = new GridBagConstraints();

		HoverButton newButton = new HoverButton();

		Image btnImage = ImageIO.read(new File("levels/" + imageLocation));
		newButton.setIcon(new ImageIcon(
				btnImage.getScaledInstance(LEVEL_BUTTON_WIDTH, LEVEL_BUTTON_HEIGHT, java.awt.Image.SCALE_SMOOTH)));
		newButton.setToolTipText("Click to start level!");
		newButton.setBorderPainted(false);
		newButton.setContentAreaFilled(false);
		newButton.setPreferredSize(new Dimension(LEVEL_BUTTON_WIDTH, LEVEL_BUTTON_HEIGHT));
		newButton.addActionListener(new lvlBtnListener(file));

		JLabel highScoreLabel = new JLabel("Highscore: " + highScore);
		highScoreLabel.setForeground(Color.WHITE);
		highScoreLabel.setFont(highScoresFont);
		highScoreLabel.setPreferredSize(new Dimension(150, 30));

		btnPanel.add(newButton, levelPanelCon);
		levelPanelCon.gridy = 1;
		btnPanel.add(highScoreLabel, levelPanelCon);

		con.gridy = ((panel.getComponents().length) / BUTTONS_PER_LINE);

		btnPanel.setPreferredSize(new Dimension(150, 130));
		panel.add(btnPanel, con);
	}

}
