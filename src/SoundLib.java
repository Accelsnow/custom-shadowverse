import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * This enum contains all sound resources used in game. All sounds are processed and played inside this class.
 */
public enum SoundLib {
    /**
     * A random selected bgm when the game starts
     */
    RANDOM_BGM(true),
    /**
     * The music when the game ends
     */
    GAME_END(true),
    /**
     * The music during player preparations
     */
    GAME_PREP(true);

    private static MediaPlayer mediaPlayer;

    static {
        new JFXPanel();
    }

    /**
     * True if the sound is music to be played repeatedly, false if the sound is sound effect and is played once only.
     */
    private boolean isMusic;

    /**
     * Set the sound type
     *
     * @param isMsc true if the sound is music to be played repeatedly, false if the sound is sound effect and is played
     *              once only
     */
    SoundLib(boolean isMsc) {
        isMusic = isMsc;
    }

    /**
     * Play current sound
     */
    public void playSound() {
        StringBuilder fileString = new StringBuilder("sounds/");

        if (mediaPlayer != null && isMusic)
            mediaPlayer.dispose();

        switch (this) {
            case RANDOM_BGM:
                fileString.append("bgm").append((int) (Math.random() * 4 + 1)).append(".wav");
                break;

            case GAME_END:
                fileString.append("end.wav");
                break;

            case GAME_PREP:
                fileString.append("prep.wav");
                break;
        }

        Media sound = new Media(SoundLib.class.getResource(fileString.toString()).toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));
        mediaPlayer.play();
    }
}
