package uk.ac.nulondon;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.ByteArrayOutputStream;

public class GUIJFrame {
    JFrame frame;
    public GUIJFrame(String title, ImageIcon img) {
        initialize(title, img);
    }

    private void initialize(String title, ImageIcon img) {
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new JLabel(img));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void kill() {
        frame.setVisible(false); //you can't see me!
        frame.dispose(); //Destroy the JFrame object
    }
}
