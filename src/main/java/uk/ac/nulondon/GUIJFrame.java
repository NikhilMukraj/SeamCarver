package uk.ac.nulondon;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.ByteArrayOutputStream;

/**
 * Used to help display the image when a change is made in the User Interface
 * @see UserInterface
 */
public class GUIJFrame {
    JFrame frame;

    /**
     * Basic constructor
     */
    public GUIJFrame(String title, ImageIcon img) {
        initialize(title, img);
    }

    /**
     * Privated helper function to help initialize the GUIJFrame class
     * @param title the name of the displayed image
     * @param img the image being displayed
     */
    private void initialize(String title, ImageIcon img) {
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new JLabel(img));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Kills the last made frame
     */
    public void kill() {
        frame.setVisible(false); //you can't see me!
        frame.dispose(); //Destroy the JFrame object
    }
}
