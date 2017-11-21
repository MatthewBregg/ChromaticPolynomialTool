package main;


import java.awt.*;
import java.util.ArrayList;

public class Node {
    public Node(Point position) {
        this.position = position;
    }

    public final Point position;
    public final ArrayList<Node> edgesTo = new ArrayList<>();
}
