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

public class PausePanel extends JPanel{
	
	
	private Image Background;
	private Image menuButton;
	private Image resumeButton;

	private HoverButton Menu;
	private HoverButton Resume;
	private JFrame frame;
	
	public PausePanel(JFrame frame) {
		//GameMaster.toggleCursorPointer();
		this.frame = frame;
		try{
			Background = ImageIO.read(getClass().getResourceAsStream("pausescreen.png"));
			menuButton = ImageIO.read(getClass().getResourceAsStream("returntomenu.png"));
			resumeButton = ImageIO.read(getClass().getResourceAsStream("resumebutton.png"));
		}
			catch(Exception e) {
			e.printStackTrace();
		}
		
		setBackground(Color.black);
		setOpaque(false);

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
				System.out.println("return to menu");
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
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		add(Resume,c);
		c.gridy = 2;
		add(Menu,c);

		System.out.println("pause screen");
		setVisible(true);
		setBounds(200, 100, 2*GameMaster.WIDTH/3, 2*GameMaster.HEIGHT/3);
	}
	
	public void paintComponent(Graphics g) {
		// Access the JPanel super class's
		// function for painting.
		super.paintComponent(g);
		
	    // Draw the background for this JPanel
	    //g.setColor(Color.BLACK);
        g.drawImage(Background, 0, 0, null);
     
	}

}
