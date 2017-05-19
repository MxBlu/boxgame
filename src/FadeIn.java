
import java.awt.AlphaComposite;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class FadeIn extends JPanel implements ActionListener {

    Image imagem;
    Timer timer;
    	
    private float alpha = 0f;

    public FadeIn() {
    	// Emily! This method of loading in images works.
    	// For some reason 
    	//  imagem = new ImageIcon("logo.png").getImage();
    	// dont work
		// Get the image file for the background
		try {
			imagem = ImageIO.read(getClass().getResourceAsStream("logo.png"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}	
		
        // Initialize attributes of this JPanel
		setPreferredSize(new Dimension (GameMaster.WIDTH, GameMaster.HEIGHT));
		// How the buttons for this JPanel are arranged/
		// the layout
		setLayout(new GridLayout(3,5));
        
        //imagem = new ImageIcon("logo.png").getImage();
        timer = new Timer(100, this);
        timer.start();
        this.setFocusable(true);
        this.requestFocusInWindow();
        System.out.println("FadeIn");
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.pink);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                    alpha));
        g2d.drawImage(imagem, 0, 0, null);
        
    }


    public void actionPerformed(ActionEvent e) {
        alpha += 0.05f;
        if (alpha >1) {
            alpha = 1;
            timer.stop();
            //imagem = new ImageIcon("logo2.png").getImage();
    		try {
    			imagem = ImageIO.read(getClass().getResourceAsStream("logo2.png"));
    		}
    		catch(Exception s) {
    			s.printStackTrace();
    		}	
    		
			// Would probably have the code to go to Menu here.
    		
        }

        repaint();
    }
}