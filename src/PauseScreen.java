import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class PauseScreen extends JPanel{
	
	private JButton Menu;
	private JButton Resume;
	
	
	public PauseScreen() {
		
		setPreferredSize(new Dimension(200,200));
		setBackground(Color.black);
		Menu = new JButton("Menu");
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
				System.out.println("next level load");
				GameMaster.changeScreens(new Level(GameMaster.WIDTH, GameMaster.HEIGHT, 40, new LevelGenBlock()));
			}
		});
		
		
		add(Menu);
		add(Resume);
		System.out.println("pause screen");
		setVisible(true);
		setBounds(200, 100, 2*GameMaster.WIDTH/3, 2*GameMaster.HEIGHT/3);
	}

}
