import static org.junit.Assert.*;

import org.junit.Test;

public class LevelTest {

	@Test
	public void testLevel() {
		Level l = new Level(1240, 680, 20, new LevelGenBlot());
		System.out.println(l.toString());
	}
	
	@Test
	public void testLevelString() {
		String s = "00000000\n"
				 + "01111000\n"
				 + "00011000\n"
				 + "00011000\n"
				 + "00000000\n";
		
		Level l = new Level(s, 20);
		System.out.println(l.toString());
	}

}
