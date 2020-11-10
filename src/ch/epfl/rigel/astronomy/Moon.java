package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

import java.util.Locale;

/**
 * Moon, a celestial object with a phase.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class Moon extends CelestialObject {

    private final static ClosedInterval PHASE_INTERVAL = ClosedInterval.of(0, 1);
    private final float phase;


    /**
     * Constructor for the object Moon.
     *
     * @param equatorialPos equatorial coordinates.
     * @param angularSize   the Moon's angular size.
     * @param magnitude     the Moon's magnitude.
     * @param phase         the Moon's phase.
     * @throws IllegalArgumentException if the phase value doesn't belong to the closed interval [0,1].
     */
    public Moon(EquatorialCoordinates equatorialPos, float angularSize, float magnitude, float phase) {
        super("Lune", equatorialPos, angularSize, magnitude);
        this.phase = (float) Preconditions.checkInInterval(PHASE_INTERVAL, phase);
    }


    /**
     * Override info method.
     *
     * @return string in the form : Lune (x %).
     */
    @Override
    public String info() {
        return String.format(Locale.ROOT, "%s (%.1f%s)", name(), phase * 100, "%");
    }

}
