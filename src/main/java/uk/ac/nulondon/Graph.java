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
        Color rgb;
        for(int i = 0; i < imageInp.getHeight(); i++) {
            rgb = new Color(image.getRGB(0, i));
            Node head = new Node(rgb);
            Node iter = head;
            for(int j = 1; j < imageInp.getWidth(); j++) {
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
     * Updates the BufferedImage image parameter after editing it.
     * Helper function for add, delete, and highlight. Pulls info from pixGraph
     */
    private void imgUpdate() {
        BufferedImage newImg = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
        if(pixGraph.length == 0) {
            return;
        }
        for(int i = 0; i < rows; i++) {
            Node iter = pixGraph[i];
            for(int j = 0; j < cols; j++) {
                while(iter.skip) {
                    iter = iter.right;
                }
                newImg.setRGB(j, i, iter.value.getRGB());
                iter = iter.right;
            }
        }
        image = newImg;
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
                iter.blueAcc = iter.value.getBlue();
                iter = iter.right;
            }
        }
    }

    /**
     * Helper function for seamfinder. will find the smallest value of
     * the possible Doubles in the list of Map.Entry
     * @param n represents the first node that we search through
     * @return an array of Nodes representing the seam
     */
    private Node[] findLeastEnergy(Node n) {
        Node smallestNode = n;
        n = n.right;
        for(int i = 1; i < cols; i++) {
            if (n.energy < smallestNode.energy) {
                smallestNode = n;
            }
            n = n.right;
        }
        Node[] nList = new Node[rows];
        Node iter = smallestNode;
        for(int i = nList.length-1; i > 0; i--) {
            nList[i] = iter;
            iter = iter.lastSeam;
        }
        nList[0] = iter;
        return nList;
    }

    /**
     * Helper function for bluefinder. will find the greatest value of
     * the possible ints in the list of Map.Entry
     * @param n represents the first node that we look through
     * @return an array of Nodes representing the seam
     */
    private Node[] findGreatestBlue(Node n) {
        Node greatest = n;
        n = n.right;
        for(int i = 1; i < cols; i++) {
            if (n.blueAcc > greatest.blueAcc) {
                greatest = n;
            }
            n = n.right;
        }

        Node[] nList = new Node[rows];
        Node iter = greatest;
        for(int i = nList.length-1; i > 0; i--) {
            nList[i] = iter;
            iter = iter.lastSeam;
        }
        nList[0] = iter;

        return nList;
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

        Node iter = pixGraph[0];

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
                    if(iter1.energy < iter1.right.energy) {
                        iter2.energy = iter1.energy + iter2.energy;
                        iter2.lastSeam = iter1;
                    } else {
                        iter2.energy = iter1.right.energy + iter2.energy;
                        iter2.lastSeam = iter1.right;
                    }
                }
                //case if the node is at the right edge
                else if(iter1.right == null) {
                    if(iter1.energy < iter1.left.energy) {
                        iter2.energy = iter1.energy + iter2.energy;
                        iter2.lastSeam = iter1;
                    } else {
                        iter2.energy = iter1.left.energy + iter2.energy;
                        iter2.lastSeam = iter1.left;
                    }
                }
                //otherwise, compare all three possible nodes above & save the value
                else {
                    if((iter1.energy < iter1.left.energy) && (iter1.energy < iter1.right.energy))  {
                        iter2.energy = iter1.energy + iter2.energy;
                        iter2.lastSeam = iter1;
                    } else if((iter1.right.energy < iter1.energy) && (iter1.right.energy < iter1.left.energy)) {
                        iter2.energy = iter1.right.energy + iter2.energy;
                        iter2.lastSeam = iter1.right;
                    }
                    else {
                        iter2.energy = iter1.left.energy + iter2.energy;
                        iter2.lastSeam = iter1.left;
                    }
                }
                iter1 = iter1.right;
                iter2 = iter2.right;
            }
        }

        //makes an ArrayList of Nodes based on the int[] with the least energy
        return findLeastEnergy(pixGraph[rows - 1]);
    }

    /**
     * Finds the seam with the mast blue values and returns it as a list of nodes
     * @return an array of nodes representing the seam with the highest blue value
     * @exception Exception is thrown if there is no image uploaded
     */
    public Node[] blueFinder() throws Exception{
        //case if the graph is empty
        if(pixGraph.length == 0) {
            throw new Exception("No image uploaded");
        }

        //adding to the list as we search through the entire graph
        for(int i = 1; i < rows; i++) {
            Node iter1 = pixGraph[i - 1];
            Node iter2 = pixGraph[i];
            for(int j = 0; j < cols; j++) {
                //case if the node is at the left edge
                if(iter1.left == null) {
                    if(iter1.blueAcc > iter1.right.blueAcc) {
                        iter2.blueAcc = iter2.blueAcc + iter1.blueAcc;
                        iter2.lastSeam = iter1;
                    } else {
                        iter2.blueAcc = iter2.blueAcc + iter1.right.blueAcc;
                        iter2.lastSeam = iter1.right;
                    }
                }
                //case if the node is at the right edge
                else if(iter1.right == null) {
                    if(iter1.blueAcc > iter1.left.blueAcc) {
                        iter2.blueAcc = iter2.blueAcc + iter1.blueAcc;
                        iter2.lastSeam = iter1;
                    } else {
                        iter2.blueAcc = iter2.blueAcc + iter1.left.blueAcc;
                        iter2.lastSeam = iter1.left;
                    }
                }
                //otherwise, compare all three possible nodes above & save the value
                else {
                    if((iter1.blueAcc >= iter1.left.blueAcc)
                            && (iter1.value.getBlue() >= iter1.right.value.getBlue()))  {
                        iter2.blueAcc = iter2.blueAcc + iter1.blueAcc;
                        iter2.lastSeam = iter1;
                    } else if((iter1.right.blueAcc > iter1.blueAcc)
                            && (iter1.right.blueAcc > iter1.left.blueAcc)) {
                        iter2.blueAcc = iter2.blueAcc + iter1.right.blueAcc;
                        iter2.lastSeam = iter1.right;
                    }
                    else {
                        iter2.blueAcc = iter2.blueAcc + iter1.left.blueAcc;
                        iter2.lastSeam = iter1.left;
                    }
                }
                iter1 = iter1.right;
                iter2 = iter2.right;
            }
        }

        //get the node list & return
        return findGreatestBlue(pixGraph[rows - 1]);
    }

    /**
     * Highlights a set of nodes on pixGraph with a given color c
     * then calls imgUpdate to update the BufferedImage image value
     * @param c the color that the nodes will be highlighted
     * @param nodes the list of nodes we will be highlighting
     */
    public void highlightNodes(Node[] nodes, Color c) {
        for(int i = 0; i < nodes.length; i++) {
            nodes[i].value = c;
        }
        imgUpdate();
    }

    /**
     * Given a list of nodes, removes those nodes and resizes the graph
     * by updating the rows value and cols value in pixGraph. then updates
     * the BufferedImage image after this process
     * @param nodes the list of nodes we will be deleting
     * @return a boolean if the resizing & deletion was successfully completed
     */
    public boolean delete(Node[] nodes) {
        for (Node node : nodes) {
            if (node.right == null && node.left == null) {
                return false;
            }
            if (node.left == null) {
                node.right.left = null;
                node.skip = true;
            }
            if (node.right == null) {
                node.left.right = null;
                node.skip = true;
            }
            if (node.right != null && node.left != null) {
                node.right.left = node.left;
                node.left.right = node.right;
            }

        }
        cols--;
        imgUpdate();
        return true;
    }

    /**
     * Given a list of nodes, adds those nodes and resizes the graph
     * by updating the rows value and cols value in pixGraph. then updates
     * the BufferedImage image after this process
     * @param nodes the list of nodes we will be adding
     * @return a boolean if the resizing & addition was successfully completed
     */
    public boolean add(Node[] nodes) {
        for (Node node : nodes) {
            node.value = node.ogValue;
            if (node.right == null && node.left == null) {
                return false;
            }
            if (node.left == null) {
                node.right.left = node;
                node.skip = false;
            }
            if (node.right == null) {
                node.left.right = node;
                node.skip = false;
            }
            if (node.right != null && node.left != null) {
                node.right.left = node;
                node.left.right = node;
                node.skip = false;
            }

        }
        cols++;
        imgUpdate();
        return true;
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
                    while(iter.skip) {
                        iter = iter.right;
                    }
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

    /**
     * toString representation of the graph function for testing but
     * with energy values instead
     * @return a String format representation of graph
     */
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

    /**
     * Will write the current image to a specified file upon prompting
     * @param f the file/filepath specified
     */
    public void saveImg(File f) throws IllegalAccessException {
        try{
            ImageIO.write(image, "png", f);
            System.out.println(f + " has been successfully saved!");
        } catch (Exception e) {
            throw new IllegalAccessException("Path doesn't exist");
        }
    }
}
