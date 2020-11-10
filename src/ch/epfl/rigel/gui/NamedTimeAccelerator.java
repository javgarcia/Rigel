package ch.epfl.rigel.gui;

import java.time.Duration;

/**
 * Represents a named accelerator, i.e a pair (name, accelerator)
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public enum NamedTimeAccelerator {

    TIMES_1("1x", TimeAccelerator.continuous(1)),
    TIMES_30("30x", TimeAccelerator.continuous(30)),
    TIMES_300("300x", TimeAccelerator.continuous(300)),
    TIMES_3000("3000x", TimeAccelerator.continuous(3000)),
    DAY("jour", TimeAccelerator.discrete(60, Duration.parse("PT24H"))),
    SIDEREAL_DAY("jour sidéral", TimeAccelerator.discrete(60, Duration.parse("PT23H56M4S")));

    private final String display;
    private final TimeAccelerator timeAccelerator;


    /**
     * Constructor for the enum.
     *
     * @param display         the accelerator name.
     * @param timeAccelerator the given time accelerator.
     */
    NamedTimeAccelerator(String display, TimeAccelerator timeAccelerator) {
        this.display = display;
        this.timeAccelerator = timeAccelerator;
    }


    /**
     * Getter for the accelerator name.
     *
     * @return the accelerator name.
     */
    public String getName() {
        return display;
    }


    /**
     * Getter for the time accelerator.
     *
     * @return the time accelerator.
     */
    public TimeAccelerator getAccelerator() {
        return timeAccelerator;
    }


    /**
     * String including the name of the pair.
     *
     * @return the string.
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return display;
    }

}
