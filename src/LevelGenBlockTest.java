import static org.junit.Assert.*;

import org.junit.Test;

public class LevelGenBlockTest {

	@Test
	public void testGenerate() {
		Level l = new Level(60 * 9, 60 * 9, 60, new LevelGenBlock());
		System.out.println(l);
	}

}
