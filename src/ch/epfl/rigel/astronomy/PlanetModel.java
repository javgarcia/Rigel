package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

import static java.lang.Math.*;

import java.util.List;

/**
 * Model of the eight planets of the Solar System.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public enum PlanetModel implements CelestialObjectModel<Planet> {

    MERCURY("Mercure", 0.24085, 75.5671, 77.612, 0.205627,
            0.387098, 7.0051, 48.449, 6.74, -0.42),
    VENUS("Vénus", 0.615207, 272.30044, 131.54, 0.006812,
            0.723329, 3.3947, 76.769, 16.92, -4.40),
    EARTH("Terre", 0.999996, 99.556772, 103.2055, 0.016671,
            0.999985, 0, 0, 0, 0),
    MARS("Mars", 1.880765, 109.09646, 336.217, 0.093348,
            1.523689, 1.8497, 49.632, 9.36, -1.52),
    JUPITER("Jupiter", 11.857911, 337.917132, 14.6633, 0.048907,
            5.20278, 1.3035, 100.595, 196.74, -9.40),
    SATURN("Saturne", 29.310579, 172.398316, 89.567, 0.053853,
            9.51134, 2.4873, 113.752, 165.60, -8.88),
    URANUS("Uranus", 84.039492, 356.135400, 172.884833, 0.046321,
            19.21814, 0.773059, 73.926961, 65.80, -7.19),
    NEPTUNE("Neptune", 165.84539, 326.895127, 23.07, 0.010483,
            30.1985, 1.7673, 131.879, 62.20, -6.87);

    /**
     * Immutable list of all the planets of the Solar System.
     */
    public final static List<PlanetModel> ALL = List.of(PlanetModel.values());

    private final static double MEAN_ANGULAR_SPEED = Angle.TAU / 365.242191;
    private final String name;
    private final double tropicalYear;
    private final double longitude2010;
    private final double longitudePerigee;
    private final double orbitEccentricity;
    private final double orbitSemiMajorAxis;
    private final double sinEclipticInclination;
    private final double cosEclipticInclination;
    private final double node;
    private final double angularSizeUnit;
    private final double magnitudeUnit;


    /**
     * Constructor of the planet, assigns the parameters to the attributes and transforms the degrees in radians.
     *
     * @param name                the name of the plan.
     * @param tropicalYear        tropical years.
     * @param longitude2010       longitude when J2010 epoch.
     * @param longitudePerigee    longitude when perigee.
     * @param orbitEccentricity   the eccentricity of the orbit.
     * @param orbitSemiMajorAxis  semi major axis of the orbit.
     * @param EclipticInclination orbit inclination in the ecliptic.
     * @param node                the rising node longitude.
     * @param AngularSizeUnit     the angular size with 1 AU.
     * @param magnitudeUnit       the magnitude with 1 AU.
     */
    PlanetModel(String name, double tropicalYear, double longitude2010, double longitudePerigee, double orbitEccentricity,
                double orbitSemiMajorAxis, double EclipticInclination, double node,
                double AngularSizeUnit, double magnitudeUnit) {

        this.name = name;
        this.tropicalYear = tropicalYear;
        this.longitude2010 = Angle.ofDeg(longitude2010);
        this.longitudePerigee = Angle.ofDeg(longitudePerigee);
        this.orbitEccentricity = orbitEccentricity;
        this.orbitSemiMajorAxis = orbitSemiMajorAxis;
        sinEclipticInclination = sin(Angle.ofDeg(EclipticInclination));
        cosEclipticInclination = cos(Angle.ofDeg(EclipticInclination));
        this.node = Angle.ofDeg(node);
        this.angularSizeUnit = Angle.ofArcsec(AngularSizeUnit);
        this.magnitudeUnit = magnitudeUnit;
    }


    /**
     * Public method that creates a model of one of the eight planets according to the given parameters.
     * It uses the Planet class to create a planet.
     *
     * @param daysSinceJ2010                 days since 2010.
     * @param eclipticToEquatorialConversion allows for converting the ecliptic coordinates system
     *                                       calculated into an equatorial one.
     * @return the planet model.
     */
    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        //Average and true anomalies of the Planet.
        double averageAnomaly =  MEAN_ANGULAR_SPEED  * (daysSinceJ2010 / tropicalYear) + longitude2010 - longitudePerigee;
        double trueAnomaly = averageAnomaly + 2 * orbitEccentricity * sin(averageAnomaly);

        //Radius: distance to Sun.
        //helioLong: heliocentric longitude.
        double radius = (orbitSemiMajorAxis * (1 - orbitEccentricity * orbitEccentricity)) / (1 + orbitEccentricity * cos(trueAnomaly));
        double helioLong = trueAnomaly + longitudePerigee;

        //Heliocentric ecliptic latitude.
        double helioLatEclip = asin(sin(helioLong - node) * sinEclipticInclination);

        //Projection of radius in the ecliptic.
        double radiusProj = radius * cos(helioLatEclip);

        //Heliocentric ecliptic longitude.
        double helioLongProj = atan2(sin(helioLong - node) * cosEclipticInclination, cos(helioLong - node)) + node;

        //Previous notions applied to Earth.
        double averageAnomalyEarth = MEAN_ANGULAR_SPEED * (daysSinceJ2010 / EARTH.tropicalYear) + EARTH.longitude2010 - EARTH.longitudePerigee;
        double trueAnomalyEarth = averageAnomalyEarth + 2 * EARTH.orbitEccentricity * sin(averageAnomalyEarth);
        double radiusEarth = (EARTH.orbitSemiMajorAxis * (1 - EARTH.orbitEccentricity * EARTH.orbitEccentricity))
                / (1 + EARTH.orbitEccentricity * cos(trueAnomalyEarth));
        double helioLongEarth = trueAnomalyEarth + EARTH.longitudePerigee;

        double deltaLong = helioLongProj - helioLongEarth;

        //Geocentric ecliptic longitude.
        double eclipLong;

        eclipLong = (name.equals("Mercure") || name.equals("Vénus")) ?
                PI + helioLongEarth + atan2(radiusProj * sin(helioLongEarth - helioLongProj), radiusEarth - radiusProj * cos(helioLongEarth - helioLongProj))
                : helioLongProj + atan2(radiusEarth * sin(deltaLong), radiusProj - radiusEarth * cos(deltaLong));

        //Geocentric ecliptic latitude.
        double eclipLat = atan(
                (radiusProj * tan(helioLatEclip) * sin(eclipLong - helioLongProj)) / (radiusEarth * sin(deltaLong))
        );

        //Distance between a planet and Earth.
        double distance = sqrt(radiusEarth * radiusEarth + radius * radius
                - 2 * radiusEarth * radius * cos(helioLong - helioLongEarth) * cos(helioLatEclip));
        double angularSize = angularSizeUnit / distance;

        double phase = (1 + cos(eclipLong - helioLong)) / 2;
        double magnitude = magnitudeUnit + 5 * log10(radius * distance / sqrt(phase));

        return new Planet(name,
                eclipticToEquatorialConversion.apply(EclipticCoordinates.of(Angle.normalizePositive(eclipLong), eclipLat)),
                (float) angularSize, (float) magnitude);
    }

}
