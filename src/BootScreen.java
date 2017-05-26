
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;

import java.awt.Graphics2D;
import java.awt.Image;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;


/*
 * Animation with music at start of game
 */

public class BootScreen extends JPanel implements ActionListener{

	//JFrame main;
    Image imagem;
    Timer timer;
    
    public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	
	public static final String SKIP = "SKIP";
	
    private float alpha = 0.0f;
    private int state = 0;
    private AudioManager audioSource;
    
    private JFrame frame;

    public BootScreen(JFrame frame) {
    	this.frame = frame;
    	// gets the logo image for the boot screen
        imagem = new ImageIcon("logo.png").getImage();
        timer = new Timer(GameMaster.FRAME_DELTA, this);
        // starts the game's timer
        timer.start();
        
        // plays a sound during the boot screen
        audioSource = new AudioManager();
        audioSource.playSound("intro_sound.wav", 1.0f);
        
        // checks if the user input anything during the boot screen
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), SKIP);
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), SKIP);
        
        // skips the boot screen straight to the menu
        this.getActionMap().put(SKIP, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				audioSource.stopSound();
				timer.stop();
				GameMaster.changeScreens(frame, new MenuScreen(frame));	
			}
		});
    } 
    
    /* Repaints the image of boot screen */
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.pink);
        // does the alpha
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.drawImage(imagem, 0, 0, null);
    }

    /* 
     * Triggers events for timer
     *  
     * Calls repaint on the screen at every timer interval
     * and calls for change in image
     * 
     * @param ActionEvent e is triggered by the timer
     *
     */
    public void actionPerformed(ActionEvent e) {
    	// checks if it is an initial state
    	if (state == 0) {
	        alpha += 0.015f;
	        
	        // checks alpha count
	        if (alpha > 1) {
	            alpha = 1;
	            timer.setDelay(2000);
	            state = 1;
	           
	            // prints the secondary boot screen image
	            imagem = new ImageIcon("logo2.png").getImage();
	        }

	        repaint();
    	} else {
    		//stops the boot screen operations and changes over to menu screen
    		audioSource.stopSound();
    		timer.stop();
    		GameMaster.changeScreens(frame, new MenuScreen(frame));
    	}
    }
}