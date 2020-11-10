package ch.epfl.rigel.math;

/**
 * Interval, characterized by its lower and upper bounds.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public abstract class Interval {

    private final double lowerBound;
    private final double upperBound;
    private final double size;


    /**
     * Interval constructor, takes the lower and upper bounds in argument.
     *
     * @param lowerBound lower bound.
     * @param upperBound upper bound.
     */
    protected Interval(double lowerBound, double upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        size = upperBound - lowerBound;
    }


    /**
     * Getter for the lower bound.
     *
     * @return the lower bound.
     */
    public final double low() {
        return lowerBound;
    }


    /**
     * Getter for the upper bound.
     *
     * @return the upper bound.
     */
    public final double high() {
        return upperBound;
    }


    /**
     * Getter for the interval's size.
     *
     * @return the size of the interval.
     */
    public final double size() {
        return size;
    }


    /**
     * Abstract method, determines whether a value is contained in the interval or not.
     *
     * @param v the value tested.
     * @return a boolean, true if v is contained in the interval.
     */
    abstract public boolean contains(double v);


    /**
     * @return throws an exception.
     * @throws UnsupportedOperationException if the method is called.
     * @see Object#equals(Object)
     */
    @Override
    public final boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return throws an exception.
     * @throws UnsupportedOperationException if the method is called.
     * @see Object#hashCode()
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }
}
