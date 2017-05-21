
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

public class BootScreen extends JPanel implements ActionListener{

	//JFrame main;
    Image imagem;
    Timer timer;
    
    public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	
	public static final String SKIP = "SKIP";
	
    private float alpha = 0.0f;
    private int state = 0;

    public BootScreen( ) {
    	//main = m;
        imagem = new ImageIcon("logo.png").getImage();
        timer = new Timer(GameMaster.FRAME_DELTA, this);
        timer.start();
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), SKIP);
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), SKIP);
        
        this.getActionMap().put(SKIP, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				GameMaster.changeScreens(new MenuScreen());	
			}
		});
    } 

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.pink);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.drawImage(imagem, 0, 0, null);
    }


    public void actionPerformed(ActionEvent e) {
    	if (state == 0) {
	        alpha += 0.015f;
	        
	        if (alpha > 1) {
	            alpha = 1;
	            timer.setDelay(2000);
	            state = 1;
	           
	            imagem = new ImageIcon("logo2.png").getImage();
	        }

	        repaint();
    	} else {
    		timer.stop();
    		GameMaster.changeScreens(new MenuScreen());
    	}
    }
}