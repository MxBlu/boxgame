import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class Menu extends JPanel{
	
    private Image background;
	private JButton playButton;

	public Menu() {
		
		// Get the image file for the background
		try {
			background = ImageIO.read(getClass().getResourceAsStream("menu.png"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}		
        // Initialize attributes of this JPanel
		setPreferredSize(new Dimension (GameMaster.WIDTH, GameMaster.HEIGHT));
		// How the buttons for this JPanel are arranged/
		// the layout
		setLayout(new GridLayout(3,5));
        setFocusable(true);
        requestFocusInWindow();
        
		// Initialize Play JButton
		playButton = new JButton("Play");
		playButton.setToolTipText("Click this to start the game");
		playButton.setPreferredSize(new Dimension(120, 38));
		
		// Add Play button to this JPanel
		add(playButton);
		
		// Play button action listener
		playButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Button pressed");

			}

		});	
	}
	
	public void paintComponent(Graphics g) {
		// Access the JPanel super class's
		// function for painting.
		super.paintComponent(g);
		
		// Converts graphics to graphics2d
        //Graphics2D g2d = (Graphics2D) g;

	    // Draw the background for this JPanel
	    g.setColor(Color.BLACK);
        g.drawImage(background, 0, 0, null);
	     
	}
}
