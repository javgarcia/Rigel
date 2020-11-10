package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * JavaFX bean containing the observer location parameters.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class ObserverLocationBean {

    private final SimpleDoubleProperty lonDeg;
    private final SimpleDoubleProperty latDeg;
    private final ObjectBinding<GeographicCoordinates> coordinates;


    /**
     * Constructor, which initializes the longitude and the latitude at 0.
     * The coordinates (composite) depends on the current longitude and latitude.
     */
    public ObserverLocationBean() {
        lonDeg = new SimpleDoubleProperty(0);
        latDeg = new SimpleDoubleProperty(0);
        coordinates = Bindings.createObjectBinding(() -> GeographicCoordinates.ofDeg(getLonDeg(), getLatDeg()), lonDeg, latDeg);

    }


    /**
     * Getter for the longitude property of the observer location, in degrees.
     *
     * @return the longitude property of the observer location, in degrees.
     */
    public SimpleDoubleProperty lonDegProperty() {
        return lonDeg;
    }

    /**
     * Getter for the longitude of the observer location, in degrees.
     *
     * @return the longitude of the observer location, in degrees.
     */
    public double getLonDeg() {
        return lonDeg.get();
    }

    /**
     * Setter for the longitude of the observer location, in degrees.
     *
     * @param longitude the new longitude of the observer location, in degrees.
     */
    public void setLonDeg(double longitude) {
        lonDeg.set(longitude);
    }


    /**
     * Getter for the latitude property of the observer location, in degrees.
     *
     * @return the latitude property of the observer location, in degrees.
     */
    public SimpleDoubleProperty latDegProperty() {
        return latDeg;
    }

    /**
     * Getter for the latitude of the observer location, in degrees.
     *
     * @return the latitude of the observer location, in degrees.
     */
    public double getLatDeg() {
        return latDeg.get();
    }

    /**
     * Setter for the latitude of the observer location, in degrees.
     *
     * @param latitude the new latitude of the observer location, in degrees.
     */
    public void setLatDeg(double latitude) {
        latDeg.set(latitude);
    }


    /**
     * Getter for the geographic coordinates property of the observer location, in degrees.
     *
     * @return the geographic coordinates property of the observer location, in degrees.
     */
    public ObjectBinding<GeographicCoordinates> coordinatesProperty() {
        return coordinates;
    }

    /**
     * Getter for the geographic coordinates of the observer location, in degrees.
     *
     * @return the geographic coordinates of the observer location, in degrees.
     */
    public GeographicCoordinates getCoordinates() {
        return coordinates.get();
    }

    /**
     * Setter for the geographic coordinates of the observer location, in degrees.
     *
     * @param coordinates the new geographic coordinates of the observer location, in degrees.
     */
    public void setCoordinates(GeographicCoordinates coordinates) {
        setLonDeg(coordinates.lonDeg());
        setLatDeg(coordinates.latDeg());
    }

}
