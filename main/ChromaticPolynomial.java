package main;

import java.util.*;

public class ChromaticPolynomial {

    private final ArrayList<Node> graph;
    private HashMap<Node,Boolean> visited;
    private HashMap<String,Integer> polynomialCount = new HashMap<>();

    public ChromaticPolynomial(ArrayList<Node> graph) {
        this.graph = graph;
        getPopulatedVisitedMap();
    }

    // Populate a map with nodes, and mark them all as un visited.
    private HashMap<Node,Boolean> getPopulatedVisitedMap() {
        visited = new HashMap<>();
        for ( Node n : graph ) {
            visited.put(n,false);
        }
        return visited;
    }

    private Queue<Node> bfs = new ArrayDeque<>();

    public static String calculateChromaticPolynomial(ArrayList<Node> graph) {
        ChromaticPolynomial calc = new ChromaticPolynomial(graph);

        Node node = graph.get(0);
        calc.calculateChromaticPolynomialHelper(node);
        StringBuilder result = new StringBuilder();
        for ( Map.Entry<String, Integer> poly : calc.polynomialCount.entrySet()) {
            result.append(poly.getKey()+"^"+poly.getValue());
        }
        return result.toString();
    }

    private void calculateChromaticPolynomialHelper(Node startingNode) {
        bfs.add(startingNode);
        while(!bfs.isEmpty()) {
            Node node = bfs.remove();
            if (visited.get(node)) {
                // If we've already visited this node, skip this iteration.
                continue;
            }
            // Find out how many adjacent nodes we have already visited, then the term we add to the poly is (x-alreadyVisited)
            int alreadyVisited = 0;
            for (Node adjacent : node.edgesTo) {
                if (visited.get(adjacent)) {
                    ++alreadyVisited;
                }
            }
            // Mark this node as visited
            visited.put(node, true);
            String polynomial = "(x-" + alreadyVisited + ")";

            if (polynomialCount.get(polynomial) == null) {
                polynomialCount.put(polynomial, 1);
            } else {
                polynomialCount.put(polynomial, polynomialCount.get(polynomial) + 1);
            }

            // Now move on to all adjacent nodes!
            for (Node adjacent : node.edgesTo) {
                if (!visited.get(adjacent)) {
                    bfs.add(adjacent);
                }
            }
        }
    }

}
