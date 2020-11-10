package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

/**
 * Class providing methods in order to work with polynomials.
 *
 * @author Victor Nazianzeno -- Le Jamtel(312075)
 * @author Javier García Arredondo(311166)
 */
public final class Polynomial {

    //An array collecting the coefficients of the polynomial in decreasing order.
    //Guarantees the immutability of the class.
    private final double[] coeffList;


    /**
     * Copies the given coefficient array into the attribute array in order to guarantee the immutability of the class.
     *
     * @param inputCoefficientsList the given coefficients array.
     */
    private Polynomial(double[] inputCoefficientsList) {
        coeffList = inputCoefficientsList;
    }


    /**
     * Creates a new polynomial collecting the given coefficients.
     *
     * @param coefficientN the coefficient of the monomial with higher value of the polynomial. Can not be 0.
     * @param coefficients the other coefficients in decreasing order.
     * @return a polynomial collecting the given coefficients.
     * @throws IllegalArgumentException if the coefficient N is null.
     */
    public static Polynomial of(double coefficientN, double... coefficients) {

        Preconditions.checkArgument(coefficientN != 0);
        double[] allCoefficients = new double[coefficients.length + 1];

        allCoefficients[0] = coefficientN;
        System.arraycopy(coefficients, 0, allCoefficients, 1, allCoefficients.length - 1);

        return new Polynomial(allCoefficients);
    }


    /**
     * Computes the value of the polynomial on the given x.
     *
     * @param x Value of x.
     * @return the value of the polynomial.
     */
    public double at(double x) {
        double totalAddition;

        totalAddition = coeffList[0];
        for (int i = 1; i < coeffList.length; ++i) {
            totalAddition = totalAddition * x;
            totalAddition += coeffList[i];
        }
        return totalAddition;
    }


    /**
     * Redefinition of the Object method.
     * Does not print a monomial with coefficient 0.
     * Does not print the coefficient part of a monomial with coefficient 1.
     * Does not print ^1 pour a x with degree 1.
     * Doest not print the x for a monomial of degree 0.
     *
     * @return the string displaying the polynomial.
     * @see Object#toString().
     */
    @Override
    public String toString() {
        StringBuilder pFormat = new StringBuilder();

        for (int i = 0; i < coeffList.length; i++) {

            double c = coeffList[i];

            if (c != 0) {

                if (c > 0 && i > 0) {
                    pFormat.append("+");
                } else if (c < 0) {
                    pFormat.append("-");
                }

                if ((Math.abs((int) c) != 1)) {
                    pFormat.append(Math.abs(c));
                }

                if (i < coeffList.length - 2) {
                    pFormat.append(("x^"));
                    pFormat.append(coeffList.length - 1 - i);
                } else if (i == coeffList.length - 2) {
                    pFormat.append("x");
                }
            }
        }

        return pFormat.toString();
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
