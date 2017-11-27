package main;

import javax.swing.*;
import java.awt.*;

public class GraphProgram {
    // we'll see later this is an unsafe way to start a GUI based program,
    // and we'll cover how to do it properly.

    public static void main( String[] args ) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public static void createAndShowGUI() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // Improve later, atm it just makes a window smaller than the screen.
        JFrame frame = new GraphCreationFrame(screenSize.width-200, screenSize.height-200);
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible(true); // frame.show(); was depreciated as of java 1.5.
    }

}
