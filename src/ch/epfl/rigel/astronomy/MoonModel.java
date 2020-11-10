package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

import static java.lang.Math.*;

/**
 * Describes the Moon positions.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public enum MoonModel implements CelestialObjectModel<Moon> {
    MOON;

    private final static double MEAN_LONGITUDE = Angle.ofDeg(91.929336);
    private final static double MEAN_LONGITUDE_PER = Angle.ofDeg(130.143076);
    private final static double ASCENDING_NODE_LON = Angle.ofDeg(291.682547);
    private final static double ORBITAL_INCLINATION = Angle.ofDeg(5.145396);
    private final static double ECCENTRICITY = 0.0549;
    private final static double ORBITAL_LON_FACTOR = Angle.ofDeg(13.1763966);
    private final static double MOON_ANOMALY_CONSTANT = Angle.ofDeg(0.1114041);
    private final static double EVECTION_FACTOR = Angle.ofDeg(1.2739);
    private final static double ANNUAL_EQUATION_CORRECTION_FACTOR = Angle.ofDeg(0.1858);
    private final static double THIRD_CORRECTION_FACTOR = Angle.ofDeg(0.37);
    private final static double CENTER_EQUATION_CORRECTION_FACTOR = Angle.ofDeg(6.2886);
    private final static double FOURTH_CORRECTION_FACTOR = Angle.ofDeg(0.214);
    private final static double VARIATION_FACTOR = Angle.ofDeg(0.6583);
    private final static double ASCENDING_NODE_LON_FACTOR = Angle.ofDeg(0.0529539);
    private final static double CORRECTED_ASCENDING_LON_FACTOR = Angle.ofDeg(0.16);
    private final static double ANGULAR_SIZE_NUMERATOR = Angle.ofDeg(0.5181);


    /**
     * Returns the Moon model, i.e positions in equatorial coordinates
     * at a given day since J2010 and the given ecliptic coordinates.
     *
     * @param daysSinceJ2010                 the number of days since J2010.
     * @param eclipticToEquatorialConversion the given conversion system from ecliptic coordinates to equatorial coordinates.
     * @return an instance of the Moon.
     */
    @Override
    public Moon at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        // We use the SunModel class to get the ecliptic longitude of the sun, as well as its mean anomaly.
        Sun sun = SunModel.SUN.at(daysSinceJ2010, eclipticToEquatorialConversion);
        double lonEclipSun = sun.eclipticPos().lon();
        double sinMeanAnomalySun = sin(sun.meanAnomaly());


        double meanLongOrbital = ORBITAL_LON_FACTOR * daysSinceJ2010 + MEAN_LONGITUDE;
        double meanAnomaly = meanLongOrbital - MOON_ANOMALY_CONSTANT * daysSinceJ2010 - MEAN_LONGITUDE_PER;

        double evection = EVECTION_FACTOR * sin(2 * (meanLongOrbital - lonEclipSun) - meanAnomaly);
        // Annual equation correction.
        double annualEqCorrection = ANNUAL_EQUATION_CORRECTION_FACTOR * sinMeanAnomalySun;
        double thirdCorrection = THIRD_CORRECTION_FACTOR * sinMeanAnomalySun;

        // Given the different computed correction, we get the corrected anomaly.
        double correctedAnomaly = meanAnomaly + evection - annualEqCorrection - thirdCorrection;

        // Center equation correction
        double centerEqCorrection = CENTER_EQUATION_CORRECTION_FACTOR * sin(correctedAnomaly);
        double fourthCorrection = FOURTH_CORRECTION_FACTOR * sin(2 * correctedAnomaly);

        // Given some correction variables, we correct the  orbital longitude of the Moon.
        double correctedLongOrbital = meanLongOrbital + evection + centerEqCorrection - annualEqCorrection + fourthCorrection;
        double variation = VARIATION_FACTOR * sin(2 * (correctedLongOrbital - lonEclipSun));

        // The computed variation allow us to calculate the real orbital longitude.
        double realLongOrbital = correctedLongOrbital + variation;

        double meanAscendingNodeLon = ASCENDING_NODE_LON - ASCENDING_NODE_LON_FACTOR * daysSinceJ2010;
        double correctedAscendingNodeLon = meanAscendingNodeLon - CORRECTED_ASCENDING_LON_FACTOR * sinMeanAnomalySun;

        double sinOrbitalNodeLonDifference = sin(realLongOrbital - correctedAscendingNodeLon);
        double cosOrbitalNodeLonDifference = cos(realLongOrbital - correctedAscendingNodeLon);

        // Ecliptic coordinates of the Moon.
        double moonEclipLon = atan2(sinOrbitalNodeLonDifference * cos(ORBITAL_INCLINATION),
                                    cosOrbitalNodeLonDifference)
                              + correctedAscendingNodeLon;

        double moonEclipLat = asin(sinOrbitalNodeLonDifference * sin(ORBITAL_INCLINATION));

        double moonPhase = (1 - cos(realLongOrbital - lonEclipSun)) / 2;

        // Length of the semi-major axis of the Moon orbital around the Earth.
        double rho = (1 - ECCENTRICITY * ECCENTRICITY) / (1 + ECCENTRICITY * cos(correctedAnomaly + centerEqCorrection));
        double angularSize = ANGULAR_SIZE_NUMERATOR / rho;

        EclipticCoordinates ecl = EclipticCoordinates.of(Angle.normalizePositive(moonEclipLon), moonEclipLat);

        return new Moon(eclipticToEquatorialConversion.apply(ecl), (float) angularSize, 0, (float) moonPhase);
    }

}
