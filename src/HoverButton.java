import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class HoverButton extends JButton {

	private Image cursorImage;

	public HoverButton() {
		try {
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
