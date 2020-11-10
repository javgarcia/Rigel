package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

import java.util.Locale;

/**
 * Interval with closed left bound and open right bound.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class RightOpenInterval extends Interval {

    /**
     * Constructor,takes the lower and upper bounds in argument.
     *
     * @param low  lower bound.
     * @param high upper bound.
     */
    private RightOpenInterval(double low, double high) {
        super(low, high);
    }


    /**
     * Generating a right open interval given the lower and upper bounds.
     *
     * @param low  lower bound.
     * @param high upper bound.
     * @return a right open interval.
     * @throws IllegalArgumentException if the lower bound is superior or equal to the upper bound.
     */
    public static RightOpenInterval of(double low, double high) {
       Preconditions.checkArgument(low < high);
            return new RightOpenInterval(low, high);
    }


    /**
     * Generating a symmetric right open interval given its size.
     *
     * @param size the size of the entire interval.
     * @return a right open interval.
     * @throws IllegalArgumentException if the size is non positive.
     */
    public static RightOpenInterval symmetric(double size) {
        Preconditions.checkArgument(size > 0);
        return new RightOpenInterval(-size / 2, size / 2);
    }


    /**
     * Reduces the given value into the equivalent one included in the interval.
     *
     * @param v the given value.
     * @return the equivalent value in the interval.
     */
    public double reduce(double v) {
        double floor = Math.floor((v - low()) / (high() - low()));

        return v - (high() - low()) * floor;
    }


    /**
     * Redefinition of the super class.
     *
     * @param v value tested
     * @return true if the value is contained in the interval.
     */
    @Override
    public boolean contains(double v) {
        return (low() <= v && v < high());
    }


    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%s,%s[", low(), high());
    }

}
