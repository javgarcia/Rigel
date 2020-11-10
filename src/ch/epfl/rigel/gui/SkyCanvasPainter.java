package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;

import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;

import java.util.List;

/**
 * Class allowing the celestial objects and the horizon to be drawn on a canvas.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class SkyCanvasPainter {

    private final static double STANDARD_ANG_SIZE = Math.tan(Angle.ofDeg(0.5) / 4);
    private final Canvas canvas;
    private final GraphicsContext ctx;

    private final static double SUN_OPACITY = 0.25;
    private final static HorizontalCoordinates ZERO_COORDINATES = HorizontalCoordinates.of(0, 0);
    private final static double OCTANT_ALTITUDE_SHIFT = -0.5;

    private final static double PARALLEL_MERIDIAN_LINE_WIDTH = 0.25;

    // Used for ovals.
    private final static Color PARALLEL_MERIDIAN_COLOUR_OVALS = Color.LAVENDERBLUSH.deriveColor(0, 1, 1, 0.25);
    // Used for lines. Sets a bigger opacity factor in order to counteract the visual effect of the brighter ovals.
    private final static Color PARALLEL_MERIDIAN_COLOUR_LINES = Color.LAVENDERBLUSH.deriveColor(0, 1, 1, 0.5);


    /**
     * Constructor of the class.
     *
     * @param canvas the canvas on which the sky is drawn.
     */
    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
        this.ctx = this.canvas.getGraphicsContext2D();
    }


    /**
     * Clear method, draws a black rectangle on the whole canvas.
     */
    public void clear() {
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }


    /**
     * Draws the Moon on the canvas.
     *
     * @param sky                     the observed sky at a given zoned date time.
     * @param stereographicProjection the given stereographic projection of the coordinates onto a plane.
     * @param planeToCanvas           the affine transformation from the stereographic projection to the canvas.
     */
    public void drawMoon(ObservedSky sky, StereographicProjection stereographicProjection, Transform planeToCanvas) {

        CartesianCoordinates prevCoords = sky.moonPosition();
        // We use an overload method to construct the transformed cartesian coordinates.
        CartesianCoordinates canvasCoords = CartesianCoordinates.of(planeToCanvas.transform(prevCoords.x(), prevCoords.y()));

        double prevRadius = stereographicProjection.applyToAngle(sky.moon().angularSize()) / 2;
        // We get the absolute value of the delta transform x component as it corresponds to the magnitude of the resulting vector.
        double canvasRadius = Math.abs(planeToCanvas.deltaTransform(prevRadius, 0).getX());

        ctx.setFill(Color.WHITE);
        // We have to subtract by the radius as the x and y coordinates correspond to the upper left bound.
        drawOval(canvasCoords.x(), canvasCoords.y(), canvasRadius, true);
    }


    /**
     * Draws the Sun on the canvas.
     *
     * @param sky                     the observed sky at a given zoned date time.
     * @param stereographicProjection the given stereographic projection of the coordinates onto a plane.
     * @param planeToCanvas           the affine transformation from the stereographic projection to the canvas.
     */
    public void drawSun(ObservedSky sky, StereographicProjection stereographicProjection, Transform planeToCanvas) {

        CartesianCoordinates prevCoords = sky.sunPosition();
        // We use an overload method to construct the transformed cartesian coordinates.
        CartesianCoordinates canvasCoords = CartesianCoordinates.of(planeToCanvas.transform(prevCoords.x(), prevCoords.y()));

        double prevRadius = stereographicProjection.applyToAngle(sky.sun().angularSize()) / 2;
        // We get the absolute value of the delta transform x component as it corresponds to the magnitude of the resulting vector.
        double canvasRadius = Math.abs(planeToCanvas.deltaTransform(prevRadius, 0).getX());

        //Drawing  3 discs, starting with the largest one.
        // In the arguments of fillOval, have to subtract by the radius as the x and y coordinates correspond to the upper left bound.

        ctx.setFill(Color.YELLOW.deriveColor(0, 1, 1, SUN_OPACITY));
        double radiusBigDisc = 2.2 * canvasRadius;
        drawOval(canvasCoords.x(), canvasCoords.y(), radiusBigDisc, true);

        ctx.setFill(Color.YELLOW);
        double radiusMediumDisc = 2 + canvasRadius;
        drawOval(canvasCoords.x(), canvasCoords.y(), radiusMediumDisc, true);

        ctx.setFill(Color.WHITE);
        drawOval(canvasCoords.x(), canvasCoords.y(), canvasRadius, true);
    }


    /**
     * Draws the planets on the canvas.
     *
     * @param sky           the observed sky at a given zoned date time.
     * @param planeToCanvas the affine transformation from the stereographic projection to the canvas.
     */
    public void drawPlanets(ObservedSky sky, Transform planeToCanvas) {

        double[] planetsCoords = sky.planetsPosition();

        // The given array in the observed sky allow us to perform a simultaneous transformation of all the planets.
        planeToCanvas.transform2DPoints(planetsCoords, 0, planetsCoords, 0, planetsCoords.length / 2);

        List<Planet> planets = sky.planets();
        for (int i = 0; i < planets.size(); ++i) {
            // The radius is computed depending on the magnitude.
            double canvasRadius = RadiusOf(planets.get(i));
            // We get the absolute value of the delta transform x component as it corresponds to the magnitude of the resulting vector.
            double finalRadius = Math.abs(planeToCanvas.deltaTransform(canvasRadius, 0).getX());

            ctx.setFill(Color.LIGHTGRAY);
            drawOval(planetsCoords[2 * i], planetsCoords[2 * i + 1], finalRadius, true);

        }
    }


    /**
     * Draws the stars on the canvas.
     *
     * @param sky                the observed sky at a given zoned date time.
     * @param planeToCanvas      the affine transformation from the stereographic projection to the canvas.
     * @param drawConstellations boolean that indicates whether the constellations or the asterisms should  by drawn.
     */
    public void drawStars(ObservedSky sky, Transform planeToCanvas, boolean drawConstellations) {

        double[] starsCoords = sky.starsPosition();
        // The given array in the observed sky allow us to perform a simultaneous transformation of all the planets.
        planeToCanvas.transform2DPoints(starsCoords, 0, starsCoords, 0, starsCoords.length / 2);

        List<Star> stars = sky.stars();
        // Before drawing the stars, we draw the asterisms to prevent them to obstruct the star thereafter.
        drawAsterismsOrConstellations(sky, starsCoords, drawConstellations);

        for (int i = 0; i < stars.size(); ++i) {
            Star star = stars.get(i);
            double canvasRadius = RadiusOf(star);
            // We get the absolute value of the delta transform x component as it corresponds to the magnitude of the resulting vector.
            double finalRadius = Math.abs(planeToCanvas.deltaTransform(canvasRadius, 0).getX());
            // We compute the stars color using the BlackBodyColor class.
            ctx.setFill(BlackBodyColor.colorForTemperature(star.colorTemperature()));
            drawOval(starsCoords[2 * i], starsCoords[2 * i + 1], finalRadius, true);
        }
    }


    /**
     * Draws the asterisms or the constellations on the canvas.
     *
     * @param sky                the observed sky at a given zoned date time.
     * @param starsCoords        the star coordinates in the observed sky catalogue.
     * @param drawConstellations boolean that indicates whether the constellations or the asterisms should by drawn.
     */
    private void drawAsterismsOrConstellations(ObservedSky sky, double[] starsCoords, boolean drawConstellations) {
        // We get the bounds of the sky painter canvas.
        Bounds b = canvas.getBoundsInLocal();

        //We set the width and color of the asterisms lines.
        ctx.setLineWidth(1.0);

        if (drawConstellations) {
            ctx.setStroke(Color.LIGHTGREEN);
        } else {
            ctx.setStroke(Color.BLUE);
        }

        List<Integer> indixes;
        for (Asterism ast : sky.asterisms()) {


            // We get the indexes of the stars in each asterism.
            indixes = sky.asterismIndices(ast);
            int firstIndex = 2 * indixes.get(0);
            // Thus, we get a correspondence between the stars indexes in the asterisms and their coordinates in the array.
            double x = starsCoords[firstIndex];
            double y = starsCoords[firstIndex + 1];

            // For each star in the asterism, we compute the next star coordinates and check if at least one of them is present on the canvas.
            // The textCoordX and textCoordY helps on placing the text of the constellation in a better way.
            double textCoordX = 0;
            double textCoordY = 0;

            for (int i = 1; i < indixes.size(); ++i) {
                int index = 2 * indixes.get(i);
                double x1 = starsCoords[index];
                double y1 = starsCoords[index + 1];
                if (b.contains(x, y) || b.contains(x1, y1)) {
                    ctx.strokeLine(x, y, x1, y1);
                }
                textCoordX = (x + x1) / 2;
                textCoordY = (y + y1) / 2;
                x = x1;
                y = y1;
            }

            String currentConstellation = ast.getConstellationName();
            if (!currentConstellation.equals("-") && drawConstellations)
                ctx.strokeText(currentConstellation, textCoordX, textCoordY);
        }
    }


    /**
     * Draws the horizon and the 8 octants on the canvas.
     *
     * @param projection        the given stereographic projection of the coordinates onto a plane.
     * @param planeToCanvas     the affine transformation from the stereographic projection to the canvas.
     * @param roundedViewAltDeg the current viewing altitude parameter, rounded, in degrees.
     * @param canvasWidth       the current width of the canvas.
     */
    public void drawHorizon(StereographicProjection projection, Transform planeToCanvas, int roundedViewAltDeg, double canvasWidth) {

        ctx.setLineWidth(2.0);
        ctx.setStroke(Color.RED);

        // Draw an oval representing the horizon.
        if (roundedViewAltDeg != 0) {
            CartesianCoordinates center = projection.circleCenterForParallel(ZERO_COORDINATES);
            double radius = projection.circleRadiusForParallel(ZERO_COORDINATES);

            CartesianCoordinates canvasCenter = CartesianCoordinates.of(planeToCanvas.transform(center.x(), center.y()));
            // We get the absolute value of the delta transform as it corresponds to the magnitude of the resulting vector.
            double canvasRadius = Math.abs(planeToCanvas.deltaTransform(radius, 0).getX());

            drawOval(canvasCenter.x(), canvasCenter.y(), canvasRadius, false);

            // Draws the line representing the horizon when the latitude of the projection center and the parallel equals 0.
        } else {
            HorizontalCoordinates projCenter = projection.getCenter();

            HorizontalCoordinates zeroProjCenter = HorizontalCoordinates.of(projCenter.az(), 0);
            CartesianCoordinates cartZeroProjCenter = projection.apply(zeroProjCenter);
            double canvasZeroAlt = planeToCanvas.transform(0, cartZeroProjCenter.y()).getY();

            ctx.strokeLine(0, canvasZeroAlt, canvasWidth, canvasZeroAlt);
        }


        // Drawing the cardinal points.
        ctx.setTextBaseline(VPos.TOP);
        ctx.setFill(Color.RED);
        for (int i = 0; i < 8; ++i) {
            HorizontalCoordinates h = HorizontalCoordinates.ofDeg(45 * i, OCTANT_ALTITUDE_SHIFT);
            String octantName = h.azOctantName("N", "E", "S", "O");

            CartesianCoordinates c = projection.apply(h);
            CartesianCoordinates f = CartesianCoordinates.of(planeToCanvas.transform(c.x(), c.y()));
            ctx.fillText(octantName, f.x(), f.y());
        }
    }


    /**
     * Draws the parallels on the canvas.
     *
     * @param projection    the given stereographic projection of the coordinates onto a plane.
     * @param planeToCanvas the affine transformation from the stereographic projection to the canvas.
     * @param canvasWidth   the current width of the canvas.
     */
    public void drawParallels(StereographicProjection projection, Transform planeToCanvas, double canvasWidth) {
        ctx.setLineWidth(PARALLEL_MERIDIAN_LINE_WIDTH);
        ctx.setStroke(PARALLEL_MERIDIAN_COLOUR_OVALS);

        // Draws all the parallels.
        for (int i = -18; i <= 18; ++i) {
            CartesianCoordinates center = projection.circleCenterForParallelAltDeg(5 * i);
            double radius = projection.circleRadiusForParallelAltDeg(5 * i);

            drawCanvasOval(planeToCanvas, center, radius);
        }

        // Draws a line representing the parallel that has a infinite radius.
        // The position of this parallel depends on the altitude degree of the viewing parameters:
        // It is the parallel located at the inverse of the current altitude (of the viewing parameters).
        HorizontalCoordinates projCenter = projection.getCenter();

        HorizontalCoordinates invertedProjCenter = HorizontalCoordinates.of(projCenter.az(), -projCenter.alt());
        CartesianCoordinates cartInvProjCenter = projection.apply(invertedProjCenter);
        CartesianCoordinates canvasInvProjCenter = CartesianCoordinates.of(planeToCanvas.transform(cartInvProjCenter.x(), cartInvProjCenter.y()));

        ctx.strokeLine(0, canvasInvProjCenter.y(), canvasWidth, canvasInvProjCenter.y());
    }


    /**
     * Draws the meridians on the canvas.
     *
     * @param projection        the given stereographic projection of the coordinates onto a plane.
     * @param planeToCanvas     the affine transformation from the stereographic projection to the canvas.
     * @param roundedViewAltDeg the current viewing altitude parameter, rounded, in degrees.
     * @param canvasWidth       the current width of the canvas.
     * @param canvasHeight      the current height of the canvas.
     */
    public void drawMeridians(StereographicProjection projection, Transform planeToCanvas, int roundedViewAltDeg, double canvasWidth, double canvasHeight) {
        ctx.setLineWidth(PARALLEL_MERIDIAN_LINE_WIDTH);
        double halfCanvasHeight = canvasHeight / 2;
        double halfCanvasWidth = canvasWidth / 2;

        // Draws all the meridians when the projection center is not at 90º of altitude.
        if (roundedViewAltDeg != 90) {
            ctx.setStroke(PARALLEL_MERIDIAN_COLOUR_OVALS);
            for (int i = 0; i <= 35; ++i) {
                CartesianCoordinates center = projection.circleCenterForMeridianAzDeg(10 * i);
                double radius = projection.circleRadiusForMeridianAzDeg(10 * i);

                drawCanvasOval(planeToCanvas, center, radius);
            }

            // Draws the lines representing the meridians when they have infinite radius in the stereographic projection.
            // The latitude of the viewing parameters always changes by steps of 10 degrees.
        } else {

            ctx.setStroke(PARALLEL_MERIDIAN_COLOUR_LINES);
            for (int i = 1; i <= 8; ++i) {
                // Height representing the distance between the point of the meridian intersecting the canvas limit and
                // the horizontal line which is in the middle of the canvas.
                double height = Math.tan(Angle.ofDeg(10 * i)) * canvasWidth / 2;

                // Each strokeLine call draws a symmetric meridian.
                ctx.strokeLine(0, halfCanvasHeight + height, canvasWidth, halfCanvasHeight - height);
                ctx.strokeLine(0, halfCanvasHeight - height, canvasWidth, halfCanvasHeight + height);
            }
            // The meridian represented by the horizontal line in the middle of the canvas.
            ctx.strokeLine(0, halfCanvasHeight, canvasWidth, halfCanvasHeight);
        }

        // The meridian represented by the vertical line in the middle of the canvas.
        // It has to be drawn in all the possible viewing altitude cases.
        ctx.setStroke(PARALLEL_MERIDIAN_COLOUR_LINES);
        ctx.strokeLine(halfCanvasWidth, 0, halfCanvasWidth, canvasHeight);

    }


    /**
     * Transforms the stereographic projection coordinates of the center and the radius
     * into the canvas coordinates of these elements.
     * Draws the oval given by the converted canvas parameters(center and radius).
     *
     * @param planeToCanvas the affine transformation from the stereographic projection to the canvas.
     * @param center        the coordinates of the center in the stereographic projection.
     * @param radius        the magnitude of the radius in the stereographic projection.
     */
    private void drawCanvasOval(Transform planeToCanvas, CartesianCoordinates center, double radius) {
        CartesianCoordinates canvasCenter = CartesianCoordinates.of(planeToCanvas.transform(center.x(), center.y()));
        // We get the absolute value of the delta transform as it corresponds to the magnitude of the resulting vector.
        double canvasRadius = Math.abs(planeToCanvas.deltaTransform(radius, 0).getX());

        ctx.setLineWidth(PARALLEL_MERIDIAN_LINE_WIDTH);
        ctx.setStroke(PARALLEL_MERIDIAN_COLOUR_OVALS);
        drawOval(canvasCenter.x(), canvasCenter.y(), canvasRadius, false);
    }


    /**
     * Computes the radius of a celestial object using its magnitude.
     *
     * @param celestialObject the given celestial object.
     * @return the object radius.
     */
    private double RadiusOf(CelestialObject celestialObject) {
        double magnitude = celestialObject.magnitude();

        ClosedInterval c = ClosedInterval.of(-2, 5);
        double clippedMagnitude = c.clip(magnitude);

        double factor = (99 - 17 * clippedMagnitude) / 140d;

        return factor * STANDARD_ANG_SIZE;
    }


    /**
     * Generalization of the strokeOval and fillOval methods.
     *
     * @param centerX  x-coordinate of the oval center
     * @param centerY  y-coordinate of the oval center;
     * @param radius   radius of the oval (considering that we only draw circles or disks.
     * @param isFilled indicates whether a disk or a circle has to be drawn.
     */
    private void drawOval(double centerX, double centerY, double radius, boolean isFilled) {
        double diameter = 2 * radius;
        if (isFilled) {
            ctx.fillOval(centerX - radius, centerY - radius, diameter, diameter);
        } else {
            ctx.strokeOval(centerX - radius, centerY - radius, diameter, diameter);
        }
    }

}

