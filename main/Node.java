package main;


import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Node {
    int id;
    public Node(Point position, int id) {
        this.position = position;
        this.id = id;
    }

    public final Point position;
    public final Set<Node> edgesTo = new HashSet<>();

    public void setID(int ID) {
        this.id = ID;
    }
}
