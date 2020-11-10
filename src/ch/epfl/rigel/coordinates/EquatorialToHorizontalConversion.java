package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.math.Angle;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static java.lang.Math.*;

/**
 * Conversion from equatorial to horizontal coordinates.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class EquatorialToHorizontalConversion implements Function<EquatorialCoordinates, HorizontalCoordinates> {

    // The local sidereal time.
    private final double siderealLocal;

    // sinus of the given geographic coordinates altitude.
    private final double sinPhi;
    // cosinus of the given geographic coordinates altitude.
    private final double cosPhi;


    /**
     * Constructor.
     *
     * @param when  the given zoned date time.
     * @param where the given geographic coordinates.
     */
    public EquatorialToHorizontalConversion(ZonedDateTime when, GeographicCoordinates where) {
        siderealLocal = SiderealTime.local(when, where);

        sinPhi = sin(where.lat());
        cosPhi = cos(where.lat());
    }


    /**
     * Method converting the given equatorial coordinates to horizontal coordinates.
     *
     * @param equ the given equatorial coordinates.
     * @return horizontal coordinates.
     */
    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equ) {

        double sinDelta = sin(equ.dec());
        double cosDelta = cos(equ.dec());

        double hourAngle = siderealLocal - equ.ra();

        double altitudeSin = sinDelta * sinPhi + cosDelta * cosPhi * cos(hourAngle);

        double azimuth = atan2(-cosDelta * cosPhi * Math.sin(hourAngle),
                                sinDelta - sinPhi * altitudeSin);

        return HorizontalCoordinates.of(Angle.normalizePositive(azimuth), asin(altitudeSin));
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
