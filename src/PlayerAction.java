import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class PlayerAction extends AbstractAction {
	
	private int action;
	private Player curr;
	private Level game;
	
	public PlayerAction(int number, Player x, Level on) {
		this.action = number;
		curr = x;
		game = on;
		System.out.println("called player action");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (action){
		case 1:
			System.out.println("UP");
			curr.moveUp();
			game.update();
			game.repaint();
			break;
		case 2:
			System.out.println("DOWN");
			curr.moveDown();
			game.update();
			game.repaint();
			break;
		case 3:
			System.out.println("LEFT");
			curr.moveLeft();
			game.update();
			game.repaint();
			break;
		case 4:
			System.out.println("RIGHT");
			curr.moveRight();
			game.update();
			game.repaint();
			break;
		}
		
	}
	

}
