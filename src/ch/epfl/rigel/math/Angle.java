package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

/**
 * Class providing constants and methods in order to work with angles.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class Angle {

    public final static double TAU = 2 * Math.PI;

    private final static double HOUR_PER_RAD = 24.0 / TAU;
    private final static double RAD_PER_HOUR = TAU / 24.0;

    private final static double RAD_PER_ARCSEC = TAU / (360.0 * 3600.0);

    private final static double SEC_PER_MIN = 60;
    private final static double MIN_PER_DEG = 60;

    private final static RightOpenInterval ZERO_TAU_RIGHT_OPEN_INTERVAL = RightOpenInterval.of(0, TAU);
    private final static RightOpenInterval ZERO_SIXTY_RIGHT_OPEN_INTERVAL = RightOpenInterval.of(0, 60);


    /**
     * Private constructor, avoids instantiation.
     */
    private Angle(){}


    /**
     * Converts an angle in radians into an equivalent one in [0, τ[
     *
     * @param rad angle in radians.
     * @return the equivalent angle in radians.
     */
    public static double normalizePositive(double rad) {
        return ZERO_TAU_RIGHT_OPEN_INTERVAL.reduce(rad);
    }


    /**
     * Converts an angle in arc seconds into radians.
     *
     * @param sec angle in arc seconds.
     * @return the angle in radians.
     */
    public static double ofArcsec(double sec) {
        return sec * RAD_PER_ARCSEC;
    }


    /**
     * If the minutes or seconds of an angle are equal or greater than 60, then it throws and exception.
     * If not, then it converts the angle in degrees, arc minutes and arc seconds into radians.
     *
     * @param deg degrees part of the expression of the angle in degrees.
     * @param min arc minutes part of the expression of the angle in degrees.
     * @param sec arc seconds part of the expression of the angle in degrees.
     * @return the angle in radians
     * @throws IllegalArgumentException if the seconds and minutes are in an invalid interval (i.e not [0,60)).
     *                                  or if the degree value is negative.
     */
    public static double ofDMS(int deg, int min, double sec) {
        Preconditions.checkArgument(deg >= 0);

        double totalSecs = Preconditions.checkInInterval(ZERO_SIXTY_RIGHT_OPEN_INTERVAL, sec);
        totalSecs += Preconditions.checkInInterval(ZERO_SIXTY_RIGHT_OPEN_INTERVAL, min) * SEC_PER_MIN;
        totalSecs += deg * MIN_PER_DEG * SEC_PER_MIN;

        return totalSecs * RAD_PER_ARCSEC;
    }


    /**
     * Converts the angle expressed in degrees into radians.
     *
     * @param deg the angle in degrees.
     * @return the angle in radians.
     */
    public static double ofDeg(double deg) {
        return Math.toRadians(deg);
    }


    /**
     * Converts the angle expressed in radians into degrees.
     *
     * @param rad the angle in radians.
     * @return the angle in degrees.
     */
    public static double toDeg(double rad) {
        return Math.toDegrees(rad);
    }


    /**
     * Converts the angle expressed in hours into radians.
     *
     * @param hr the angle in hours.
     * @return the angle in radians.
     */
    public static double ofHr(double hr) {
        return hr * RAD_PER_HOUR;
    }


    /**
     * Converts the angle expressed in radians into hours.
     *
     * @param rad the angle in radians.
     * @return the angle in hours.
     */
    public static double toHr(double rad) {
        return rad * HOUR_PER_RAD;
    }

}
