package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.util.Objects;

/**
 * Sun, a celestial object with fixed magnitude.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class Sun extends CelestialObject {

    private final EclipticCoordinates eclipticPos;
    private final float meanAnomaly;


    /**
     * Constructor for the object Sun.
     *
     * @param eclipticPos   ecliptic coordinates.
     * @param equatorialPos equatorial coordinates.
     * @param angularSize   the sun's angular size.
     * @param meanAnomaly   the sun's mean anomaly.
     * @throws NullPointerException if the ecliptic coordinates are null.
     */
    public Sun(EclipticCoordinates eclipticPos, EquatorialCoordinates equatorialPos, float angularSize, float meanAnomaly) {
        super("Soleil", equatorialPos, angularSize, -26.7f);
        this.meanAnomaly = meanAnomaly;
        this.eclipticPos = Objects.requireNonNull(eclipticPos);
    }


    /**
     * Getter for the ecliptic coordinates.
     *
     * @return the Sun's ecliptic coordinates.
     */
    public EclipticCoordinates eclipticPos() {
        return eclipticPos;
    }


    /**
     * Getter for the mean anomaly.
     *
     * @return the sun's mean anomaly.
     */
    public double meanAnomaly() {
        return meanAnomaly;
    }
}
