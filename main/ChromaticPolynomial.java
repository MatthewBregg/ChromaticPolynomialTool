package main;

import java.util.*;

public class ChromaticPolynomial {

    class PolyNode {
        public PolyNode(int id) {
            this.id = id;
        }

        int id;
        Set<Integer> edgesTo = new HashSet<>();
    }

    private int ComponentsInGraph(Set<PolyNode> graph) {
        Map<Integer,PolyNode> graphMap = new HashMap<>();
        for ( PolyNode node : graph ) {
            graphMap.put(node.id,node);
        }

        Map<PolyNode,Boolean> visited = new HashMap<>();
        for ( PolyNode node : graph ) {
            visited.put(node,false);
        }
        int components = 0;
        for (PolyNode node : graph ) {
            if ( !visited.get(node) ) {
                ++components;
                recurseAll(node, graphMap, visited);
            }
        }
        return components;
    }

    private void recurseAll(PolyNode node, Map<Integer, PolyNode> graphMap, Map<PolyNode, Boolean> visited) {
       if ( visited.get(node) ) {
           return;
       }
       visited.put(node,true);
       for ( Integer adjacentNode : node.edgesTo) {
           recurseAll(graphMap.get(adjacentNode),graphMap,visited);
       }
    }

    // Returns new node UV, mutates graph so that node u and V are contracted.
    private PolyNode ContractGraph(Set<PolyNode> graph, PolyNode u, PolyNode v) {
       // We will reuse node U and node UV.
        // Remove any edge u had to v.
        for ( Integer nodeID : u.edgesTo ) {
            if (nodeID.equals(v.id)) {
                u.edgesTo.remove(nodeID);
                break;
            }
        }

        for ( PolyNode node : graph ) {
            for ( Integer nodeID : node.edgesTo ) {
                if (nodeID.equals(v.id)) {
                    node.edgesTo.remove(nodeID);
                    node.edgesTo.add(u.id);
                    u.edgesTo.add(node.id);
                    break;
                }
            }
        }

        for ( PolyNode node : graph ) {
            if ( node.id == v.id ) {
                graph.remove(node);
                break;
            }
        }

        return u;
    }


    // Simple enough, remove u from all edges, and then remove u from the set.
    private void removeFromGraph(Set<PolyNode> graph, PolyNode u) {
        for ( PolyNode node : graph ) {
            for ( Integer nodeID : node.edgesTo ) {
                if (nodeID.equals(u.id)) {
                    node.edgesTo.remove(nodeID);
                    break;
                }
            }
        }
        graph.remove(u);
    }


    public String calculateChromaticPolynomial(Set<Node> graph) {
        Set<PolyNode> newGraph = new HashSet<>();
        for (Node n : graph ) {
            PolyNode newNode = new PolyNode(n.hashCode());
            for ( Node adjacent : n.edgesTo ) {
                newNode.edgesTo.add(adjacent.hashCode());
            }
            newGraph.add(newNode);
        }
        return calculateChromaticPolynomialHelper(newGraph);
    }

    private boolean doesGraphHaveEdges(Set<PolyNode> graph) {
        for (PolyNode node : graph ) {
            if ( node.edgesTo.size() > 0 ) {
                return true;
            }
        }
        return false;
    }

    private Set<PolyNode> deepCopy(Set<PolyNode> graph) {
        Set<PolyNode> copy = new HashSet<>();
        for ( PolyNode node : graph ) {
            PolyNode newNode = new PolyNode(node.id);
            newNode.edgesTo = new HashSet<>(node.edgesTo);
            copy.add(newNode);
        }
        return copy;
    }

    private void removeEdge(Set<PolyNode> graph, int nodeIDA, int nodeIDB) {
       for (PolyNode node : graph) {
           if ( node.id == nodeIDA ) {
               for ( Integer nodeID : node.edgesTo) {
                    if ( nodeID.equals(nodeIDB) ) {
                        node.edgesTo.remove(nodeID);
                        break;
                    }
               }
           }
       }
        for (PolyNode node : graph) {
            if ( node.id == nodeIDB ) {
                for ( Integer nodeID : node.edgesTo) {
                    if ( nodeID.equals(nodeIDA) ) {
                        node.edgesTo.remove(nodeID);
                        break;
                    }
                }
            }
        }
    }

    private String calculateChromaticPolynomialHelper(Set<PolyNode> graph) {
        if ( !doesGraphHaveEdges(graph) ) {
            return "(x^"+graph.size() + ")";
        }

        PolyNode a = null;
        PolyNode b = null;

        for ( PolyNode node : graph ) {
            if ( node.edgesTo.size() > 0 ) {
                a = node;
                int otherId = node.edgesTo.iterator().next();
                for ( PolyNode node2 : graph ) {
                    if ( node2.id == otherId ) {
                        b = node2;
                        break;
                    }
                }
                break;
            }
        }

        Set<PolyNode> contracted = deepCopy(graph);
        Set<PolyNode> deleted = deepCopy(graph);
        System.out.println(contracted.size());
        if ( a == null || b == null ) {
            System.out.println("A is " + a + " B is " + b);
        }
        removeEdge(deleted,a.id,b.id);
        ContractGraph(contracted,a,b);
        String contractedPoly = calculateChromaticPolynomialHelper(contracted);
        String deletedPoly = calculateChromaticPolynomialHelper(deleted);

        return deletedPoly + "+" + contractedPoly;

    }

    private boolean isCutEdge(int edgeBegin, int edgeEnd, Set<PolyNode> graph, Map<Integer, PolyNode> graphMap) {
        Set<PolyNode> newGraph = deepCopy(graph);
        for ( PolyNode n : newGraph ) {
            if ( n.id == edgeBegin ) {
                n.edgesTo.remove(edgeEnd);
            }
        }
        for ( PolyNode n : newGraph ) {
            if ( n.id == edgeEnd ) {
                n.edgesTo.remove(edgeBegin);
            }
        }

        return ( ComponentsInGraph(newGraph) > ComponentsInGraph(graph) );
    }


}
