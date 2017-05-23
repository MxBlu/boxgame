import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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

public class PauseScreen extends JPanel{
	
	private JButton Menu;
	private JButton Resume;
	Image menuButton;
	
	
	
	public PauseScreen() {
		
		try{
			menuButton = ImageIO.read(getClass().getResourceAsStream("menubutton.png"));
		}
			catch(Exception e) {
			e.printStackTrace();
		}
		
		setPreferredSize(new Dimension(200,200));
		setBackground(Color.black);
		Menu = new JButton();
		Menu.setIcon(new ImageIcon(menuButton));
		Menu.setBorderPainted(false);
		Menu.setContentAreaFilled(false);
		
		Resume = new JButton("Resume");
		// Return to menu button action listener
		Menu.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				System.out.println("return to menu");
	    		GameMaster.changeScreens(new MenuScreen());
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
		add(Menu,c);
		c.gridx = 2;
		add(Resume,c);
		

		
		System.out.println("pause screen");
		setVisible(true);
		setBounds(200, 100, 2*GameMaster.WIDTH/3, 2*GameMaster.HEIGHT/3);
	}

}
