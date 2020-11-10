package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

/**
 * Star, a Celestial Object defined by an hipparcos ID and a color index.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class Star extends CelestialObject {

    private final int hipparcosId;
    private final static ClosedInterval COLOR_INTERVAL = ClosedInterval.of(-0.5, 5.5);
    private final int colorTemperature;


    /**
     * Constructor of the Star.
     * The angular size of a star is 0.
     *
     * @param hipparcosId   hipparcos ID.
     * @param name          name of star.
     * @param equatorialPos equatorial position of the star.
     * @param magnitude     magnitude of the star.
     * @param colorIndex    color index of the star.
     * @throws IllegalArgumentException if the Hipparcos ID is negative or
     *                                  if the color index is not included in the color interval given as an attribute.
     */
    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos, float magnitude, float colorIndex) {
        super(name, equatorialPos, 0, magnitude);

        Preconditions.checkArgument(hipparcosId >= 0);
        this.hipparcosId = hipparcosId;
        float checkedColorIndex = (float) Preconditions.checkInInterval(COLOR_INTERVAL, colorIndex);
        colorTemperature = (int) (4600 * (1 / (0.92 * checkedColorIndex + 1.7) + 1 / (0.92 * checkedColorIndex + 0.62)));
    }


    /**
     * Return the Hipparcos ID of the star.
     *
     * @return the Hipparcos ID of the star.
     */
    public int hipparcosId() {
        return hipparcosId;
    }


    /**
     * Return the color temperature of the star in Kelvin degrees and rounded by default.
     *
     * @return the color temperature of the star.
     */
    public int colorTemperature() {
        return colorTemperature;
    }

}

