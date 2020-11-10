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
public final class EclipticCoordinates extends SphericalCoordinates {

    private final static RightOpenInterval LON_INTERVAL_RAD = RightOpenInterval.of(0, Angle.TAU);
    private final static ClosedInterval LAT_INTERVAL_RAD = ClosedInterval.symmetric(Math.PI);


    /**
     * Constructor, takes the ecliptic longitude and ecliptic latitude in argument.
     *
     * @param lon longitude
     * @param lat latitude
     */
    private EclipticCoordinates(double lon, double lat) {
        super(lon, lat);
    }


    /**
     * Generating ecliptic coordinates.
     *
     * @param lon longitude in radians.
     * @param lat latitude, in radians.
     * @return ecliptic coordinates, or throws an exception if one of the values is invalid.
     * @throws IllegalArgumentException if a value is not contained in the interval.
     */
    public static EclipticCoordinates of(double lon, double lat) {
        return new EclipticCoordinates(
                        Preconditions.checkInInterval(LON_INTERVAL_RAD, lon),
                        Preconditions.checkInInterval(LAT_INTERVAL_RAD, lat));
    }


    /**
     * Getter for the longitude.
     *
     * @return the longitude in radians.
     */
    public double lon() {
        return super.lon();
    }


    /**
     * Getter for the longitude.
     *
     * @return the longitude in degrees.
     */
    public double lonDeg() {
        return super.lonDeg();
    }


    /**
     * Getter for the latitude.
     *
     * @return the latitude in radians.
     */
    public double lat() {
        return super.lat();
    }


    /**
     * Getter for the latitude.
     *
     * @return the latitude in degrees.
     */
    public double latDeg() {
        return super.latDeg();
    }


    /**
     * @return the string.
     * @see Object#toString()
     * String including the ecliptic longitude and ecliptic latitude coordinates, respectively.
     * Approximates on the 4th decimal number.
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(λ=%.4f°, β=%.4f°)", this.lonDeg(), this.latDeg());
    }

}
