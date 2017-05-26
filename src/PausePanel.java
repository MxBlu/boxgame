import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * Pause Panel
 * 
 * Displayed over level screen when player presses esc or clicks pause button
 * Allows user to go back to Main menu, or resume playing
 * 
 */

public class PausePanel extends JPanel{
	
	
	private Image Background;
	private Image menuButton;
	private Image resumeButton;

	private HoverButton Menu;
	private HoverButton Resume;
	private JFrame frame;
	
	/**
	 * Representative of the pause panel that shows up when the user pauses their level
	 */
	public PausePanel(JFrame frame) {

		this.frame = frame;
		try{
			// Sets the images to be used
			Background = ImageIO.read(getClass().getResourceAsStream("pausescreen.png"));
			menuButton = ImageIO.read(getClass().getResourceAsStream("returntomenu.png"));
			resumeButton = ImageIO.read(getClass().getResourceAsStream("resumebutton.png"));
		}
			catch(Exception e) {
			e.printStackTrace();
		}
		
		setBackground(Color.black);
		setOpaque(false);

		// Sets the hover buttons
		Menu = new HoverButton();
		Menu.setIcon(new ImageIcon(menuButton));
		Menu.setBorderPainted(false);
		Menu.setContentAreaFilled(false);
		
		Resume = new HoverButton();
		Resume.setIcon(new ImageIcon(resumeButton));
		Resume.setBorderPainted(false);
		Resume.setContentAreaFilled(false);
		
		// Return to menu button action listener
		Menu.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
	    		GameMaster.changeScreens(frame, new MenuScreen(frame));
			}
		});
		
		// Next level button action listener
		Resume.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				Level l = (Level) getParent();
				l.togglePaused();
			}
		});
		
		//Centers the buttons
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		add(Resume,c);
		c.gridy = 2;
		add(Menu,c);

		setVisible(true);
		setBounds(200, 100, 2*GameMaster.WIDTH/3, 2*GameMaster.HEIGHT/3);
	}
	
	/**
	 * Paints the components for the pause panel for the user
	 */
	public void paintComponent(Graphics g) {
		// Access the JPanel super class's
		// function for painting.
		super.paintComponent(g);
		
	    // Draw the background for this JPanel
        g.drawImage(Background, 0, 0, null);
     
	}

}
