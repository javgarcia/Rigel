package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.util.Objects;

/**
 * Generalized definition of a Celestial object, defined by a name,
 * equatorial coordinates, an angular size and a magnitude.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public abstract class CelestialObject {

    private final String name;
    private final EquatorialCoordinates equatorialPos;
    private final float angularSize;
    private final float magnitude;


    /**
     * Constructor for a celestial object.
     *
     * @param name          name of the celestial object.
     * @param equatorialPos equatorial coordinates.
     * @param angularSize   the object's angular size.
     * @param magnitude     the object's magnitude.
     * @throws NullPointerException     if either the name or the equatorial coordinates are null.
     * @throws IllegalArgumentException if the angular size is negative.
     */
    CelestialObject(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {

        // Checks if the string is null before copying it to the private attribute.
        this.name = Objects.requireNonNull(name);
        this.equatorialPos = Objects.requireNonNull(equatorialPos);

        // Checks if the angular size value is correct (i.e non negative).
        Preconditions.checkArgument(angularSize >= 0);
        this.angularSize = angularSize;
        this.magnitude = magnitude;
    }


    /**
     * Getter for the object's name.
     *
     * @return the object's name.
     */
    public String name() {
        return name;
    }


    /**
     * Getter for the object's angular size.
     *
     * @return the object's angular size.
     */
    public double angularSize() {
        return angularSize;
    }


    /**
     * Getter for the object's magnitude
     *
     * @return the object's magnitude.
     */
    public double magnitude() {
        return magnitude;
    }


    /**
     * Getter for the object's equatorial coordinates.
     *
     * @return the equatorial coordinates.
     */
    public EquatorialCoordinates equatorialPos() {
        return equatorialPos;
    }


    /**
     * Method giving information about the celestial object.
     * By default, return the object's name.
     */
    public String info() {
        return name;
    }


    /**
     * @return the object's name, by default.
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return info();
    }

}