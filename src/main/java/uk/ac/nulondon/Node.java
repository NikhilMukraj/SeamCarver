package uk.ac.nulondon;

public class Node {
    int value;
    Node left;
    Node right;

    // constructor to create a new node
    Node(int value){
        this.value = value;
        this.left = null;
        this.right = null;
    }

    // constructor that takes in sibling nodes as well
    Node(int value, Node left, Node right){
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
