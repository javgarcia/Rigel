package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Loads the content of the HYG database which contains the information about a list of stars.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public enum HygDatabaseLoader implements StarCatalogue.Loader {
    INSTANCE;


    /**
     * Loads the content of the inputStream in order to add stars to the star catalogue builder.
     *
     * @param inputStream the given input stream, i.e a file containing stars data.
     * @param builder     the given star catalogue builder.
     * @throws IOException if there is an input error.
     */
    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {

        Charset c = StandardCharsets.US_ASCII;

        try (BufferedReader r = new BufferedReader(new InputStreamReader(inputStream, c))) {

            r.readLine();

            while (r.ready()) {
                String[] parts = r.readLine().split(",");

                //Hipparcos number of the star, 0 by default.
                int hipparcosNumber = Integer.parseInt(stringOf(parts, Columns.HIP, "0"));

                //Name of the star.
                String name;
                if (parts[Columns.PROPER.ordinal()].isEmpty()) {
                    name = stringOf(parts, Columns.BAYER, "?");
                    name += " " + parts[Columns.CON.ordinal()];
                } else {
                    name = parts[Columns.PROPER.ordinal()];
                }

                //Equatorial coordinates of the star which are always defined (no default value).
                EquatorialCoordinates eqPosition =
                        EquatorialCoordinates.of(
                                (Double.parseDouble(parts[Columns.RARAD.ordinal()])),
                                Double.parseDouble(parts[Columns.DECRAD.ordinal()]));

                //Magnitude of the star, 0 by default.
                float magnitude = (float) Double.parseDouble(stringOf(parts, Columns.MAG, "0"));

                //Color index of the star, 0 by default.
                float colorIndex = (float) Double.parseDouble(stringOf(parts, Columns.CI, "0"));

                builder.addStar(new Star(hipparcosNumber, name, eqPosition, magnitude, colorIndex));
            }
        }
    }


    /**
     * Checking if a string at a certain column in an array is empty, either returning the content or a default value.
     *
     * @param strings      the string array.
     * @param column       the column, i.e the index of the string in the array.
     * @param defaultValue the default string to be returned.
     * @return a string, either the content of the array at the given index, or the default string.
     */
    private String stringOf(String[] strings, Columns column, String defaultValue) {
        String content = strings[column.ordinal()];
        return (content.isEmpty()) ? defaultValue : content;
    }


    /**
     * Private enum which helps for the load method in order to identify
     * the required columns from which we obtain the correct values.
     */
    private enum Columns {
        ID, HIP, HD, HR, GL, BF, PROPER, RA, DEC, DIST, PMRA, PMDEC,
        RV, MAG, ABSMAG, SPECT, CI, X, Y, Z, VX, VY, VZ,
        RARAD, DECRAD, PMRARAD, PMDECRAD, BAYER, FLAM, CON,
        COMP, COMP_PRIMARY, BASE, LUM, VAR, VAR_MIN, VAR_MAX
    }

}
