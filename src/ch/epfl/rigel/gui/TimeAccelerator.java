package ch.epfl.rigel.gui;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Function that computes the simulated time given the real time.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
@FunctionalInterface
public interface TimeAccelerator {

    double NANOS_TO_SECONDS = 1e-9;


    /**
     * Functional method, computes the new zoned date time based on the previous one and the real elapsed time, i.e the difference
     * between two instants.
     *
     * @param initSimulatedTime the initial simulated zoned date time.
     * @param realElapsedTime   the difference between the current and initial real time, in nanoseconds.
     * @return a new zoned date time.
     */
    ZonedDateTime adjust(ZonedDateTime initSimulatedTime, long realElapsedTime);


    /**
     * Definition of a continuous time accelerator, accelerates the real time with a factor.
     *
     * @param accFactor the acceleration factor.
     * @return the computed zoned date time.
     */
    static TimeAccelerator continuous(int accFactor) {
        return (initZDT, realET) -> initZDT.plusNanos(accFactor * realET);
    }


    /**
     * Definition of a discrete time accelerator, accelerates the real time with discrete steps.
     *
     * @param frequency the number of steps.
     * @param step      duration of each step.
     * @return the computed zoned date time.
     */
    static TimeAccelerator discrete(int frequency, Duration step) {
        return (initZDT, realET) -> initZDT.plus(step.multipliedBy((long) (frequency * realET * NANOS_TO_SECONDS)));
    }
}
