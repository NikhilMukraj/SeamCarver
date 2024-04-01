package uk.ac.nulondon;

import java.awt.*;

public class Node {
    Color value;
    Node left;
    Node right;
    int brightness;

    // constructor to create a new node
    Node(){
        this.value = null;
        this.left = null;
        this.right = null;
    }

    // constructor to create a new node
    Node(Color value){
        this.value = value;
        this.left = null;
        this.right = null;
    }

    // constructor that takes in sibling nodes as well
    Node(Color value, Node left, Node right){
        this.value = value;
        this.left = left;
        this.right = right;
    }

    /**
     * Set the right child node
     * @param node new node
     */
    public void setRight(Node node) {
        this.right = node;
    }

    /**
     * Set the left child node
     * @param node new node
     */
    public void setLeft(Node node) {
        this.left = node;
    }
}
