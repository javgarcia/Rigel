package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * Planet, a celestial object.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class Planet extends CelestialObject {

    /**
     * Constructor for the object Planet.
     *
     * @param name          name of the planet.
     * @param equatorialPos equatorial coordinates.
     * @param angularSize   the planet's angular size.
     * @param magnitude     the planet's magnitude.
     */
    public Planet(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        super(name, equatorialPos, angularSize, magnitude);
    }

}
