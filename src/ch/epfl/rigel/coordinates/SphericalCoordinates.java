package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

/**
 * Definition of a spherical coordinate system with constant radial distance.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
abstract class SphericalCoordinates {

    private final double longitude;
    private final double latitude;


    /**
     * Constructor, takes the longitude and latitude in argument.
     *
     * @param longitude the given longitude.
     * @param latitude  the given latitude.
     */
    SphericalCoordinates(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }


    /**
     * Getter for the longitude.
     *
     * @return the longitude coordinate in radians.
     */
    double lon() {
        return longitude;
    }


    /**
     * Getter for the longitude.
     *
     * @return the longitude coordinate in degrees.
     */
    double lonDeg() {
        return Angle.toDeg(longitude);
    }


    /**
     * Getter for the latitude.
     *
     * @return the latitude coordinate in radians.
     */
    double lat() {
        return latitude;
    }


    /**
     * Getter for the latitude.
     *
     * @return the latitude coordinate in degrees.
     */
    double latDeg() {
        return Angle.toDeg(latitude);
    }


    /**
     * @return throws an exception.
     * @throws UnsupportedOperationException if the method is called.
     * @see Object#equals(Object)
     */
    @Override
    public final boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return throws an exception.
     * @throws UnsupportedOperationException if the method is called.
     * @see Object#hashCode()
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }
}
