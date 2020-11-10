package ch.epfl.rigel.sound;

import ch.epfl.rigel.Preconditions;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * Manager for the different sounds and musics in the program.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class SkySoundManager {

    // Notification sound clip.
    private final static AudioClip NOTIF_PLAYER = new AudioClip(initMediaPath("/notif.mp3"));

    // Home screen music media player.
    private final static MediaPlayer WELCOME_PLAYER = new MediaPlayer(new Media(initMediaPath("/welcome_screen.mp3")));

    // Ambient music media player.
    private final static MediaPlayer AMBIENT_PLAYER = new MediaPlayer(new Media(initMediaPath("/ambient.mp3")));

    // Volume properties.
    private final static DoubleProperty AMBIENT_VOLUME_PROPERTY = AMBIENT_PLAYER.volumeProperty();
    private final static DoubleProperty NOTIF_VOLUME_PROPERTY = NOTIF_PLAYER.volumeProperty();
    private final DoubleProperty HOME_SCREEN_VOLUME_PROPERTY = WELCOME_PLAYER.volumeProperty();


    /**
     * Constructor of the class, initializes the volumes.
     */
    public SkySoundManager() {
        init();
        setNotifVolume(0.4);
        setHomeScreenVolume(0.3);
        setAmbientVolume(0.3);
    }


    /**
     * Getter for the ambient music volume property.
     *
     * @return the ambient volume property.
     */
    public DoubleProperty getAmbientVolumeProperty() {
        return AMBIENT_VOLUME_PROPERTY;
    }


    /**
     * Setter for the ambient music volume.
     *
     * @param value new volume value.
     */
    public void setAmbientVolume(double value) {
        Preconditions.checkArgument(value < 1 && value >= 0);
        AMBIENT_VOLUME_PROPERTY.setValue(value);
    }


    /**
     * Getter for the notification sound volume property.
     *
     * @return the notification sound volume property.
     */
    public DoubleProperty getNotifVolumeProperty() {
        return NOTIF_VOLUME_PROPERTY;
    }


    /**
     * Setter for the notification sound volume.
     *
     * @param value new volume value.
     */
    public void setNotifVolume(double value) {
        Preconditions.checkArgument(value < 1 && value >= 0);
        NOTIF_VOLUME_PROPERTY.setValue(value);
    }


    /**
     * Getter for the home screen music volume property.
     *
     * @return the home screen volume property.
     */
    public DoubleProperty getHomeScreemVolumeProperty() {
        return HOME_SCREEN_VOLUME_PROPERTY;
    }


    /**
     * Setter for the home screen volume.
     *
     * @param value new volume value.
     */
    public void setHomeScreenVolume(double value) {
        Preconditions.checkArgument(value < 1 && value >= 0);
        HOME_SCREEN_VOLUME_PROPERTY.setValue(value);
    }


    /**
     * Launches a notification sound.
     *
     * @param windowHasLaunched check if the information windows has already been launched.
     */
    public void launchNotifSound(boolean windowHasLaunched) {
        if (!windowHasLaunched) NOTIF_PLAYER.play();

    }


    /**
     * Initializes the notification sound at the beginning of the program, avoiding lags.
     */
    private void init() {
        NOTIF_PLAYER.setVolume(0);
        NOTIF_PLAYER.play();
    }


    /**
     * Launches the home screen music.
     */
    public void launchHomeScreenMusic() {
        // Setting up an infinite loop for the sound.
        WELCOME_PLAYER.setOnEndOfMedia(() -> WELCOME_PLAYER.seek(Duration.ZERO));
        WELCOME_PLAYER.play();
    }


    /**
     * Implementation of a fade out for the home screen music.
     */
    public void endHomeMusic() {
        Timeline musicFadeOut = new Timeline(
                new KeyFrame(Duration.seconds(4),
                        new KeyValue(WELCOME_PLAYER.volumeProperty(), 0)));
        musicFadeOut.play();
        musicFadeOut.setOnFinished(event -> WELCOME_PLAYER.stop());

        // As soon as the home screen music has stopped, the ambient music starts.
        WELCOME_PLAYER.setOnStopped(this::launchAmbientMusic);
    }


    /**
     * Launches the ambient music.
     */
    private void launchAmbientMusic() {
        if (!(WELCOME_PLAYER.getStatus() == MediaPlayer.Status.PLAYING)) {
            AMBIENT_PLAYER.setOnEndOfMedia(() -> AMBIENT_PLAYER.seek(Duration.ZERO));
            AMBIENT_PLAYER.play();
        }
    }


    /**
     * Loading a sound given the resource name.
     *
     * @param music the music name.
     * @return the string of the URL associated to the music name.
     */
    private static String initMediaPath(String music) {
        return SkySoundManager.class.getResource(music).toExternalForm();
    }
}