package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

import java.util.Locale;

/**
 * Interval with closed bounds.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class ClosedInterval extends Interval {

    /**
     * Constructor,takes the lower and upper bounds in argument.
     *
     * @param low  lower bound.
     * @param high upper bound.
     */
    private ClosedInterval(double low, double high) {
        super(low, high);
    }


    /**
     * Generating a closed interval given the lower and upper bounds.
     *
     * @param low  lower bound.
     * @param high upper bound.
     * @return a closed interval.
     * @throws IllegalArgumentException if the lower bound is superior or equal to the upper bound.
     */
    public static ClosedInterval of(double low, double high) {
       Preconditions.checkArgument(low < high);
       return new ClosedInterval(low, high);
    }


    /**
     * Generating a symmetric closed interval given its size.
     *
     * @param size the size of the entire interval.
     * @return a closed interval.
     * @throws IllegalArgumentException if the size is non positive.
     */
    public static ClosedInterval symmetric(double size) {
        Preconditions.checkArgument(size > 0);
        return new ClosedInterval(-(size / 2), size / 2);
    }


    /**
     * Function assigning a value to itself or to one of the bounds.
     *
     * @param v the value.
     * @return the resulting value.
     */
    public double clip(double v) {
        return (v <= low()) ? low() : Math.min(v, high());
    }


    /**
     * Redefinition of the super class.
     *
     * @param v value tested.
     * @return true if the value is contained in the interval.
     */
    @Override
    public boolean contains(double v) {
        return (low() <= v && v <= high());
    }


    /**
     * Redefinition of the toString method.
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%s,%s]", low(), high());
    }

}
