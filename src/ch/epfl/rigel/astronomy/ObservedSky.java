package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class which represents a set of celestial objects using a specific stereographic projection,
 * given the moment and the geographic position of the observation.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class ObservedSky {

    private final StarCatalogue catalogue;
    private final StereographicProjection stereoProj;

    //Map that includes all the celestial objects of the observed sky and their respective coordinates.
    private final HashMap<CelestialObject, CartesianCoordinates> celestialToCoordinates = new HashMap<>();

    private final Sun sun;
    private final CartesianCoordinates sunCarCoords;

    private final Moon moon;
    private final CartesianCoordinates moonCarCoords;

    private final static List<PlanetModel> PLANET_MODELS = PlanetModel.ALL;

    private final List<Planet> planetsList = new ArrayList<>();
    private final double[] planetCoords;

    private final List<Star> starsList;
    private final double[] starCoords;


    /**
     * Constructor which creates the representation of the moon, the Sun,
     * the planets (except the Earth) and the stars of the catalogue,
     * and the respective projected coordinates for each celestial object.
     *
     * @param obsTime    the observation moment.
     * @param obsCoords  the observation position (in geographic coordinates).
     * @param stereoProj the stereographic projection to employ.
     * @param catalogue  the catalogue which includes the stars and the asterisms.
     */
    public ObservedSky(ZonedDateTime obsTime, GeographicCoordinates obsCoords, StereographicProjection stereoProj, StarCatalogue catalogue) {
        double daysSinceJ2010 = Epoch.J2010.daysUntil(obsTime);
        this.stereoProj = stereoProj;
        this.catalogue = catalogue;

        EclipticToEquatorialConversion eclConversion = new EclipticToEquatorialConversion(obsTime);
        EquatorialToHorizontalConversion eqConversion = new EquatorialToHorizontalConversion(obsTime, obsCoords);

        //Sun representation and its projected coordinates.
        sun = SunModel.SUN.at(daysSinceJ2010, eclConversion);
        sunCarCoords = cartesianCoordinatesOf(sun, eqConversion);
        celestialToCoordinates.put(sun, sunCarCoords);

        //Moon representation and its projected coordinates.
        moon = MoonModel.MOON.at(daysSinceJ2010, eclConversion);
        moonCarCoords = cartesianCoordinatesOf(moon, eqConversion);
        celestialToCoordinates.put(moon, moonCarCoords);


        //Planet representations and their projected coordinates.
        int planetsSize = PLANET_MODELS.size();
        planetCoords = new double[2 * (planetsSize - 1)];

        int index = 0;
        for (PlanetModel planetModel : PLANET_MODELS) {
            if (!planetModel.equals(PlanetModel.EARTH)) {

                Planet planet = planetModel.at(daysSinceJ2010, eclConversion);
                planetsList.add(planet);
                CartesianCoordinates cartCoords = cartesianCoordinatesOf(planet, eqConversion);
                planetCoords[2 * index] = cartCoords.x();
                planetCoords[2 * index + 1] = cartCoords.y();
                celestialToCoordinates.put(planet, cartCoords);
                ++index;
            }
        }


        //Star representations and their projected coordinates.
        starsList = catalogue.stars();
        int starListSize = starsList.size();
        starCoords = new double[2 * starListSize];

        for (int i = 0; i < starListSize; ++i) {
            Star star = starsList.get(i);
            CartesianCoordinates cartCoords = cartesianCoordinatesOf(star, eqConversion);
            starCoords[2 * i] = cartCoords.x();
            starCoords[2 * i + 1] = cartCoords.y();
            celestialToCoordinates.put(star, cartCoords);
        }
    }


    /**
     * Computes the cartesian coordinates of a celestial object projection.
     *
     * @param celestialObject the given celestial object.
     * @param eqConversion    the equatorial to horizontal conversion.
     * @return the cartesian coordinates.
     */
    private CartesianCoordinates cartesianCoordinatesOf(CelestialObject celestialObject, EquatorialToHorizontalConversion eqConversion) {
        Function<EquatorialCoordinates, CartesianCoordinates> eqToCart = eqConversion.andThen(stereoProj);
        return eqToCart.apply(celestialObject.equatorialPos());
    }


    /**
     * Getter of the immutable representation of the Sun.
     *
     * @return the Sun.
     */
    public Sun sun() {
        return sun;
    }


    /**
     * Getter of the cartesian coordinates of the Sun projection.
     *
     * @return the cartesian coordinates.
     */
    public CartesianCoordinates sunPosition() {
        return sunCarCoords;
    }


    /**
     * Getter of the immutable representation of the moon.
     *
     * @return the moon.
     */
    public Moon moon() {
        return moon;
    }


    /**
     * Getter of the cartesian coordinates of the moon projection.
     *
     * @return the cartesian coordinates.
     */
    public CartesianCoordinates moonPosition() {
        return moonCarCoords;
    }


    /**
     * Getter of a immutable list including the immutable representations of the planets (except the Earth).
     *
     * @return the list with the planets.
     */
    public List<Planet> planets() {
        return List.copyOf(planetsList);
    }


    /**
     * Getter of a copy of the table including the cartesian coordinates of the planet projections.
     * The table includes first the x coordinate and then the y coordinate for each planet.
     * The planet coordinates appear in the same order as the planets are included in the planet list of the class.
     *
     * @return the table with the coordinates.
     */
    public double[] planetsPosition() {
        return planetCoords.clone();
    }


    /**
     * Getter of a immutable list including the immutable representations of the stars.
     *
     * @return the list with the stars.
     */
    public List<Star> stars() {
        return starsList;
    }


    /**
     * Getter of a copy of the table including the cartesian coordinates of the star projections.
     * The table includes first the x coordinate and then the y coordinate for each star.
     * The star coordinates appear in the same order as the stars are included in the star list of the class.
     *
     * @return the table with the coordinates.
     */
    public double[] starsPosition() {
        return starCoords.clone();
    }


    /**
     * Getter for the asterisms list (as a set).
     *
     * @return an immutable copy of the asterisms set.
     * @see StarCatalogue#asterisms()
     */
    public Set<Asterism> asterisms() {
        return catalogue.asterisms();
    }

    /**
     * Getter for the star indexes of the given asterism.
     *
     * @param asterism the asterism.
     * @return the stars indexes of the given asterism.
     * @throws IllegalArgumentException if the given asterism is not included in the asterisms list of the star catalogue.
     * @see StarCatalogue#asterismIndices(Asterism)
     */
    public List<Integer> asterismIndices(Asterism asterism) {
        return catalogue.asterismIndices(asterism);
    }


    /**
     * Finds the celestial object in the observed sky which is the closest to the given cartesian coordinates,
     * if the distance between the coordinates and the celestial object is shorter than the given maximum distance.
     *
     * @param cartCoords  the cartesian coordinates a point.
     * @param maxDistance the maximum distance.
     * @return an empty cell if there is no object within the maxDistance,
     * or a cell containing the closest object to the cartCoords.
     */
    public Optional<CelestialObject> objectClosestTo(CartesianCoordinates cartCoords, double maxDistance) {

        /* filteredCelestToCoord is a map which includes a selection of celestial objects within an illustrative distance,
        in order to reduce later the number of slower (but necessary) operations computed by the computeDistance method.

        A stream is used to filter the celestial objects map. The collect method then allows the construction of the filtered map.
        The filter predicate states that the object's abscissa and ordinate coordinates must respectively be at most maxDistance away
        from the given cartesian coordinates in the argument.
        */

        Map<CelestialObject, CartesianCoordinates> filteredCelestToCoords =
                celestialToCoordinates.entrySet()
                        .stream()
                        .filter(map -> Math.abs(map.getValue().x() - cartCoords.x()) <= maxDistance
                                    && Math.abs(map.getValue().y() - cartCoords.y()) <= maxDistance)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        CelestialObject closestObject = null;
        //Temporary squared distance that decreases according to the distance to the current closest object.
        double closestDistanceSquared = maxDistance * maxDistance;
        double currentObjectDistance;

        if (!filteredCelestToCoords.isEmpty()) {
            for (Map.Entry<CelestialObject, CartesianCoordinates> entry : filteredCelestToCoords.entrySet()) {

                if ((currentObjectDistance = entry.getValue().distanceSquared(cartCoords)) <= closestDistanceSquared) {
                    closestDistanceSquared = currentObjectDistance;
                    closestObject = entry.getKey();
                }
            }
            return (closestObject == null) ? Optional.empty() : Optional.of(closestObject);
        } else {
            return Optional.empty();
        }
    }

}