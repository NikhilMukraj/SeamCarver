package uk.ac.nulondon;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;

/**
 * Used to help display the image when a change is made in the User Interface
 * @see UserInterface
 */
public class GUIJFrame {
    JFrame frame;
    JLabel label;

    /**
     * Basic constructor
     */
    public GUIJFrame(String title) {
        initialize(title);
    }

    /**
     * Privated helper function to help initialize the GUIJFrame class
     * @param title the name of the displayed image
     */
    private void initialize(String title) {
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(false);
    }

    public void setUpDisp(ImageIcon img) {
        label = new JLabel(img);
        frame.add(label);
        frame.setAlwaysOnTop(true);
    }
    public void updateDisp(ImageIcon img) {
        frame.remove(label);

        label = new JLabel(img);
        frame.add(label);

        frame.invalidate();
        frame.revalidate();
        frame.repaint();

        frame.pack();
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
