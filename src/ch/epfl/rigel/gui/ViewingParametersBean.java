package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;



/**
 * JavaFX bean containing properties of the viewing parameters.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class ViewingParametersBean {

    private final SimpleDoubleProperty fieldOfViewDeg;
    private final ObjectProperty<HorizontalCoordinates> center;


    /**
     * Constructor, initializes the properties with trivial and null values.
     */
    public ViewingParametersBean() {
        fieldOfViewDeg = new SimpleDoubleProperty(0);
        center = new SimpleObjectProperty<>();
    }


    /**
     * Getter for the field of viewing property, in degrees.
     *
     * @return the field of viewing property, in degrees.
     */
    public SimpleDoubleProperty fieldOfViewDegProperty() {
        return fieldOfViewDeg;
    }

    /**
     * Getter for the field of viewing, in degrees.
     *
     * @return the field of viewing, in degrees.
     */
    public double getFieldOfViewDeg() {
        return fieldOfViewDeg.get();
    }

    /**
     * Setter for the field of viewing, in degrees.
     *
     * @param fov the new field of viewing, in degrees.
     */
    public void setFieldOfViewDeg(double fov) {
        fieldOfViewDeg.set(fov);
    }


    /**
     * Getter for the projection center property.
     *
     * @return the projection center property.
     */
    public ObjectProperty<HorizontalCoordinates> centerProperty() {
        return center;
    }

    /**
     * Getter for the projection center.
     *
     * @return the projection center.
     */
    public HorizontalCoordinates getCenter() {
        return center.get();
    }

    /**
     * Setter for the projection center.
     *
     * @param newCenter the new projection center.
     */
    public void setCenter(HorizontalCoordinates newCenter) {
        center.set(newCenter);
    }

}
