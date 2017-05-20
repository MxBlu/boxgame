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
		curr.setMove(action);
		
	}
	

}
