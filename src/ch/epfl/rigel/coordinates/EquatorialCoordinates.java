package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

/**
 * Specific spherical coordinate system.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class EquatorialCoordinates extends SphericalCoordinates {

    private final static RightOpenInterval RA_INTERVAL_RAD = RightOpenInterval.of(0, Angle.TAU);
    private final static ClosedInterval DEC_INTERVAL_RAD = ClosedInterval.symmetric(Math.PI);


    /**
     * Constructor, takes the right ascension and declination in argument.
     *
     * @param ra  right ascension.
     * @param dec declination.
     */
    private EquatorialCoordinates(double ra, double dec) {
        super(ra, dec);
    }


    /**
     * Generating equatorial coordinates given the right ascension and the declination, in radians.
     *
     * @param ra  right ascension, must be in the right interval.
     * @param dec declination, must be in the right interval.
     * @return equatorial coordinates, or an exception if the values are invalids.
     * @throws IllegalArgumentException if a value is not contained in the interval.
     */
    public static EquatorialCoordinates of(double ra, double dec) {
        return new EquatorialCoordinates(
                        Preconditions.checkInInterval(RA_INTERVAL_RAD, ra),
                        Preconditions.checkInInterval(DEC_INTERVAL_RAD, dec));
    }


    /**
     * Getter for the right ascension.
     *
     * @return the right ascension in radians.
     */
    public double ra() {
        return super.lon();
    }


    /**
     * Getter for the right ascension.
     *
     * @return the right ascension in degrees.
     */
    public double raDeg() {
        return super.lonDeg();
    }


    /**
     * Getter for the right ascension.
     *
     * @return the right ascension in hours.
     */
    public double raHr() {
        return Angle.toHr(this.ra());
    }


    /**
     * Getter for the declination.
     *
     * @return the declination in radians.
     */
    public double dec() {
        return super.lat();
    }


    /**
     * Getter for the declination.
     *
     * @return the declination in degrees.
     */
    public double decDeg() {
        return super.latDeg();
    }


    /**
     * @return the string.
     * @see Object#toString()
     * String including the right ascension and declination coordinates, respectively.
     * Approximates on the 4th decimal number.
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(ra=%.4fh, dec=%.4f°)", this.raHr(), this.decDeg());
    }

}
