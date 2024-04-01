package uk.ac.nulondon;

import java.awt.*;

/**
 * @see Graph for implementation
 * Acts as the nodes of the list of graphs used in the implementation of
 * Graph.java
 */
public class Node {
    Color value;
    Node left;
    Node right;
    int brightness;

    /**
     * Default constructor for Node
     */
    Node(){
        this.value = null;
        this.left = null;
        this.right = null;
    }

    /**
     * Constructor for Node
     * @param value sets a color
     */
    Node(Color value){
        this.value = value;
        this.left = null;
        this.right = null;
    }

    /**
     * Constructor for Node
     * @param value sets a color
     * @param left sets its left node neighbor
     * @param right sets its right node neighbor
     */
    Node(Color value, Node left, Node right){
        this.value = value;
        this.left = left;
        this.right = right;
    }

}
