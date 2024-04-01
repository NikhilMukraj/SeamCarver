package uk.ac.nulondon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

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
     * @exception IndexOutOfBoundsException thrown if either index is out of
     *                                      bounds
     */
    public Node getGraphIdx(int idxX, int idxY) {
        if((idxX > cols || idxX < 0) || (idxY > rows || idxY < 0)) {
            throw new IndexOutOfBoundsException("Error: Provided Index out of Bounds");
        }
        Node iter = new Node();
        for(int i = 0; i < idxX; i++) {
            iter = pixGraph[idxY].right;
        }
        return iter;
    }

}
