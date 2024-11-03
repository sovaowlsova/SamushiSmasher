package SamushiGame;
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.*;

public class AudioPlayer {

    AudioInputStream audioInputStream;
    Clip clip;

    public AudioPlayer(String audioPath) {
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream(audioPath);
            InputStream buffered = new BufferedInputStream(in);
            audioInputStream = AudioSystem.getAudioInputStream(buffered);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            buffered.close();
        } catch (Exception e) {
            System.out.println("Couldn't find the sound " + audioPath);
            throw new RuntimeException(e);
        }
    }

    public void play() {
        clip.start();
    }
    public void stop() { clip.stop(); }
}
