package main;


import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Node {
    public Node(Point position) {
        this.position = position;
    }

    public final Point position;
    public final Set<Node> edgesTo = new HashSet<>();

}
