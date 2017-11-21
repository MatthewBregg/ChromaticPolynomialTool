package main;

import java.awt.*;
import java.util.*;

public class ChromaticPolynomial {

    class PolyNode {
        public PolyNode(int id) {
            this.id = id;
        }

        int id;
        ArrayList<Integer> edgesTo = new ArrayList<>();
    }


    public String calculateChromaticPolynomial(ArrayList<Node> graph) {
        boolean[][] newGraph = new boolean[graph.size()][graph.size()];
        for (int i = 0; i != graph.size(); ++i ) {
            for ( Node node : graph.get(i).edgesTo ) {
                newGraph[i][node.id] = true;
                newGraph[node.id][i] = true;
            }
        }
        return calculateChromaticPolynomialHelper(newGraph);
    }

    private boolean doesGraphHaveEdges(boolean[][] graph) {
        for (int i = 0; i != graph.length; ++i ) {
            for (int j = 0; j != graph.length; ++j) {
                if (graph[i][j]) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean[][] deepCopy(boolean[][] graph) {
        boolean[][] newGraph = new boolean[graph.length][graph.length];
        for (int i = 0; i != graph.length; ++i ) {
            for (int j = 0; j != graph.length; ++j ) {
                newGraph[i][j] = graph[i][j];
            }
        }
        return newGraph;
    }

    private String calculateChromaticPolynomialHelper(boolean[][] graph) {
        for (int i = 0; i != graph.length; ++i ) {
                if ( graph[i][i] ) {
                    System.err.println("Error, loop detected!");
            }
        }
        if ( !doesGraphHaveEdges(graph) ) {
            return "(x^"+graph.length + ")";
        }

        String deletion = "";
        {
            // Remove an edge from the graph
            boolean[][] graphCopy = deepCopy(graph);
            removeRandomEdgeFromGraph(graphCopy);
            deletion = calculateChromaticPolynomialHelper(graphCopy);
        }
        String contraction = "";
        {
            boolean[][] graphCopy = deepCopy(graph);
            graphCopy = contractRandomEdge(graphCopy);
            contraction = calculateChromaticPolynomialHelper(graphCopy);
        }
        return "(("+deletion + ")-(" + contraction+"))";
    }


    private boolean[][] contractRandomEdge(boolean[][] graph) {
        int edgeBegin = -1;
        int edgeEnd = -1;
        for (int i = 0; i != graph.length; ++i ) {
            for (int j = 0; j != graph.length; ++j) {
                if ( graph[i][j] ) {
                    if ( !graph[j][i] ) {
                        throw new IllegalStateException("Error, graph is directional?");
                    }
                    edgeBegin = i;
                    edgeEnd = j;
                    if ( i == j ) {
                        throw new IllegalStateException("Error, Cycle to self!!");
                    }
                }
            }
        }

        if ( edgeBegin == -1 || edgeEnd == -1 ) {
            throw new IllegalStateException("Error, Never found an edge!");
        }

        ArrayList<Node> newGraph = new ArrayList<>();
        for ( int i = 0; i != graph.length; ++i ) {
            // Ignore point for now, we don't use it.
            newGraph.add(new Node(new Point(0,0),i));
        }

        for (int i = 0; i != graph.length; ++i ) {
            for (int j = 0; j != graph.length; ++j) {
                if ( graph[i][j]) {
                    newGraph.get(i).edgesTo.add(newGraph.get(j));
                    newGraph.get(j).edgesTo.add(newGraph.get(i));
                }
            }
        }

        Node newNode = new Node(new Point(0,0),-1);
        newGraph.add(newNode);

        for ( Node node : newGraph.get(edgeBegin).edgesTo ) {
            newNode.edgesTo.add(node);
            node.edgesTo.add(newNode);

        }
        for ( Node node : newGraph.get(edgeEnd).edgesTo ) {
            newNode.edgesTo.add(node);
            node.edgesTo.add(newNode);
        }
        newNode.edgesTo.remove(newNode);
        for ( Node node : newGraph ) {
            node.edgesTo.remove(newGraph.get(edgeBegin));
            node.edgesTo.remove(newGraph.get(edgeEnd));
        }

        Node edgBeginNode = newGraph.get(edgeBegin);
        Node edgeEndNode= newGraph.get(edgeEnd);
        newGraph.remove(edgBeginNode);
        newGraph.remove(edgeEndNode);

        for ( int i = 0; i != newGraph.size(); ++i ) {
            newGraph.get(i).setID(i);
        }


        boolean[][] newNewGraph = new boolean[newGraph.size()][newGraph.size()];
        for (int i = 0; i != newGraph.size(); ++i ) {
            for ( Node node : newGraph.get(i).edgesTo ) {
                newNewGraph[i][node.id] = true;
                newNewGraph[node.id][i] = true;
            }
        }
        return newNewGraph;
    }


    private void removeRandomEdgeFromGraph(boolean[][] graph) {
        for (int i = 0; i != graph.length; ++i ) {
            for (int j = 0; j != graph.length; ++j) {
                if ( graph[i][j] ) {
                    if ( !graph[j][i] ) {
                        throw new IllegalStateException("Error, graph is directional?");
                    }
                    graph[i][j] = false;
                    graph[j][i] = false;
                    return;
                }
            }
        }
        throw new IllegalStateException("Error, no edges to contract!");
    }

}
