package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static java.lang.Math.*;

/**
 * Conversion from ecliptic to equatorial coordinates.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {

    //Obliquity of the ecliptic sinus and cosinus functions.
    private final double sinObl;
    private final double cosObl;
    private final static Polynomial ECLIPTIC_OBL_POLYNOMIAL = Polynomial.of(
            +Angle.ofArcsec(0.00181),
            -Angle.ofArcsec(0.0006),
            -Angle.ofArcsec(46.815),
            +Angle.ofDMS(23, 26, 21.45));

    /**
     * Constructor.
     *
     * @param when the given Zoned date time.
     */
    public EclipticToEquatorialConversion(ZonedDateTime when) {

        // Julian centuries between J2000 and when (parameter of constructor).
        double julianCenturies = Epoch.J2000.julianCenturiesUntil(when);

        // Obliquity of the ecliptic.
        double eclipticObl = ECLIPTIC_OBL_POLYNOMIAL.at(julianCenturies);

        sinObl = sin(eclipticObl);
        cosObl = cos(eclipticObl);
    }


    /**
     * From the ecliptic coordinates parameters, ecliptic longitude and ecliptic latitude,
     * computes the right ascension and declination of equivalent equatorial coordinates system.
     *
     * @param ecl ecliptic coordinates parameters (right ascension and declination).
     * @return the equivalent equatorial coordinates system.
     */
    @Override
    public EquatorialCoordinates apply(EclipticCoordinates ecl) {
        double sinLonEclip = sin(ecl.lon());

        double ra = atan2(sinLonEclip * cosObl - tan(ecl.lat()) * sinObl,
                          cos(ecl.lon()));

        double dec = asin(sin(ecl.lat()) * cosObl + cos(ecl.lat()) * sinObl * sinLonEclip);

        return EquatorialCoordinates.of(Angle.normalizePositive(ra), dec);
    }


    /**
     * @return throws an exception.
     * @throws UnsupportedOperationException if the method is called.
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return throws an exception.
     * @throws UnsupportedOperationException if the method is called.
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

}
