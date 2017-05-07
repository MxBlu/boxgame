import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter{
	
	public static int input = 0;
    private long lastPressProcessed = 0;

	public KeyInput () {
		
	}
	
	public static int getPressed() {
		return input;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
        if(System.currentTimeMillis() - lastPressProcessed > 500) {
            
            if (e.getKeyCode() == KeyEvent.VK_UP) {
            	input = 1;
            	return;
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            	input = 2;
            	return;
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            	input = 3;
            	return;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            	input = 4;
            	return;
            }		
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            	input = 5;
            	return;
            }            
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            	input = 6;
            }
            lastPressProcessed = System.currentTimeMillis();
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
        if (e.getKeyCode()== KeyEvent.VK_ESCAPE) {
        	input = 0;
        }
	}

		
	
}
