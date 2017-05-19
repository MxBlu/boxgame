import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MenuStateTrial extends JPanel{

	private JButton play;
	private JButton credits;
	private JButton highScore;
	//private JPanel gamePanel;
	
	public MenuStateTrial() {
		System.out.println("in menu screen");
		play = new JButton("Play");
		credits = new JButton("Credits");
		highScore = new JButton("High Score");
		
		play.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				System.out.println("change thing");
				GameMaster.changeScreens(newScreen);
			}
		});
		
		credits.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				System.out.println("change screen");
			}
		});
		
		add(play);
		add(credits);
		add(highScore);
		revalidate();
		setBackground(Color.BLUE);
		this.setFocusable(true);
	    this.requestFocusInWindow();
		
	}
/*
	@Override
	public void init() {
		play = new JButton("Play");
		credits = new JButton("Credits");
		highScore = new JButton("High Score");
		gamePanel = new JPanel(new FlowLayout());
		gamePanel.add(play);
		gamePanel.add(credits);
		gamePanel.add(highScore);
		gamePanel.revalidate();
		gamePanel.setBackground(Color.BLUE);
		
		System.out.println("finished in menu");
		
	}*/

	/*@Override
	public void update() {
		handleInput();
		
		
	}

	@Override
	public void draw(Graphics2D g) {
		Font font = new Font("arial", Font.BOLD, 50);
		g.setFont(font);
		g.setColor(Color.WHITE);
		g.drawString("BLOCK STUFF",GameMaster.WIDTH/2 , 100);
		System.out.println("inside the block for draw mentustate ");
	}

	@Override
	public void handleInput() {
		play.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				StateManager.getFrame().removeAll();
				StateManager.setState("LEVEL");		
			}
		});
		
		credits.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				StateManager.getFrame().removeAll();
				StateManager.setState("CREDITS");	
			}
		});
		
	}*/

}
