import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter{
	
	public static int input = 0;
	
	public KeyInput () {
		
	}
	
	public static int getPressed() {
		return input;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
        	input = 1;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
        	input = 2;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
        	input = 3;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
        	input = 4;
        }		
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
        	input = 5;
        }
	}

	@Override
	public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
        	input = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
           input = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            input = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
           input = 0;
        }			
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
        	input = 0;
        }
	}

		
	
}
