import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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

    // Here for debugging reasons only
    public static float adj1Chance = 0.40f;
    public static float adj2Chance = 0.20f;
    public static float adj3Chance = 0.50f;
    public static float adj4Chance = 0.60f;
    private static Image cursor;
    private static Image hover;
   
    private static AudioManager audio;
    private static Boolean audioPlaying;
    // JFrame which holds all of the game's
    // JPanels and their contents. 
    private static JFrame frame;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() { 
                // Initialize the JFrames attributes
                initScreen();
                // Add a boot up screen/JPanel to this
                // JFrame
                frame.getContentPane().add(new BootScreen());
                
            }
        });  
    }
    
    private static void initScreen(){
    	audio = new AudioManager();
    	audioPlaying = false;
       
    	
    	
    	try {
			cursor = ImageIO.read(GameMaster.class.getClassLoader().getResourceAsStream("pointer.png"));
			hover = ImageIO.read(GameMaster.class.getClassLoader().getResourceAsStream("hand.png"));

		}
		catch(Exception e) {
			e.printStackTrace();
		}	
    	
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
        toggleCursorPointer();
        //Need to adjust the width/height so it includes the window border
        int widthWithBorder = WIDTH  + (frame.getWidth()  - frame.getContentPane().getWidth()); 
        int heightWithBorder = HEIGHT + (frame.getHeight() - frame.getContentPane().getHeight());
        frame.setSize(widthWithBorder, heightWithBorder);
        
        
        /*panel = new JPanel(new CardLayout());
        frame.add(panel);*/
    }
    
    public static void changeScreens(JPanel newScreen) {
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
    
    public static void playMusic(){
    	 audio.playSound("song.wav", 1.0f);
    }
    
    public static boolean isPlaying(){
    	return audioPlaying;
    }
    
    public static void toggleCursorHover(){
    	
    	Toolkit toolkit = Toolkit.getDefaultToolkit();
     	Point cursorHotSpot = new Point(0,0);
     	Cursor customCursor= null;
     
     	customCursor = toolkit.createCustomCursor(hover, cursorHotSpot, "Cursor");
     		
        frame.setCursor(customCursor);
 
    	
    }
    
    public static void toggleCursorPointer(){
    	
    	Toolkit toolkit = Toolkit.getDefaultToolkit();
     	Point cursorHotSpot = new Point(0,0);
     	Cursor customCursor= null;
     	
     	customCursor = toolkit.createCustomCursor(cursor, cursorHotSpot, "Cursor");
   
        frame.setCursor(customCursor);
 
    	
    }
    
   
    
}
    
    
    
    