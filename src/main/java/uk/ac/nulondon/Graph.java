package uk.ac.nulondon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.util.ArrayList;

/**
 * Image representation and code for returning a node from the list of graphs
 * Representation is a list of nodes that are linked with left and right neighbor
 */
public class Graph {
    private Node[] pixGraph;
    private BufferedImage image;
    private int rows;
    private int cols;

    /**
     * Constructor for the graph.
     * @param imageInp is the BufferedImage inserted through the UI
     */
    public Graph(BufferedImage imageInp) {
        pixGraph = new Node[imageInp.getHeight()];
        this.image = imageInp;
        Color rgb = new Color(image.getRGB(0, 0));
        for(int i = 0; i < imageInp.getHeight(); i++) {
            Node head = new Node(rgb);
            Node iter = head;
            for(int j = 0; j < imageInp.getWidth(); j++) {
                if(i != 0 && j != 0) {
                    rgb = new Color(image.getRGB(j, i));
                    iter.right = new Node(rgb, iter, null);
                    iter = iter.right;
                }
            }
            pixGraph[i] = head;
        }
        this.rows = image.getHeight();
        this.cols = image.getWidth();
    }

    /**
     * Returns the Node given a set of coordinates input by the user
     * @param idxX represents the X value (column position) of the pixel
     * @param idxY represents the Y value (row position) of the pixel
     * @return Node the Node that exists at x, y in the graph
     * @exception IndexOutOfBoundsException thrown if either index is out of
     *                                      bounds
     */
    public Node getGraphIdx(int idxX, int idxY) {
        try {
            Node iter = new Node();
            for(int i = 0; i < idxX; i++) {
                iter = pixGraph[idxY].right;
            }
            return iter;
        } catch(IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("Error: Provided Index Out of Bounds");
        }
    }

    /**
     * Calculates the energy of each pixel in the grid and assigns
     * it an energy value
     * @see Node class to see the energy value storage
     */
    public void setEnergyGrid() throws IllegalArgumentException{
        if( rows == 0 || cols == 0) {
            throw new IllegalArgumentException("No image uploaded");
        }
        ArrayList<ArrayList<Color>> pixelMatrix = new ArrayList<ArrayList<Color>>();
        ArrayList<Color> row1 = new ArrayList<Color>();
        ArrayList<Color> row2 = new ArrayList<Color>();
        ArrayList<Color> row3 = new ArrayList<Color>();
        for(int i = 0; i < rows; i++) {
            Node iter = pixGraph[i];
            for(int j = 0; j < cols; j++) {
                //clearing pixel matrix before every run
                pixelMatrix.clear();

                //adding to the pixel matrix to calculate energy
                //if the pixel is in the first row
                if(i == 0) {
                    for(int n = 0; n < 3; n++) {
                        row1.add(iter.value);
                    }
                }
                //otherwise add as normal (three cases for column edge cases)
                else {
                    if(j == 0) {
                        row1.add(iter.value);
                    } else {
                        row1.add(getGraphIdx(j - 1, i - 1).value);
                    }
                    row1.add(getGraphIdx(j, i - 1).value);
                    if(j == cols - 1) {
                        row1.add(iter.value);
                    } else {
                        row1.add(getGraphIdx(j + 1, i - 1).value);
                    }
                }

                //second row
                if(j == 0) {
                    row2.add(iter.value);
                } else {
                    row2.add(getGraphIdx(j - 1, i).value);
                }
                row2.add(getGraphIdx(j, i).value);
                if(j == cols - 1) {
                    row2.add(iter.value);
                } else {
                    row2.add(getGraphIdx(j + 1, i).value);
                }

                //if the pixel is in the last row
                if(i == rows - 1) {
                    for(int n = 0; n < 3; n++) {
                        row3.add(iter.value);
                    }
                }
                //otherwise add as normal (three cases for column edge cases)
                else {
                    if(j == 0) {
                        row3.add(iter.value);
                    } else {
                        row3.add(getGraphIdx(j - 1, i + 1).value);
                    }
                    row3.add(getGraphIdx(j, i + 1).value);
                    if(j == cols - 1) {
                        row3.add(iter.value);
                    } else {
                        row3.add(getGraphIdx(j + 1, i + 1).value);
                    }
                }
                EnergyCalculation energyCalc = new EnergyCalculation(pixelMatrix);
                iter.energy = energyCalc.Energy();
                iter = iter.right;
            }
        }
    }
}
