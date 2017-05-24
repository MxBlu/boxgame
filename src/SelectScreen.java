
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
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

import javafx.scene.text.Font;

public class SelectScreen extends JPanel{
	
	class lvlBtnListener implements ActionListener {
		private String levelString;
		
		public lvlBtnListener(String levelString) {
			this.levelString = levelString;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			GameMaster.changeScreens(new Level(levelString, 40));
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
    
	public SelectScreen() {
		// Get the image file for the background and buttons
		try {
			background = ImageIO.read(getClass().getResourceAsStream("menu.png"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}		

		setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		
		// Set the JPanels attributes and revalidate.
		revalidate();
		setFocusable(true);
	    requestFocusInWindow();
	    
	    // Keyinput
	    getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), MOVE_UP);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), MOVE_DOWN);
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
		
		getActionMap().put(QUIT_MENU, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				back();
			}
		});
		
		JButton back = new JButton();
		try {
			back.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("back.png"))));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		back.setToolTipText("Back button");
		back.setBorderPainted(false);
		back.setContentAreaFilled(false);
		back.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				back();
			}
		});
		add(back, c);
		
		levelsPanel = new JPanel();
		setUpLevelsPanel();
		c.gridy = 0;
		add(levelsPanel, c);
	}
	
	public void paintComponent(Graphics g) {
		// Access the JPanel super class's
		// function for painting.
		super.paintComponent(g);

	    // Draw the background for this JPanel
	    g.setColor(Color.BLACK);
        g.drawImage(background, 0, 0, null);
	}
	
	private void handleDown() {
	}
	
	private void handleUp() {
	}
	
	private void quitMenu() {
		((JFrame) SwingUtilities.getWindowAncestor(this)).dispose();
	}
	
	private void back() {
		GameMaster.changeScreens(new MenuScreen());
	}
	
	private void setUpLevelsPanel() {
		GridBagConstraints lvlPnlCon = new GridBagConstraints();
		levelsPanel.setOpaque(false);
		levelsPanel.setLayout(new GridBagLayout());
		
		JLabel worldLabel = new JLabel("World 1");
		worldLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 40));
		lvlPnlCon.gridy = 0;
		levelsPanel.add(worldLabel, lvlPnlCon);
		
		lvlPnlCon.gridy = 1;

		JButton world1But = new JButton();
		world1But.setText("World 1");
		world1But.setToolTipText("World 1");
		world1But.setBorderPainted(false);
		//world1But.setContentAreaFilled(false);
		world1But.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				back();
			}
		});
		levelsPanel.add(world1But, lvlPnlCon);
		
		JButton world2But = new JButton();
		world2But.setText("World 2");
		world2But.setToolTipText("World 2");
		world2But.setBorderPainted(false);
		//world1But.setContentAreaFilled(false);
		world2But.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				back();
			}
		});
		levelsPanel.add(world2But, lvlPnlCon);
		
		JButton world3But = new JButton();
		world3But.setText("World 3");
		world3But.setToolTipText("World 3");
		world3But.setBorderPainted(false);
		//world1But.setContentAreaFilled(false);
		world3But.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				back();
			}
		});
		levelsPanel.add(world3But, lvlPnlCon);
		
		File levelsFolder = new File("levels");
		File[] filesList = levelsFolder.listFiles();

		for (File file : filesList) {
			if (file.isFile()) {
				if (file.getName().startsWith("level1") && file.getName().endsWith(".txt")) {
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
	}

	private void setUpLvlButton(File file, JPanel panel, GridBagConstraints con) throws FileNotFoundException, IOException {
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
		String highScore = sc.nextLine();
		
		if (sc != null) sc.close();
	    
		JPanel btnPanel = new JPanel();
		btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		btnPanel.setOpaque(false);
		btnPanel.setLayout(new GridBagLayout());
		GridBagConstraints levelPanelCon = new GridBagConstraints();
		
		JButton newButton = new JButton();
		
		Image btnImage = ImageIO.read(getClass().getResourceAsStream("levels/" + imageLocation));
		newButton.setIcon(new ImageIcon(btnImage.getScaledInstance(LEVEL_BUTTON_WIDTH, LEVEL_BUTTON_HEIGHT, java.awt.Image.SCALE_SMOOTH)));
		newButton.setToolTipText("Click to start level!");
		newButton.setBorderPainted(false);
		newButton.setContentAreaFilled(false);
		newButton.setPreferredSize(new Dimension(LEVEL_BUTTON_WIDTH, LEVEL_BUTTON_HEIGHT));
		newButton.addActionListener(new lvlBtnListener(levelString));
		
		JLabel highScoreLabel = new JLabel("Highscore: " + highScore);

		btnPanel.add(newButton, levelPanelCon);
		levelPanelCon.gridy = 1;
		btnPanel.add(highScoreLabel, levelPanelCon);
		
		con.gridy = ((panel.getComponents().length - 4) / BUTTONS_PER_LINE) + 2;
		
		panel.add(btnPanel, con);
	}

}
