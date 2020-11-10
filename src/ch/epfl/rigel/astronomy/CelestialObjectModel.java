package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

/**
 * Representation of a model of general type O.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
@FunctionalInterface
public interface CelestialObjectModel<O> {

    /**
     * General method for building a celestial object model, based on 3 main arguments.
     *
     * @param daysSinceJ2010                 the number of days since J2010.
     * @param eclipticToEquatorialConversion the given conversion system from ecliptic coordinates to equatorial coordinates.
     * @return an instance of the Celestial object.
     */
    O at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion);

}
