package ch.epfl.rigel.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * JavaFX Bean representing a zoned date time property.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class DateTimeBean {

    private final ObjectProperty<LocalDate> date;
    private final ObjectProperty<LocalTime> time;
    private final ObjectProperty<ZoneId> zoneId;


    /**
     * Constructor, initializes the values as null.
     */
    public DateTimeBean() {
        date = new SimpleObjectProperty<>(null);
        time = new SimpleObjectProperty<>(null);
        zoneId = new SimpleObjectProperty<>(null);
    }


    /**
     * Getter for the date property.
     *
     * @return the date property.
     */
    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    /**
     * Getter for the date.
     *
     * @return the local date.
     */
    public LocalDate getDate() {
        return date.get();
    }

    /**
     * Setter for the date.
     *
     * @param date new local date.
     */
    public void setDate(LocalDate date) {
        this.date.set(date);
    }


    /**
     * Getter for the time property.
     *
     * @return the local time property.
     */
    public ObjectProperty<LocalTime> timeProperty() {
        return time;
    }

    /**
     * Getter for the time.
     *
     * @return the local time.
     */
    public LocalTime getTime() {
        return time.get();
    }

    /**
     * Setter for the time.
     *
     * @param time the new local time.
     */
    public void setTime(LocalTime time) {
        this.time.set(time);
    }


    /**
     * Getter for the zone ID property.
     *
     * @return the zone ID property.
     */
    public ObjectProperty<ZoneId> zoneIdProperty() {
        return zoneId;
    }

    /**
     * Getter for the zone ID.
     *
     * @return the zone ID.
     */
    public ZoneId getZoneId() {
        return zoneId.get();
    }

    /**
     * Setter for the zone ID.
     *
     * @param zoneId the new zone ID.
     */
    public void setZoneId(ZoneId zoneId) {
        this.zoneId.set(zoneId);
    }


    /**
     * Computes the zoned date time based on the class properties.
     *
     * @return a new zoned date time.
     */
    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.of(getDate(), getTime(), getZoneId());
    }


    /**
     * Setter for the zoned date time, i.e the three properties.
     *
     * @param zdt the given zoned date time.
     */
    public void setZonedDateTime(ZonedDateTime zdt) {
        setDate(zdt.toLocalDate());
        setTime(zdt.toLocalTime());
        setZoneId(zdt.getZone());
    }
}
