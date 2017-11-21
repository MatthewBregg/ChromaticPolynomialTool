package main;

import javax.swing.*;

public class GraphProgram {
    private static final int WIDTH = 1820;
    private static final int HEIGHT = 980;

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
        JFrame frame = new GraphCreationFrame(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible(true); // frame.show(); was depreciated as of java 1.5.
    }

}
