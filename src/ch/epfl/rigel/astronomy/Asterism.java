package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;

import java.util.List;

/**
 * An asterism, characterized by a list of stars.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class Asterism {

    // The list of stars contained in the asterisms.
    private final List<Star> starList;


    /*
     * Name of the constellation containing the asterism.
     * Note that due to the database implementation, only the leading asterisms are given the constellation name in order
     * to avoid duplicates.
     *
     * Hence, the name is replaced with "-" if an asterism has already been given the constellation name.
     */
    private final String constellation;


    /**
     * Constructor of an asterism.
     *
     * @param stars         the  given list of stars.
     * @param constellation the name of the constellation containing the asterism.
     * @throws IllegalArgumentException if the stars list is empty.
     */
    public Asterism(List<Star> stars, String constellation) {
        Preconditions.checkArgument(!stars.isEmpty());
        starList = List.copyOf(stars);

        this.constellation = constellation;

    }


    /**
     * Getter for the asterism stars.
     *
     * @return the list of the stars included in the asterism.
     */
    public List<Star> stars() {
        return starList;
    }


    /**
     * Getter for the asterism's constellation name.
     *
     * @return the constellation name.
     */
    public String getConstellationName() {
        return constellation;
    }
}
