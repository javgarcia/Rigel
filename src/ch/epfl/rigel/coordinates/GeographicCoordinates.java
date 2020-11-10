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
public final class GeographicCoordinates extends SphericalCoordinates {

    //Interval for the latitude, in degrees.
    private final static ClosedInterval LAT_INTERVAL = ClosedInterval.symmetric(180);

    //Interval for the longitude, in degrees.
    private final static RightOpenInterval LON_INTERVAL = RightOpenInterval.symmetric(360);


    /**
     * Constructor, takes the longitude and latitude in argument.
     *
     * @param lon longitude.
     * @param lat latitude.
     */
    private GeographicCoordinates(double lon, double lat) {
        super(lon, lat);
    }


    /**
     * Generating Geographical coordinates given a latitude and a longitude, in degrees.
     *
     * @param lonDeg longitude in degrees.
     * @param latDeg latitude in degrees.
     * @return geographical coordinates.
     * @throws IllegalArgumentException if a value is not contained in the interval.
     */
    public static GeographicCoordinates ofDeg(double lonDeg, double latDeg) {
        double lonRad = Angle.ofDeg(Preconditions.checkInInterval(LON_INTERVAL, lonDeg));
        double latRad = Angle.ofDeg(Preconditions.checkInInterval(LAT_INTERVAL, latDeg));
        return new GeographicCoordinates(lonRad, latRad);
    }


    /**
     * Tests whether the given longitude is valid.
     *
     * @param lonDeg longitude, in degrees.
     * @return true if it is contained in the longitude interval.
     */
    public static boolean isValidLonDeg(double lonDeg) {
        return LON_INTERVAL.contains(lonDeg);
    }


    /**
     * Tests whether the given latitude is valid.
     *
     * @param latDeg latitude, in degrees.
     * @return true if it is contained in the latitude interval.
     */
    public static boolean isValidLatDeg(double latDeg) {
        return LAT_INTERVAL.contains(latDeg);
    }


    /**
     * Getter for the longitude
     *
     * @return the longitude in radians.
     */
    @Override
    public double lon() {
        return super.lon();
    }


    /**
     * Getter for the latitude.
     *
     * @return the latitude in radians.
     */
    @Override
    public double lat() {
        return super.lat();
    }


    /**
     * Getter for the longitude
     *
     * @return the longitude in degrees.
     */
    @Override
    public double lonDeg() {
        return super.lonDeg();
    }


    /**
     * Getter for the latitude.
     *
     * @return the latitude in degrees.
     */
    @Override
    public double latDeg() {
        return super.latDeg();
    }


    /**
     * @return the string.
     * @see Object#toString()
     * String including the right longitude and latitude coordinates, respectively.
     * Approximates on the 4th decimal number.
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(lon=%.4f°, lat=%.4f°)", this.lonDeg(), this.latDeg());
    }

}
