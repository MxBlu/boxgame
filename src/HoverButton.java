import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/*
 * JButton that toggles cursor 
 * 
 * All buttons in game have a different cursor when hovered over
 */

public class HoverButton extends JButton {

	private Image cursorImage;

	/*
	 * Sets the default pointer cursor
	 * 
	 */
	public HoverButton() {
		try {
			// Creates the cursor when hovering oveer a button
			cursorImage = ImageIO.read(getClass().getResourceAsStream("hand.png"));
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Point cursorHotSpot = new Point(0, 0);
			Cursor customCursor = toolkit.createCustomCursor(cursorImage, cursorHotSpot, "Cursor");
			setCursor(customCursor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
