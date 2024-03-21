package uk.ac.nulondon;

import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class EnergyCalculation {
    private double a, b, c, d, e, f, g, h, i;

    /**
     * Returns the brightness of a given pixel
     * @param pixel : Pixel to get brightness of
     * @return double : Brightness value
     */
    private static double Brightness(Color pixel) {
        return (pixel.getRed() + pixel.getGreen() + pixel.getBlue()) / 3.0;
    }

    /**
     * Generates an object to calculate energy from
     * @param matrix : 3x3 grid of pixels to get energy from
     * @throws IllegalArgumentException : Thrown if matrix is not 3x3
     */
    public EnergyCalculation(ArrayList<ArrayList<Color>> matrix) throws IllegalArgumentException {
        // a b c
        // d e f
        // g h i

        if (matrix.size() != 3) {
            throw new IllegalArgumentException("Matrix must be 3x3");
        }
        for (ArrayList<Color> row : matrix) {
            if (row.size() != 3) {
                throw new IllegalArgumentException("Matrix must be 3x3");
            }
        }

        a = Brightness(matrix.getFirst().getFirst());
        b = Brightness(matrix.getFirst().get(1));
        c = Brightness(matrix.getFirst().getLast());
        d = Brightness(matrix.get(1).getFirst());
        e = Brightness(matrix.get(1).get(1));
        f = Brightness(matrix.get(1).getLast());
        g = Brightness(matrix.getLast().getFirst());
        h = Brightness(matrix.getLast().get(1));
        i = Brightness(matrix.getLast().getLast());
    }

    /**
     * Basic formula for energy calculation
     * @param term1First : First variable of first term
     * @param term1Second : Second variable of first term
     * @param term1Third : Third variable of first term
     * @param term2First : First variable of second term
     * @param term2Second : Second variable of second term
     * @param term2Third : Third variable of second term
     * @return : Value of basic energy calculation
     */
    private static double basicEnergyCalculation(
            double term1First,
            double term1Second,
            double term1Third,
            double term2First,
            double term2Second,
            double term2Third
    ) {
        double term1 = (term1First + (2.0 * term1Second) + term1Third);
        double term2 = -(term2First + (2.0 * term2Second) + term2Third);

        return term1 + term2;
    }

    /**
     * Calculates Horiz Energy from grid
     * @return : Value of Horiz energy
     */
    private double HorizEnergy() {
        return basicEnergyCalculation(a, d, g, c, f, i);
    }

    /**
     * Calculates Vert energy
     * @return : Value of Vert energy
     */
    private double VertEnergy() {
        return basicEnergyCalculation(a, b, c, g, h, i);
    }

    /**
     * Calculates total energy of grid
     * @return : Total energy value
     */
    public double Energy() {
        return sqrt(pow(HorizEnergy(), 2.0) + pow(VertEnergy(), 2.0));
    }
}
