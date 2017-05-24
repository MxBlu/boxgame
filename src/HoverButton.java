import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class HoverButton extends JButton{

	private Image cursor1;
	private Image cursor2;
	public HoverButton() {

		try{
	
			cursor1 = ImageIO.read(getClass().getResourceAsStream("pointer.png"));
			cursor2 = ImageIO.read(getClass().getResourceAsStream("hand.png"));

		}
			catch(Exception e) {
			e.printStackTrace();
		}
		addMouseListener(new java.awt.event.MouseAdapter() {
			  
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		    	GameMaster.toggleCursorHover();
		    }
		    public void mouseExited(java.awt.event.MouseEvent evt) {
		    	GameMaster.toggleCursorPointer();
		    }
		});
	}
	

}
