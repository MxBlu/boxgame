
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
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

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

    private Image background;
    private GridBagConstraints c;
    
    private static final String MOVE_UP = "move up";
    private static final String MOVE_DOWN = "move down";
    private static final String QUIT_MENU = "quit menu";
	
	public SelectScreen() {
		// Get the image file for the background and buttons
		try {
			background = ImageIO.read(getClass().getResourceAsStream("menu.png"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}		

		// Set the layout of this JPanel
		// We can use one of the preset layouts from
		// JPanel's API to arrange the layout and 
		// positions of where components appear in this
		// JPanel. This is a better alternative opposed
		// to manually hard coding pixel perfect coordinates
		// where each button should appear. 
		setLayout(new GridBagLayout());
		c = new GridBagConstraints();

		// Add the buttons to the JPanel
		// gridx and gridy are part of JPanel's layout
		// API, and lets us organise the location of the buttons.
		//c.gridx = 2;
		//c.gridy = 1;
		//add(play,c);
		//c.gridy = 2;
		//add(credits,c);
		//c.gridy = 3;
		//add(highScore,c);
		
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
				quitMenu();
			}
		});
		
		
		File levelsFolder = new File("levels");
		File[] filesList = levelsFolder.listFiles();

		for (File file : filesList) {
			if (file.isFile()) {
				if (file.getName().startsWith("level1") && file.getName().endsWith(".txt")) {
					try {
						setUpButton(file);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
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

	private void setUpButton(File file) throws FileNotFoundException, IOException {
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

		JButton newButton = new JButton();
		int buttonWidth = 100;
		int buttonHeight = 100;
		Image btnImage = ImageIO.read(getClass().getResourceAsStream("levels/" + imageLocation));
		newButton.setIcon(new ImageIcon(btnImage.getScaledInstance(buttonWidth, buttonHeight, java.awt.Image.SCALE_SMOOTH)));
		newButton.setToolTipText("Click to start level!");
		newButton.setBorderPainted(false);
		newButton.setContentAreaFilled(false);
		newButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		newButton.addActionListener(new lvlBtnListener(levelString));

		btnPanel.add(newButton);
		add(btnPanel, c);
	}

}
