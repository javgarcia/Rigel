package ch.epfl.rigel.gui;

import javafx.animation.AnimationTimer;
import javafx.beans.property.*;

import java.time.ZonedDateTime;

/**
 * Generates a simulated time given a time accelerator.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class TimeAnimator extends AnimationTimer {

    // The initial time of each animation step.
    private long initalTime;
    // Check if the animation is starting, false as soon as the animation has started.
    private boolean isStarting;

    private final DateTimeBean dtb;
    private ZonedDateTime startTime;

    // Accelerator property, initially null.
    private final ObjectProperty<TimeAccelerator> accelerator;
    // True if the animation is being executed.
    private final SimpleBooleanProperty running;


    /**
     * Constructor, takes the initial zoned date time.
     *
     * @param dtb the given zoned date time.
     */
    public TimeAnimator(DateTimeBean dtb) {
        this.dtb = dtb;
        accelerator = new SimpleObjectProperty<>(null);
        running = new SimpleBooleanProperty(false);
    }


    /**
     * Getter for the running property.
     *
     * @return running as an read-only property.
     */
    public ReadOnlyBooleanProperty runningProperty() {
        return running;
    }


    /**
     * Getter for the accelerator property.
     *
     * @return the accelerator property.
     */
    public ObjectProperty<TimeAccelerator> acceleratorProperty() {
        return accelerator;
    }

    /**
     * Getter for the time accelerator.
     *
     * @return the time accelerator.
     */
    public TimeAccelerator getAccelerator() {
        return accelerator.get();
    }


    /**
     * Makes the animation progress using a timer.
     *
     * @param now the elapsed time since an unspecified starting time.
     */
    @Override
    public void handle(long now) {
        if (isStarting) {
            initalTime = now;
            isStarting = false;
        } else {
            dtb.setZonedDateTime(accelerator.get().adjust(startTime, now - initalTime));
        }
    }


    /**
     * Start method, sets the running property at true.
     */
    public void start() {
        isStarting = true;
        startTime = dtb.getZonedDateTime();
        super.start();
        running.set(true);
    }


    /**
     * Stop method, sets the running property at false.
     */
    @Override
    public void stop() {
        super.stop();
        running.set(false);
    }
}
