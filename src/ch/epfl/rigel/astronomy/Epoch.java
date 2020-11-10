package ch.epfl.rigel.astronomy;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * Defines the reference epochs and methods and calculates periods
 * of time regarding these references.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public enum Epoch {

    // Reference epochs: J2000 and J2010
    J2000(LocalDate.of(2000, Month.JANUARY, 1), 12),
    J2010(LocalDate.of(2010, Month.JANUARY, 1).minusDays(1), 0);

    private final static double DAYS_PER_MILLIS = (1 / (1000.0 * 3600.0 * 24.0));
    private final static double JULIAN_CENTURIES_PER_DAY = 1 / 36525.0;

    //The ZonedDateTime which represents an epoch.
    private final ZonedDateTime dateTime;


    /**
     * Constructor of the J2000 and J2010 reference epochs.
     * Creates an epoch (a ZonedDateTime) from a local date (parameter), an hour (parameter)
     * with 0 minutes and the local time zone UTC.
     *
     * @param localDate the local date.
     * @param hour      the hour.
     */
    Epoch(LocalDate localDate, int hour) {
        dateTime = ZonedDateTime.of(
                localDate,
                LocalTime.of(hour, 0),
                ZoneOffset.UTC);
    }


    /**
     * Computes the number of days between the epoch J2000 or J2010 and when.
     *
     * @param when the epoch (apart from J2000 or J2010) from which the number of days is computed.
     * @return the number of days between the epochs, a positive number if when occurs after the chosen
     * reference epoch, or a negative number otherwise.
     */
    public double daysUntil(ZonedDateTime when) {
        return dateTime.until(when, ChronoUnit.MILLIS) * DAYS_PER_MILLIS;

    }


    /**
     * Computes the julian centuries between the epoch J2000 or J2010 and when.
     *
     * @param when the epoch (apart from J2000 or J2010) from which the number of julian centuries is computed.
     * @return the julian centuries, positive if a positive number if when occurs after the chosen
     * reference epoch, or a negative number otherwise.
     */
    public double julianCenturiesUntil(ZonedDateTime when) {
        return this.daysUntil(when) * JULIAN_CENTURIES_PER_DAY;
    }

}
