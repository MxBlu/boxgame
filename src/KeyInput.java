import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter{
	
	public static int input = 0;
	
	public KeyInput () { }
	
	public static int getPressed() {
		return input;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
        input = e.getKeyCode();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() != KeyEvent.VK_ESCAPE)
			input = 0;        
	}

		
	
}
