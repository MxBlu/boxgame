import java.awt.Graphics2D;

public interface  GameState {	
	public abstract void init();
	public abstract void update();//KeyInput input
	public abstract void draw(Graphics2D g);
	public abstract void handleInput();
}
