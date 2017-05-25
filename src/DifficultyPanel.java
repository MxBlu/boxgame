import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class DifficultyPanel extends JPanel{
	
    private Image background;
    private Image easy;
    private Image medium;
    private Image hard;
    
	private HoverButton Easy;
	private HoverButton Medium;
	private HoverButton Hard;
	
	public DifficultyPanel() {
		GameMaster.toggleCursorPointer();
		// Get the image file for the background and buttons
		try {
			background = ImageIO.read(getClass().getResourceAsStream("difficultySelect.png"));
			easy = ImageIO.read(getClass().getResourceAsStream("easy.png"));
			medium = ImageIO.read(getClass().getResourceAsStream("medium.png"));
			hard = ImageIO.read(getClass().getResourceAsStream("hard.png"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}		
		
		setPreferredSize(new Dimension(514,385));
		setBackground(Color.black);
		setOpaque(false);
		
		Easy = new HoverButton();
		Easy.setIcon(new ImageIcon(easy));
		Easy.setBorderPainted(false);
		Easy.setContentAreaFilled(false);
		
		Medium = new HoverButton();
		Medium.setIcon(new ImageIcon(medium));
		Medium.setBorderPainted(false);
		Medium.setContentAreaFilled(false);
		
		Hard = new HoverButton();
		Hard.setIcon(new ImageIcon(hard));
		Hard.setBorderPainted(false);
		Hard.setContentAreaFilled(false);
		
		// Easy button action listener
		Easy.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				System.out.println("easy");
				GameMaster.changeScreens(new LoadingScreen(GameMaster.WIDTH, GameMaster.HEIGHT, 50, Level.EASY));
			}
		});
		
		// Medium button action listener
		Medium.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				System.out.println("medium");
				GameMaster.changeScreens(new LoadingScreen(GameMaster.WIDTH, GameMaster.HEIGHT, 50, Level.MEDIUM));
			}
		});
		
		// Hard button action listener
		Hard.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				System.out.println("hard");
				GameMaster.changeScreens(new LoadingScreen(GameMaster.WIDTH, GameMaster.HEIGHT, 50, Level.HARD));
			}
		});
		
		setButtonLayout();

		setVisible(true);
		setBounds(200, 100, 2*GameMaster.WIDTH/3, 2*GameMaster.HEIGHT/3);
	}
	
	public void paintComponent(Graphics g) {
		// Access the JPanel super class's
		// function for painting.
		super.paintComponent(g);

	    // Draw the background for this JPanel
	    g.setColor(Color.BLACK);
        g.drawImage(background, 0, 0, null);
	}
	
	private void setButtonLayout() {
		// Set the layout of this JPanel
		// We can use one of the preset layouts from
		// JPanel's API to arrange the layout and 
		// positions of where components appear in this
		// JPanel. This is a better alternative opposed
		// to manually hard coding pixel perfect coordinates
		// where each button should appear. 
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// Add the buttons to the JPanel
		// gridx and gridy are part of JPanel's layout
		// API, and lets us organise the location of the buttons.
		c.gridx = 2;
		c.gridy = 1;
		add(Easy,c);
		c.gridy = 2;
		add(Medium,c);
		c.gridy = 3;
		add(Hard,c);

	}

}
