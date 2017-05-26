import java.awt.Cursor;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GameMaster{
    
    // The window and game dimensions
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public static final int PANEL_HEIGHT = 691;
    
    // Set the frames per second
    public static final int FPS = 60;
    public static final int FRAME_DELTA = 1000/FPS;
   
    //private static AudioManager audio;
    //private static Boolean audioPlaying;
	
    // JFrame which holds all of the game's
    // JPanels and their contents. 
    private JFrame frame;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() { 
            	GameMaster gameMaster = new GameMaster();
                // Initialize the JFrames attributes
            	gameMaster.initScreen();               
            }
        });  
    }
    
    private void initScreen(){
    	//audio = new AudioManager();
    	//audioPlaying = false;    	
    	
        // Initialize the settings and attributes
        // for our JFrame. This class, GameMaster
        // acts as the JFrame. We initialize its
        // size, and its visibility. 
        frame = new JFrame("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setFocusable(true);
        setCursorPointer();
        //Need to adjust the width/height so it includes the window border
        int widthWithBorder = WIDTH  + (frame.getWidth()  - frame.getContentPane().getWidth()); 
        int heightWithBorder = HEIGHT + (frame.getHeight() - frame.getContentPane().getHeight());
        frame.setSize(widthWithBorder, heightWithBorder);
        
        
        /*panel = new JPanel(new CardLayout());
        frame.add(panel);*/
        
        // Add a boot up screen/JPanel to this
        // JFrame
        frame.getContentPane().add(new BootScreen(frame));
    }
    
    public static void changeScreens(JFrame frame, JPanel newScreen) {
        // This function is used to change between
        // screens. For our implementation, each
        // screen is stored as a JPanel. This
        // function removes all the JPanels in the
        // main JFrame, adds a new screen/JPanel 
        // to it, and then repaints and revalidates. 
	    frame.getContentPane().removeAll();
	    frame.getContentPane().add(newScreen);
	    frame.repaint();
	    frame.revalidate();
        
    }
    
    /*public void playMusic(){
    	 audio.playSound("song.wav", 1.0f);
    }
    
    public boolean isPlaying(){
    	return audioPlaying;
    }*/
 
    public void setCursorPointer(){
    	Toolkit toolkit = Toolkit.getDefaultToolkit();
     	Point cursorHotSpot = new Point(0,0);
     	Cursor customCursor= null;
     	
     	try {
			customCursor = toolkit.createCustomCursor(ImageIO.read(GameMaster.class.getClassLoader().getResourceAsStream("pointer.png")), cursorHotSpot, "Cursor");
			frame.setCursor(customCursor);    
		} catch (HeadlessException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}        	
    }

}
    
    
    
    