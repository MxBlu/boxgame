import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage; 

public class GameMaster{
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static final int FPS = 60;
	public static final int FRAME_DELTA = 1000/FPS;

	// Here for debugging reasons only
	public static float adj1Chance = 0.40f;
	public static float adj2Chance = 0.20f;
	public static float adj3Chance = 0.50f;
	public static float adj4Chance = 0.60f;

	
	private static JFrame frame;
	//private static JPanel panel;
	

    public static void main(String[] args) {
    	SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {                                           
            	initScreen();
            	frame.getContentPane().add(new BootScreen());
            	
            }
        });  
    }
    
    private static void initScreen(){
    	frame = new JFrame("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        /*panel = new JPanel(new CardLayout());
        frame.add(panel);*/
    }
    
    public static void changeScreens(JPanel newScreen) {
    	frame.getContentPane().removeAll();
		frame.getContentPane().add( newScreen);
		frame.repaint();
		frame.revalidate();
		
    }
    
    
    
    
}
    
    
    
	