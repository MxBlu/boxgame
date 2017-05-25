import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;


public class LoadingScreen extends JPanel implements ActionListener {
	
	private Image background;
	
	private int screenWidth;
	private int screenHeight;
	private int tileSize;
	private int difficulty;
	
	Timer renderWait;

	public LoadingScreen(int screenWidth, int screenHeight, int tileSize, int difficulty) {
		this.renderWait = new Timer(10, this);
		
		try {
			this.background = ImageIO.read(getClass().getResourceAsStream("loading.png")).getScaledInstance(screenWidth,
					screenHeight, Image.SCALE_DEFAULT);
		} catch (IOException e) { e.printStackTrace();}
		
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.tileSize = tileSize;
		this.difficulty = difficulty;
		
		renderWait.start();
	}
	
	public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(background, 0, 0, null);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		renderWait.stop();
		GameMaster.changeScreens(new Level(screenWidth, screenHeight, tileSize, difficulty));
	}
}
