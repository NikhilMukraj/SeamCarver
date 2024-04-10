package uk.ac.nulondon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

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
                rgb = new Color(image.getRGB(j, i));
                iter.right = new Node(rgb, iter, null);
                iter = iter.right;
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
            Node iter = pixGraph[idxY];
            for(int i = 0; i < idxX; i++) {
                iter = iter.right;
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
                row1.clear();
                row2.clear();
                row3.clear();
                pixelMatrix.clear();

                //adding to the pixel matrix to calculate energy
                //first row
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
                row2.add(iter.value);
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
                pixelMatrix.add(row1);
                pixelMatrix.add(row2);
                pixelMatrix.add(row3);
                EnergyCalculation energyCalc = new EnergyCalculation(pixelMatrix);
                iter.energy = energyCalc.Energy();
                iter = iter.right;
            }
        }
    }

    /**
     * Finds the seam with the least energy & returns the seam as a list of nodes
     * @return an array of nodes representing the seam with the least energy
     * @exception Exception is thrown if there is no image uploaded or no energy values assigned
     */
    public Node[] seamFinder() throws Exception{
        if(pixGraph.length == 0) {
            throw new Exception("No image uploaded");
        } else if (pixGraph[0].energy == -1) {
            throw new Exception("No energy values assigned");
        }

        ArrayList<Map.Entry<int[], Double>> energyCoords = new ArrayList<>();
        Node iter = pixGraph[0];

        //adding the preliminary format of energyCoords
        for(int i = 0; i < cols; i++) {
            int[] coordList = new int[rows];
            coordList[0] = i;
            Map.Entry<int[], Double> energyCoord = new AbstractMap.SimpleEntry<>(coordList, iter.energy);
            energyCoords.add(energyCoord);
            iter = iter.right;
        }

        /*
        updating energyCoords for each row/column, taking note of the path
        taken as a list of ints
         */
        for(int i = 1; i < rows; i++) {
            Node iter1 = pixGraph[i-1];
            Node iter2 = pixGraph[i];
            for(int j = 0; j < cols; j++) {
                //case if the node is at the left edge
                if(iter1.left == null) {
                    if(iter1.energy > iter1.right.energy) {
                        energyCoords.get(j).getKey()[i] = j;
                        energyCoords.get(j).setValue(iter1.energy + energyCoords.get(j).getValue());
                    } else {
                        energyCoords.get(j).getKey()[i] = j+1;
                        energyCoords.get(j).setValue(iter1.right.energy + energyCoords.get(j).getValue());
                    }
                }
                //case if the node is at the right edge
                else if(iter1.right == null) {
                    if(iter1.energy > iter1.left.energy) {
                        energyCoords.get(j).getKey()[i] = j;
                        energyCoords.get(j).setValue(iter1.energy + energyCoords.get(j).getValue());
                    } else {
                        energyCoords.get(j).getKey()[i] = j-1;
                        energyCoords.get(j).setValue(iter1.left.energy + energyCoords.get(j).getValue());
                    }
                }
                //otherwise, compare all three possible nodes above & save the value
                else {
                    if((iter1.energy > iter1.left.energy) && (iter1.energy > iter1.right.energy))  {
                        energyCoords.get(j).getKey()[i] = j;
                        energyCoords.get(j).setValue(iter1.energy + energyCoords.get(j).getValue());
                    } else if((iter1.right.energy > iter1.energy) && (iter.right.energy > iter.left.energy)) {
                        energyCoords.get(j).getKey()[i] = j + 1;
                        energyCoords.get(j).setValue(iter1.right.energy + energyCoords.get(j).getValue());
                    }
                    else {
                        energyCoords.get(j).getKey()[i] = j-1;
                        energyCoords.get(j).setValue(iter1.left.energy + energyCoords.get(j).getValue());
                    }
                }
            }
            iter1 = iter1.right;
            iter2 = iter2.right;
        }

        //finding the list with the smallest energy value
        Double leastEnergy = energyCoords.getFirst().getValue();
        int idx = 0;
        for(int i = 1; i < energyCoords.size(); i++) {
            if(energyCoords.get(i).getValue() < leastEnergy) {
                leastEnergy = energyCoords.get(i).getValue();
                idx = i;
            }
        }

        //makes an ArrayList of Nodes based on the int[] with the least energy
        int[] idxList = energyCoords.get(idx).getKey();
        for(int i = 0; i < idxList.length; i++) {
            System.out.print(idxList[i] + "," + i + " ");
        }
        System.out.println();
        Node[] nodeList = new Node[idxList.length];
        for(int i = 0; i < idxList.length; i++) {
            nodeList[i] = getGraphIdx(idxList[i], i);
        }
        return nodeList;
    }

    /**
     * Override method of toString to represent the graph function for testing
     * & visualization
     * @return a String format representation of graph
     */
    @Override
    public String toString() {
        if(pixGraph.length == 0) {
            return "";
        }
        StringBuilder string = new StringBuilder();

        for(int i = 0; i < rows; i++) {
            Node iter = pixGraph[i];
            for(int j = 0; j < cols; j++) {
                if(iter != null) {
                    string.append(iter.value.getRed() + "," + iter.value.getGreen() + "," + iter.value.getBlue());
                    if(j != cols - 1) {
                        string.append(" - ");
                    }
                    iter = iter.right;
                }
            }
            string.append(System.lineSeparator());
        }
        return string.toString();
    }

    public String toStringEnergy() {
        if(pixGraph.length == 0) {
            return "";
        }
        StringBuilder string = new StringBuilder();

        for(int i = 0; i < rows; i++) {
            Node iter = pixGraph[i];
            for(int j = 0; j < cols; j++) {
                if(iter != null) {
                    string.append(iter.energy);
                    if(j != cols - 1) {
                        string.append(" - ");
                    }
                    iter = iter.right;
                }
            }
            string.append(System.lineSeparator());
        }
        return string.toString();
    }

    public static void main(String args[]) throws Exception {
        File originalFile = new File("src/resources/beach.png");
        BufferedImage tester = ImageIO.read(originalFile);

        Graph pixgraph = new Graph(tester);
        System.out.println(pixgraph);
        pixgraph.setEnergyGrid();
        System.out.println(pixgraph.toStringEnergy());

        Node[] nodeList = pixgraph.seamFinder();
        for(int i = 0; i < nodeList.length; i++) {
            System.out.print(nodeList[i] + " - ");
        }

    }
}
