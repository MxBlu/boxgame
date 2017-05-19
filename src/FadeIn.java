
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;

import java.awt.Graphics2D;
import java.awt.Image;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class FadeIn extends JPanel implements ActionListener {

    Image imagem;
    Timer timer;
    
    public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	
    private float alpha = 0f;

    public FadeIn() {
    	
        imagem = new ImageIcon("logo.png").getImage();
        timer = new Timer(100, this);
        timer.start();
        this.setFocusable(true);
        this.requestFocusInWindow();

    }

    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Fade out");
        frame.add(new FadeIn());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        // frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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
            imagem = new ImageIcon("logo2.png").getImage();
          
        }

        repaint();
    }
}