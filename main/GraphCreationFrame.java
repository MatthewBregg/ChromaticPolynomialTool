package main;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GraphCreationFrame extends JFrame {

    private BufferedImage visualGraph;

    private boolean isPointWithinCircle(Point circleOrigin, int circleRadius, Point point) {
        int xDiff = (point.x - circleOrigin.x);
        xDiff*=xDiff;
        int yDiff = (point.y - circleOrigin.y);
        yDiff*=yDiff;
        return (xDiff+yDiff) < (circleRadius*circleRadius);
    }

    private Node getNodeThatContainsPoint(Point point) {
        for ( Node n : nodes ) {
            if ( isPointWithinCircle(n.position,nodeRadius,point)) {
                return n;
            }
        }
        return null;
    }

    Point startingDrag = null;
    private void colorDraggingNode(Color toColor) {
        Node dragging = getNodeThatContainsPoint(startingDrag);
        colorNode(dragging,toColor);
        drawEdges(visualGraph.createGraphics());
    }
    Color specialNodeSelectedColor = Color.BLUE;
    private class GraphMouseyCreatey extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if ( mouseEvent.getButton() == MouseEvent.BUTTON1) {
                if (isPointOnBackground(mouseEvent.getPoint()) && isPointOutsideNode(mouseEvent.getPoint())) {
                    System.out.println("Adding a new node!");
                    createNewNodeAtPoint(mouseEvent.getPoint());
                    GraphCreationFrame.this.repaint();
                } else {
                    System.out.println("Already was a node here");
                }
            }
            if ( mouseEvent.getButton() == MouseEvent.BUTTON3 ) {
                System.out.println("Deleting node and all edges to it!");
                Node toBeDeleted = getNodeThatContainsPoint(mouseEvent.getPoint());
                // Remove all it's edges
                for (Node node : nodes) {
                    node.edgesTo.remove(toBeDeleted);
                }
                // Remove the node itself.
                nodes.remove(toBeDeleted);
                // Now reorder all ids!!!!
                for ( int id = 0; id != nodes.size(); ++id ) {
                    nodes.get(id).setID(id);
                }
                setBufferedImageToColor(backgroundColor);
                redrawImage();
                repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            if ( mouseEvent.getButton() == MouseEvent.BUTTON1) {
                if (isPointInsideNode(mouseEvent.getPoint())) {
                    startingDrag = mouseEvent.getPoint();
                    colorDraggingNode(specialNodeSelectedColor);
                    repaint();
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                if (isPointInsideNode(mouseEvent.getPoint()) && startingDrag != null) {
                    Node startingNode = getNodeThatContainsPoint(startingDrag);
                    Node endingNode = getNodeThatContainsPoint(mouseEvent.getPoint());
                    if (endingNode == null || startingNode == null) {
                        throw new NullPointerException("Error, got a null node!");
                    }
                    if (startingNode == endingNode) {
                        JOptionPane.showMessageDialog(GraphCreationFrame.this, "Error, not allowing a edge to self currently.");
                    } else {
                        createNewEdgeBetweenNodes(startingNode, endingNode);
                    }
                    colorDraggingNode(nodeColor);
                    startingDrag = null;
                    GraphCreationFrame.this.repaint();
                } else if ( startingDrag != null ) {
                    colorDraggingNode(nodeColor);
                    startingDrag = null;
                    GraphCreationFrame.this.repaint();
                }
            }
        }

    }
    private int getValidPositiveNumber(String message) {
        int number = 0;
        while (true) {
            try {
                String result = JOptionPane.showInputDialog(message);
                number = Integer.parseInt(result);
                if (number <= 0) {
                    throw new NumberFormatException("Number must be >= 0!");
                }
                break;
            } catch (NumberFormatException e) {
                message = "ERROR, Invalid number, try again. Enter a VALID positive number into the field.";
                System.out.println("Failed to parse number, " + e);
            }
        }
        return number;
    }

    private final Color backgroundColor = Color.WHITE;
    public GraphCreationFrame(int width, int height) {
        // setup the frame's attributes.
        this.setTitle("Fun with Graphs");
        this.setSize(width, height);
        Rectangle actualSize = this.getBounds();
        width = actualSize.width;
        height = actualSize.height;

        this.setLayout(new BorderLayout());

        visualGraph = new BufferedImage(width,height-100,BufferedImage.TYPE_INT_ARGB);
        setBufferedImageToColor(backgroundColor);
        JLabel visualizedGraphLabel = new JLabel(new ImageIcon(visualGraph));
        visualizedGraphLabel.addMouseListener(new GraphMouseyCreatey());

        this.add(visualizedGraphLabel,BorderLayout.NORTH);
        JButton calcChromaticPoly = new JButton("Calculate The Chromatic Polynomial");
        calcChromaticPoly.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ChromaticPolynomial chromaticPolynomial = new ChromaticPolynomial();
                String polynomial = chromaticPolynomial.calculateChromaticPolynomial(nodes);
                JTextArea answer = new JTextArea();
                answer.setText(polynomial);
                answer.setWrapStyleWord(true);
                answer.setLineWrap(true);
                answer.setCaretPosition(0);
                answer.setEditable(false);
                JOptionPane.showMessageDialog(GraphCreationFrame.this, new JScrollPane(answer), "Chromatic Polynomial", JOptionPane.INFORMATION_MESSAGE);

            }
        });
        this.add(calcChromaticPoly,BorderLayout.EAST);
        JButton changeNodeSize = new JButton("Change the size of the nodes.");
        changeNodeSize.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int newSize = getValidPositiveNumber("Enter a size for the nodes, default is 30.");
                setBufferedImageToColor(backgroundColor);
                nodeRadius = newSize;
                redrawImage();
                visualizedGraphLabel.repaint();
            }
        });
        this.add(changeNodeSize, BorderLayout.CENTER);
        JButton calcSpanningTree = new JButton("Reduce The Grapth to a spanning tree!");
        calcSpanningTree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
               SpanningTree spanningTree = new SpanningTree();
               spanningTree.mutateIntoSpanningTree(nodes);
               setBufferedImageToColor(backgroundColor);
               redrawImage();
               repaint();
            }
        });
        this.add(calcSpanningTree,BorderLayout.WEST);
    }

    private void setBufferedImageToColor(Color color) {
        Graphics2D g2d = visualGraph.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0,0,visualGraph.getWidth(),visualGraph.getHeight());
    }

    private static final Color nodeColor = Color.RED;
    private static final Color nodeBounderyColor = Color.BLACK;
    private static int nodeRadius = 30;
    private final ArrayList<Node> nodes = new ArrayList<>();
    private void createNewNodeAtPoint(Point point) {
        nodes.add(new Node(point,nodes.size()));
        redrawImage();
    }

    private void redrawImage() {
        Graphics2D g2d = visualGraph.createGraphics();
        drawNodeBoundaries(g2d);
        drawNodes(g2d);
        drawEdges(g2d);
    }

    private void drawNodes(Graphics2D g2d) {
        for ( Node n : nodes ) {
            Point point = n.position;
            g2d.setColor(nodeColor);
            drawSingleNode(g2d,point);
        }
    }

    private void drawSingleNode(Graphics2D g2d, Point point) {
        g2d.fillOval(point.x - nodeRadius, point.y - nodeRadius, nodeRadius * 2, nodeRadius * 2);
    }

    private void colorNode(Node n,Color localnodeColor) {
        Graphics2D g2d = visualGraph.createGraphics();
        Point point = n.position;
        g2d.setColor(localnodeColor);
        drawSingleNode(g2d,point);

    }

    // Note: Since I am always adding an edge to both startingNode.edgesTo and endingNode.edgesTo, we draw all edges twice.
    // Oh well!
    private void drawEdges(Graphics2D g2d) {
        for ( Node startingNode : nodes ) {
            for ( Node endingNode : startingNode.edgesTo ) {
                g2d.setColor(edgeColor);
                DrawArrow.drawArrow(g2d,startingNode.position.x, startingNode.position.y, endingNode.position.x, endingNode.position.y);
            }
        }
    }
    private void drawNodeBoundaries(Graphics2D g2d) {
          for ( Node n : nodes ) {
            Point point = n.position;
            g2d.setColor(nodeBounderyColor);
            g2d.fillOval((int)(point.x-nodeRadius*2.5),(int)(point.y-nodeRadius*2.5),nodeRadius*5,nodeRadius*5);
        }
    }


    // For now, a directionless graph!
    // Modify this code to make directional!!!
    private final Color edgeColor = Color.green;
    private void createNewEdgeBetweenNodes(Node startingNode, Node endingNode) {
        startingNode.edgesTo.add(endingNode);
        endingNode.edgesTo.add(startingNode);
        redrawImage();
    }


    private boolean isPointInsideNode(Point point) {
            return ( getNodeThatContainsPoint(point) != null);
    }

    private boolean isPointOutsideNode(Point point) {
        return ( getNodeThatContainsPoint(point) == null);
    }

    private boolean isPointOnBackground(Point point) {
        Color pointColor = new Color(visualGraph.getRGB(point.x,point.y));
        return (pointColor.equals(backgroundColor));
    }

}
