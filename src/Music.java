import javazoom.jl.player.Player;
import java.io.InputStream;

public class Music {
    private final String filePath;
    private InputStream inputStream;
    private Player player;
    private Thread playbackThread;

    public Music(String filePath, Game game) {
        this.filePath = filePath;
        inputStream = getClass().getResourceAsStream(filePath);
    }

    public void play() {
        stop();

        playbackThread = new Thread(() -> {
            try {
                do {
                    inputStream = getClass().getResourceAsStream(filePath);
                    player = new Player(inputStream);
                    player.play();
                } while (player.isComplete());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        playbackThread.setDaemon(true);
        playbackThread.start();
    }

    public void stop() {
        if (player != null) {
            player.close();
            player = null;
        }

        if (playbackThread != null) {
            playbackThread.interrupt();
            playbackThread = null;
        }
    }
}
