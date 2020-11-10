package ch.epfl.rigel.astronomy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Collections;


/**
 * Creates a star catalogue.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */

public final class StarCatalogue {

    //List with all the stars.
    private final List<Star> starList;

    //Map that associates for each asterism the list with its stars represented by the their index in the star list attribute.
    private final HashMap<Asterism, List<Integer>> asterismListMap;

    //Set with all the valid asterisms.
    private final Set<Asterism> asterismList;


    /**
     * Constructor that creates the star catalogue with the given stars and asterisms lists.
     *
     * @param stars     the list with all the stars to be included in the star catalogue.
     * @param asterisms the list of the asterisms to be included in the asterism attribute.
     * @throws IllegalArgumentException if a given asterism includes a star that does not exist in the given stars list.
     */
    public StarCatalogue(List<Star> stars, List<Asterism> asterisms) {
        starList = List.copyOf(stars);
        asterismListMap = new HashMap<>();
        HashMap<Star, Integer> starToIndex = new HashMap<>();
        for (int i = 0; i < stars.size(); ++i) {
            starToIndex.put(stars.get(i), i);
        }

        for (Asterism asterism : asterisms) {

            List<Integer> indexes = new ArrayList<>();

            for (Star star : asterism.stars()) {
                int index;
                if ((index = starToIndex.getOrDefault(star, -1)) != -1) {
                    indexes.add(index);
                } else {
                    throw new IllegalArgumentException("A star in an asterism does not exist in the given stars list");
                }
            }
            asterismListMap.put(asterism, indexes);
        }

        asterismList = Collections.unmodifiableSet(asterismListMap.keySet());
    }


    /**
     * Getter for the stars list.
     *
     * @return an immutable copy of the stars list.
     */
    public List<Star> stars() {
        return starList;
    }


    /**
     * Getter for the asterisms list (as a set).
     *
     * @return an immutable copy of the asterisms set.
     */
    public Set<Asterism> asterisms() {
        return asterismList;
    }


    /**
     * Getter for the star indexes of the given asterism.
     *
     * @param asterism the asterism.
     * @return the stars indexes of the given asterism.
     * @throws IllegalArgumentException if the given asterism is not included in the asterisms list of the star catalogue.
     */
    public List<Integer> asterismIndices(Asterism asterism) {
        if (asterismListMap.containsKey(asterism)) {
            return Collections.unmodifiableList(asterismListMap.get(asterism));
        } else {
            throw new IllegalArgumentException("The given asterism is not included in the asterism list.");
        }
    }


    /**
     * Builder for the star catalogue.
     */
    public final static class Builder {

        private final List<Star> stars;
        private final List<Asterism> asterisms;

        /**
         * Default constructor, creating empty instances of the stars and asterisms lists.
         */
        public Builder() {
            stars = new ArrayList<>();
            asterisms = new ArrayList<>();
        }

        /**
         * Adds the  given star into the builder list.
         *
         * @param star the given star.
         * @return the builder containing the new star.
         */
        public Builder addStar(Star star) {
            stars.add(star);
            return this;
        }

        /**
         * Getter for the list of stars.
         *
         * @return an unmodifiable view of the stars list.
         */
        public List<Star> stars() {
            return Collections.unmodifiableList(stars);
        }

        /**
         * Adds the given asterism into the builder list.
         *
         * @param asterism the given asterism.
         * @return the builder containing the new star.
         */
        public Builder addAsterism(Asterism asterism) {
            asterisms.add(asterism);
            return this;
        }

        /**
         * Getter for the list of asterisms.
         *
         * @return an unmodifiable view of the asterisms list.
         */
        public List<Asterism> asterisms() {
            return Collections.unmodifiableList(asterisms);
        }

        /**
         * Generates the star catalogue with the actual list of asterisms and stars.
         *
         * @return an instance of the catalogue.
         */
        public StarCatalogue build() {
            return new StarCatalogue(stars, asterisms);
        }

        /**
         * Loading method, given an input stream and a loader.
         *
         * @param inputStream the input stream, i.e a list of asterisms or a list of stars data.
         * @param loader      the loader corresponding to the input stream.
         * @return the Builder containing the loaded data.
         * @throws IOException if there is an input error.
         */
        public Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {
            loader.load(inputStream, this);
            return this;
        }

    }


    /**
     * Interface defining a general loader.
     */
    public interface Loader {

        /**
         * Loads method, takes an input stream and add the file information into the given builder.
         *
         * @param inputStream the given input stream, i.e a file containing asterisms or stars data.
         * @param builder     the given star catalogue builder.
         * @throws IOException if there is an input error.
         */
        void load(InputStream inputStream, Builder builder) throws IOException;
    }

}
