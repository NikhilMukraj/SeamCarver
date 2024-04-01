package uk.ac.nulondon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

public class Graph {
    private Node[] pixGraph;
    private BufferedImage image;
    private int rows;
    private int cols;

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

    //returns the color value of the specified pixel
    public Color getGraphIdx(int idxX, int idxY) {
        Node iter = new Node();
        for(int i = 0; i < idxY; i++) {
            iter = pixGraph[idxX].right;
        }
        return iter.value;
    }

}
