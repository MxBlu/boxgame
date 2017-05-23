import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioManager  {
	
	private HashMap<String, Clip> soundClips;
	private Clip currentClip;
	
	public AudioManager() {
		soundClips = new HashMap<String, Clip>();
		currentClip = null;
	}
	
	public boolean addSound(String filePath) {
		return addSound(filePath, filePath);
	}
	
	/*
	 * Loads a sound file and associates a name (soundName) with it
	 */
	public boolean addSound(String filePath, String soundName) {
		try {
			File soundFile = new File(filePath);
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(soundFile));
			soundClips.put(soundName, clip);
			return true;
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/*
	 * Plays a sound file that's already been loaded
	 */
	public void playSound(String soundName, float volume) {
		//Get the sound clip if it's already loaded
		Clip clip = soundClips.get(soundName);
		
		/*
		 * On the off chance someone messes up and doesn't load the audio file before
		 * trying to play it, we fall back and see if soundName is also the name of the
		 * file and try to load that
		 */
		if (clip == null) {
			//Try to add the sound to the hashmap
			if (addSound(soundName)) {
				clip = soundClips.get(soundName);
			} else {
				//soundName is not a valid filename, just return
				return;
			}
		}
		
		// Stop what's currently playing
		if (currentClip != null)
			currentClip.stop();
		currentClip = clip;
		
		//Set the volume of the clip
		FloatControl gainCon = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainCon.setValue(20.0f * (float) Math.log10(volume));
		
		//Reset the clip for the case it was previously running
		clip.stop();
		clip.setFramePosition(0);
		
		clip.start();
	}
	
	public void stopSound() {
		if (currentClip != null)
			currentClip.stop();
		
		currentClip = null;
	}
	
}
