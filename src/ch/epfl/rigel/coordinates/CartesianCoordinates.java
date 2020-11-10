package ch.epfl.rigel.coordinates;

import javafx.geometry.Point2D;

import java.util.Locale;

/**
 * Cartesian coordinates system.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class CartesianCoordinates {

    private final double abscissa;
    private final double ordinate;


    /**
     * Assigns the given abscissa and ordinate to the private attributes by the of method.
     *
     * @param abs the abscissa coordinate
     * @param ord the ordinate coordinate
     */
    private CartesianCoordinates(double abs, double ord) {
        abscissa = abs;
        ordinate = ord;
    }


    /**
     * Creates a cartesian coordinates system by using the constructor.
     *
     * @param x the abscissa coordinate
     * @param y the ordinate coordinate
     * @return new cartesian coordinates given the x and y values.
     */
    public static CartesianCoordinates of(double x, double y) {
        return new CartesianCoordinates(x, y);
    }

    /**
     * Creates a cartesian coordinates system given a 2D geometric point.
     * @param point the given 2D geometric point.
     * @return cartesian coordinates.
     */
    public static CartesianCoordinates of(Point2D point){
        return new CartesianCoordinates(point.getX(), point.getY());
    }


    /**
     * Gives the abscissa coordinate of the cartesian coordinates system.
     *
     * @return the abscissa coordinate
     */
    public double x() {
        return abscissa;
    }


    /**
     * Gives the ordinate coordinate of the cartesian coordinates system.
     *
     * @return the ordinate coordinate
     */
    public double y() {
        return ordinate;
    }


    /**
     * Given cartesian coordinates, computes the distance between them and the current coordinates.
     * It does not use the square root, given that the method is only used to compare between
     * distances computed with the same performance-enhancing method.
     *
     * @param coords the given cartesian coordinates.

     * @return the squared distance.
     */
    public double distanceSquared(CartesianCoordinates coords) {
        return Math.pow(coords.x() - this.x(), 2) + Math.pow(coords.y() - this.y(), 2);

    }

    /**
     * String including the abscissa and the ordinate coordinates, respectively.
     * Approximates on the 4th decimal number.
     *
     * @return the string.
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(abs=%.4f, ord=%.4f)", abscissa, ordinate);
    }


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
