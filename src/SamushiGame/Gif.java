package SamushiGame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Gif {
    private final List<Icon> frames = new ArrayList<>();

    public List<Icon> getFrames() {
        return new ArrayList<>(frames);
    }

    public Gif(String gifFolder, String fileExtension) {
        System.out.println();
        int i = 0;
        while (true) {
            try {
                Icon ico = new ImageIcon(getClass().getClassLoader().getResource(gifFolder + "/Frame" + i + "." + fileExtension));
                frames.add(ico);
            } catch (NullPointerException e) {
                break;
            }
            i++;
        }
        /*int numOfFrames = new File(getClass().getClassLoader().getResource(gifFolder).getFile()).list().length;
        for (int i = 0; i < numOfFrames; i++) {
           Icon ico = new ImageIcon(getClass().getClassLoader().getResource(gifFolder + "/Frame" + String.valueOf(i) + "." + fileExtension));
           frames.add(ico);
        }*/
    }
}

class GifThread extends Thread {
    private final List<Icon> frames;
    private final JLabel label;
    private final int delay;
    private final int executionTime;

    public int getExecutionTime() {
        return executionTime;
    }

    public GifThread(Gif gifToPlay, JLabel label, int delay) {
        this.frames = gifToPlay.getFrames();
        this.label = label;
        this.delay = Math.abs(delay);
        this.executionTime = this.delay * frames.size();
        if (delay < 0) {
            Collections.reverse(this.frames);
        }
    }

    public void run() {
        for (Icon frame : frames) {
            try {
                Thread.sleep(delay);
                label.setIcon(frame);
            } catch (InterruptedException e) {
                System.out.println("Gif thread is somehow interrupted");
                //System.out.println("You should kill yourself now!");
                throw new RuntimeException(e);
            }
        }
    }
}
