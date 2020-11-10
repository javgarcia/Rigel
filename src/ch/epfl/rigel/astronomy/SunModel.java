package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

import static java.lang.Math.*;

/**
 * Describes the Sun's positions.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public enum SunModel implements CelestialObjectModel<Sun> {
    SUN;

    private final static double SUN_LONGITUDE_J2010_RAD = Angle.ofDeg(279.557208);
    private final static double SUN_LONGITUDE_PERIGEE_RAD = Angle.ofDeg(283.112438);
    private final static double ECCENTRICITY = 0.016705;
    private final static double ANGULAR_SPEED = (Angle.TAU / 365.242191);
    private final static double SEMI_MAJOR_AXIS_ANGULAR_DIAMETER = Angle.ofDeg(0.533128);


    /**
     * Returns the Sun model, i.e positions in equatorial coordinates
     * at a given day since J2010 and the given ecliptic coordinates.
     *
     * @param daysSinceJ2010                 the given number of days after J2010.
     * @param eclipticToEquatorialConversion the conversion system between ecliptic and equatorial coordinates.
     * @return an instance of the Sun.
     */
    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        double meanAnomaly = ANGULAR_SPEED * daysSinceJ2010 + SUN_LONGITUDE_J2010_RAD - SUN_LONGITUDE_PERIGEE_RAD;
        double trueAnomaly = meanAnomaly + 2 * ECCENTRICITY * sin(meanAnomaly);
        double lambda = trueAnomaly + SUN_LONGITUDE_PERIGEE_RAD;

        //We use normalizePositive in order to prevent an exception due to a wrong interval.
        EclipticCoordinates ecl = EclipticCoordinates.of(Angle.normalizePositive(lambda), 0);

        float angularSize = (float) (SEMI_MAJOR_AXIS_ANGULAR_DIAMETER * ((1 + ECCENTRICITY * cos(trueAnomaly)) / (1 - ECCENTRICITY * ECCENTRICITY)));

        return new Sun(ecl, eclipticToEquatorialConversion.apply(ecl), angularSize, (float) meanAnomaly);
    }

}
