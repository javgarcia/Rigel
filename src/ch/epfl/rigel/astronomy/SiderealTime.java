package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Sidereal time, defined as a relative time system or as an angle.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class SiderealTime {

    private final static double HOURS_PER_MILLIS = 1.0 / (1000.0 * 3600.0);
    private final static Polynomial JULIAN_POLYNOMIAL = Polynomial.of(
            0.000025862,
            2400.051336,
            6.697374558);
    private final static double HOURS_DAYS_NOW_FACTOR = 1.002737909;


    /**
     * Private constructor, avoids instantiation.
     */
    private SiderealTime() {
    }


    /**
     * Method converting a given Zoned date time to the greenwich sidereal time.
     *
     * @param when the given Zoned date time.
     * @return the greenwich sidereal time, in radians.
     */
    public static double greenwich(ZonedDateTime when) {

        ZonedDateTime whenGreenwichZone = when.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime whenGreenwichZoneTruncated = whenGreenwichZone.truncatedTo(ChronoUnit.DAYS);

        double centBeginningDay = Epoch.J2000.julianCenturiesUntil(whenGreenwichZoneTruncated);

        long millis = whenGreenwichZoneTruncated.until(whenGreenwichZone, ChronoUnit.MILLIS);

        // Conversion from milliseconds to hours.
        double hoursDayNow = millis * HOURS_PER_MILLIS;

        double sidTime0 = JULIAN_POLYNOMIAL.at(centBeginningDay);

        double sidTime1 = HOURS_DAYS_NOW_FACTOR * hoursDayNow;

        double sidTimeGreenwich = sidTime0 + sidTime1;

        // Normalization after the conversion from hours to radians.
        return Angle.normalizePositive(Angle.ofHr(sidTimeGreenwich));
    }


    /**
     * For a given zoned date time and geographic coordinates, return the local sidereal time.
     *
     * @param when  the given Zoned date time.
     * @param where the given geographic coordinates.
     * @return the local sidereal time, in radians.
     */
    public static double local(ZonedDateTime when, GeographicCoordinates where) {
        return Angle.normalizePositive(greenwich(when) + where.lon());
    }
}
