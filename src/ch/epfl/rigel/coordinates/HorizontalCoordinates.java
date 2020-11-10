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
public final class HorizontalCoordinates extends SphericalCoordinates {

    //Interval for the azimuth, in degrees.
    private final static RightOpenInterval AZ_INTERVAL_DEG = RightOpenInterval.of(0, 360);

    //Interval for the altitude, in degrees.
    private final static ClosedInterval ALT_INTERVAL_DEG = ClosedInterval.symmetric(180);

    //Interval for the azimuth, in radians.
    private final static RightOpenInterval AZ_INTERVAL_RAD = RightOpenInterval.of(0, Angle.TAU);

    //Interval for the altitude, in radians.
    private final static ClosedInterval ALT_INTERVAL_RAD = ClosedInterval.symmetric(Math.PI);


    /**
     * Constructor, takes the azimuth and altitude in argument.
     *
     * @param az  azimuth, in radians.
     * @param alt altitude, in radians.
     */
    private HorizontalCoordinates(double az, double alt) {
        super(az, alt);
    }


    /**
     * Generating horizontal coordinates given an azimuth and an altitude, in radians.
     *
     * @param az  azimuth, in radians.
     * @param alt altitude, in radians.
     * @return horizontal coordinates, or throws an exception if one of the input values is invalid.
     */
    public static HorizontalCoordinates of(double az, double alt) {
        return new HorizontalCoordinates(
                        Preconditions.checkInInterval(AZ_INTERVAL_RAD, az),
                        Preconditions.checkInInterval(ALT_INTERVAL_RAD, alt));
    }


    /**
     * Generating horizontal coordinates given an azimut and an altitude, in degrees.
     *
     * @param azDeg  azimuth, in degrees.
     * @param altDeg altitude, in degrees.
     * @return horizontal coordinates, or throws an exception if one of the input values is invalid.
     * @throws IllegalArgumentException if a value is not contained in the interval.
     */
    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) {
        double azRad = Angle.ofDeg(Preconditions.checkInInterval(AZ_INTERVAL_DEG, azDeg));
        double altRad = Angle.ofDeg(Preconditions.checkInInterval(ALT_INTERVAL_DEG, altDeg));

        return new HorizontalCoordinates(azRad, altRad);
    }


    /**
     * Getter for the azimuth.
     *
     * @return the azimuth in radians.
     */
    public double az() {
        return super.lon();
    }


    /**
     * Getter for the azimuth.
     *
     * @return the azimuth in degrees.
     */
    public double azDeg() {
        return super.lonDeg();
    }


    /**
     * Getter for the altitude.
     *
     * @return altitude in radians.
     */
    public double alt() {
        return super.lat();
    }


    /**
     * Getter for the altitude.
     *
     * @return altitude in degrees.
     */
    public double altDeg() {
        return super.latDeg();
    }


    /**
     * Computes and output the octant corresponding to the azimut value.
     *
     * @param n North cardinal point string.
     * @param e East cardinal point string.
     * @param s South cardinal point string.
     * @param w West cardinal point string.
     * @return a String, the octant name.
     */
    public String azOctantName(String n, String e, String s, String w) {

        // The octant number is computed using a shifted modulo 8.
        int octantNumber = (int) (Math.floor((azDeg() + 22.5) / 45)) % 8;

        switch (octantNumber) {
            case 0:
                return n;
            case 1:
                return n+e;
            case 2:
                return e;
            case 3:
                return s + e;
            case 4:
                return s;
            case 5:
                return s + w;
            case 6:
                return w;
            case 7:
                return n + w;
            default:
                return "invalid";
        }

    }


    /**
     * Computes the angular distance between the receiver and the given point in argument.
     *
     * @param that the given coordinates.
     * @return the angular distance, in radians.
     */
    public double angularDistanceTo(HorizontalCoordinates that) {
        return Math.acos(Math.sin(this.alt()) * Math.sin(that.alt())
                         + Math.cos(this.alt()) * Math.cos(that.alt()) * Math.cos(this.az()-that.az()));
    }


    /**
     * @return the string.
     * @see Object#toString()
     * String including the azimuth and altitude coordinates, respectively.
     * Approximates on the 4th decimal number.
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(az=%.4f°, alt=%.4f°)", this.azDeg(), this.altDeg());
    }

}
