package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

import java.util.Locale;
import java.util.function.Function;

import static java.lang.Math.*;

/**
 * Mapping that projects a sphere onto a plane.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class StereographicProjection implements Function<HorizontalCoordinates, CartesianCoordinates> {

    private final double lambdaCenter;
    private final double phiCenter;
    private final double sinPhiCenter;
    private final double cosPhiCenter;


    /**
     * Constructor, takes horizontal coordinates in argument,
     * i.e. the center's coordinates.
     *
     * @param center coordinates of the center of the projection.
     */
    public StereographicProjection(HorizontalCoordinates center) {

        //  Computing useful values for future calculations.
        lambdaCenter = center.az();
        phiCenter = center.alt();
        sinPhiCenter = sin(phiCenter);
        cosPhiCenter = cos(phiCenter);
    }


    /**
     * Getter for the projection center in horizontal coordinates.
     *
     * @return an instance of HorizontalCoordinates.
     */
    public HorizontalCoordinates getCenter() {
        return HorizontalCoordinates.of(lambdaCenter, phiCenter);
    }


    /**
     * Returns the coordinates of the center of the circle corresponding to the parallel's projection going
     * through the point with given coordinates.
     *
     * @param parallel the given horizontal coordinates of the parallel.
     * @return cartesian coordinates of the center.
     */
    public CartesianCoordinates circleCenterForParallel(HorizontalCoordinates parallel) {
        double coordinateY = cosPhiCenter / (sin(parallel.alt()) + sinPhiCenter);
        return CartesianCoordinates.of(0, coordinateY);
    }


    /**
     * Returns the coordinates of the center of the circle corresponding to the parallel's projection going
     * through the point with given altitude.
     *
     * @param alt the given horizontal altitude coordinate of the parallel, in degrees.
     * @return cartesian coordinates of the center.
     */
    public CartesianCoordinates circleCenterForParallelAltDeg(double alt) {
        double coordinateY = cosPhiCenter / (sin(Angle.ofDeg(alt)) + sinPhiCenter);
        return CartesianCoordinates.of(0, coordinateY);
    }


    /**
     * Returns the radius of the circle corresponding to the parallel's projection going
     * through the point with given coordinates.
     *
     * @param parallel the given horizontal coordinates of the parallel.
     * @return a radius (can be infinite).
     */
    public double circleRadiusForParallel(HorizontalCoordinates parallel) {
        return cos(parallel.alt()) / (sin(parallel.alt()) + sinPhiCenter);
    }


    /**
     * Returns the radius of the circle corresponding to the parallel's projection going
     * through the point with given coordinates.
     *
     * @param alt the given horizontal altitude coordinate of the parallel, in degrees.
     * @return a radius (can be infinite).
     */
    public double circleRadiusForParallelAltDeg(double alt) {
        double altRad = Angle.ofDeg(alt);
        return cos(altRad) / (sin(altRad) + sinPhiCenter);
    }


    /**
     * Returns the coordinates of the center of the circle corresponding to the meridian's projection going
     * through the point with given coordinates.
     *
     * @param az the given horizontal azimuth coordinate of the meridian, in degrees.
     * @return cartesian coordinates of the center.
     */
    public CartesianCoordinates circleCenterForMeridianAzDeg(double az) {
        double coordinateX = -1 / (cosPhiCenter * tan(Angle.ofDeg(az) - lambdaCenter));
        double coordinateY = -tan(phiCenter);
        return CartesianCoordinates.of(coordinateX, coordinateY);
    }


    /**
     * Returns the radius of the circle corresponding to the meridian's projection going
     * through the point with given coordinates.
     *
     * @param az the given horizontal azimuth coordinate of the meridian, in degrees.
     * @return a radius (can be infinite).
     */
    public double circleRadiusForMeridianAzDeg(double az) {
        return 1 / (cosPhiCenter * sin(Angle.ofDeg(az) - lambdaCenter));
    }


    /**
     * Given the angular size, returns the projected diameter of the sphere.
     *
     * @param rad the given angular size.
     * @return the projected diameter.
     */
    public double applyToAngle(double rad) {
        return 2 * tan(rad / 4.0);
    }


    /**
     * Method that projects horizontal coordinates onto a plane.
     *
     * @param azAlt the given horizontal coordinates.
     * @return the cartesian coordinates of the projection of this point onto a plane.
     */
    @Override
    public CartesianCoordinates apply(HorizontalCoordinates azAlt) {
        double lambda = azAlt.az();
        double phi = azAlt.alt();

        double sinPhi = sin(phi);
        double cosPhi = cos(phi);

        double lambdaDifference = lambda - lambdaCenter;

        double sinLambdaDelta = sin(lambdaDifference);
        double cosLambdaDelta = cos(lambdaDifference);

        double d = 1d / (1d + sinPhi * sinPhiCenter + cosPhi * cosPhiCenter * cosLambdaDelta);

        double x = d * cosPhi * sinLambdaDelta;
        double y = d * (sinPhi * cosPhiCenter - cosPhi * sinPhiCenter * cosLambdaDelta);

        return CartesianCoordinates.of(x, y);
    }


    /**
     * Converts the projection xy to horizontal coordinates.
     *
     * @param cartCoords the given cartesian coordinates.
     * @return horizontal coordinates.
     */
    public HorizontalCoordinates inverseApply(CartesianCoordinates cartCoords) {
        double xCoordinate = cartCoords.x();
        double yCoordinate = cartCoords.y();

        if (xCoordinate == 0 && yCoordinate == 0) {
            return HorizontalCoordinates.of(Angle.normalizePositive(lambdaCenter), phiCenter);
        } else {
            double squaredRho = xCoordinate * xCoordinate + yCoordinate * yCoordinate;
            double rho = sqrt(squaredRho);

            double sinC = 2 * rho / (squaredRho + 1);
            double cosC = (1 - squaredRho) / (squaredRho + 1);

            double denominator = rho * cosPhiCenter * cosC - yCoordinate * sinPhiCenter * sinC;

            double lambda = atan2(xCoordinate * sinC, denominator) + lambdaCenter;
            double phi = asin(cosC * sinPhiCenter + (yCoordinate * sinC * cosPhiCenter) / rho);

            return HorizontalCoordinates.of(Angle.normalizePositive(lambda), phi);
        }

    }


    /**
     * @return the string.
     * @see Object#toString()
     * String including the stereographic projection coordinates, respectively.
     * Approximates on the 6th decimal number.
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "StereographicProjection (%.6f, %.6f)", lambdaCenter, phiCenter);
    }


    /**
     * @return throws an exception.
     * @throws UnsupportedOperationException if the method is called.
     * @see Object#equals(Object)
     */
    @Override
    public final boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return throws an exception.
     * @throws UnsupportedOperationException if the method is called.
     * @see Object#hashCode()
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

}
