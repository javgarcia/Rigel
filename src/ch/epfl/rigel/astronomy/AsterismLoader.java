package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Loader for the asterisms file.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public enum AsterismLoader implements StarCatalogue.Loader {
    INSTANCE;


    /**
     * Method that loads the different asterisms in a star catalogue builder.
     *
     * @param inputStream the given inputStream, i.e. the asterisms file.
     * @param builder     the builder of the star catalogue.
     * @throws IOException if there is an input error.
     */
    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {

        Charset c = StandardCharsets.US_ASCII;
        String aString;

        // We create a HashMap to store the stars loaded in the Builder alongside their hipparcos Id.
        HashMap<Integer, Star> starHashMap = new HashMap<>();
        for (Star star : builder.stars()) {
            starHashMap.put(star.hipparcosId(), star);
        }

        try (BufferedReader r = new BufferedReader(new InputStreamReader(inputStream, c))) {
            while ((aString = r.readLine()) != null) {

                // Using the split method, we retrieve the different hipparcos ID for each asterism.
                String[] parts = aString.split(",");
                List<Star> starList = new ArrayList<>();
                String constellation = parts[parts.length-1];
                for (int i = 0; i <parts.length -1 ; i++) {

                    if (starHashMap.containsKey(Integer.parseInt(parts[i])) && Integer.parseInt(parts[i]) > 0) {
                        starList.add(starHashMap.get(Integer.parseInt(parts[i])));
                }}

                builder.addAsterism(new Asterism(starList, constellation));
            }

        }
    }

}
