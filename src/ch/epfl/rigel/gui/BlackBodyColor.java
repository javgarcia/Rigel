package ch.epfl.rigel.gui;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Non instantiable class, determines the stars color.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class BlackBodyColor {

    private final static String BBCOLOR_DATA = "/bbr_color.txt";

    private final static HashMap<Integer, Color> TEMPERATURE_TO_COLOR = init();

    private final static ClosedInterval TEMPERATURE_INTERVAL = ClosedInterval.of(1000, 40_000);


    /**
     * Private constructor, avoids instantiation.
     */
    private BlackBodyColor() {}


    /**
     * Given a temperature of the star, returns the body color.
     *
     * @param tempInKelvin the star temperature, in kelvin.
     * @return the corresponding color.
     * @throws IllegalArgumentException if the temperature in Kelvin is not included in the closed interval 1000K - 40000K.
     */
    public static Color colorForTemperature(double tempInKelvin) {
        Preconditions.checkInInterval(TEMPERATURE_INTERVAL, tempInKelvin);
        int roundValue = (int) Math.round(tempInKelvin / 100) * 100;
        return TEMPERATURE_TO_COLOR.get(roundValue);
    }


    /**
     * Initialization of Temperature-to-color map using the dataset file "bbr_color.txt".
     *
     * @throws UncheckedIOException if there is an input error.
     */
    private static HashMap<Integer,Color> init() throws UncheckedIOException {
        String line;
        final HashMap<Integer, Color> tempToColor = new HashMap<>();
        final Charset charset = StandardCharsets.US_ASCII;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(BlackBodyColor.class.getResourceAsStream(BBCOLOR_DATA), charset))) {
            while ((line = r.readLine()) != null) {
                if (line.charAt(0) != '#' && line.substring(10, 15).equalsIgnoreCase("10deg")) {
                    tempToColor.put(toInteger(line.substring(1, 6)), Color.web(line.substring(80, 87)));
                }
            }

            return tempToColor;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    /**
     * Conversion of a string to an integer.
     *
     * @param s the given string.
     * @return an integer, omitting the case where s cannot be an integer.
     */
    private static int toInteger(String s) {
        // In the  file, the temperature can have 4 or 5 digits.
        return (s.charAt(0) == ' ') ? Integer.parseInt(s.substring(1)) : Integer.parseInt(s);
    }
}