import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class HoverButton extends JButton {

	private Image cursor1;
	private Image cursor2;

	public HoverButton() {
		try {
			cursor1 = ImageIO.read(getClass().getResourceAsStream("pointer.png"));
			cursor2 = ImageIO.read(getClass().getResourceAsStream("hand.png"));
			Toolkit toolkit = Toolkit.getDefaultToolkit();
	     	Point cursorHotSpot = new Point(0,0);
	     	Cursor customCursor= null;
	     
	     	customCursor = toolkit.createCustomCursor(cursor2, cursorHotSpot, "Cursor");
	     	setCursor(customCursor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				//GameMaster.toggleCursorHover();
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				//GameMaster.toggleCursorPointer();
			}
		});
	}

}
