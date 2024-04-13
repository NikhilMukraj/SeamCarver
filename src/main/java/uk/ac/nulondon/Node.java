package uk.ac.nulondon;

import java.awt.*;

/**
 * @see Graph for implementation
 * Acts as the nodes of the list of graphs used in the implementation of
 * Graph.java
 */
public class Node {
    Color value;
    Color ogValue;
    int blueAcc;
    Node left;
    Node right;
    Node lastSeam;
    double energy;

    /**
     * Default constructor for Node
     */
    Node(){
        this.value = null;
        this.ogValue = null;
        this.left = null;
        this.right = null;
        this.energy = -1;
    }

    /**
     * Constructor for Node
     * @param value sets a color
     */
    Node(Color value){
        this.value = value;
        this.ogValue = value;
        this.left = null;
        this.right = null;
        this.energy = -1;
        this.blueAcc = value.getBlue();
    }

    /**
     * Constructor for Node
     * @param value sets a color
     * @param left sets its left node neighbor
     * @param right sets its right node neighbor
     */
    Node(Color value, Node left, Node right){
        this.value = value;
        this.ogValue = value;
        this.left = left;
        this.right = right;
        this.energy = -1;
        this.blueAcc = value.getBlue();
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(value.getRed()).append(",").append(value.getGreen()).append(",").append(value.getBlue());
        return str.toString();
    }

}
