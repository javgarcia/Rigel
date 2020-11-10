package ch.epfl.rigel;

import ch.epfl.rigel.math.Interval;

/**
 * Tool class providing test methods, potentially throwing exceptions.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class Preconditions {

    /**
     * Private constructor, avoids instantiation.
     */
    private Preconditions() {
    }


    /**
     * Checks for the argument in order to throw an exception.
     *
     * @param isTrue boolean, causes an exception if false.
     * @throws IllegalArgumentException if the argument is false.
     */
    public static void checkArgument(boolean isTrue) {
        if (!isTrue) throw new IllegalArgumentException("Argument not true.");
    }


    /**
     * Method testing if the value is included in the interval.
     *
     * @param interval the tested interval.
     * @param value    the tested value.
     * @return the value.
     * @throws IllegalArgumentException if the value is not contained in the interval.
     */
    public static double checkInInterval(Interval interval, double value) {
        if (interval.contains(value)) return value;
        else throw new IllegalArgumentException("Value not contained in the interval.");
    }

}
